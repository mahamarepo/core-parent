package com.mahama.parent.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public class EntityRedisService<CR extends CrudRepository<T, ID>, T, ID> extends EntityServiceBase {
    @Autowired
    private CR crudRepository;

    public CR getRepository() {
        return crudRepository;
    }

    public Optional<T> findById(ID id) {
        return crudRepository.findById(id);
    }

    public boolean existsById(ID id) {
        return crudRepository.existsById(id);
    }

    public <S extends T> S save(S entity) {
        return crudRepository.save(entity);
    }

    public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
        return crudRepository.saveAll(entities);
    }

    public Iterable<T> findAll() {
        return crudRepository.findAll();
    }

    public Iterable<T> findAllById(Iterable<ID> ids) {
        return crudRepository.findAllById(ids);
    }

    public long count() {
        return crudRepository.count();
    }

    public void deleteById(ID id) {
        crudRepository.deleteById(id);
    }

    public void delete(T entity) {
        crudRepository.delete(entity);
    }

    public void deleteAll(Iterable<? extends T> entities) {
        crudRepository.deleteAll(entities);
    }

    public void deleteAll() {
        crudRepository.deleteAll();
    }
}
