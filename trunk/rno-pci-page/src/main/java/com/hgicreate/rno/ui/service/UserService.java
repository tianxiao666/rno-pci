package com.hgicreate.rno.ui.service;

import com.hgicreate.rno.ui.mapper.UserMapper;
import com.hgicreate.rno.ui.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserMapper userMapper;

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public User getUser(int id) {
        return userMapper.selectUserById(id);
    }

    public List<User> getAllUser() {
        return userMapper.selectAllUsers();
    }

    public void addUser(User user) {
        userMapper.insertUser(user);
    }
}
