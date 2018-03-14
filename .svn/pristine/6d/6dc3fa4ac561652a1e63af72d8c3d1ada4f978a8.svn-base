package com.hgicreate.rno.repo;

import com.hgicreate.rno.model.AzimuthEvalTask;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 方位角评估任务数据仓库
 *
 * @author li.tf
 * @date 2018-01-12 14:16:39
 */
public interface AzimuthEvalTaskRepository extends JpaRepository<AzimuthEvalTask, Long> {

    /**
     * 获取全部任务记录
     *
     * @param spec 查询条件
     * @return 任务记录集
     * @date 2018-01-12 14:16:38
     */
    List<AzimuthEvalTask> findAll(Specification<AzimuthEvalTask> spec);

    /**
     * 通过任务ID获取任务记录
     *
     * @param jobId 任务ID
     * @return 任务记录
     * @date 2018-01-12 14:17:39
     */
    AzimuthEvalTask findOneByJobId(long jobId);

}
