package com.hgicreate.rno.model;

import lombok.Data;

/**
 * 结果信息对象
 *
 * @author li.tf
 * @date 2018-01-12 14:12:47
 */
@Data
public class ResultInfo {
    private boolean flag = false;
    private String msg;
    private Object attach;
}
