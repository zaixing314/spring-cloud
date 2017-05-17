package com.sdj.lzx.spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sdj.lzx.spring.bean.User;

@RestController
@RequestMapping("/springboot")
public class HelloWorldController {
	
	@Autowired
	User user;
	
	@RequestMapping(value = "/{name}")
	@ResponseBody
	public String say(@PathVariable("name") String name){
		return "Hello, " + name;
	}
}
