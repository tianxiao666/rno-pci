package com.hgicreate.rno.lte.common.config;

import com.hgicreate.rno.lte.common.model.Area;
import com.hgicreate.rno.lte.common.model.Job;
import com.hgicreate.rno.lte.common.model.Report;
import com.hgicreate.rno.lte.common.model.cellmgr.Cell;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;

/**
 * @author chen.c10
 */
@Configuration
public class RepositoryConfig extends RepositoryRestConfigurerAdapter {
    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.exposeIdsFor(Cell.class, Report.class, Area.class, Job.class);
    }
}