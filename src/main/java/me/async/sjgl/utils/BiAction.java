package me.async.sjgl.utils;

public interface BiAction<T, U> {

    void accept(T t, U u) throws Exception;
}