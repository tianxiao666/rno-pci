package com.hgicreate.rno.lte.common.service.azimutheval;

import com.hgicreate.rno.lte.common.model.Job;
import com.hgicreate.rno.lte.common.model.SubmitResult;
import com.hgicreate.rno.lte.common.model.TaskQueryCond;
import com.hgicreate.rno.lte.common.model.azimutheval.AzimuthEvalTask;
import com.hgicreate.rno.lte.common.model.azimutheval.AzimuthEvaluationSubmitTaskVM;
import com.hgicreate.rno.lte.common.repo.JobRepository;
import com.hgicreate.rno.lte.common.repo.azimutheval.AzimuthEvalTaskRepository;
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
import java.util.List;

/**
 * @author chen.c10
 */
@Slf4j
@Service
public class AzimuthEvalTaskService {

    private final JobRepository jobRepository;
    private final AzimuthEvalTaskRepository azimuthEvalTaskRepository;

    @Value("${rno.job-type-code.azimuth-eval:RNO_LTE_AZIMUTH_EVALUATION}")
    private String jobTypeCodeAzimuthEval;

    public AzimuthEvalTaskService(JobRepository jobRepository,
                                  AzimuthEvalTaskRepository azimuthEvalTaskRepository) {
        this.jobRepository = jobRepository;
        this.azimuthEvalTaskRepository = azimuthEvalTaskRepository;
    }

    public List<AzimuthEvalTask> findAll(TaskQueryCond cond) {
        return azimuthEvalTaskRepository.findAll(Specifications.where(getWhereClause(cond)));
    }

    private Specification<AzimuthEvalTask> getWhereClause(TaskQueryCond cond) {
        String allStatus = "all";
        String runningStatus = "LaunchedOrRunning";
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
                        builder.greaterThanOrEqualTo(join.get("createTime"),
                                Timestamp.valueOf(cond.getStartSubmitTime()))
                );
            }
            if (StringUtils.isNotBlank(cond.getEndSubmitTime())) {
                predicate.getExpressions().add(
                        builder.lessThanOrEqualTo(join.get("createTime"),
                                Timestamp.valueOf(cond.getEndSubmitTime()))
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
            if (StringUtils.isNotBlank(cond.getTaskStatus())
                    && !StringUtils.equalsIgnoreCase(cond.getTaskStatus(), allStatus)) {
                if (StringUtils.equalsIgnoreCase(cond.getTaskStatus(), runningStatus)) {
                    predicate.getExpressions().add(
                            builder.or(builder.equal(join.get("jobRunningStatus"), "Launched"),
                                    builder.equal(join.get("jobRunningStatus"), "Running"))
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

    public AzimuthEvalTask getOneAzimuthEvalTask(long jobId) {
        return azimuthEvalTaskRepository.findOneByJobId(jobId);
    }

    @Transactional
    public SubmitResult submitTask(AzimuthEvaluationSubmitTaskVM cond) {
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

    public boolean updateTaskStatus(long jobId, String jobStatus) {
        AzimuthEvalTask task = azimuthEvalTaskRepository.getOne(jobId);
        task.setFinishState(jobStatus);
        return null != azimuthEvalTaskRepository.save(task);
    }

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

}
