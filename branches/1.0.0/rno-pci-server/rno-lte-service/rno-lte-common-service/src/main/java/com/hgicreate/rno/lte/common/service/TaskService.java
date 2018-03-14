package com.hgicreate.rno.lte.common.service;

import com.hgicreate.rno.lte.common.model.AbstractTask;
import com.hgicreate.rno.lte.common.model.Job;
import com.hgicreate.rno.lte.common.model.SubmitResult;
import com.hgicreate.rno.lte.common.model.TaskQueryCond;
import com.hgicreate.rno.lte.common.model.azimutheval.AzimuthEvalTask;
import com.hgicreate.rno.lte.common.model.azimutheval.AzimuthJobRecord;
import com.hgicreate.rno.lte.common.repo.JobRepository;
import com.hgicreate.rno.lte.common.repo.azimutheval.AzimuthEvalTaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author chen.c10
 */
@Slf4j
@Service
public class TaskService {

    private final JobRepository jobRepository;
    private final AzimuthEvalTaskRepository azimuthEvalTaskRepository;

    @Value("${rno.job-type-code.azimuth-eval:RNO_LTE_AZIMUTH_EVALUATION}")
    private String jobTypeCodeAzimuthEval;

    public TaskService(JobRepository jobRepository, AzimuthEvalTaskRepository azimuthEvalTaskRepository) {
        this.jobRepository = jobRepository;
        this.azimuthEvalTaskRepository = azimuthEvalTaskRepository;
    }

    public Page findAll(TaskQueryCond cond, Pageable pageable) {
        if (StringUtils.equalsIgnoreCase(jobTypeCodeAzimuthEval, cond.getJobType())) {
            return null;//azimuthEvalTaskRepository.findAll(Specifications.where(getWhereClause(cond)), pageable);
        }
        return null;
    }

    private Specification<AzimuthEvalTask> getWhereClause(TaskQueryCond cond) {
        return (root, query, builder) -> {
            Predicate predicate = builder.conjunction();
            if (cond.getCityId() > 0) {
                predicate.getExpressions().add(
                        builder.equal(root.get("areaId"), cond.getCityId())
                );
            }
            /*if (StringUtils.isNotBlank(cond.getMeaTime())) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date date = simpleDateFormat.parse(cond.getMeaTime());
                    predicate.getExpressions().add(
                            builder.lessThanOrEqualTo(root.get("begMeaTime"), date)
                    );
                    predicate.getExpressions().add(
                            builder.greaterThanOrEqualTo(root.get("endMeaTime"), date)
                    );
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
*/
            // 任务记录表
            Join<AzimuthEvalTask, Job> join = root.join("job", JoinType.INNER);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (StringUtils.isNotBlank(cond.getStartSubmitTime())) {
                try {
                    Date date = simpleDateFormat.parse(cond.getStartSubmitTime());
                    predicate.getExpressions().add(
                            builder.greaterThanOrEqualTo(join.get("createTime"), date)
                    );
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if (StringUtils.isNotBlank(cond.getEndSubmitTime())) {
                try {
                    Date date = simpleDateFormat.parse(cond.getEndSubmitTime());
                    predicate.getExpressions().add(
                            builder.lessThanOrEqualTo(join.get("createTime"), date)
                    );
                } catch (ParseException e) {
                    e.printStackTrace();
                }
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
            return predicate;
        };
    }

    public AbstractTask findOne(long jobId, String jobType) {
        if (StringUtils.equalsIgnoreCase(jobTypeCodeAzimuthEval, jobType)) {
            return azimuthEvalTaskRepository.getOne(jobId);
        }
        return null;
    }

    public AbstractTask saveTask(String jobType, AbstractTask task) {
        if (StringUtils.equalsIgnoreCase(jobTypeCodeAzimuthEval, jobType)) {
            return azimuthEvalTaskRepository.save((AzimuthEvalTask) task);
        }
        return null;
    }

    @Transactional
    public SubmitResult submitTask(AzimuthJobRecord cond) {
        String msg;
        SubmitResult submitResult = new SubmitResult();
        submitResult.setFlag(false);

        try {
            // 创建job
            Job job = new Job();
            job.setCreator(cond.getAccount());
            job.setJobName(cond.getTaskName());
            job.setJobType(jobTypeCodeAzimuthEval);
            job.setDescription(cond.getTaskDesc());

            job = jobRepository.save(job);
            if (job == null || job.getJobId() <= 0) {
                msg = "创建jobId失败！";
                log.error(msg);
            } else {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                long jobId = job.getJobId();
                AzimuthEvalTask azimuthEvalTask = new AzimuthEvalTask();
                azimuthEvalTask.setJobId(jobId);
                azimuthEvalTask.setBegMeaTime(simpleDateFormat.parse(cond.getBegMeaTime()));
                azimuthEvalTask.setEndMeaTime(simpleDateFormat.parse(cond.getEndMeaTime()));
                azimuthEvalTask.setAreaId(cond.getCityId());
                azimuthEvalTask.setDlFileName(cond.getCityName() + "_" + jobId + "_LTE天线方位角评估结果.csv");
                azimuthEvalTask.setEvalType(cond.getEvalType());
                azimuthEvalTask = azimuthEvalTaskRepository.save(azimuthEvalTask);
                if (null != azimuthEvalTask) {
                    msg = "任务提交成功，请等待分析完成！";
                    log.info(msg);
                    submitResult.setFlag(true);
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
}
