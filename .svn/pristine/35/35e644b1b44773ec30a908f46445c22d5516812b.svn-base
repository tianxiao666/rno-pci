package com.hgicreate.rno.ui.service;

import com.hgicreate.rno.ui.mapper.LoginRecordMapper;
import com.hgicreate.rno.ui.model.LoginRecord;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoginRecordService {

    private final LoginRecordMapper loginRecordMapper;

    public LoginRecordService(LoginRecordMapper loginRecordMapper) {
        this.loginRecordMapper = loginRecordMapper;
    }

    /**
     * 将用户登入信息加入login_record表中
     */
    public void addLoginInfo(LoginRecord record) {
         loginRecordMapper.insertLoginInfo(record);
    }

    /**
     * 查询用户登入信息
     */
    public List<LoginRecord> getAllLoginRecords() {
        return loginRecordMapper.selectLoginInfo();
    }
}
