package com.hgicreate.rno.lteazimuthevaluation.service;

import com.hgicreate.rno.lteazimuthevaluation.dao.RnoCommonDao;
import com.hgicreate.rno.lteazimuthevaluation.model.Area;
import com.hgicreate.rno.lteazimuthevaluation.model.JobProfile;
import com.hgicreate.rno.lteazimuthevaluation.model.JobStatus;
import com.hgicreate.rno.lteazimuthevaluation.model.Report;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RnoCommonServiceImpl implements RnoCommonService {

    private static Logger logger = LoggerFactory.getLogger(RnoCommonServiceImpl.class);

    private RnoCommonDao rnoCommonDao;

    public RnoCommonServiceImpl(RnoCommonDao rnoCommonDao) {
        this.rnoCommonDao = rnoCommonDao;
    }

    @Override
    public Boolean verifyUserIdentity(String account) {
        return null != account && rnoCommonDao.queryAccountCnt(account) == 1;
    }

    @Override
    public Long queryJobReportCnt(long jobId) {
        return rnoCommonDao.queryJobReportCnt(jobId);
    }

    @Override
    public List<Report> queryJobReportByPage(Map<String, Object> map) {
        return rnoCommonDao.queryJobReportByPage(map);
    }

    @Override
    public Long stopJobByJobId(long jobId) {
        return rnoCommonDao.stopJobByJobId(jobId);
    }

    @Override
    public JobStatus checkJobStatus(long jobId) {
        return rnoCommonDao.checkJobStatus(jobId);
    }

    @Override
    public Long addOneReport(Report report) {
        return rnoCommonDao.addOneReport(report);
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
    public Map<String, List<Area>> getAreaByAccountAndCityId(String account, long cityId) {
        Map<String, List<Area>> map = new HashMap<>();
        // 获取省列表
        List<Area> provinceAreas = getSpecialAreaByAccount(account, "省");
        if (provinceAreas != null && provinceAreas.size() > 0) {

            // 获取用户全部可访问城市列表
            List<Area> cityAreas = getSpecialAreaByAccount(account, "市");
            Area city = null;
            if (cityId > 0) {
                for (Area a : cityAreas) {
                    if (a.getAreaId() == cityId) {
                        city = a;
                    }
                }
            }

            if (city == null) {
                // 通过获取默认城市，然后置顶
                city = rnoCommonDao.getUserAreaByAccount(account);
            }

            long cfgCityId = 0;
            long cfgProvinceId;
            if (city != null && cityAreas.contains(city)) {
                // 获取到默认配置则将默认配置调到最前
                cfgCityId = city.getAreaId();
                cfgProvinceId = city.getParentId();

                move2Top(provinceAreas, cfgProvinceId);
            } else {
                // 如果该帐户没有设定过默认区域，哪就默认第一个省份为默认区域
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
        logger.debug("getSubAreaByParent：account={},parentId={},areaLevel={}", account, parentId, areaLevel);
        // 所有指定级别的列表
        return rnoCommonDao.getSpecialLevelAreaByAccount(account, parentId, areaLevel);
    }

    @Override
    public List<Area> getSpecialAreaByAccount(String account, String areaLevel) {
        logger.debug("进入getSpecialArea：account={}, areaLevel={}", account, areaLevel);
        // 所有指定级别的列表
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
}
