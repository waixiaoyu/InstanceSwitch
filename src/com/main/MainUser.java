package com.main;

import java.sql.Date;

import org.hibernate.Session;

import com.bean.User;
import com.util.HibernateUtils;

public class MainUser {
	public static void main(String[] args) {
		for (int i = 0; i < 10; i++) {
			Session session = HibernateUtils.getSession();
			session.beginTransaction();
			User user = new User();
			user.setUsername("aaa" + i);
			user.setPassword("aaa");
			user.setBorn(new Date(System.currentTimeMillis()));
			try {
				session.saveOrUpdate(user);
				session.getTransaction().commit();
			} catch (Exception e) {
				System.err.println("Ìá½»Ê§°Ü");
				HibernateUtils.switchInstance();
			}
		}
	}
}
