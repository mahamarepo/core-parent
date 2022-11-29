package com.mahama.parent.vo;

import com.alibaba.fastjson.JSONObject;
import com.mahama.parent.factory.RetFactory;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Ret<T> implements Serializable {

    @ApiModelProperty("状态码")
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
        this.success = RetFactory.SUCCESS().equals(code);
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}

