package com.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * ����������������Դ���л�����ָ������Դ
 * 
 * @author Administrator
 *
 */
public class HibernateUtils {
	private static final String CFG_SURFIX = "cfg.xml";

	private static String DIR = "";

	private static List<SessionFactory> lFactories;
	private static List<Configuration> lConfigurations;
	private static String[] strCfgFiles;

	private volatile static boolean bStop = false;
	private volatile static Thread thread = null;// �������̶��󣬷�ֹ��ʵ�����̶߳���½������߳�
	// �����������
	private static final int MAX_ATTEMPTS_TIMES = 30;
	// ����ʱ����
	private static final long ATTEMPTS_INTERVAL = 1000;

	private static int nDefaultConfigurationId = 1;// Ĭ�ϵ�����id
	static {
		// ��ʼ����ʱ���ȡ���е�����
		try {
			DIR = HibernateUtils.class.getResource("/").getPath();// ��ȡ��ǰclassesĿ¼��xml�ļ�ֱ�ӻᱻ��������Ŀ¼��
			init();
			System.out.println("\n ����ʵ�����ӳɹ�! \n");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// strCfgFiles = getConfigurationFileNames();
		// new Thread(new Reconnect(nDefaultConfigurationId)).start();

	}

	/**
	 * ��ʼ��������
	 */
	private static void init() {
		strCfgFiles = getConfigurationFileNames();
		lConfigurations = new ArrayList<Configuration>();
		lFactories = new ArrayList<SessionFactory>();
		for (String str : strCfgFiles) {
			initConfigurationsAndFactories(str);
		}
	}

	/**
	 * ��ʼ��Configuration��Factory
	 * 
	 * @param path
	 */
	private static void initConfigurationsAndFactories(String path) {
		Configuration cfg = new Configuration().configure(path);
		lConfigurations.add(cfg);
		SessionFactory factory = cfg.buildSessionFactory();
		lFactories.add(factory);
		System.out.println(cfg.getProperty("connection.url") + " ���ӳɹ�!");
	}

	/**
	 * ������ʱ���޸�configuration��factory,ֱ�Ӵ�������������id����
	 * 
	 * @param nId
	 */
	private static void modifyConfigurationsAndFactories(int nId) {
		Configuration cfg = new Configuration().configure(strCfgFiles[nId]);
		lConfigurations.remove(nId);
		lConfigurations.add(nId, cfg);
		SessionFactory factory = cfg.buildSessionFactory();
		lFactories.remove(nId);
		lFactories.add(nId, factory);
		System.out.println(cfg.getProperty("connection.url") + " �����ӳɹ�!");
	}

	/**
	 * ����Ĭ��·������ȡĿ¼�µ����������ļ���
	 * 
	 * @return
	 */
	private static String[] getConfigurationFileNames() {
		return getConfigurationFileNames(DIR);
	}

	/**
	 * ����ָ��·������ȡĿ¼�µ����������ļ���
	 * 
	 * @return
	 */
	private static String[] getConfigurationFileNames(String dir) {
		File file = new File(dir);
		// only get configuration files
		FilenameFilter ff = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(CFG_SURFIX);
			}
		};
		return file.list(ff);
	}

	public static void closeSession(Session session) {
		if (session != null) {
			if (session.isOpen()) {
				session.close();
			}
		}
	}

	/**
	 * ��ȡsessionFactory��ָ������id
	 * 
	 * @param index
	 * @return
	 */
	public static SessionFactory getSessionFactory(int nConfigurationId) {
		if (nConfigurationId >= lFactories.size()) {
			System.err.println("���ȡ��SessionFactory���������õ����ʵ��������");
			System.exit(1);
			return null;
		} else {
			return lFactories.get(nConfigurationId);
		}
	}

	/**
	 * ֱ��ʹ��Ĭ�ϵ�����
	 * 
	 * @return
	 */
	public static SessionFactory getSessionFactory() {
		return lFactories.get(nDefaultConfigurationId);
	}

	/**
	 * ��ȡsession������ָ��index������ѡȡĬ��
	 * 
	 * @param index
	 * @return
	 */
	public static Session getSession(int index) {
		return lFactories.get(index).openSession();
	}

	public static Session getSession() {
		return lFactories.get(nDefaultConfigurationId).openSession();
	}

	/**
	 * ���������ʱ���л���һ��ʵ����ֱ���޸�ָ��������id
	 */
	public static synchronized void switchInstance() {
		if (thread == null) {
			// ����һ���߳�����������
			thread = new Thread(new Reconnect(nDefaultConfigurationId));
			thread.start();
			++nDefaultConfigurationId;
			int nSize = lFactories.size();
			if (nDefaultConfigurationId >= nSize) {
				nDefaultConfigurationId = (nDefaultConfigurationId - nSize) % nSize;
			}
		} else {
			System.out.println("�Ѿ����������̣߳�ʹ����Ч��Ĭ���̣߳�");
		}
	}

	/**
	 * �ṩ��ֹ�����̵߳ķ���
	 */
	public static void stopAttemptingReconnect() {
		if (thread != null) {
			bStop = true;
			thread.interrupt();
			thread = null;
		} else {
			System.out.println("thread is null");
		}

	}

	/**
	 * �ڲ��࣬����������������
	 * 
	 * @author Administrator
	 *
	 */
	static class Reconnect implements Runnable {
		private int nOldConfigurationId;

		Reconnect(int nOldConfigurationId) {
			this.nOldConfigurationId = nOldConfigurationId;
		}

		@Override
		public void run() {
			int nTringTimes = 0;
			while (MAX_ATTEMPTS_TIMES > nTringTimes && !bStop) {
				++nTringTimes;
				try {
					Thread.sleep(ATTEMPTS_INTERVAL);
				} catch (InterruptedException e) {
					System.out.println("�߳��ж�...");
				}
				try {
					modifyConfigurationsAndFactories(nOldConfigurationId);
					System.out.println(strCfgFiles[nOldConfigurationId] + "���ӳɹ����л�����......");
					nDefaultConfigurationId = nOldConfigurationId;
					return;
				} catch (Exception e) {
					System.out.println(e.getMessage());
					System.out.println(strCfgFiles[nOldConfigurationId] + "����ʧ�ܣ�����������" + nTringTimes + "��......");
				}
			}
			System.out.println("�Ѵﵽ����������������߳��˳���");
		}
	}
}
