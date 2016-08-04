package com.dao;

import java.util.Collection;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import com.util.HibernateUtils;

public class DAOImpl implements DAO<Object> {
	private Session session;
	private SessionFactory sessionFactory;

	private static final String EXCEPTION_CLASS_NAME = "org.hibernate.exception.JDBCConnectionException";

	/**
	 * DAO有两个构造函数，可以选择默认配置，或指定配置
	 */
	public DAOImpl() {
		super();
		this.sessionFactory = HibernateUtils.getSessionFactory();
		this.session = this.sessionFactory.openSession();
	}

	/**
	 * 使用指定配置
	 */
	public DAOImpl(int nConfigurationId) {
		super();
		this.sessionFactory = HibernateUtils.getSessionFactory(nConfigurationId);
		this.session = this.sessionFactory.openSession();
	}

	@Override
	public void save(Object t) {
		try {
			session.save(t);
			session.beginTransaction().commit();
		} catch (Exception e) {
			System.out.println(e.getClass().getName());
			if (e.getClass().getName().equals(EXCEPTION_CLASS_NAME)) {
				// 重新本次提交
				switchConfiguration();
				save(t);
			} else {
				throw e;
			}
		}
	}

	@Override
	public void saveOrUpdate(Object t) {
		try {
			session.saveOrUpdate(t);
			session.beginTransaction().commit();
		} catch (Exception e) {
			System.out.println(e.getClass().getName());
			if (e.getClass().getName().equals(EXCEPTION_CLASS_NAME)) {
				// 重新本次提交
				switchConfiguration();
				saveOrUpdate(t);
			} else {
				throw e;
			}
		}
	}

	@Override
	public void update(Object t) {
		try {
			session.update(t);
			session.beginTransaction().commit();
		} catch (Exception e) {
			System.out.println(e.getClass().getName());
			if (e.getClass().getName().equals(EXCEPTION_CLASS_NAME)) {
				// 重新本次提交
				switchConfiguration();
				update(t);
			} else {
				throw e;
			}
		}
	}

	/**
	 * <根据HQL语句查找唯一实体>
	 * 
	 */
	@Override
	public Object getByHQL(String hqlString, Object... values) {
		try {
			Query<?> query = session.createQuery(hqlString);
			if (values != null) {
				for (int i = 0; i < values.length; i++) {
					query.setParameter(i, values[i]);
				}
			}
			return query.getSingleResult();
		} catch (Exception e) {
			System.out.println(e.getClass().getName());
			if (e.getClass().getName().equals(EXCEPTION_CLASS_NAME)) {
				// 重新本次提交
				switchConfiguration();
				return getByHQL(hqlString, values);
			} else {
				throw e;
			}
		}
	}

	/**
	 * <根据HQL语句，得到对应的list>
	 * 
	 */
	@Override
	public List<?> getListByHQL(String hqlString, Object... values) {
		try {
			Query<?> query = session.createQuery(hqlString);
			if (values != null) {
				for (int i = 0; i < values.length; i++) {
					query.setParameter(i, values[i]);
				}
			}
			return query.getResultList();
		} catch (Exception e) {
			System.out.println(e.getClass().getName());
			if (e.getClass().getName().equals(EXCEPTION_CLASS_NAME)) {
				// 重新本次提交
				switchConfiguration();
				return getListByHQL(hqlString, values);
			} else {
				throw e;
			}
		}
	}

	/**
	 * <refresh>
	 */
	@Override
	public void refresh(Object t) {
		try {
			this.session.refresh(t);
		} catch (Exception e) {
			if (e.getClass().getName().equals(EXCEPTION_CLASS_NAME)) {
				switchConfiguration();
			} else {
				throw e;
			}
		}
	}

	@Override
	public boolean contains(Object t) {
		try {
			return session.contains(t);
		} catch (Exception e) {
			System.out.println(e.getClass().getName());
			if (e.getClass().getName().equals(EXCEPTION_CLASS_NAME)) {
				// 重新本次提交
				switchConfiguration();
				return contains(t);
			} else {
				throw e;
			}
		}
	}

	@Override
	public void queryHql(String hqlString, Object... values) {
		try {
			Query<?> query = session.createQuery(hqlString);
			if (values != null) {
				for (int i = 0; i < values.length; i++) {
					query.setParameter(i, values[i]);
				}
			}
			query.executeUpdate();
		} catch (Exception e) {
			System.out.println(e.getClass().getName());
			if (e.getClass().getName().equals(EXCEPTION_CLASS_NAME)) {
				// 重新本次提交
				switchConfiguration();
				queryHql(hqlString, values);
			} else {
				throw e;
			}
		}

	}

	@Override
	public void delete(Object t) {
		try {
			session.delete(t);
			session.getTransaction().commit();
		} catch (Exception e) {
			System.out.println(e.getClass().getName());
			if (e.getClass().getName().equals(EXCEPTION_CLASS_NAME)) {
				// 重新本次提交
				switchConfiguration();
				delete(t);
			} else {
				throw e;
			}
		}

	}

	@Override
	public void deleteAll(Collection<Object> entities) {
		try {
			for (Object entity : entities) {
				session.delete(entity);
			}
			session.getTransaction().commit();
		} catch (Exception e) {
			System.out.println(e.getClass().getName());
			if (e.getClass().getName().equals(EXCEPTION_CLASS_NAME)) {
				// 重新本次提交
				switchConfiguration();
				deleteAll(entities);
			} else {
				throw e;
			}
		}

	}

	@Override
	public int countByHql(String hql, Object... values) {
		try {
			Query<?> query = session.createQuery(hql);
			if (values != null) {
				for (int i = 0; i < values.length; i++) {
					query.setParameter(i, values[i]);
				}
			}
			return query.getResultList().size();
		} catch (Exception e) {
			System.out.println(e.getClass().getName());
			if (e.getClass().getName().equals(EXCEPTION_CLASS_NAME)) {
				// 重新本次提交
				switchConfiguration();
				return countByHql(hql, values);
			} else {
				throw e;
			}
		}
	}

	// ---------------------------------------------------------------------------------------------//
	/**
	 * 配置切换函数，会调用HibernateUtils中的方法
	 */
	@Override
	public void switchConfiguration() {
		System.err.println("提交失败,切换数据库配置");
		HibernateUtils.switchInstance();
		// 获取新配置的session，并且重新提交数据
		session = HibernateUtils.getSessionFactory().openSession();
	}

}
