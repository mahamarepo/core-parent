package com.mahama.parent.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Ret<T> implements Serializable {

    @ApiModelProperty("状态码：20000成功，99999失败，50014 token无效或过期")
    private Integer code;
    @ApiModelProperty("说明")
    private String msg;
    @ApiModelProperty("返回数据")
    private T data;
    private boolean success;

    public Ret() {

    }

    public Ret(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.success = Rets.SUCCESS.equals(code);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        builder.append("'code':").append(code).append(",");
        builder.append("'msg':").append(msg).append(",");
        builder.append("'success':").append(success).append(",");
        builder.append("}");
        return builder.toString();
    }
}

