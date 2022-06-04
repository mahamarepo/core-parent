package com.mahama.parent.enumeration;

import com.mahama.common.exception.ServiceExceptionEnum;

/**
 * 所有业务异常的枚举
 */
public enum BizExceptionEnum implements ServiceExceptionEnum {
	PARAMETER_ERROR("访问参数有误"),
	P_CODE_COINCIDENCE("父编号和本身编号不能一致"),
	ID_IS_NULL("id不能为空"),
	DATA_IN_TABLE("数据已存在"),
	DATA_NOT_FOUND("数据不存在或者已被删除"),
	NOT_IN_TENANT("当前不允许进行业务操作"),
	FILE_READING_ERROR("读取文件失败!"),
	FILE_NOT_FOUND("文件未找到!"),
	PERM_ERROR("无操作权限");

	BizExceptionEnum(String message) {
		this.message = message;
	}
	private String message;

	@Override
	public String getMessage() {
		return message;
	}
}
