package com.hgicreate.rno.service;

import com.hgicreate.rno.mapper.DataMapper;
import com.hgicreate.rno.model.*;
import com.hgicreate.rno.repo.AzimuthEvalResultRepository;
import com.hgicreate.rno.repo.AzimuthEvalTaskRepository;
import com.hgicreate.rno.repo.JobRepository;
import com.hgicreate.rno.repo.ReportRepository;
import com.hgicreate.rno.web.rest.vm.AzimuthEvaluationSubmitTaskVm;
import com.hgicreate.rno.web.rest.vm.TaskQueryVm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

/**
 * 方位角评估服务实现
 *
 * @author li.tf
 * @date 2018-01-12 14:31:30
 */
@Slf4j
@Service
public class AzimuthEvalServiceImpl implements AzimuthEvalService {

    private final DataMapper dataMapper;
    private final AzimuthEvalResultRepository azimuthEvalResultRepository;
    private final AzimuthEvalTaskRepository azimuthEvalTaskRepository;
    private final JobRepository jobRepository;
    private final ReportRepository reportRepository;

    @Value("${rno.job-type-code.azimuth-eval:RNO_LTE_AZIMUTH_EVALUATION}")
    private String jobTypeCodeAzimuthEval;

    @Value("${rno.batch:10000}")
    private int batch;

    public AzimuthEvalServiceImpl(DataMapper dataMapper,
                                  AzimuthEvalResultRepository azimuthEvalResultRepository,
                                  AzimuthEvalTaskRepository azimuthEvalTaskRepository,
                                  JobRepository jobRepository,
                                  ReportRepository reportRepository) {
        this.dataMapper = dataMapper;
        this.azimuthEvalResultRepository = azimuthEvalResultRepository;
        this.azimuthEvalTaskRepository = azimuthEvalTaskRepository;
        this.jobRepository = jobRepository;
        this.reportRepository = reportRepository;
    }

    @Override
    public Map<String, Object> getNewestTime() {
        return dataMapper.getNewestYearAndMonth();
    }

    @Override
    public List<AzimuthEvalResult> calcAzimuth(Map<String, Object> map) {
        return dataMapper.calcAzimuth(map);
    }

    @Override
    public List<Map<String, Object>> findAzimuthEvalResultsByJobId(long jobId) {
        return dataMapper.findAzimuthEvalResultsByJobId(jobId);
    }

    @Override
    public List<Map<String, Object>> findTop1000AzimuthEvalResultsByJobId(long jobId) {
        return dataMapper.findTop1000AzimuthEvalResultsByJobId(jobId);
    }

    @Override
    public List<AzimuthEvalResult> saveAzimuthEvalResults(List<AzimuthEvalResult> azimuthEvalResults) {
        return azimuthEvalResultRepository.save(azimuthEvalResults);
    }

    @Override
    public List<AzimuthEvalTask> findAllTask(TaskQueryVm cond) {
        return azimuthEvalTaskRepository.findAll(Specifications.where(getWhereClause(cond)));
    }

    private Specification<AzimuthEvalTask> getWhereClause(TaskQueryVm cond) {
        return (root, query, builder) -> {
            Predicate predicate = builder.conjunction();
            if (cond.getCityId() > 0) {
                predicate.getExpressions().add(
                        builder.equal(root.get("areaId"), cond.getCityId())
                );
            }

            // 任务记录表
            Join<AzimuthEvalTask, Job> join = root.join("job", JoinType.INNER);
            if (StringUtils.isNotBlank(cond.getStartSubmitTime())) {
                predicate.getExpressions().add(
                        builder.greaterThanOrEqualTo(join.get("createTime"), Timestamp.valueOf(cond.getStartSubmitTime()))
                );
            }
            if (StringUtils.isNotBlank(cond.getEndSubmitTime())) {
                predicate.getExpressions().add(
                        builder.lessThanOrEqualTo(join.get("createTime"), Timestamp.valueOf(cond.getEndSubmitTime()))
                );
            }
            predicate.getExpressions().add(
                    builder.equal(join.get("creator"), cond.getAccount())
            );
            if (StringUtils.isNotBlank(cond.getTaskName())) {
                predicate.getExpressions().add(
                        builder.like(join.get("jobName"), "%" + cond.getTaskName() + "%")
                );
            }
            if (StringUtils.isNotBlank(cond.getTaskStatus()) && !StringUtils.equalsIgnoreCase(cond.getTaskStatus(), "ALL")) {
                if (StringUtils.equalsIgnoreCase(cond.getTaskStatus(), "LaunchedOrRunning")) {
                    predicate.getExpressions().add(
                            builder.or(builder.equal(join.get("jobRunningStatus"), "Launched"), builder.equal(join.get("jobRunningStatus"), "Running"))
                    );
                } else {
                    predicate.getExpressions().add(
                            builder.equal(join.get("jobRunningStatus"), cond.getTaskStatus())
                    );
                }
            }
            query.orderBy(builder.desc(root.get("createTime").as(Timestamp.class)));
            return predicate;
        };
    }

    @Override
    public AzimuthEvalTask getOneAzimuthEvalTask(long jobId) {
        return azimuthEvalTaskRepository.findOneByJobId(jobId);
    }


