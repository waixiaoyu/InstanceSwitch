package com.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import com.util.HibernateUtils;

public class DAOImpl implements DAO<Object> {
	private Session session;
	private SessionFactory sessionFactory;

	private static final String CONNECTION_FAIL_MSG = "Unable to obtain JDBC Connection";

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
			if (e.getMessage().equals(CONNECTION_FAIL_MSG)) {
				// 重新本次提交
				switchConfiguration();
				save(t);
			} else {
				throw e;
			}
		}

	}

	/**
	 * <根据HQL语句查找唯一实体>
	 * 
	 * @param hqlString
	 *            HQL语句
	 * @param values
	 *            不定参数的Object数组
	 * @return 查询实体
	 * @see com.itv.launcher.util.IBaseDao#getByHQL(java.lang.String,
	 *      java.lang.Object[])
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
			return query.uniqueResult();
		} catch (Exception e) {
			if (e.getMessage().equals(CONNECTION_FAIL_MSG)) {
				// 重新本次提交
				switchConfiguration();
				return getByHQL(hqlString, values);
			} else {
				throw e;
			}
		}
	}

	@Override
	public void switchConfiguration() {
		System.err.println("提交失败,切换数据库配置");
		HibernateUtils.switchInstance();
		// 获取新配置的session，并且重新提交数据
		session = HibernateUtils.getSessionFactory().openSession();
	}
}
