package com.hgicreate.rno.ltestrucanlsservice.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rno")
public class RnoProperties {
	/**
	 * 运行模式，取值如下:
	 * always : 上传文件后立刻开始解析导入任务
	 * scheduler : 上传文件后直接结束，由定时器启动解析导入任务
	 * never : 上传文件后直接结束，不启动定时器
	 * 缺省值为 always
	 */
	private String runMode = "always";

	/**
	 * 批量提交的数量
	 */
	private int batch = 5000;

	public String getRunMode() {
		return runMode;
	}

	public void setRunMode(String runMode) {
		this.runMode = runMode;
	}

	public int getBatch() {
		return batch;
	}

	public void setBatch(int batch) {
		this.batch = batch;
	}
}
