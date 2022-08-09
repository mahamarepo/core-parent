package com.mahama.parent.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class PageData implements Serializable {
    @ApiModelProperty(value = "页数", example = "1")
    protected Integer page = 1;
    @ApiModelProperty(value = "每页数量", example = "10")
    protected Integer limit = 10;
    @ApiModelProperty("排序字段")
    protected String sort = "";
    @ApiModelProperty(value = "排序方式", example = "desc")
    protected Order order = Order.desc;
}
