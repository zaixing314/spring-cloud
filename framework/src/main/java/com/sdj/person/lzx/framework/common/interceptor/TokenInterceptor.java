package com.sdj.person.lzx.framework.common.interceptor;

import java.lang.reflect.Method;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.sdj.person.lzx.framework.common.annonation.Token;



public class TokenInterceptor extends HandlerInterceptorAdapter{
	private static Logger logger = LoggerFactory.getLogger(TokenInterceptor.class);

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if(handler instanceof HandlerMethod){
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			Method method = handlerMethod.getMethod();
			Token token = method.getAnnotation(Token.class);
			if(null != token){
				boolean needSaveSession = token.save();
				if(needSaveSession){
					request.getSession(false).setAttribute("token", UUID.randomUUID().toString());
				}
				
				boolean needRemoveSession = token.remove();
				if(needRemoveSession){
					if(isRepeatSubmit(request)){
						return false;
					}
					request.getSession(false).removeAttribute("token");
				}
			}
			return true;
		}
		return super.preHandle(request, response, handler);
	}
	
	private boolean isRepeatSubmit(HttpServletRequest request){
		String serverToken = (String) request.getSession(false).getAttribute("token");
		if(serverToken == null){
			return true;
		}
		
		String clientToken = request.getParameter("token");
		if(clientToken == null){
			return true;
		}
		
		if(!serverToken.equals(clientToken)){
			return true;
		}
		return false;
	}
	
}
