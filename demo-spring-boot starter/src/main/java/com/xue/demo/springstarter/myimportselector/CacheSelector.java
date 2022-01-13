package com.xue.demo.springstarter.myimportselector;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import java.text.MessageFormat;
import java.util.Map;

/**
 * @author xuexiong@souche.com
 * @return
 * @exception
 * @date 2022/1/13 16:20
 */
public class CacheSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        Map<String, Object> annotationAttributes = importingClassMetadata.getAnnotationAttributes(EnableMyCache.class.getName());
        CacheTypeEnums type = (CacheTypeEnums) annotationAttributes.get("type");
        switch (type) {
            case MEMORY: {
                return new String[]{MemoryCacheServiceImpl.class.getName()};
            }
            case REDIS: {
                return new String[]{RedisCacheServiceImpl.class.getName()};
            }
            default: {
                throw new RuntimeException(MessageFormat.format("该缓存类型不支持 {0}", type.toString()));
            }
        }
    }
}

