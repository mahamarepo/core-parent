package com.mahama.parent.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class PageData implements Serializable {
    @ApiModelProperty("页数")
    protected Integer page = 1;
    @ApiModelProperty("每页数量")
    protected Integer limit = 10;
    @ApiModelProperty("排序字段")
    protected String sort = "";
    @ApiModelProperty("排序方式")
    protected Order order = Order.desc;
}
