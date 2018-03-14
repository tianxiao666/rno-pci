package com.hgicreate.rno.ltestrucanlsclient.client;

import com.hgicreate.rno.ltestrucanlsclient.model.Area;
import com.hgicreate.rno.ltestrucanlsclient.model.JobProfile;
import com.hgicreate.rno.ltestrucanlsclient.model.Report;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by chen.c10 on 2016/12/28.
 * 通用rest客户端
 */
@Component
public class RnoCommonRestClientImpl implements RnoCommonRestClient {
    @Value("${rno.service-url}")
    private String serviceUrl;

    private RestTemplate restTemplate;

    public RnoCommonRestClientImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Boolean verifyUserIdentity(String account) {
        return null != account && restTemplate.getForObject(serviceUrl + "verifyUserIdentity/?account={account}", Boolean.class, account);
    }

    @Override
    public Map<String, List<Area>> getAreaByAccount(String account, long currentCityId) {
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("account", account);
        param.add("currentCityId", currentCityId);
        return restTemplate.postForObject(serviceUrl + "getAreaByAccount", param, Map.class);
    }

    @Override
    public List<Area> getSubAreaByParent(String account, long parentAreaId, String subAreaLevel) {
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("account", account);
        param.add("parentAreaId", parentAreaId);
        param.add("subAreaLevel", subAreaLevel);
        return Arrays.asList(restTemplate.postForObject(serviceUrl + "getSubAreaByParent", param, Area[].class));
    }

    @Override
    public Long queryReportCnt(long jobId) {
        return restTemplate.getForObject(serviceUrl + "queryReportCnt/{jobId}", Long.class, jobId);
    }

    @Override
    public List<Report> queryReportByPage(long jobId, int start, int end) {
        return Arrays.asList(restTemplate.getForObject(serviceUrl + "queryReportByPage/{jobId}?start={start}&end={end}", Report[].class, jobId, start, end));
    }

    @Override
    public Long addOneJob(JobProfile job) {
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("account", job.getAccount());
        param.add("jobName", job.getJobName());
        param.add("jobType", job.getJobType());
        return restTemplate.postForObject(serviceUrl + "addOneJob", param, Long.class);
    }

    @Override
    public void stopJobByJobId(Long jobId) {
        restTemplate.put(serviceUrl + "stopJob/{jobId}", null, jobId);
    }
}
