package com.xue.dubbodemo.bean;

import com.xue.dubbodemo.service.ValidationService;
import lombok.Data;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Auther: xuexiong@souche.com
 * @Date: 2018/12/26 16:54
 * @Description:
 */
@Data
public class ValidationParameter implements Serializable {
    private static final long serialVersionUID = 8649726128869972619L;
    @NotNull(message = "name 不可为null") // 不允许为空
    @Size(min = 1, max = 2) // 长度或大小范围
    private String name;

//    @NotNull(groups = ValidationService.Save.class) // 保存时不允许为空，更新时允许为空 ，表示不更新该字段
//    @Pattern(regexp = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$")
//    private String email;
//
//    @Min(18) // 最小值
//    @Max(100) // 最大值
//    private int age;
//
//    @Past // 必须为一个过去的时间
//    private Date loginDate;
//
//    @Future // 必须为一个未来的时间
//    private Date expiryDate;
}
