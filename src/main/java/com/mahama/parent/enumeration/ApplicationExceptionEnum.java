package com.mahama.parent.enumeration;

import com.mahama.common.exception.ServiceExceptionEnum;

/**
 * 异常枚举
 */
public enum ApplicationExceptionEnum implements ServiceExceptionEnum {
	REQUEST_NULL("请求有错误"),
	SESSION_TIMEOUT("会话超时"),
	SERVER_ERROR("服务器异常");

	ApplicationExceptionEnum(String message) {
		this.message = message;
	}

	private String message;

	@Override
	public String getMessage() {
		return message;
	}
}
