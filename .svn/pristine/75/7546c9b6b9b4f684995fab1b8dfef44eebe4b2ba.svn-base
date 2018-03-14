# RNO3 4G-LTE 网络优化系统

需求原型：http://sdcpm.hgicreate.com/demo/rno-pci

代码管理：http://svn.hgicreate.com/srcsvn/rno/rno-lte

## Modules
下面是 PCI 优化项目中各服务的端口分配。

|服务名|简介|端口|
|-----|----|----|
|rno-pci-platform|PCI优化平台前端页面|9000|
|rno-registry|集中配置与注册发现服务|8761|
|rno-zuul-proxy|Zuul代理服务|8080|
|rno-lte-web-client|优化计算类WEB前端|8081|
|rno-lte-gis-client|GIS应用类WEB前端|8082|
|rno-lte-common-service|公共服务|8090|
|rno-lte-interference-matrix-service|LTE干扰矩阵计算服务|8091|
|rno-lte-pci-afp-service|区域PCI翻频方案服务|8092|
|rno-lte-structure-analysis-service|LTE结构优化服务|8093|
|rno-lte-azimuth-evaluation-service|LTE天线方位角评估服务|8094|
|rno-lte-gis-service|LTE GIS应用后端服务|8095|

## 调用关系
1. *rno-registry* 为除 *rno-pci-platform* 以外的所有服务提供集中配置与注册发现服务。  
2. *rno-zuul-proxy* 提供网关代理，所有对微服务的访问通过它进行。  
3. *rno-pci-platform* 通过war包独立部署，不使用集中配置与注册发现服务，为用户提供前端页面，通过 *rno-zuul-proxy* 访问 *rno-lte-web-client* 、 *rno-lte-gis-client* 获取需要的页面或功能。  
4. *rno-lte-web-client* 、 *rno-lte-gis-client* 提供页面服务，需要 *rno-lte-common-service* 提供基础数据访问，对应功能需要相应的微服务支持。  
5. *rno-lte-common-service* 集中所有关系型数据库的访问，各微服务通过它获取公共服务。  
6. *其他微服务* 提供各自功能的计算服务，对数据仓库的访问由各微服务自行处理。  

## 最小启动
假如要获取pci区域翻频功能，最少要启动6个服务：  
1. 首先启动 *rno-registry* 和 *rno-pci-platform*
2. 再启动 *rno-zuul-proxy* 和 *rno-lte-common-service*
3. 接着启动 *rno-lte-web-client* 和 *rno-lte-pci-afp-service*

## 部署为 Linux 服务
分为两部分：
1. 基础服务，共3个：
<pre>
rno-registry
rno-zuul-proxy
rno-pci-platform
</pre>
安装命令：
<pre>
cd /opt/rno-lte
mvn -DskipTests clean install
</pre>
部署命令：
<pre>./deploy-rno.sh</pre>
运行命令（执行不带参数的命令会打印帮助信息）：
<pre>./run-rno.sh {start|restart|stop|reload|status} {all|1-3}</pre>
如果是在 Linux 中初次使用部署与运行命令，需先执行：
<pre>
dos2unix deploy-rno.sh run-rno.sh
chmod +x deploy-rno.sh run-rno.sh
</pre>

2. 微服务，共8个：
<pre>
rno-lte-common-service
rno-lte-azimuth-evaluation-service
rno-lte-interference-matrix-service
rno-lte-pci-afp-service
rno-lte-structure-analysis-service
rno-lte-gis.service
rno-lte-web-client
rno-lte-gis-client
</pre>
安装命令：
<pre>
cd /opt/rno-lte
mvn -DskipTests clean install
</pre>
部署命令：
<pre>./deploy-service.sh</pre>
运行命令（执行不带参数的命令会打印帮助信息）：
<pre>./run-service.sh {start|restart|stop|reload|status} {all|1-9}</pre>
如果是在 Linux 中初次使用部署与运行命令，需先执行：
<pre>
dos2unix deploy-service.sh run-service.sh
chmod +x deploy-service.sh run-service.sh
</pre>

3. 由于在 Linux 服务模式下，使用的 JAVA 为 /usr/bin/java，并且会忽略环境变量中的 JAVA_HOME 和 PATH，而 CentOS 6.x 缺省是 Java 1.7.x，因此，需要运行以下命令更改 /usr/bin/java 的版本：
<pre>
alternatives --install /usr/bin/java java /opt/java/jdk1.8.0_121/bin/java 1
alternatives --config java
</pre>
