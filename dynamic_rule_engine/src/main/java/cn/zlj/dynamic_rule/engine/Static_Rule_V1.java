package cn.zlj.dynamic_rule.engine;

import cn.zlj.dynamic_rule.pojo.LogBean;
import cn.zlj.dynamic_rule.pojo.ResultBean;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.util.Collector;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

/**
 * * 需求中要实现的判断规则：
 *  * 触发条件：E事件 可以扩展为原子条件
 *  * 画像属性条件：  k3=v3 , k100=v80 , k230=v360
 *  * 行为属性条件：  U(p1=v3,p2=v2) >= 3次 且  G(p6=v8,p4=v5,p1=v2)>=1   （原子事件：事件类型 String，事件属性 hashMAP，阈值 int）
 *  * 行为次序条件：  依次做过：  W(p1=v4) ->   R(p2=v3) -> F
 */

public class Static_Rule_V1 {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "hadoop102:9092,hadoop103:9092,hadoop104:9092");
        properties.setProperty("group.id", "test");
        properties.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        FlinkKafkaConsumer<String> user_applog = new FlinkKafkaConsumer<String>("user_applog", new SimpleStringSchema(), properties);
        SingleOutputStreamOperator<LogBean> LogBeanStream = env.addSource(user_applog).map(new MapFunction<String, LogBean>() {
            @Override
            public LogBean map(String s) throws Exception {
                LogBean logBean = JSONObject.parseObject(s, LogBean.class);
                return logBean;
            }
        });
        KeyedStream<LogBean, String> beanStringKeyedStream = LogBeanStream.keyBy(new KeySelector<LogBean, String>() {
            @Override
            public String getKey(LogBean logBean) throws Exception {
                return logBean.getDeviceId();
            }
        });
        beanStringKeyedStream.process(new KeyedProcessFunction<String, LogBean, ResultBean>() {
            Table user_perfile;
            ListStateDescriptor<LogBean> logBeanListStateDescriptor;
            ListState<LogBean> listState;
            @Override
            public void open(Configuration parameters) throws Exception {
                org.apache.hadoop.conf.Configuration configuration = HBaseConfiguration.create();
                configuration.set("hbase.zookeeper.quorum","hadoop102:2181,hadoop103:2181,hadoop104:2181");
                Connection conn = ConnectionFactory.createConnection(configuration);
                user_perfile = conn.getTable(TableName.valueOf("user_profile"));
                logBeanListStateDescriptor = new ListStateDescriptor<>("LogBeanList", LogBean.class);
                listState = getRuntimeContext().getListState(logBeanListStateDescriptor);
            }

            @Override
            public void processElement(LogBean logBean, Context context, Collector<ResultBean> out) throws Exception {
                listState.add(logBean);//每个行为加入状态
                if ("a".equals(logBean.getEventId())){//触发条件是a事件event_id = a
                    Get get = new Get(Bytes.toBytes(logBean.getAccount()));
                    get.addColumn(Bytes.toBytes("f"),Bytes.toBytes("k30")); //过滤出含有k30的列
                    Result result = user_perfile.get(get);
                    byte[] value = result.getValue(Bytes.toBytes("f"), Bytes.toBytes("k30"));
                    String k30_v = value.toString();
                    if ("v30".equals(k30_v)){//用户画像条件过滤 k30 = v30
                        Iterable<LogBean> logBeans = listState.get();
                        int u_amt=0;
                        int p_amt=1;
                        for (LogBean bean : logBeans) {
                            //行为属性条件 u(k1=v1)>=3次,p(k2=v2)>=1次触发
                            if ("u".equals(bean.getEventId())&&"v1".equals(bean.getProperties().get("k1"))){
                                u_amt++;
                            }
                            if ("p".equals(bean.getEventId())&&"v2".equals(bean.getProperties().get("k2"))){
                                p_amt++;
                            }


                        }
                        if (u_amt>=3&&p_amt>=1){
                            ArrayList<LogBean> beanList = new ArrayList<>();
                            CollectionUtils.addAll(beanList, logBeans.iterator());
                            // 则，继续判断行为次序条件：  依次做过：  W(p1=v4) ->   R(p2=v3) -> F

                            int index = -1;
                            for (int i = 0; i < beanList.size(); i++) {
                                LogBean beani = beanList.get(i);
                                if ("W".equals(beani.getEventId())) {
                                    Map<String, String> properties = beani.getProperties();
                                    String p1 = properties.get("p1");
                                    if ("v4".equals(p1)) {
                                        index = i;
                                        break;
                                    }
                                }
                            }

                            int index2 = -1;
                            if (index >= 0 && index + 1 < beanList.size()) {

                                for (int i = index + 1; i < beanList.size(); i++) {
                                    LogBean beani = beanList.get(i);
                                    if ("R".equals(beani.getEventId())) {
                                        Map<String, String> properties = beani.getProperties();
                                        String p2 = properties.get("p2");
                                        if ("v3".equals(p2)) {
                                            index2 = i;
                                            break;
                                        }
                                    }
                                }
                            }
//ewrrrrr
                            int index3 = -1;
                            if (index2 >= 0 && index2 + 1 < beanList.size()) {

                                for (int i = index2 + 1; i < beanList.size(); i++) {
                                    LogBean beani = beanList.get(i);
                                    if ("F".equals(beani.getEventId())) {
                                        index3 = i;
                                        break;
                                    }
                                }
                            }

                            if(index3>-1){
                                ResultBean resultBean = new ResultBean();
//                                resultBean.setDeviceId(logBean.getDeviceId());
//                                resultBean.setRuleId("test_rule_1");
//                                resultBean.setTimeStamp(logBean.getTimeStamp());
                                out.collect(resultBean);
                            }

                        }

                    }
                }
            }
        });

    }
}
