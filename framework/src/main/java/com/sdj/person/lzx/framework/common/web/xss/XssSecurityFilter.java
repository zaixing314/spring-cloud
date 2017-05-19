package com.sdj.person.lzx.framework.common.web.xss;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XssSecurityFilter implements Filter{
	private static final Logger LOGGER = LoggerFactory.getLogger(XssSecurityFilter.class);
	
	@Override
	public void destroy() {
		LOGGER.info("Xss SecurityFilter end");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		XssHttpRequestWrapper xssRequest = new XssHttpRequestWrapper(httpServletRequest);
		httpServletRequest = XssSecurityManager.wrapRequest(xssRequest);
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		LOGGER.info("Xss SecurityFilter Initializing");
	}

}
