package com.sdj.person.lzx.framework.common.web.xss;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XssSecurityManager {
	private static Logger log = LoggerFactory.getLogger(XssSecurityManager.class);

	// 危险的javascript:关键字j av a script
	private final static Pattern[] DANGEROUS_TOKENS = new Pattern[] {
			Pattern.compile("^j\\s*a\\s*v\\s*a\\s*s\\s*c\\s*r\\s*i\\s*p\\s*t\\s*:", Pattern.CASE_INSENSITIVE) };

	// javascript:替换字符串（全角中文字符）
	private final static String[] DANGEROUS_TOKEN_REPLACEMENTS = new String[] { "ＪＡＶＡＳＣＲＩＰＴ：" };

	// 非法的字符集
	private static final char[] INVALID_CHARS = new char[] { '<', '>', '\'', '\"', '\\' };

	// 统一替换可能造成XSS漏洞的字符为全角中文字符
	private static final char[] VALID_CHARS = new char[] { '＜', '＞', '’', '“', '＼' };

	// 开启xss过滤功能开关
	public static boolean enable = false;

	// url-patternMap(符合条件的url-param进行xss过滤）<String ArrayList>
	public static Map urlPatternMap = new HashMap();

	private static HashSet excludeUris = new HashSet();

	private XssSecurityManager() {
		// 不可被实例化
	}

	static {
		init();
	}

	private static void init() {
		try {
			String xssConfig = "/xss_security_config.xml";
			if (log.isDebugEnabled()) {
				log.debug("XSS config file[" + xssConfig + "] init...");
			}
			InputStream is = XssSecurityManager.class.getResourceAsStream(xssConfig);
			if (is == null) {
				log.warn("XSS config file[" + xssConfig + "] not found.");
			} else {
				// 初始化过滤配置文件
				initConfig(is);
				log.info("XSS config file[" + xssConfig + "] init completed.");
			}
		} catch (Exception e) {

			log.error("XSS config file init fail:" + e.getMessage(), e);
		}

	}

	private static void initConfig(InputStream is) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		DocumentBuilder builder = factory.newDocumentBuilder();

		Element root = builder.parse(is).getDocumentElement();
		// -------------------
		NodeList nl = root.getElementsByTagName("enable");
		Node n = null;
		if (nl != null && nl.getLength() > 0) {
			n = ((org.w3c.dom.Element) nl.item(0)).getFirstChild();
		}
		if (n != null) {
			enable = new Boolean(n.getNodeValue().trim()).booleanValue();
		}
		log.info("XSS switch=" + enable);
		// -------------------------
		nl = root.getElementsByTagName("filter-mapping");
		NodeList urlPatternNodes = null;
		if (nl != null && nl.getLength() > 0) {
			Element el = (Element) nl.item(0);
			urlPatternNodes = el.getElementsByTagName("url-pattern");
			// -----------------------------------------------------
			NodeList nl2 = el.getElementsByTagName("exclude-url");
			if (nl2 != null && nl2.getLength() > 0) {
				for (int i = 0; i < nl2.getLength(); i++) {
					Element e = (Element) urlPatternNodes.item(i);
					Node paramNode = e.getFirstChild();
					if (paramNode != null) {
						String paramName = paramNode.getNodeValue().trim();
						if (paramName.length() > 0) {
							excludeUris.add(paramName.toLowerCase());
						}
					}
				}
			}
		}
		// ----------------------
		if (urlPatternNodes != null && urlPatternNodes.getLength() > 0) {
			for (int i = 0; i < urlPatternNodes.getLength(); i++) {
				Element e = (Element) urlPatternNodes.item(i);
				String urlPattern = e.getAttribute("value");
				if (urlPattern != null && (urlPattern = urlPattern.trim()).length() > 0) {
					List filtParamList = new ArrayList(2);
					if (log.isDebugEnabled()) {
						log.debug("Xss filter mapping:" + urlPattern);
					}
					// -------------------------------
					NodeList temp = e.getElementsByTagName("include-param");
					if (temp != null && temp.getLength() > 0) {
						for (int m = 0; m < temp.getLength(); m++) {
							Node paramNode = (temp.item(m)).getFirstChild();
							if (paramNode != null) {
								String paramName = paramNode.getNodeValue().trim();
								if (paramName.length() > 0) {
									filtParamList.add(paramName);
								}
							}

						}

					}
					// 一定得将url转换为小写
					urlPatternMap.put(urlPattern.toLowerCase(), filtParamList);
				}
			}
		}

		// ----------------------
	}

	public static HttpServletRequest wrapRequest(HttpServletRequest httpRequest) {
		if (httpRequest instanceof XssHttpRequestWrapper) {
			// log.info("httpRequest instanceof XssHttpRequestWrapper");
			// include/forword指令会重新进入此Filter
			XssHttpRequestWrapper temp = (XssHttpRequestWrapper) httpRequest;
			// include指令会增加参数，这里需要清理掉缓存
			temp.refiltParams();
			return temp;
		} else {
			// log.info("httpRequest is not instanceof XssHttpRequestWrapper");
			return httpRequest;
		}
	}

	public static List getFiltParamNames(String url) {
		// 获取需要xss过滤的参数
		url = url.toLowerCase();
		List paramNameList = (ArrayList) urlPatternMap.get(url);
		if (paramNameList == null || paramNameList.size() == 0) {
			return null;
		}
		return paramNameList;
	}

	public static void filtRequestParams(Map params, String servletPath) {
		long t1 = System.currentTimeMillis();
		// 得到需要过滤的参数名列表，如果列表是空的，则表示过滤所有参数
		List filtParamNames = XssSecurityManager.getFiltParamNames(servletPath);
		filtRequestParams(params, filtParamNames);
	}

	public static void filtRequestParams(Map params, List filtParamNames) {
		// 获取当前参数集
		Set parameterNames = params.keySet();
		Iterator it = parameterNames.iterator();
		// 得到需要过滤的参数名列表，如果列表是空的，则表示过滤所有参数

		while (it.hasNext()) {
			String paramName = (String) it.next();
			if (filtParamNames == null || filtParamNames.contains(paramName)) {
				String[] values = (String[]) params.get(paramName);
				proceedXss(values);
			}
		}
	}

	/**
	 * 对参数进行防止xss漏洞处理
	 * 
	 * @param value
	 * @return
	 */
	private static void proceedXss(String[] values) {
		for (int i = 0; i < values.length; ++i) {
			String value = values[i];
			if (!isNullStr(value)) {
				values[i] = replaceSpecialChars(values[i]);
			}
		}
	}

	/**
	 * 替换非法字符以及危险关键字
	 * 
	 * @param str
	 * @return
	 */
	private static String replaceSpecialChars(String str) {
		for (int j = 0; j < INVALID_CHARS.length; ++j) {
			if (str.indexOf(INVALID_CHARS[j]) >= 0) {
				str = str.replace(INVALID_CHARS[j], VALID_CHARS[j]);
			}
		}
		str = str.trim();
		for (int i = 0; i < DANGEROUS_TOKENS.length; ++i) {
			str = DANGEROUS_TOKENS[i].matcher(str).replaceAll(DANGEROUS_TOKEN_REPLACEMENTS[i]);
		}

		return str;
	}

	/**
	 * 判断是否为空串，建议放到某个工具类中
	 * 
	 * @param value
	 * @return
	 */
	private static boolean isNullStr(String value) {
		return value == null || value.trim().length() == 0;
	}

	public static void main(String args[]) throws Exception {
		Map datas = new HashMap();
		String paramName = "test";
		datas.put(paramName, new String[] { "Javascript:<script>alert('yes');</script>" });
		filtRequestParams(datas, "/test/sample.do");
		System.out.println(((String[]) datas.get(paramName))[0]);
	}
}
