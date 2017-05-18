package com.sdj.lzx.spring.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sdj.lzx.spring.bean.User;
import com.wordnik.swagger.annotations.ApiOperation;

@RestController
public class SwaggerController {

	@ApiOperation(value = "Get all users", notes = "requires noting")
	@RequestMapping(method = RequestMethod.GET)
	public List<User> getUser(){
		List<User> list = new ArrayList<>();
		return list;
	}
	
	@RequestMapping(value = "/{name}", method = RequestMethod.GET)
	public User getUserById(@PathVariable String name){
		User user = new User();
		user.setName("hello, world");
		return user;
	}
}
