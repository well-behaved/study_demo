package com.xue.study.springStudy.utils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.Optional;

/**
 * @Auther: xuexiong@souche.com
 * @Date: 2018/8/29 16:21
 * @Description:
 */
public class JsonUtils {
    /**
     * json转对象
     */


    /**
     * 对象转json通用配置
     * @param object
     * @return
     */
    public static String objetcToJsonCommon(Object object) {
        /**
         * SerializerFeature 分别为
         * List字段如果为null,输出为[],而非null
         * 字符类型字段如果为null,输出为”“,而非null
         * 是否输出值为null的字段,默认为false
         */
        return Optional.ofNullable(object)
                .map(objetc -> JSONObject.toJSONString(objetc
                        , SerializerFeature.WriteNullListAsEmpty
                        , SerializerFeature.WriteNullStringAsEmpty
                        , SerializerFeature.WriteMapNullValue))
                .orElse("");
    }

}
