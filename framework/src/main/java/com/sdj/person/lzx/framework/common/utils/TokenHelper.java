package com.sdj.person.lzx.framework.common.utils;

import java.util.Random;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

public class TokenHelper {
	public static final String TOKEN_NAMESPACE = "xxx.tokens";
	public static final String TOKEN_NAME_FIELD = "xxx.token.name";
	
	private static final Random RANDOM = new Random();
	
	public static String setToken(HttpServletRequest request){
		return setToken(request, generateGUID());
	}
	public static String setToken(HttpServletRequest request, String tokenName){
		String token = generateGUID();
		setCacheToken(request, tokenName, token);
		return token;
	}
	
	private static void setCacheToken(HttpServletRequest request, String tokenName, String token) {
	}
	private static String generateGUID() {
		return UUID.randomUUID().toString();
	}
}
