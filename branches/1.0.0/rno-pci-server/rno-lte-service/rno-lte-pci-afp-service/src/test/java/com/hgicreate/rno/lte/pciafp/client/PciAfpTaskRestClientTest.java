package com.hgicreate.rno.lte.pciafp.client;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PciAfpTaskRestClientTest {

    @Autowired
    private PciAfpTaskRestClient pciAfpTaskRestClient;

    @Test
    public void queryTaskRecordByJobId() {
    }

    @Test
    @Transactional
    public void updateTaskStatus() {
        long jobId = 20101;
        String jobStatus = "Waiting";
        boolean bool = pciAfpTaskRestClient.updateTaskStatus(jobId, jobStatus);
        System.out.println(bool);
        assertTrue("没搞定。", bool);
    }

    @Test
    public void getThresholdsByModuleType() {
    }

    @Test
    public void queryParamInfo() {
    }

    @Test
    public void saveBestPlan() {
    }

    @Test
    public void queryBestPlanByJobId() {
    }
}