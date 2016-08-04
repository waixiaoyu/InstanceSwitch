package com.main;

import java.sql.Date;

import com.bean.User;
import com.dao.DAO;
import com.dao.DAOImpl;

public class MainUser {
	public static void main(String[] args) throws InterruptedException {
		MainUser mu = new MainUser();
		//mu.save();
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
		String[] values = { "1", "bbbb" };
		dao.getByHQL("from USER_TABLE where USERNAME=? and PASSWORD=?", (Object) values);
	}
}
