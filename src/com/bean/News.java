package com.bean;

public class News {
	// ��Ϣ��ı�ʶ����
	private Integer id;
	// ��Ϣ����
	private String title;
	// ��Ϣ����
	private String content;

	// id���Ե�setter��getter����
	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return this.id;
	}

	// title���Ե�setter��getter����
	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return this.title;
	}

	// content ���Ե�setter��getter����
	public void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
		return this.content;
	}
}