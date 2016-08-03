package com.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtils {
	private static List<SessionFactory> lFactories;
	private static List<Configuration> lConfigurations;
	private static int nDefaultIndex = 1;
	static {
		try {
			String[] strCfgFiles = getConfigurationFileNames();
			lConfigurations = new ArrayList<Configuration>();
			lFactories = new ArrayList<SessionFactory>();
			for (String str : strCfgFiles) {
				initConfigurationsAndFactories(str);
			}

			System.out.println("connect success!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
	}

	private static void initConfigurationsAndFactories(String path) {
		Configuration cfg = new Configuration().configure(path);
		lConfigurations.add(cfg);
		SessionFactory factory = cfg.buildSessionFactory();
		lFactories.add(factory);
		System.out.println(cfg.getProperty("connection.url") + " init success!");
	}

	private static String[] getConfigurationFileNames() {
		File file = new File("./src");
		// only get configuration files
		FilenameFilter ff = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith("cfg.xml");
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
	 * 获取sessionFactory，选取默认，或者指定index
	 * 
	 * @param index
	 * @return
	 */
	public static SessionFactory getSessionFactory(int index) {
		if (index >= lFactories.size()) {
			System.err.println("beyond the total number of instances");
			System.exit(1);
			return null;
		} else {
			return lFactories.get(index);
		}
	}

	public static SessionFactory getSessionFactory() {
		return lFactories.get(nDefaultIndex);
	}

	/**
	 * 获取session，可以指定index，或者选取默认
	 * 
	 * @param index
	 * @return
	 */
	public static Session getSession(int index) {
		return lFactories.get(index).openSession();
	}

	public static Session getSession() {
		return lFactories.get(nDefaultIndex).openSession();
	}

	/**
	 * 出现问题的时候，切换下一个实例
	 */
	public static void switchInstance() {
		++nDefaultIndex;
		int nSize = lFactories.size();
		if (nDefaultIndex >= nSize) {
			nDefaultIndex = (nDefaultIndex - nSize) % nSize;
		}
	}
}
