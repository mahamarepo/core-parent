package com.mahama.parent.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class PageRet<T> implements Serializable {
    @ApiModelProperty("总记录数")
    private Long total;
    @ApiModelProperty("页数")
    private Integer page;
    @ApiModelProperty("数据")
    private T data;
    @ApiModelProperty("每页数量")
    private Integer limit;

    public PageRet() {}
    public PageRet(T data,Long total,Integer page,Integer limit) {
        this.total=total;
        this.page=page;
        this.limit=limit;
        this.data=data;
    }
    public PageRet(T data,Long total,Long page,Long limit) {
        this.total=total;
        this.page=page.intValue();
        this.limit=limit.intValue();
        this.data=data;
    }
}
