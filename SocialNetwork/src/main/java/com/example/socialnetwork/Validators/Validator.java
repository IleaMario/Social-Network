package com.example.socialnetwork.Validators;

@FunctionalInterface
public interface Validator<T> {
    void validate(T entity) throws ValidException;
}