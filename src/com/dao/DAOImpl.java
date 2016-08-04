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
	 * DAO���������캯��������ѡ��Ĭ�����ã���ָ������
	 */
	public DAOImpl() {
		super();
		this.sessionFactory = HibernateUtils.getSessionFactory();
		this.session = this.sessionFactory.openSession();
	}

	/**
	 * ʹ��ָ������
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
				// ���±����ύ
				switchConfiguration();
				save(t);
			} else {
				throw e;
			}
		}

	}

	/**
	 * <����HQL������Ψһʵ��>
	 * 
	 * @param hqlString
	 *            HQL���
	 * @param values
	 *            ����������Object����
	 * @return ��ѯʵ��
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
				// ���±����ύ
				switchConfiguration();
				return getByHQL(hqlString, values);
			} else {
				throw e;
			}
		}
	}

	@Override
	public void switchConfiguration() {
		System.err.println("�ύʧ��,�л����ݿ�����");
		HibernateUtils.switchInstance();
		// ��ȡ�����õ�session�����������ύ����
		session = HibernateUtils.getSessionFactory().openSession();
	}
}
