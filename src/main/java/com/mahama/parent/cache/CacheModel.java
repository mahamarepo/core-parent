package com.mahama.parent.cache;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class CacheModel<I,T> implements Serializable {
    private I index;
    private T value;
}
