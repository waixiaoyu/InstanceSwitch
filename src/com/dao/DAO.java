package com.dao;

import java.util.Collection;
import java.util.List;

public interface DAO<T> {
	public abstract void save(T t);

	public abstract void saveOrUpdate(T t);

	public abstract void update(T t);

	public abstract boolean contains(T t);

	public abstract void queryHql(String hqlString, Object... values);

	public abstract T getByHQL(String hqlString, Object... values);

	public abstract List<?> getListByHQL(String hqlString, Object... values);

	public abstract void delete(T t);

	public abstract void deleteAll(Collection<T> entities);

	public abstract void refresh(T t);

	public abstract int countByHql(String hql, Object... values);

	public abstract void switchConfiguration();
}
