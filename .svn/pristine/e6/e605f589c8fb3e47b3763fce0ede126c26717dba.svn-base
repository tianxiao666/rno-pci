# rnopciafp
这是PCI翻频方案微服务项目。

### 需自行安装的类库
由于二进制许可（binary license）的限制，Oracle JDBC 驱动不能通过 Maven 公共仓库获取，需安装到本地仓库以便提供给项目使用。

1.下载 Oracle JDBC 驱动：http://192.168.6.70/soft/maven/ojdbc7.jar

2.在终端窗口进入 ojdbc7.jar 所在目录，执行：

mvn install:install-file -DgroupId=com.oracle -DartifactId=ojdbc7 -Dversion=12.1.0.1 -Dpackaging=jar -Dfile=ojdbc7.jar -DgeneratePom=true

3.mapreduce模块已分离，现在pci翻频与干扰矩阵计算采用同一个mapreduce模块 rno-intermatrix-mapreduce 计算数据，保证数据一致性

4.mybatis具有自动寻找javabean的功能，但建议使用全路径来查找类