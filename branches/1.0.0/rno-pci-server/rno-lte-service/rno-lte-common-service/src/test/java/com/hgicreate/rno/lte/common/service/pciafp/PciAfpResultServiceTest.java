package com.hgicreate.rno.lte.common.service.pciafp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hgicreate.rno.lte.common.service.pciafp.dto.PciAfpResultDTO;
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
public class PciAfpResultServiceTest {
    @Autowired
    private PciAfpResultService pciAfpResultService;

    private JacksonTester<PciAfpResultDTO> json;

    private JacksonTester<List<PciAfpResultDTO>> jsonList;

    @Before
    public void setup() {
        ObjectMapper objectMapper = new ObjectMapper();
        JacksonTester.initFields(this, objectMapper);
    }

    @Test
    public void getPciAfpResultsByJobId() {
        List<PciAfpResultDTO> pciAfpResultDTOS = pciAfpResultService.getPciAfpResultsByJobId(100);
        assertNotNull("空", pciAfpResultDTOS);
        pciAfpResultDTOS.forEach(e-> {
            try {
                System.out.println(json.write(e).getJson());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
        try {
            System.out.println(jsonList.write(pciAfpResultDTOS).getJson());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}