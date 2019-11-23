package com.aia.ahs.aso.model;

public class SympNotification {
	private Integer documentId;
	private String jsonData;
	private String inBoundFileName;
	private String inBoundFilePath;
	private String outBoundFilePath;
	private String outBoundFileName;
	private String email;
	private String channel;
	private String fullName;
	
	public Integer getDocumentId() {
		return documentId;
	}
	public void setDocumentId(Integer documentId) {
		this.documentId = documentId;
	}
	public String getJsonData() {
		return jsonData;
	}
	public void setJsonData(String jsonData) {
		this.jsonData = jsonData;
	}
	public String getInBoundFileName() {
		return inBoundFileName;
	}
	public void setInBoundFileName(String inBoundFileName) {
		this.inBoundFileName = inBoundFileName;
	}
	
	public String getInBoundFilePath() {
		return inBoundFilePath;
	}
	public void setInBoundFilePath(String inBoundFilePath) {
		this.inBoundFilePath = inBoundFilePath;
	}
	public String getOutBoundFilePath() {
		return outBoundFilePath;
	}
	public void setOutBoundFilePath(String outBoundFilePath) {
		this.outBoundFilePath = outBoundFilePath;
	}
	public String getOutBoundFileName() {
		return outBoundFileName;
	}
	public void setOutBoundFileName(String outBoundFileName) {
		this.outBoundFileName = outBoundFileName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	

}
