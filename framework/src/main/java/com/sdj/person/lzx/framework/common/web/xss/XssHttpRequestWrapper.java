package com.sdj.person.lzx.framework.common.web.xss;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class XssHttpRequestWrapper extends HttpServletRequestWrapper {
	protected Map parameters;

	public XssHttpRequestWrapper(HttpServletRequest request) {
		super(request);
	}

	@Override
	public void setCharacterEncoding(String enc) throws UnsupportedEncodingException {
		super.setCharacterEncoding(enc);
		// 当编码重新设置时，重新加载重新过滤缓存。
		refiltParams();
	}

	void refiltParams() {
		parameters = null;
	}

	@Override
	public String getParameter(String string) {
		String strList[] = getParameterValues(string);
		if (strList != null && strList.length > 0)
			return strList[0];
		else
			return null;
	}

	@Override
	public String[] getParameterValues(String string) {
		if (parameters == null) {
			paramXssFilter();
		}
		return (String[]) parameters.get(string);
	}

	@Override
	public Map getParameterMap() {
		if (parameters == null) {
			paramXssFilter();
		}
		return parameters;
	}

	/**
	 * 
	 * 校验参数，若含xss漏洞的字符,进行替换
	 */
	private void paramXssFilter() {
		parameters = getRequest().getParameterMap();
		XssSecurityManager.filtRequestParams(parameters, this.getServletPath());
	}

}
