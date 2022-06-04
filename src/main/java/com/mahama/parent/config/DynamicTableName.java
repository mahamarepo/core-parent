package com.mahama.parent.config;

import com.mahama.common.utils.DateUtil;
import com.mahama.common.utils.Lists;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
public class DynamicTableName {
    @Value("${dynamic.table-name}")
    private String tableName;

    public List<String> getList() {
        return Lists.newArrayList(tableName.split(","));
    }

    public String getTableName(String tName) {
        return tName + "_" + DateUtil.formatDate("yyyyMM");
    }
}
