# rno-lte-azimuth-evaluation
这是LTE天线方位角评估微服务项目。

### 需自行安装的类库
由于二进制许可（binary license）的限制，Oracle JDBC 驱动不能通过 Maven 公共仓库获取，需安装到本地仓库以便提供给项目使用。  

####1.在oracle中创建结果表
-- Create sequence  
create sequence SEQ_RNO_MS_LTE_AZIMUTH_E_RES;  

-- Create table  
create table RNO_MS_LTE_AZIMUTH_EVAL_RES  
(  
   RECORD_ID            NUMBER               not null,  
   JOB_ID               NUMBER,  
   CELL_ID              VARCHAR(16),  
   AZIMUTH              NUMBER,  
   AZIMUTH1             NUMBER,  
   AZIMUTH2             NUMBER,  
   DIFF1                NUMBER,  
   DIFF2                NUMBER,  
   constraint PK_RNO_MS_LTE_AZIMUTH_EVAL_RES primary key (RECORD_ID)  
);  

-- Add comments to the columns  
comment on column RNO_MS_LTE_AZIMUTH_EVAL_RES.JOB_ID is '任务ID';  
comment on column RNO_MS_LTE_AZIMUTH_EVAL_RES.DIFF1 is '算法一方位角与原方位角之差';  
comment on column RNO_MS_LTE_AZIMUTH_EVAL_RES.DIFF2 is '算法二方位角与原方位角之差';  

-- Create/Recreate indexes  
create index IDX_RNO_MS_LTE_AZIMUTH_E_R_JID on RNO_MS_LTE_AZIMUTH_EVAL_RES (JOB_ID);  