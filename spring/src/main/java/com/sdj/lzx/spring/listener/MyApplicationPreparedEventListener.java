package com.sdj.lzx.spring.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;

public class MyApplicationPreparedEventListener implements ApplicationListener<ApplicationPreparedEvent>{
	private Logger logger = LoggerFactory.getLogger(MyApplicationPreparedEventListener.class);
	@Override
	public void onApplicationEvent(ApplicationPreparedEvent event) {
		ConfigurableApplicationContext configurableApplicationContext = event.getApplicationContext();
		passContextInfo(configurableApplicationContext);
	}
	private void passContextInfo(ConfigurableApplicationContext configurableApplicationContext) {
		
	}

}
