package com.thread;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.util.HibernateUtils;
import com.util.TimeRecord;

public class ThreadTest {
	public static void main(String[] args) throws InterruptedException, IOException {
		delete();
		new HibernateUtils();
		Session session = HibernateUtils.getSession();
		Transaction tx=session.beginTransaction();
		session.createSQLQuery("delete from user_table").executeUpdate();
		tx.commit();
		ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
		for (int i = 0; i < 500; i++) {
			cachedThreadPool.execute(new DbThread());
		}

		cachedThreadPool.shutdown();
		System.out.println("shutdown()：启动一次顺序关闭，执行以前提交的任务，但不接受新任务。");
		while (true) {
			if (cachedThreadPool.isTerminated()) {
				System.out.println("所有的子线程都结束了！");
				TimeRecord.print("Ave:" + String.valueOf(TimeRecord.getAve()) + "\n");
				TimeRecord.print("Max:" + String.valueOf(TimeRecord.getMax()) + "\n");
				TimeRecord.print("Min:" + String.valueOf(TimeRecord.getMin()) + "\n");
				break;
			}
			Thread.sleep(1000);
		}

	}

	private static void delete() {
		File file = new File("out.txt");
		if (file.exists()) {
			System.out.println("文件已存在，已删除");
			file.delete();
		}
	}
}
