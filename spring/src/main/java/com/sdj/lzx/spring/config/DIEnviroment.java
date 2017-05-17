package com.sdj.lzx.spring.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class DIEnviroment {
	
	@Autowired
	private Environment environment;
	
	public String getProValueFromEnviroment(String key){
		return environment.getProperty(key);
	}
}
