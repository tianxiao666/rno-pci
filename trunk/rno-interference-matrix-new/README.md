# rnointerfermatrix
这是干扰矩阵微服务项目。

### 需自行安装的类库
由于二进制许可（binary license）的限制，Oracle JDBC 驱动不能通过 Maven 公共仓库获取，需安装到本地仓库以便提供给项目使用。

1.下载 Oracle JDBC 驱动：http://192.168.6.70/soft/maven/ojdbc7.jar


2.在终端窗口进入 ojdbc7.jar 所在目录，执行：

mvn install:install-file -DgroupId=com.oracle -DartifactId=ojdbc7 -Dversion=12.1.0.1 -Dpackaging=jar -Dfile=ojdbc7.jar -DgeneratePom=true

3.将项目com.iscreate.rno.mapreduce包中的所有内容打成jar包，并且命名为interference-matrix-mapreduce-1.0.jar,并放在本地maven仓库里，路径为:.m2\repository\com\iscreate\rno\interference-matrix-mapreduce\1.0\interference-matrix-mapreduce-1.0.jar

4.把 mapreduce 包部署到项目仓库：
mvn deploy:deploy-file -DgroupId=com.iscreate.rno -DartifactId=interference-matrix-mapreduce -Dversion=1.0 -Dpackaging=jar -Dfile=D:\interference-matrix-mapreduce-1.0.jar -Durl=http://192.168.6.70:8081/nexus/content/repositories/releases/ -DrepositoryId=releases

### 组内maven仓库
**Oracle JDBC** 和 **mapreduce** 类库都已在**组内maven仓库**部署，如使用组内maven仓库，不再需要自行安装类库
