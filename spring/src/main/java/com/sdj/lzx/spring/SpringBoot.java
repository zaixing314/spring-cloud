package com.sdj.lzx.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.sdj.lzx.spring.listener.MyApplicationEnvironmentPreparedEventListener;
import com.sdj.lzx.spring.listener.MyApplicationFailedEventListener;
import com.sdj.lzx.spring.listener.MyApplicationPreparedEventListener;
import com.sdj.lzx.spring.listener.MyApplicationStartedEventListener;

@SpringBootApplication
//@EnableEurekaServer
//@EnableHystrixDashboard
public class SpringBoot {
	public static void main(String[] args){
		//SpringApplication.run(SpringBoot.class, args).addApplicationListener(new MyApplicationStartedEventListener());
		//new SpringApplicationBuilder(Application.class).web(true).run(args);
		SpringApplication application = new SpringApplication(SpringBoot.class);
		application.addListeners(new MyApplicationStartedEventListener(), 
				new MyApplicationEnvironmentPreparedEventListener(), 
				new MyApplicationPreparedEventListener(), 
				new MyApplicationFailedEventListener());
	}
}
