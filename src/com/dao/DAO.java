package com.dao;

public interface DAO<T> {
	public abstract void save(T t);

	public abstract T getByHQL(String hqlString, Object... values);

	public abstract void switchConfiguration();
}
