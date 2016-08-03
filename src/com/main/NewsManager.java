package com.main;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.bean.News;
import com.util.HibernateUtils;

public class NewsManager {
	public static void main(String[] args) throws Exception {
		// 创建session
		Session sess = HibernateUtils.getSession();
		// 开始事物
		Transaction tx = sess.beginTransaction();
		// 创建消息实例
		News n = new News();
		// 设置消息标题和消息内容
		n.setId(1);
		n.setTitle("疯狂java联盟成立了");
		n.setContent("疯狂java联盟成立了，" + "网站地址http://www.crazyit.org");
		System.out.println(n.getId());
		// 保存消息
		sess.save(n);
		// 提交事务
		tx.commit();
	}
}
