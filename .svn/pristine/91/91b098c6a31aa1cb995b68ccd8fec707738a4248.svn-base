package com.hgicreate.rno.lte.common.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hgicreate.rno.lte.common.service.dto.ReportDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ReportServiceTest {

    @Autowired
    private ReportService reportService;

    private JacksonTester<ReportDTO> json;

    private JacksonTester<List<ReportDTO>> jsonList;

    @Before
    public void setup() {
        ObjectMapper objectMapper = new ObjectMapper();
        JacksonTester.initFields(this, objectMapper);
    }

    @Test
    public void getReportsByJobId() {
    }

    @Test
    public void getReportsByJobId2() {
        long jobId = 33;
        List<ReportDTO> reportDTOList = reportService.getReportsByJobId2(jobId);
        assertNotNull("结果为空。", reportDTOList);
        reportDTOList.forEach(e -> {
            try {
                System.out.println(json.write(e).getJson());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        try {
            System.out.println(jsonList.write(reportDTOList).getJson());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void saveReport() {
    }
}