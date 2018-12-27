package com.xue.dubbodemo.service;

import com.xue.dubbodemo.bean.ValidationParameter;

import javax.validation.GroupSequence;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 分组验证示例
 * 缺省可按服务接口区分验证场景，如：@NotNull(groups = ValidationService.class)
 */
public interface ValidationService {
    /**
     * 与方法同名接口，首字母大写，用于区分验证场景，如：@NotNull(groups = ValidationService.Save.class)，可选
     */
    @interface Save {
    }

    /**
     * 同时验证Update组规则
     */
    @GroupSequence(Save.class)
    @interface Update {
    }

    void save(@NotNull ValidationParameter parameter);

    void update(ValidationParameter parameter);

    /**
     * 直接对基本类型参数验证
     *
     * @param id
     */
    void delete(@Min(1) int id);
}