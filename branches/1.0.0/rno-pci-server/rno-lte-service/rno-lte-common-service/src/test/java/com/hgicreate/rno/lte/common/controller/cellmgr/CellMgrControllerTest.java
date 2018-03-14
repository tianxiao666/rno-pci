package com.hgicreate.rno.lte.common.controller.cellmgr;

import com.hgicreate.rno.lte.common.model.cellmgr.Cell;
import com.hgicreate.rno.lte.common.model.cellmgr.CellMgrCond;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.PagedResources;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CellMgrControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void findByPage() throws Exception {
        CellMgrCond cond = new CellMgrCond();
        cond.setAreaIds(Collections.singletonList(440100L));
        cond.setCurrentPage(0);
        cond.setPageSize(25);
        PagedResources<Cell> body = restTemplate.postForObject("/cellMgr/findByPage", cond, PagedResources.class);
//        Page body = restTemplate.postForObject("/cellMgr/findByPage", cond, Page.class);
        System.out.println(body.getContent());
        assertThat(body.getContent().size()).isGreaterThanOrEqualTo(0);
//        String body = restTemplate.postForObject("/cellMgr/findByPage", cond, String.class);
//        System.out.println(body);
    }

    @Test
    public void findAll() throws Exception {
    }
}