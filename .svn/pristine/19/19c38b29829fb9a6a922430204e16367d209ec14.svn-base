package com.iscreate.rnointerfermatrix.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rno")
public class RnoProperties {

	private String hbaseNamespace = "default";
	private String hbaseZookeeperQuorum = "127.0.0.1";
	private Integer hbaseCommitLines = 10000;
	private String hdfsFilePath;
	private String hdfsDefaultFs;
	private String hadoopUserName;
	private String localFilePath;
	private String uploadFilePath;
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

	public String getHbaseZookeeperQuorum() {
		return hbaseZookeeperQuorum;
	}

	public void setHbaseZookeeperQuorum(String hbaseZookeeperQuorum) {
		this.hbaseZookeeperQuorum = hbaseZookeeperQuorum;
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

	public String getHdfsDefaultFs() {
		return hdfsDefaultFs;
	}

	public void setHdfsDefaultFs(String hdfsDefaultFs) {
		this.hdfsDefaultFs = hdfsDefaultFs;
	}

	public String getHadoopUserName() {
		return hadoopUserName;
	}

	public void setHadoopUserName(String hadoopUserName) {
		this.hadoopUserName = hadoopUserName;
	}

	public String getLocalFilePath() {
		return localFilePath;
	}

	public void setLocalFilePath(String localFilePath) {
		this.localFilePath = localFilePath;
	}

	public String getUploadFilePath() {
		return uploadFilePath;
	}

	public void setUploadFilePath(String uploadFilePath) {
		this.uploadFilePath = uploadFilePath;
	}

	public String getDownloadFilePath() {
		return downloadFilePath;
	}

	public void setDownloadFilePath(String downloadFilePath) {
		this.downloadFilePath = downloadFilePath;
	}

}
