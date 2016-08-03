package com.main;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.bean.News;
import com.util.HibernateUtils;

public class NewsManager {
	public static void main(String[] args) throws Exception {
		// ����session
		Session sess = HibernateUtils.getSession();
		// ��ʼ����
		Transaction tx = sess.beginTransaction();
		// ������Ϣʵ��
		News n = new News();
		// ������Ϣ�������Ϣ����
		n.setId(1);
		n.setTitle("���java���˳�����");
		n.setContent("���java���˳����ˣ�" + "��վ��ַhttp://www.crazyit.org");
		System.out.println(n.getId());
		// ������Ϣ
		sess.save(n);
		// �ύ����
		tx.commit();
	}
}
