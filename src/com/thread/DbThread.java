package com.thread;

import java.io.IOException;
import java.sql.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.hibernate.Session;

import com.bean.User;
import com.util.HibernateUtils;
import com.util.TimeRecord;

public class DbThread implements Runnable {
	public static AtomicInteger ai = new AtomicInteger(0);
	private TimeRecord tr;
	private Session session;

	public DbThread() {
		super();
		this.tr = new TimeRecord();
		this.tr.start();
		session = HibernateUtils.getSessionFactory().openSession();
	}

	@Override
	public void run() {
		for (int i = 0; i < 100; i++) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			StringBuilder sb = new StringBuilder(String.valueOf(ai.incrementAndGet()));
			while (sb.length() < 7) {
				sb = new StringBuilder("0").append(sb);
			}

			User user = new User();
			user.setId(i);
			user.setUsername(sb.toString());
			user.setPassword("bbbb");
			user.setBorn(new Date(System.currentTimeMillis()));
			session.save(user);
			session.beginTransaction().commit();

		}

		try {
			tr.stop();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}