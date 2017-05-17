package com.sdj.lzx.spring.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationListener;

public class MyApplicationStartedEventListener implements ApplicationListener<ApplicationStartingEvent>{

	private Logger logger = LoggerFactory.getLogger(MyApplicationStartedEventListener.class);
	@Override
	public void onApplicationEvent(ApplicationStartingEvent event) {
		SpringApplication app = event.getSpringApplication();
		app.setBannerMode(Mode.OFF);
		logger.info("MyApplicationStartedEventListener");
	}

}
