package com.mahama.parent.config;

import com.mahama.common.utils.DateUtil;
import com.mahama.common.utils.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@AllArgsConstructor
public class DynamicTableName {
    private final DynamicTableConfig config;

    public List<String> getList() {
        return Lists.newArrayList(config.getTableName().split(","));
    }

    public String getTableName(String tName) {
        return tName + "_" + DateUtil.formatDate("yyyyMM");
    }
}
