package cn.zlj.dynamic_rule.datagen;

import cn.zlj.dynamic_rule.pojo.LogBean;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;

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
    public static void main(String[] args) {
        while (true){
            LogBean logBean = new LogBean();
            String account =StringUtils.leftPad(RandomUtils.nextInt(1,10000)+"",6,"0") ;
            logBean.setAppId("com.yiniu.amt");
            logBean.setAccount(account);
            logBean.setAppVersion("3.4");
            logBean.setCarrier("中国移动");
            logBean.setDeviceId(account);
            logBean.setOsName("andrion");
            logBean.setLatitude(RandomUtils.nextDouble());
            logBean.setLongitude(RandomUtils.nextDouble());
            logBean.setIp("19.1168.1.12");
            logBean.setNetType("5g");
            logBean.setOsVersion("9.0");
            logBean.setDeviceType("mi6");
            logBean.setReleaseChannel("mi移动商店");
            logBean.setEventId(RandomStringUtils.randomAlphabetic(1));
        }
    }
}
