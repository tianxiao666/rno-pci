# rno-lte-structure-analysis-service
这是LTE结构优化分析微服务项目的服务端，提供数据库访问，结果计算等处理。

### 需自行安装的类库
由于二进制许可（binary license）的限制，Oracle JDBC 驱动不能通过 Maven 公共仓库获取，需安装到本地仓库以便提供给项目使用。

#### 创建微服务表  
-- Create table  
create table RNO_MS_LTE_STRUCANA_JOB  
(  
  job_id       NUMBER,  
  beg_mea_time DATE,  
  end_mea_time DATE,  
  city_id      NUMBER,  
  dl_file_name VARCHAR2(1024),  
  result_dir   VARCHAR2(1024),  
  finish_state VARCHAR2(64),  
  status       VARCHAR2(2),  
  create_time  DATE,  
  mod_time     DATE  
);

-- Add comments to the columns 
comment on column RNO_MS_LTE_STRUCANA_JOB.dl_file_name is '供用户下载';  
comment on column RNO_MS_LTE_STRUCANA_JOB.result_dir is '是一个目录';  
comment on column RNO_MS_LTE_STRUCANA_JOB.finish_state is '数据整理保存结果文件成功完成、失败';  

-- Create/Recreate indexes  
create index IDX_MS_LTE_STRUCTANA_CITY on RNO_MS_LTE_STRUCANA_JOB (CITY_ID);  
create index IDX_MS_LTE_STRUCTANA_JOBID on RNO_MS_LTE_STRUCANA_JOB (JOB_ID);  

#### 创建结果表
##### 重叠覆盖结果表
-- Create sequence  
create sequence SEQ_RNO_MS_LTE_STRUC_OLC_RES;  

-- Create table  
create table RNO_MS_LTE_STRUC_OLC_RES  
(  
     RECORD_ID            NUMBER               not null,  
     JOB_ID               NUMBER,  
     CELL_ID              VARCHAR(16),  
  total_cnt	NUMBER,  
  sc_gt105_gt0_cnt	NUMBER,  
  sc_gt110_gt0_cnt	NUMBER,  
  sc_gt105_gt0_per	NUMBER,  
  sc_gt110_gt0_per	NUMBER,  
  sc_gt110_nc_sc_gt6_gt3_cnt	NUMBER,  
  sc_gt110_nc_sc_gt6_gt4_cnt	NUMBER,  
  sc_gt110_nc_sc_gt6_gt5_cnt	NUMBER,  
  sc_gt110_nc_sc_gt6_gt6_cnt	NUMBER,  
  sc_gt110_nc_sc_gt6_gt3_per	NUMBER,  
  sc_gt110_nc_sc_gt6_gt4_per	NUMBER,  
  sc_gt110_nc_sc_gt6_gt5_per	NUMBER,  
  sc_gt110_nc_sc_gt6_gt6_per	NUMBER,  
  sc_gt110_nc_sc_gt10_gt3_cnt	NUMBER,  
  sc_gt110_nc_sc_gt10_gt4_cnt	NUMBER,  
  sc_gt110_nc_sc_gt10_gt5_cnt	NUMBER,  
  sc_gt110_nc_sc_gt10_gt6_cnt	NUMBER,  
  sc_gt110_nc_sc_gt10_gt3_per	NUMBER,  
  sc_gt110_nc_sc_gt10_gt4_per	NUMBER,  
  sc_gt110_nc_sc_gt10_gt5_per	NUMBER,  
  sc_gt110_nc_sc_gt10_gt6_per	NUMBER,  
  sc_gt110_eq1_cnt	NUMBER,  
  sc_gt110_eq2_cnt	NUMBER,  
  sc_gt110_eq3_cnt	NUMBER,  
  sc_gt110_eq4_cnt	NUMBER,  
  sc_gt110_eq5_cnt	NUMBER,  
  sc_gt110_eq6_cnt	NUMBER,  
  sc_gt110_eq7_cnt	NUMBER,  
  sc_gt110_eq8_cnt	NUMBER,  
  sc_gt110_eq9_cnt	NUMBER,  
  constraint PK_RNO_MS_LTE_STRUC_OLC_RES primary key (RECORD_ID)  
);
  
-- Add comments to the columns  
comment on column RNO_MS_LTE_STRUC_OLC_RES.JOB_ID is '任务ID';  
  
-- Create/Recreate indexes  
create index IDX_RNO_MS_LTE_STRUC_OLC_R_JID on RNO_MS_LTE_STRUC_OLC_RES (JOB_ID);  

##### 过覆盖结果表
-- Create sequence  
create sequence SEQ_RNO_MS_LTE_STRUC_OC_RES;  

-- Create table  
	create table RNO_MS_LTE_STRUC_OC_RES  
(  
     RECORD_ID            NUMBER               not null,  
     JOB_ID               NUMBER,  
     CELL_ID              VARCHAR(16),  
     CELL_NAME            VARCHAR(256),  
     CELL_PCI             NUMBER,  
     CELL_EARFCN          NUMBER,  
     CELL_LON             NUMBER,  
     CELL_LAT             NUMBER,  
     STATION_SPACE        NUMBER,  
     NCELL_ID             VARCHAR(16),  
     NCELL_NAME           VARCHAR(256),  
     NCELL_PCI            NUMBER,  
     NCELL_EARFCN         NUMBER,  
     NCELL_LON            NUMBER,  
     NCELL_LAT            NUMBER,  
     NCELL_CNT            NUMBER,  
     TOTAL_CNT            NUMBER,  
     NCELL_PER            NUMBER,  
     DIS                  NUMBER,  
  constraint PK_RNO_MS_LTE_STRUC_OC_RES primary key (RECORD_ID)  
);
  
-- Add comments to the columns  
comment on column RNO_MS_LTE_STRUC_OC_RES.JOB_ID is '任务ID';  
  
-- Create/Recreate indexes  
create index IDX_RNO_MS_LTE_STRUC_OC_R_JID on RNO_MS_LTE_STRUC_OC_RES (JOB_ID);  

##### 指标汇总结果表
-- Create sequence  
create sequence SEQ_RNO_MS_LTE_STRUC_MS_RES;  

-- Create table  
create table RNO_MS_LTE_STRUC_MS_RES  
(  
     RECORD_ID	NUMBER	not null,  
     JOB_ID	NUMBER,  
     CELL_ID	VARCHAR(16),  
     CELL_NAME	VARCHAR(256),  
     WEAK_FLAG	VARCHAR(16),  
     OVERLAP_FLAG	VARCHAR(16),  
     OVER_CNT	NUMBER,  
     OVER_FLAG	VARCHAR(16),  
     OVER16_CNT	NUMBER,  
     OVER16_FLAG	VARCHAR(16),  
  constraint PK_RNO_MS_LTE_STRUC_MS_RES primary key (RECORD_ID)  
);

-- Add comments to the columns  
comment on column RNO_MS_LTE_STRUC_MS_RES.JOB_ID is '任务ID';  

-- Create/Recreate indexes  
create index IDX_RNO_MS_LTE_STRUC_MS_R_JID on RNO_MS_LTE_STRUC_MS_RES (JOB_ID);  
