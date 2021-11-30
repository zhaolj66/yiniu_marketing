package cn.zlj.dynamic_rule.datagen;

import cn.zlj.dynamic_rule.pojo.LogBean;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.HashMap;
import java.util.Properties;

/**
 *  private String account;
 *     private String appId;
 *     private String appVersion;
 *     private String carrier;
 *     private String deviceId;
 *     private String deviceType;
 *     private String ip;
 *     private double latitude;
 *     private double longitude;
 *     private String netType;
 *     private String osName;
 *     private String osVersion;
 *     private String releaseChannel;
 *     private String resolution;
 *     private String sessionId;
 *     private long timeStamp;
 *     private String eventId;
 *     private Map<String,String> properties;
 */
public class ActionLogGen {
    public static void main(String[] args) throws InterruptedException {

        for (int i = 0; i < 400; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Properties kafkaPropertis = new Properties();
                    kafkaPropertis.setProperty("bootstrap.servers","hadoop102:9092,hadoop103:9092,hadoop103:9092");
                    kafkaPropertis.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
                    kafkaPropertis.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
                    //构造kafka的生产者
                    KafkaProducer<String, String> stringStringKafkaProducer = new KafkaProducer<>(kafkaPropertis);
                    while (true){
                        LogBean logBean = new LogBean();
                        String account =StringUtils.leftPad(RandomUtils.nextInt(1,10000)+"",6,"0") ;
                        logBean.setAppId("com.yiniu.amt");
                        logBean.setAccount(account);
                        logBean.setAppVersion("3.4");
                        logBean.setCarrier("中国移动");
                        logBean.setDeviceId(account);
                        logBean.setOsName("andrion");
                        logBean.setLatitude(RandomUtils.nextDouble(30,60));
                        logBean.setLongitude(RandomUtils.nextDouble(80,130));
                        logBean.setIp("19.1168.1.12");
                        logBean.setNetType("5g");
                        logBean.setOsVersion("9.0");
                        logBean.setDeviceType("mi6");
                        logBean.setReleaseChannel("mi移动商店");
                        logBean.setResolution("2048*1028");
                        logBean.setEventId(RandomStringUtils.randomAlphabetic(1));
                        HashMap<String, String> prperties = new HashMap<>();
                        for (int i = 0; i <RandomUtils.nextInt(1,5); i++) {
                            prperties.put("k"+RandomUtils.nextInt(1,10),"v"+RandomUtils.nextInt(1,20));
                        }
                        logBean.setProperties(prperties);
                        logBean.setTimeStamp(System.currentTimeMillis());
                        logBean.setSessionId(RandomStringUtils.randomNumeric(10,10));

                        String jsonString = JSON.toJSONString(logBean);
                        //写入kafka的topic
                        stringStringKafkaProducer.send(new ProducerRecord<String,String>("yinew_applog",jsonString));
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }

    //日活30万，高峰时日活60万，高峰同时在线人数10万，每秒日志8000条
    }
}
