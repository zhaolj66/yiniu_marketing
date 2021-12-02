package cn.zlj.dynamic_rule.datagen;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class UserProfileGen {
    public static void main(String[] args) throws Exception {
        Configuration configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum","hadoop102:2181,hadoop103:2181,hadoop104:2181");
        Connection conn = ConnectionFactory.createConnection(configuration);
        Table user_perfile = conn.getTable(TableName.valueOf("user_profile"));
        int i = 0;
        ArrayList<Put> puts = new ArrayList<>();
        while(true){
            i++;
            String account = StringUtils.leftPad(i+"",6,"0") ;
            Put put = new Put(Bytes.toBytes(account));//确定rowkey
            for (int j = 0; j <500 ; j++) {
                put.addColumn(Bytes.toBytes("f"),Bytes.toBytes("k"+RandomUtils.nextInt(1,500)),Bytes.toBytes("v"+RandomUtils.nextInt(1,100)));
            }

            puts.add(put);
            if (i%20==0){
                user_perfile.put(puts);
                puts.clear();
                System.out.println(i);
            }

        }
//        if (!conn.isClosed()) {
//            conn.close();
//        }

    }
}
