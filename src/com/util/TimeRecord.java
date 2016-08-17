package com.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TimeRecord {
	private long lStartTime = 0;
	private long lEndTime = 0;
	private static List<Long> lTimes = new ArrayList<Long>();

	public static void main(String[] args) throws InterruptedException {

	}

	public TimeRecord() {
		super();
	}

	public void start() {
		lStartTime = System.currentTimeMillis();
		System.out.println("start at: " + new SimpleDateFormat("hh:mm:ss").format(lStartTime));
	}

	public void stop() throws IOException {
		DecimalFormat df = new java.text.DecimalFormat("#.##");
		lEndTime = System.currentTimeMillis();
		SimpleDateFormat sf = new SimpleDateFormat("hh:mm:ss");
		// System.out.println("end at: " + new
		// SimpleDateFormat("hh:mm:ss").format(lEndTime));
		// System.out.println(lEndTime - lStartTime);
		lTimes.add(lEndTime - lStartTime);
		String str = "Thread Id is " + Thread.currentThread().getName() + ". Start at:" + sf.format(lStartTime)
				+ ". End at:" + sf.format(lEndTime) + ". Total time is "
				+ df.format(((double) (lEndTime - lStartTime)));
		print(str + "\n");
		System.out.println(str);

	}

	public static void print(String str) throws IOException {
		// 创建txt文件并输出结果

		File file = new File("out.txt");
		if (!file.exists()) {
			file.createNewFile();
		}
		BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
		bw.write(str);
		bw.close();
	}

	public static long getMax() {
		return Collections.max(lTimes);
	}

	public static long getMin() {
		return Collections.min(lTimes);
	}

	public static double getAve() {
		long lTotal = 0;
		for (Long long1 : lTimes) {
			lTotal += long1;
		}
		return (double) ((double) lTotal / (double) lTimes.size());
	}
}
