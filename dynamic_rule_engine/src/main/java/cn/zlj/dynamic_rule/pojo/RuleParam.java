package cn.zlj.dynamic_rule.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * 完整规则条件
 * * * 需求中要实现的判断规则：
 *  *  * 触发条件：E事件 可以扩展为原子条件
 *  *  * 画像属性条件：  k3=v3 , k100=v80 , k230=v360
 *  *  * 行为属性条件：  U(p1=v3,p2=v2) >= 3次 且  G(p6=v8,p4=v5,p1=v2)>=1   （原子事件：事件类型 String，事件属性 hashMAP，阈值 int）
 *  *  * 行为次序条件：  依次做过：  W(p1=v4) ->   R(p2=v3) -> F
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RuleParam implements Serializable {
    private  RuleAutomicParam trrigerPram;//触发条件
    private HashMap<String,String> userProfiePram;// 用户画像条件
    private List<RuleAutomicParam> actionCntPram;//行为属性跳进
    private List<RuleAutomicParam> actionOrderPram;//行为次序条件

}
