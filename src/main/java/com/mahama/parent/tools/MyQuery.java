package com.mahama.parent.tools;

import lombok.Data;

import java.util.Collection;

/**
 * @author mahama
 * @date 2022年12月11日
 */
@Data
public class MyQuery {
    MyQueryType type;
    Collection<MyQueryParams<?>> list;
}
