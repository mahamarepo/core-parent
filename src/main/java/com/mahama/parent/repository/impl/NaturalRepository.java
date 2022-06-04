package com.mahama.parent.repository.impl;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Map;
import java.util.Optional;

/**
 * 包含业务ID的仓库
 * @param <T> 实体类
 * @param <NID> 业务ID类型
 * @param <ID> 主键类型
 */
@NoRepositoryBean
public interface NaturalRepository<T, NID, ID> extends JpaRepository<T, ID> {

    // 单业务id ，使用 @NaturalId 注解业务id字段
    Optional<T> findBySimpleNaturalId(NID naturalId);

    // 多业务id ，使用 @NaturalId 注解业务id字段
    Optional<T> findByNaturalId(Map<String, Object> naturalIds);
}
