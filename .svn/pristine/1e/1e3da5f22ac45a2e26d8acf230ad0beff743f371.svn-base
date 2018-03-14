# rno-network-coverage
这是LTE网络覆盖分析微服务项目。

### 需自行安装的类库
由于二进制许可（binary license）的限制，Oracle JDBC 驱动不能通过 Maven 公共仓库获取，需安装到本地仓库以便提供给项目使用。

1.将项目service文件夹下的mapreduce文件夹里的全部打成jar包，并且命名为pci-azimuth-calc-mapreduce-1.0.jar,并放在本地maven仓库里，路径为:.m2\repository\com\iscreate\rno\pci-azimuth-calc-mapreduce\1.0\pci-azimuth-calc-mapreduce-1.0.jar

2.新建微服务表
create table RNO_MS_4G_AZIMUTH_JOB
(
  job_id        NUMBER,
  beg_mea_time  DATE,
  end_mea_time  DATE,
  city_id       NUMBER,
  dl_file_name  VARCHAR2(1024),
  rd_file_name  VARCHAR2(1024),
  result_dir    VARCHAR2(1024),
  finish_state  VARCHAR2(64),
  status        VARCHAR2(2),
  create_time   DATE,
  mod_time      DATE,
  mr_job_id     VARCHAR2(256),
  rela_num_type VARCHAR2(20)
)

3.部署 mapreduce 包到项目 Maven 仓库：
mvn deploy:deploy-file -DgroupId=com.iscreate.rno -DartifactId=pci-azimuth-calc-mapreduce -Dversion=1.0 -Dpackaging=jar -Dfile=D:\pci-azimuth-calc-mapreduce-1.0.jar -Durl=http://192.168.6.70:8081/nexus/content/repositories/releases/ -DrepositoryId=releases
