package com.hgicreate.rno.ltestrucanlsservice.service;

import com.hgicreate.rno.ltestrucanlsservice.dao.RnoCommonDao;
import com.hgicreate.rno.ltestrucanlsservice.model.Area;
import com.hgicreate.rno.ltestrucanlsservice.model.JobProfile;
import com.hgicreate.rno.ltestrucanlsservice.model.JobStatus;
import com.hgicreate.rno.ltestrucanlsservice.model.Report;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RnoCommonServiceImpl implements RnoCommonService {

    private RnoCommonDao rnoCommonDao;

    public RnoCommonServiceImpl(RnoCommonDao rnoCommonDao) {
        this.rnoCommonDao = rnoCommonDao;
    }

    @Override
    public Boolean verifyUserIdentity(String account) {
        return null != account && rnoCommonDao.queryAccountCnt(account) == 1;
    }

    @Override
    public Map<String, List<Area>> getAreaByAccount(String account, long currentCityId) {
        Map<String, List<Area>> map = new HashMap<>();
        // 获取省列表
        List<Area> provinceAreas = getSpecialAreaByAccount(account, "省");
        if (provinceAreas != null && provinceAreas.size() > 0) {

            // 获取用户全部可访问城市列表
            List<Area> cityAreas = getSpecialAreaByAccount(account, "市");
            Area city = null;
            if (currentCityId > 0) {
                for (Area a : cityAreas) {
                    if (a.getAreaId() == currentCityId) {
                        city = a;
                    }
                }
            }

            if (city == null) {
                // 通过获取默认城市，然后置顶
                city = rnoCommonDao.getUserAreaByAccount(account);
            }

            long cfgCityId = -1;
            long cfgProvinceId;
            if (city != null) {
                // 获取到默认配置则将默认配置调到最前
                long tmp = city.getAreaId();
                if (cityAreas.stream().anyMatch(e -> e.getAreaId() == tmp)) {
                    cfgCityId = tmp;
                    cfgProvinceId = city.getParentId();
                    move2Top(provinceAreas, cfgProvinceId);
                } else {
                    cfgProvinceId = provinceAreas.get(0).getAreaId();
                }
            } else {
                cfgProvinceId = provinceAreas.get(0).getAreaId();
            }

            cityAreas = cityAreas.stream().filter(e -> e.getParentId() == cfgProvinceId).collect(Collectors.toList());

            if (cityAreas != null) {
                move2Top(cityAreas, cfgCityId);

                map.put("provinceAreas", provinceAreas);
                map.put("cityAreas", cityAreas);
                return map;
            } else {
                map.put("provinceAreas", provinceAreas);
                map.put("cityAreas", new ArrayList<>());
                return map;
            }
        } else {
            map.put("provinceAreas", new ArrayList<>());
            map.put("cityAreas", new ArrayList<>());
            return map;
        }
    }

    @Override
    public List<Area> getSubAreaByParent(String account, long parentId, String areaLevel) {
        return rnoCommonDao.getSpecialLevelAreaByAccount(account, parentId, areaLevel);
    }

    @Override
    public List<Area> getSpecialAreaByAccount(String account, String areaLevel) {
        return rnoCommonDao.getSpecialLevelAreaByAccount(account, 0, areaLevel);
    }

    private void move2Top(List<Area> areas, long areaId) {
        if (areaId > 0 && areas.size() > 0) {
            Area tmp = areas.get(0);
            for (int j = 0; j < areas.size(); j++) {
                if (areas.get(j).getAreaId() == areaId) {
                    areas.set(0, areas.get(j));
                    areas.set(j, tmp);
                }
            }
        }
    }

    @Override
    public Long addOneReport(Report report) {
        return rnoCommonDao.addOneReport(report);
    }

    @Override
    public Long queryReportCnt(long jobId) {
        return rnoCommonDao.queryReportCnt(jobId);
    }

    @Override
    public List<Report> queryReportByPage(Map<String, Object> map) {
        return rnoCommonDao.queryReportByPage(map);
    }

    @Override
    public Long addOneJob(JobProfile job) {
        // 创建者、工作类型、工作名不能为空
        if (job.getAccount() != null && job.getJobType() != null && job.getJobName() != null) {
            job.setJobStateStr(JobStatus.Waiting.toString());
            rnoCommonDao.addOneJob(job);
        }
        return job.getJobId();
    }

    @Override
    public Long updateJobBegTime(JobProfile job) {
        return rnoCommonDao.updateJobBegTime(job);
    }

    @Override
    public Long updateJobEndTime(JobProfile job) {
        return rnoCommonDao.updateJobEndTime(job);
    }

    @Override
    public Long updateJobRunningStatus(JobProfile job) {
        return rnoCommonDao.updateJobRunningStatus(job);
    }

    @Override
    public Long getOneJob(JobProfile job) {
        return rnoCommonDao.getOneJob(job);
    }

    @Override
    public JobProfile getJobByJobId(long jobId) {
        return rnoCommonDao.getJobByJobId(jobId);
    }

    @Override
    public Long stopJobByJobId(long jobId) {
        return rnoCommonDao.stopJobByJobId(jobId);
    }

    @Override
    public JobStatus checkJobStatus(long jobId) {
        return rnoCommonDao.checkJobStatus(jobId);
    }
}
