package com.sdj.lzx.spring;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

import com.netflix.discovery.shared.Application;

@SpringBootApplication
@EnableEurekaServer
@EnableHystrixDashboard
public class SpringBoot {
	public static void main(String[] args){
		//SpringApplication.run(SpringBoot.class, args);
		new SpringApplicationBuilder(Application.class).web(true).run(args);
	}
}
