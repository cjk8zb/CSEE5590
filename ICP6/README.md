# In Class Exercise
## Sqoop

Sqoop is a transfer tool between Hadoop and SQL or Relational Databases

### Part 1
1.	**Create table in MySQL and Import into HDFS through Sqoop.**

	From the command line, start mysql and enter the mysql shell.
	
	```bash
	# Start the MySQL service:
	mysql.server start
	
	# Enter the MySQL shell:
	mysql -u root -p cloudera
	```

	Create and select database `db1`, and create and populate the `acad` table.

	```sql
	--- List existing databases
	SHOW DATABASES;
	
	--- Create a new database
	CREATE DATABASE db1;
	
	--- Select the active database
	USE db1;

	--- Create the `acad` table
	CREATE TABLE acad (
	  emp_id INT NOT NULL AUTO_INCREMENT,
	  emp_name VARCHAR(100),
	  emp_sal INT,
	  PRIMARY KEY (emp_id)
	);
	
	--- Inset data into the `acad` table
	INSERT INTO acad VALUES
	  (5, "sanam", 50000),
	  (6, "opra", 600000),
	  (7, "yella", 700000);
	  
	--- Show the contents of the table
	SELECT * FROM acad;
	```

	Exit the MySQL shell, and import the `acad` table from the command line.

	```bash
	# import the mysql table
	sqoop import --connect jdbc:mysql://localhost/db1 \
	--username root --password cloudera --table acad -m 1
	
	# list the directory contents
	hadoop fs -ls
	
	# list the contents of the acad directory
	hadoop fs -ls acad/
	
	# concatenate and print the files in acad
	hadoop fs -cat acad/*
	```

2. **Export table from HDFS to MySQL**

	From the command line, verify Hadoop data.

	```bash
	# Existing Hadoop data:
	hadoop fs -cat queryresults/*
	```

	Enter the MySQL shell and create the employee table.

	```sql
	--- create the table in mysql before exporting
	CREATE TABLE employee (
	  id INT,
	  lastName VARCHAR(20),
	  firstName VARCHAR(20),
	  address VARCHAR(10),
	  cityName VARCHAR(10)
	);
	```

	Use the `sqoop` command to export the `employee` table.

	```bash
	sqoop export --connect jdbc:mysql://localhost/db1 --table employee \
	--username root --password cloudera --export-dir queryresults -m 1
	```

	Verify that the export worked from the MySQL shell.

	```sql
	SELECT * FROM employee;
	```

### Part 2

1.	Create Hive Tables through HQL Script

	Execute a HQL script using the command line.
	
	```bash
	hive –f tables-schema.hql
	```
	
	From the hive shell, verify that the import worked.
	
	```hqsql
	--- Verify that the employee table exists.
	SHOW TABLES;
	
	--- Verify the employee table structure.
	DESCRIBE employees;
	```

2. Use Sqoop to import and export hive tables to mysql.

	From the hive shell, create and populate the `emp` table from the Hive shell.

	```hiveql
	--- Create the table
	CREATE TABLE emp (
	  empid INT,
	  emp_name STRING
	) ROW FORMAT DELIMITED
	FIELDS TERMINATED BY ','
	LINES TERMINATED BY '\n'
	STORED AS TEXTFILE;
	
	--- Verify that the table exists.
	SHOW TABLES;
	
	--- Populate the table from the Hadoop file sytem
	LOAD DATA INPATH 'acad/' INTO TABLE emp;
	
	--- Verify that the data was imported
	SELECT * FROM emp;
	```
	
	Use the `hadoop` command to verify that the `emp` table is now on the Hadoop file system.

	```bash
	hadoop fs -ls /user/hive/warehouse/
	```

	Create the `empNew` table form the MySQL shell.

	```sql
	CREATE TABLE empNew (
	  empid INT,
	  emp_name VARCHAR(100)
	);
	```

	Use the `sqoop` command to export the data to MySQL.

	```bash
	sqoop export --connect jdbc:mysql://localhost/db1 --table empNew -m 1 \
	--username root --password cloudera --export-dir /user/hive/warehouse/emp
	```

	Verify that the export worked from the MySQL shell.

	```sql
	SELECT * FROM empNew;
	```

