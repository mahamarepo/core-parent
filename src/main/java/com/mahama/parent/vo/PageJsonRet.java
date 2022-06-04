package com.mahama.parent.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class PageJsonRet implements Serializable {
    @ApiModelProperty("总记录数")
    private Long total;
    @ApiModelProperty("页数")
    private Integer page;
    @ApiModelProperty("数据")
    private Object data;
    @ApiModelProperty("每页数量")
    private Integer limit;

    public PageJsonRet() {}
    public PageJsonRet(Object data, Long total, Integer page, Integer limit) {
        this.total=total;
        this.page=page;
        this.limit=limit;
        this.data=data;
    }
    public PageJsonRet(Object data, Long total, Long page, Long limit) {
        this.total=total;
        this.page=page.intValue();
        this.limit=limit.intValue();
        this.data=data;
    }
}
