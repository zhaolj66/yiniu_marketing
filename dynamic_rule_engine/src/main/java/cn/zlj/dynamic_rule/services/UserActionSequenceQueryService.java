package cn.zlj.dynamic_rule.services;



import cn.zlj.dynamic_rule.pojo.RuleParam;
import org.apache.flink.api.common.state.ListState;

/**
 * @author 涛哥
 * @nick_name "deep as the sea"
 * @contact qq:657270652 wx:doit_edu
 * @site www.doitedu.cn
 * @date 2021-03-28
 * @desc 用户行为次序列条件查询服务接口
 */
public interface UserActionSequenceQueryService {

    public boolean queryActionSequence(String deviceId, RuleParam ruleParam) throws Exception;
}
