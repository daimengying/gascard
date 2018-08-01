package com.lirong.gascard.aspect;

import com.lirong.gascard.utils.CacheUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @Author: daimengying
 * @Date: 2018/6/11 17:56
 * @Description:自定义批量移除ecache缓存的切面
 */
@Aspect
@Component
public class CacheRemoveAspect {
    private Logger _logger = LoggerFactory.getLogger(this.getClass());

    @Pointcut(value = "(execution(* com.lirong.gascard.service.impl..*.*(..)) && @annotation(com.lirong.gascard.aspect.CacheRemove))")
    private void pointcut() {}

    @AfterReturning(value = "pointcut()")
    private void process(JoinPoint joinPoint){
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            CacheRemove cacheRemove = method.getAnnotation(CacheRemove.class);

            if (cacheRemove != null){
                String value = cacheRemove.value();
                String[] keys = cacheRemove.key(); //需要移除的正则key

                List cacheKeys = CacheUtils.cacheKeys(value);
                String keysStr=Arrays.toString(  keys ).replaceAll("'","");
                Pattern pattern = Pattern.compile(keysStr);
                for (Object cacheKey: cacheKeys){
                    String cacheKeyStr = String.valueOf(cacheKey);
                    if(pattern.matcher(cacheKeyStr).find()){
                        CacheUtils.remove(value, cacheKeyStr);
                    }
                }
            }
        }catch (Exception e){
            _logger.error("【ecache缓存】批量删除key切面异常，错误信息：" + e.getMessage());
        }

    }

}
