package com.hgicreate.rno.lte.common.service;

import com.hgicreate.rno.lte.common.model.TaskQueryCond;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;


@RunWith(SpringRunner.class)
@SpringBootTest
public class TaskServiceTest {

    @Resource
    private TaskService taskService;

    @Test
    public void findAll() {
        TaskQueryCond cond = new TaskQueryCond();
        Pageable pageable = new PageRequest(0, 5);
        org.springframework.data.domain.Page page = taskService.findAll(cond, pageable);
    }
}