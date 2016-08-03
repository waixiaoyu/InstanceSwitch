package com.bean;

public class News {
	// 消息类的标识属性
	private Integer id;
	// 消息标题
	private String title;
	// 消息内容
	private String content;

	// id属性的setter和getter方法
	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return this.id;
	}

	// title属性的setter和getter方法
	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return this.title;
	}

	// content 属性的setter和getter方法
	public void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
		return this.content;
	}
}