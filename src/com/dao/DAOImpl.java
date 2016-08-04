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
			System.out.println(e.getClass().getName());
			if (e.getClass().getName().equals(EXCEPTION_CLASS_NAME)) {
				// ���±����ύ
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
				// ���±����ύ
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
				// ���±����ύ
				switchConfiguration();
				update(t);
			} else {
				throw e;
			}
		}
	}

	/**
	 * <����HQL������Ψһʵ��>
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
				// ���±����ύ
				switchConfiguration();
				return getByHQL(hqlString, values);
			} else {
				throw e;
			}
		}
	}

	/**
	 * <����HQL��䣬�õ���Ӧ��list>
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
				// ���±����ύ
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
				// ���±����ύ
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
				// ���±����ύ
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
				// ���±����ύ
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
				// ���±����ύ
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
				// ���±����ύ
				switchConfiguration();
				return countByHql(hql, values);
			} else {
				throw e;
			}
		}
	}

	// ---------------------------------------------------------------------------------------------//
	/**
	 * �����л������������HibernateUtils�еķ���
	 */
	@Override
	public void switchConfiguration() {
		System.err.println("�ύʧ��,�л����ݿ�����");
		HibernateUtils.switchInstance();
		// ��ȡ�����õ�session�����������ύ����
		session = HibernateUtils.getSessionFactory().openSession();
	}

}
