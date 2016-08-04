package com.main;

import java.sql.Date;
import java.util.List;

import org.hibernate.query.Query;

import com.bean.User;
import com.dao.DAO;
import com.dao.DAOImpl;
import com.util.HibernateUtils;

public class MainUser {
	public static void main(String[] args) throws InterruptedException {
		// Query<?> query = HibernateUtils.getSession().createQuery("from User
		// where username=?");
		// query.setParameter(0, "2");
		// System.out.println(query.getSingleResult());

		MainUser mu = new MainUser();
		// mu.save();
		mu.find();
	}

	public void save() throws InterruptedException {
		for (int i = 0; i < 100; i++) {
			Thread.sleep(1000);
			User user = new User();
			user.setId(i);
			user.setUsername("" + i);
			user.setPassword("bbbb");
			user.setBorn(new Date(System.currentTimeMillis()));
			DAO<Object> dao = new DAOImpl();
			dao.save(user);
		}
	}

	public void find() {
		DAO<?> dao = new DAOImpl();
		User u = (User) dao.getByHQL("from User where username=? and password=?", "1", "bbbb");
		System.out.println(u.toString());
	}

	public void findAll() {
		DAO<?> dao = new DAOImpl();
		List<?> u = dao.getListByHQL("from User where username=? ", "2");
		System.out.println(u.toString());
	}
}
