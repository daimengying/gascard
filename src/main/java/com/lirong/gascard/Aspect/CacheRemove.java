package com.lirong.gascard.Aspect;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: daimengying
 * @Date: 2018/6/11 17:54
 * @Description:自定义缓存，模糊批量移除ecache缓存
 */
@Target({ java.lang.annotation.ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheRemove {
    String value();
    String[] key();
}
