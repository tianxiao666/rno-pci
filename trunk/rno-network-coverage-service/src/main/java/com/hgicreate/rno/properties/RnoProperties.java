package com.hgicreate.rno.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rno")
public class RnoProperties {

	private String hbaseNamespace = "default";
	private Integer hbaseCommitLines = 10000;
	private String hdfsFilePath;
	private String downloadFilePath;

	/**
	 * 运行模式，取值如下:
	 * always : 上传文件后立刻开始解析导入任务
	 * scheduler : 上传文件后直接结束，由定时器启动解析导入任务
	 * never : 上传文件后直接结束，不启动定时器
	 * 缺省值为 always
	 */
	private String runMode = "always";

	public String getHbaseNamespace() {
		return hbaseNamespace;
	}

	public void setHbaseNamespace(String hbaseNamespace) {
		this.hbaseNamespace = hbaseNamespace;
	}

	public String getRunMode() {
		return runMode;
	}

	public void setRunMode(String runMode) {
		this.runMode = runMode;
	}

	public Integer getHbaseCommitLines() {
		return hbaseCommitLines;
	}

	public void setHbaseCommitLines(Integer hbaseCommitLines) {
		this.hbaseCommitLines = hbaseCommitLines;
	}

	public String getHdfsFilePath() {
		return hdfsFilePath;
	}

	public void setHdfsFilePath(String hdfsFilePath) {
		this.hdfsFilePath = hdfsFilePath;
	}


	public String getDownloadFilePath() {
		return downloadFilePath;
	}

	public void setDownloadFilePath(String downloadFilePath) {
		this.downloadFilePath = downloadFilePath;
	}
}
