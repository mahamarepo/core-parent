package com.mahama.parent.vo;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class ListData implements Serializable {
    @ApiModelProperty("排序字段")
    protected String sort = "";
    @ApiModelProperty(value = "排序方式", example = "desc")
    protected Order order = Order.desc;
}