    @Override
    @Transactional
    public SubmitResult submitTask(AzimuthEvaluationSubmitTaskVm cond) {
        String msg;
        SubmitResult submitResult = new SubmitResult();
        submitResult.setFlag(false);

        try {
            // 创建job
            Job job = new Job();
            job.setCreator(cond.getAccount());
            job.setJobName(cond.getTaskName());
            job.setJobType(jobTypeCodeAzimuthEval);

            job = jobRepository.save(job);
            if (job == null || job.getJobId() <= 0) {
                msg = "创建jobId失败！";
                log.error(msg);
            } else {
                long jobId = job.getJobId();
                AzimuthEvalTask azimuthEvalTask = new AzimuthEvalTask();
                azimuthEvalTask.setJobId(jobId);
                azimuthEvalTask.setAreaId(cond.getCityId());
                azimuthEvalTask.setDlFileName(jobId + "_LTE天线方位角评估结果.csv");
                azimuthEvalTask.setEvalType(cond.getMode());
                azimuthEvalTask.setCells(cond.getEvaluationCells());
                azimuthEvalTask.setCreateTime(new Timestamp(System.currentTimeMillis()));
                azimuthEvalTask = azimuthEvalTaskRepository.save(azimuthEvalTask);
                if (null != azimuthEvalTask) {
                    msg = "任务提交成功，请等待分析完成！";
                    log.info(msg);
                    submitResult.setFlag(true);
                    submitResult.setJobId(jobId);
                } else {
                    msg = "创建任务失败！";
                    log.error(msg);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            msg = "创建天线方位角评估任务失败！";
            log.error(msg);
        }
        submitResult.setResult(msg);
        return submitResult;
    }

    @Override
    public boolean updateTaskStatus(long jobId, String jobStatus) {
        AzimuthEvalTask task = azimuthEvalTaskRepository.getOne(jobId);
        task.setFinishState(jobStatus);
        return null != azimuthEvalTaskRepository.save(task);
    }

    @Override
    public boolean updateTaskMeaTime(long jobId, String date) {
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
        AzimuthEvalTask task = azimuthEvalTaskRepository.getOne(jobId);
        try {
            task.setBegMeaTime(simpleDateFormat1.parse(date.substring(0, date.lastIndexOf("-")) + "-01"));
            task.setEndMeaTime(simpleDateFormat1.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null != azimuthEvalTaskRepository.save(task);
    }

    @Override
    public void addOneReport(long jobId, Timestamp date1, String msg, String status, String stage) {
        Report report = new Report();
        report.setJobId(jobId);
        report.setBegTime(date1);
        report.setEndTime(new Timestamp(System.currentTimeMillis()));
        report.setState(status);
        report.setStage(stage);
        report.setAttMsg(msg);
        reportRepository.save(report);
    }

    @Override
    public void startJob(Job job) {
        // 更行任务运行状态
        job.setJobRunningStatus(JobStatus.Running.toString());
        // 更新任务开始时间
        job.setLaunchTime(new Timestamp(System.currentTimeMillis()));
        jobRepository.save(job);
    }

    @Override
    public void endJob(long jobId, Timestamp startTime, String jobStatus) {
        Job job = jobRepository.getOneByJobId(jobId);
        if (null != job && job.getJobId() > 0) {
            job.setJobRunningStatus(jobStatus);
            job.setLaunchTime(startTime);
            job.setCompleteTime(new Timestamp(System.currentTimeMillis()));
            jobRepository.save(job);
        }
    }

    @Override
    public boolean isJobStopped(long jobId) {
        Job job = jobRepository.getOneByJobId(jobId);
        return !(job == null || job.getJobId() <= 0) && JobStatus.Stopped.equals(JobStatus.valueOf(job.getJobRunningStatus()));
    }

    @Override
    public Job getOneJob(Job job) {
        return jobRepository.findByJobRunningStatusAndJobTypeOrderByCreateTimeDesc(job.getJobRunningStatus(), job.getJobType());
    }

    @Override
    public Job getJobByJobId(long jobId) {
        return jobRepository.getOneByJobId(jobId);
    }

    @Override
    public void updateOwnProgress(long jobId, String jobStatus) {
        AzimuthEvalTask task = azimuthEvalTaskRepository.findOneByJobId(jobId);
        log.debug("task={}", task);
        task.setFinishState(jobStatus);
        azimuthEvalTaskRepository.save(task);
    }

    @Override
    public void updateMeatime(long jobId, String date) {
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
        AzimuthEvalTask task = azimuthEvalTaskRepository.findOneByJobId(jobId);
        try {
            task.setBegMeaTime(simpleDateFormat1.parse(date.substring(0, date.lastIndexOf("-")) + "-01"));
            task.setEndMeaTime(simpleDateFormat1.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        azimuthEvalTaskRepository.save(task);
    }

    @Override
    public AzimuthEvalTask queryTaskRecordByJobId(long jobId) {
        return azimuthEvalTaskRepository.findOneByJobId(jobId);
    }

    @Override
    public Long batchInsertResult(List<AzimuthEvalResult> results) {
        final AtomicLong cnt = new AtomicLong();

        final Map<Integer, List<AzimuthEvalResult>> group = new HashMap<>();
        IntStream.range(0, results.size()).forEach(e -> {
            int gid = e / batch;
            group.computeIfAbsent(gid, k -> new ArrayList<>()).add(results.get(e));
        });

        // 由于是绑定参数的提交方式，第一次耗时比较多，之后可以重用，速度较快。
        group.values().forEach(e -> cnt.addAndGet(azimuthEvalResultRepository.save(e).size()));

        return cnt.get();
    }

    @Override
    public List queryResultByJobId(long jobId) {
        return dataMapper.findAzimuthEvalResultsByJobId(jobId);
    }

    @Override
    public List queryEnhanceByJobId(long jobId) {
        List<Map<String, Object>> res = dataMapper.findAzimuthEvalResultsByJobId(jobId);
        res.forEach(map -> map.put("result", new Random().nextInt(360) - 180));
        return res;
    }

}
