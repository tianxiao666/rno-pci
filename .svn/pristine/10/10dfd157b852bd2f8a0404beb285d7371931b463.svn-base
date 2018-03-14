package com.hgicreate.rno.lte.common.controller;

import com.hgicreate.rno.lte.common.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chen.c10
 */
@Slf4j
@RestController
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }
//
//    /**
//     * 查询数据记录
//     */
//    @PostMapping("/queryTaskByPage")
//    public PageResultBody queryTaskByPage(@RequestBody PageCondBody<TaskQueryCond> condBody) {
//        log.debug("进入方法:queryTaskByPage。condBody = {}", condBody);
//        TaskQueryCond cond = condBody.getCond();
//        Page page = condBody.getPage();
//
//        Pageable pageable = new PageRequest(page.getCurrentPage() - 1, page.getPageSize(), Sort.Direction.DESC, "createTime");
//        org.springframework.data.domain.Page result = taskService.findAll(cond, pageable);
//
//        page.setTotalCnt(result.getTotalElements());
//        page.setTotalPageCnt(result.getTotalPages());
//        page.setForcedStartIndex(-1);
//
//        PageResultBody resultBody = new PageResultBody();
//        resultBody.setResult("success");
//        resultBody.setData(result.getContent() == null ? Collections.emptyList() : result.getContent());
//        resultBody.setPage(page);
//        log.debug("退出方法：queryTaskByPage。resultBody={}", resultBody);
//        return resultBody;
//    }
//
//    /**
//     * 通过 jobId 获取Lte方位角计算记录信息
//     */
//    @GetMapping("/queryTaskRecord/{jobId}")
//    public AbstractTask queryTaskRecordByJobId(@PathVariable("jobId") long jobId, String jobType) {
//        log.debug("进入方法：queryTaskRecordByJobId。jobId={},jobType={}", jobId, jobType);
//        return taskService.getOne(jobId, jobType);
//    }
}
