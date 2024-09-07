package dev.yudiplease.exspansi.bot.service;

import org.springframework.stereotype.Service;

@Service
public interface CrudService<T> {
    void load();
    void add(T t);
    T getById(String id);
    void save();
    void deleteById(String id);
}
