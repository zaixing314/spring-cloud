package com.sdj.lzx.spring.bean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "user")
public class User {
	@Value("${name:lzx}")
	private String name;
	
	@Value("${age}")
	private Integer age;
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
