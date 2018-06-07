--- Create Keyspace
CREATE KEYSPACE test WITH REPLICATION={'class': 'SimpleStrategy', 'replication_factor': 3};
USE test;

--- 1. List the empno,ename,jobtitle,and hiredate of employee from the employee table.
CREATE TABLE employees (empno INT PRIMARY KEY, ename TEXT, jobtitle TEXT, hiredate DATE);
INSERT INTO employees (empno, ename, hiredate, jobtitle) VALUES (1, 'Cam', '2018-01-01', 'Clerk');
INSERT INTO employees (empno, ename, hiredate, jobtitle) VALUES (2, 'Billy', '2015-03-22', 'HR');
INSERT INTO employees (empno, ename, hiredate, jobtitle) VALUES (3, 'Joe', '1980-12-17', 'Clerk');
SELECT empno, ename, jobtitle, hiredate FROM employees;

--- 2. List the name,salary of the employees who are clerks.
ALTER TABLE employees ADD salary INT;
UPDATE employees SET salary = 100 WHERE empno = 1;
UPDATE employees SET salary = 1500 WHERE empno = 2;
UPDATE employees SET salary = 2500 WHERE empno = 3;
SELECT ename, salary FROM employees WHERE jobtitle = 'Clerk' ALLOW FILTERING;

--- 3. List the name,job,salary of every employee joined on ‘december 17,1980’,
SELECT ename, jobtitle, salary FROM employees WHERE hiredate = '1980-12-17' ALLOW FILTERING;

--- 4. List name and annual salary of all the employees.
SELECT ename, salary FROM employees;

--- 5. List the department name & deptno for departments having deptno.>=20
ALTER TABLE employees ADD deptno INT;
ALTER TABLE employees ADD deptname TEXT;
UPDATE employees SET deptno = 1, deptname = 'R&D' WHERE empno = 1;
UPDATE employees SET deptno = 1, deptname = 'R&D' WHERE empno = 2;
UPDATE employees SET deptno = 22, deptname = 'Legal' WHERE empno = 3;
SELECT deptname, deptno FROM employees WHERE deptno >= 20 ALLOW FILTERING;

--- 6. Display employees’ names, salary and manager values of those employees whose salary is 500 from EMP table using SELECT statement.
ALTER TABLE employees ADD manager INT;
UPDATE employees SET manager = 0 WHERE empno =1;
UPDATE employees SET manager = 2 WHERE empno =2;
SELECT ename, salary, manager FROM employees WHERE salary = 500 ALLOW FILTERING;
