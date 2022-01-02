package cn.zlj.dynamic_rule.services;



import cn.zlj.dynamic_rule.pojo.RuleParam;

import java.io.IOException;

/**
 * @author 涛哥
 * @nick_name "deep as the sea"
 * @contact qq:657270652 wx:doit_edu
 * @site www.doitedu.cn
 * @date 2021-03-28
 * @desc 用户画像数据查询服务接口
 */
public interface UserProfileQueryService {

    public boolean judgeProfileCondition(String deviceId, RuleParam ruleParam);

}
