package com.lirong.gascard.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @Author: daimengying
 * @Date: 2018/6/11 18:28
 * @Description:获取ApplicationContext
 */
@Component
public class ApplicationContextUtils implements ApplicationContextAware {
    public static ApplicationContext applicationContext=null;

    @Override
    public void setApplicationContext(ApplicationContext arg0) throws BeansException {
        if (applicationContext == null) {
            synchronized (ApplicationContextUtils.class) {
                if (applicationContext == null) {
                    applicationContext   = arg0;
                }
            }
        }
    }
}
