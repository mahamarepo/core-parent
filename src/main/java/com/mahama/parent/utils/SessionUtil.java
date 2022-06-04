package com.mahama.parent.utils;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 对Session的管理
 */
public class SessionUtil {

	public static HttpSession getSession() {
		return getRequest().getSession();
	}

	public static HttpServletRequest getRequest() {
		return HttpUtil.getRequest();
	}
}
