Install

```Shell
brew tap gbeine/homebrew-java
brew update
brew install hive
brew install mysql-connector-java
mysql
#brew install mysql # if mysql command fails
```

Configure MySQL

```MySQL
CREATE DATABASE metastore;
USE metastore;
CREATE USER 'hiveuser'@'localhost' IDENTIFIED BY 'password';
GRANT SELECT,INSERT,UPDATE,DELETE,ALTER,CREATE,REFERENCES,INDEX ON metastore.* TO 'hiveuser'@'localhost';
```

Configure Hive

```Shell
cd /usr/local/Cellar/hive/{HIVE_VERSION_NUMBER}/libexec/lib
ln -s /usr/local/Cellar/mysql-connector-java/5.1.42/libexec/mysql-connector-java-5.1.42-bin.jar
cd /usr/local/Cellar/hive/{HIVE_VERSION_NUMBER}/libexec/conf
cp hive-default.xml.template hive-site.xml
```

Modify `hive-site.xml`

```xml
<property>
  <name>javax.jdo.option.ConnectionURL</name>
  <value>jdbc:mysql://localhost/metastore?useSSL=false</value>
</property>
<property>
  <name>javax.jdo.option.ConnectionDriverName</name>
  <value>com.mysql.jdbc.Driver</value>
</property>
<property>
  <name>javax.jdo.option.ConnectionUserName</name>
  <value>hiveuser</value>
</property>
<property>
  <name>javax.jdo.option.ConnectionPassword</name>
  <value>password</value>
</property>
```

Create default tables

```Shell
schematool -dbType mysql -initSchema
```

Troubleshoot:

```Shell
hive -hiveconf hive.root.logger=INFO,console
```
