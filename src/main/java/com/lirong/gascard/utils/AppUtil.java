package com.lirong.gascard.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ClassUtils;

import javax.servlet.ServletContext;
import java.beans.Introspector;

public class AppUtil implements ApplicationContextAware {
	private static ApplicationContext applicationContext;
	private static ServletContext servletContext;

	public static void init(ServletContext _servletContext) {
		servletContext = _servletContext;
	}

	@Override
	public void setApplicationContext(ApplicationContext contex)
			throws BeansException {
		applicationContext = contex;
	}

	public static ApplicationContext getContext() {
		return applicationContext;
	}

	public static ServletContext getServletContext() throws Exception {
		return servletContext;
	}

//	public static Object getBean(Class cls) {
//		return applicationContext.getBean(cls);
//	}

	public static Object getBean(String beanId) {
		return applicationContext.getBean(beanId);
	}

	public static <T> T getBean(Class<T> requiredType) {
		String shortClassName = ClassUtils.getShortName(requiredType);
		String beanName = Introspector.decapitalize(shortClassName);
		return (T) applicationContext.getBean(beanName, requiredType);
	}
	
	public static String getAppAbsolutePath() {
		return servletContext.getRealPath("/");
	}

	public static String getRealPath(String path) {
		return servletContext.getRealPath(path);
	}

}