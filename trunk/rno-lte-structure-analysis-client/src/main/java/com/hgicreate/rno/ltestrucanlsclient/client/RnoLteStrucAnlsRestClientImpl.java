package com.hgicreate.rno.ltestrucanlsclient.client;

import com.hgicreate.rno.ltestrucanlsclient.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Component
public class RnoLteStrucAnlsRestClientImpl implements RnoLteStrucAnlsRestClient {
    @Value("${rno.service-url}")
    private String serviceUrl;

    private RestTemplate restTemplate;

    public RnoLteStrucAnlsRestClientImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Long queryLteStrucAnlsTaskCnt(MultiValueMap<String, Object> param) {
        return restTemplate.postForObject(serviceUrl + "queryLteStrucAnlsTaskCnt", param, Long.class);
    }

    @Override
    public List<TaskQueryResult> queryLteStrucAnlsTaskByPage(MultiValueMap<String, Object> param) {
        return Arrays.asList(restTemplate.postForObject(serviceUrl + "queryLteStrucAnlsTaskByPage", param, TaskQueryResult[].class));
    }

    @Override
    public Boolean addLteStrucAnlsTask(SubmitTaskCond cond) {
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("jobId", cond.getJobId());
        param.add("begMeaTime", cond.getBegMeaTime());
        param.add("endMeaTime", cond.getEndMeaTime());
        param.add("cityId", cond.getCityId());
        param.add("dlFileName", cond.getDlFileName());
        return restTemplate.postForObject(serviceUrl + "addLteStrucAnlsTask", param, Boolean.class);
    }

    @Override
    public StrucTaskRecord queryLteStrucAnlsTaskRecByJobId(long jobId) {
        return restTemplate.getForObject(serviceUrl + "/queryLteStrucAnlsTaskRec/{jobId}", StrucTaskRecord.class, jobId);
    }

    @Override
    public List<OverlapCover> queryOverlapCoverResultByJobId(long jobId) {
        return Arrays.asList(restTemplate.getForObject(serviceUrl + "/queryOverlapCoverResult/{jobId}", OverlapCover[].class, jobId));
    }

    @Override
    public List<OverCover> queryOverCoverResultByJobId(long jobId) {
        return Arrays.asList(restTemplate.getForObject(serviceUrl + "/queryOverCoverResult/{jobId}", OverCover[].class, jobId));
    }

    @Override
    public List<MetricsSummary> queryMetricsSummaryResultByJobId(long jobId) {
        return Arrays.asList(restTemplate.getForObject(serviceUrl + "/queryMetricsSummaryResult/{jobId}", MetricsSummary[].class, jobId));
    }
}
