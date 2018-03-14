package com.hgicreate.rno.ui.mapper;

import com.hgicreate.rno.ui.model.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    User selectUserById(int id);

    List<User> selectAllUsers();

    void insertUser(User user);
}
