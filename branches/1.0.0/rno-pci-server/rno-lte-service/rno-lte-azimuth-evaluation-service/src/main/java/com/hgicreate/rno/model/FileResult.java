package com.hgicreate.rno.model;

import lombok.Data;

/**
 * 文件结果对象
 *
 * @author li.tf
 * @date 2018-01-12 14:06:06
 */
@Data
public class FileResult {
    private boolean result;
    private String msg;
    private int statusCode;
    private String filename;
    private byte[] fileBody;
    private long fileLength;
}
