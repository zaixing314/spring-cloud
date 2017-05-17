package com.sdj.lzx.spring.listener;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

public class MyApplicationEnvironmentPreparedEventListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent>{

	private Logger logger = LoggerFactory.getLogger(MyApplicationEnvironmentPreparedEventListener.class);
	@Override
	public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
		ConfigurableEnvironment environment = event.getEnvironment();
		MutablePropertySources mutablePropertySources = environment.getPropertySources();
		if(null != mutablePropertySources){
			Iterator<PropertySource<?>> iterator = mutablePropertySources.iterator();
			while(iterator.hasNext()){
				PropertySource<?> ps = iterator.next();
				logger.info("ps.getName:{}, ps.getSource:{}, ps.getClass:{}", ps.getName(), ps.getSource(), ps.getClass());
			}
		}
	}

}