### Part 3

1. Using the Stocks dataset, create a MySQL table and import as Hive.

	Create and populate the `stocks` table.

	```sql
	CREATE TABLE stocks (
	  ymd Date,
	  price_open DECIMAL(5,2),
	  price_high DECIMAL(5,2),
	  price_low DECIMAL(5,2),
	  price_close DECIMAL(5,2),
	  volume INT,
	  price_adj_close DECIMAL(5,2),
	  exchange VARCHAR(10),
	  symbol VARCHAR(4)
	);

	LOAD DATA LOCAL INFILE 'NASDAQ/AAPL/stocks.csv'
	INTO TABLE stocks FIELDS TERMINATED BY ','
	(ymd, price_open, price_high, price_low, price_close, volume, price_adj_close)
	SET exchange = 'NASDAQ', symbol = 'AAPL';

	LOAD DATA LOCAL INFILE 'NASDAQ/INTC/stocks.csv'
	INTO TABLE stocks FIELDS TERMINATED BY ','
	(ymd, price_open, price_high, price_low, price_close, volume, price_adj_close)
	SET exchange = 'NASDAQ', symbol = 'INTC';

	LOAD DATA LOCAL INFILE 'NYSE/GE/stocks.csv'
	INTO TABLE stocks FIELDS TERMINATED BY ','
	(ymd, price_open, price_high, price_low, price_close, volume, price_adj_close)
	SET exchange = 'NYSE', symbol = 'GE';

	LOAD DATA LOCAL INFILE 'NYSE/GE/stocks.csv'
	INTO TABLE stocks FIELDS TERMINATED BY ','
	(ymd, price_open, price_high, price_low, price_close, volume, price_adj_close)
	SET exchange = 'NYSE', symbol = 'GE';
	```

	Create the stocks table from the hive shell.
	
	```hiveql
	CREATE TABLE stocks (
	  ymd STRING,
	  price_open FLOAT,
	  price_high FLOAT,
	  price_low FLOAT,
	  price_close FLOAT,
	  volume FLOAT,
	  price_adj_close FLOAT
	) PARTITIONED BY (market STRING, symbol STRING);
	```
	
	```bash
	export ROWS='ymd, price_open, price_high, price_low, price_close, volume, price_adj_close'
	
	export EX='NASDAQ'
	export SYM='AAPL'
	sqoop import --connect jdbc:mysql://localhost/db1 --username root --password cloudera \
	--query "SELECT $ROWS FROM stocks WHERE exchange='$EX' AND symbol='$SYM' AND \$CONDITIONS" \
	--hcatalog-table stocks -m 1 \
	--hcatalog-partition-keys market,symbol \
	--hcatalog-partition-values "$EX","$SYM"

	export EX='NASDAQ'
	export SYM='INTC'
	sqoop import --connect jdbc:mysql://localhost/db1 --username root --password cloudera \
	--query "SELECT $ROWS FROM stocks WHERE exchange='$EX' AND symbol='$SYM' AND \$CONDITIONS" \
	--hcatalog-table stocks -m 1 \
	--hcatalog-partition-keys market,symbol \
	--hcatalog-partition-values "$EX","$SYM"

	export EX='NYSE'
	export SYM='GE'
	sqoop import --connect jdbc:mysql://localhost/db1 --username root --password cloudera \
	--query "SELECT $ROWS FROM stocks WHERE exchange='$EX' AND symbol='$SYM' AND \$CONDITIONS" \
	--hcatalog-table stocks -m 1 \
	--hcatalog-partition-keys market,symbol \
	--hcatalog-partition-values "$EX","$SYM"
	
	export EX='NYSE'
	export SYM='IBM'
	sqoop import --connect jdbc:mysql://localhost/db1 --username root --password cloudera \
	--query "SELECT $ROWS FROM stocks WHERE exchange='$EX' AND symbol='$SYM' AND \$CONDITIONS" \
	--hcatalog-table stocks -m 1 \
	--hcatalog-partition-keys market,symbol \
	--hcatalog-partition-values "$EX","$SYM"
	```
	
