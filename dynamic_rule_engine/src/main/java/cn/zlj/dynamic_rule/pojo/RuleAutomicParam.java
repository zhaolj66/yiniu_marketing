package cn.zlj.dynamic_rule.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;

/**
 * 原子规则
 * 事件ID STRIng
 * 事件属性 hashmap
 * 以及事件发生的次数
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RuleAutomicParam implements Serializable {
    private String eventId;
    private HashMap<String,String> properties;
    private int cnts;
    //事件发生时间端
    private long range_Start;
    //结束时间端
    private  long end_Start;
}
