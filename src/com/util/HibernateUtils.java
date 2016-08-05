package com.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * 该类用于主备数据源的切换，或指定数据源
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
	private volatile static Thread thread = null;// 创建进程对象，防止单实例多线程多次新建重连线程
	// 最大重连次数
	private static final int MAX_ATTEMPTS_TIMES = 30;
	// 重连时间间隔
	private static final long ATTEMPTS_INTERVAL = 1000;

	private static int nDefaultConfigurationId = 1;// 默认的配置id
	static {
		// 初始化的时候读取所有的配置
		try {
			DIR = HibernateUtils.class.getResource("/").getPath();// 读取当前classes目录，xml文件直接会被拷贝到该目录下
			init();
			System.out.println("\n 所有实例连接成功! \n");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// strCfgFiles = getConfigurationFileNames();
		// new Thread(new Reconnect(nDefaultConfigurationId)).start();

	}

	/**
	 * 初始化主函数
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
	 * 初始化Configuration和Factory
	 * 
	 * @param path
	 */
	private static void initConfigurationsAndFactories(String path) {
		Configuration cfg = new Configuration().configure(path);
		lConfigurations.add(cfg);
		SessionFactory factory = cfg.buildSessionFactory();
		lFactories.add(factory);
		System.out.println(cfg.getProperty("connection.url") + " 连接成功!");
	}

	/**
	 * 重连的时候修改configuration和factory,直接传入重连的配置id即可
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
		System.out.println(cfg.getProperty("connection.url") + " 重连接成功!");
	}

	/**
	 * 根据默认路径，获取目录下的所有配置文件名
	 * 
	 * @return
	 */
	private static String[] getConfigurationFileNames() {
		return getConfigurationFileNames(DIR);
	}

	/**
	 * 根据指定路径，获取目录下的所有配置文件名
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
	 * 获取sessionFactory，指定配置id
	 * 
	 * @param index
	 * @return
	 */
	public static SessionFactory getSessionFactory(int nConfigurationId) {
		if (nConfigurationId >= lFactories.size()) {
			System.err.println("你获取的SessionFactory超过了配置的最大实例总数！");
			System.exit(1);
			return null;
		} else {
			return lFactories.get(nConfigurationId);
		}
	}

	/**
	 * 直接使用默认的配置
	 * 
	 * @return
	 */
	public static SessionFactory getSessionFactory() {
		return lFactories.get(nDefaultConfigurationId);
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
		return lFactories.get(nDefaultConfigurationId).openSession();
	}

	/**
	 * 出现问题的时候，切换下一个实例，直接修改指定的配置id
	 */
	public static synchronized void switchInstance() {
		if (thread == null) {
			// 启动一个线程来尝试重连
			thread = new Thread(new Reconnect(nDefaultConfigurationId));
			thread.start();
			++nDefaultConfigurationId;
			int nSize = lFactories.size();
			if (nDefaultConfigurationId >= nSize) {
				nDefaultConfigurationId = (nDefaultConfigurationId - nSize) % nSize;
			}
		} else {
			System.out.println("已经启动重连线程，使用有效的默认线程！");
		}
	}

	/**
	 * 提供终止重连线程的方法
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
	 * 内部类，尝试重新连接配置
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
					System.out.println("线程中断...");
				}
				try {
					modifyConfigurationsAndFactories(nOldConfigurationId);
					System.out.println(strCfgFiles[nOldConfigurationId] + "连接成功，切换配置......");
					nDefaultConfigurationId = nOldConfigurationId;
					return;
				} catch (Exception e) {
					System.out.println(e.getMessage());
					System.out.println(strCfgFiles[nOldConfigurationId] + "连接失败，尝试重连第" + nTringTimes + "次......");
				}
			}
			System.out.println("已达到最大尝试重连次数，线程退出！");
		}
	}
}
