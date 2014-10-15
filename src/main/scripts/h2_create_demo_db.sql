CREATE TABLE IF NOT EXISTS DIM_ACCOUNT (
	ACCOUNT VARCHAR(3) not NULL,
	"ACCOUNT NAME" VARCHAR(255),
	"CUSTOMER FLAG" VARCHAR(50),
	"EMPLOYEE" VARCHAR(255),
	"SECTOR INDUSTRY" VARCHAR(255),
	"OPEN OPPORTUNITY" VARCHAR(255),
	PRIMARY KEY ( ACCOUNT )
);	

INSERT INTO DIM_ACCOUNT (ACCOUNT,	"ACCOUNT NAME",	"CUSTOMER FLAG",	"EMPLOYEE",	"SECTOR INDUSTRY",	"OPEN OPPORTUNITY")
SELECT  "Account",	"Account Name",	"Customer Flag",	"Employee",	"Sector Industry",	"Open Opportunity"
FROM CSVREAD ('demo/demo_data/Account.csv','Account,Account Name,Customer Flag,Employee,Sector Industry,Open Opportunity')
WHERE  "Account" != 'Account';


CREATE TABLE IF NOT EXISTS DIM_PROVINCE (
	COUNTRY VARCHAR(255) not NULL,
	NAME VARCHAR(255),
	PROVINCE VARCHAR(255),
	PRIMARY KEY ( PROVINCE )
);

INSERT INTO DIM_PROVINCE  (COUNTRY, NAME, PROVINCE)
SELECT  "Country", "Name", "Province"
FROM CSVREAD ('demo/demo_data/Province.csv','Country,Name,Province')
WHERE  "Province" != 'Province';


CREATE TABLE IF NOT EXISTS DIM_PRODUCT (
	PRODUCT VARCHAR(9) not NULL,
	"PRODUCT NAME" VARCHAR(255),
	CATEGORY VARCHAR(255),
	SUBCATEGORY VARCHAR(255),
	PRIMARY KEY ( PRODUCT )
);

INSERT INTO DIM_PRODUCT   (PRODUCT,"PRODUCT NAME", CATEGORY, SUBCATEGORY )
SELECT  "Product","Category","Product Name","Subcategory"
FROM CSVREAD ('demo/demo_data/Product.csv','Product,Category,Product Name,Subcategory')
WHERE  "Product" != 'Product';


CREATE TABLE IF NOT EXISTS DIM_SCENARIO (
	SCENARIO VARCHAR(10) not NULL,
	SCENARIO_NAME VARCHAR(255),
	PRIMARY KEY ( SCENARIO )
);

INSERT INTO DIM_SCENARIO   ( SCENARIO , SCENARIO_NAME )
SELECT "Scenario","Name"
FROM CSVREAD ('demo/demo_data/Scenario.csv','Scenario,Name')
WHERE  "Scenario" != 'Scenario';

CREATE TABLE IF NOT EXISTS DIM_PERIOD (
	PERIOD VARCHAR (6) not NULL,
	YEAR VARCHAR (4),
	MONTH VARCHAR (2),
	PRIMARY KEY ( PERIOD )
);

INSERT INTO DIM_PERIOD  (PERIOD ,YEAR ,MONTH )
SELECT  "Period" ,"Year" ,"Month"
FROM CSVREAD ('demo/demo_data/Period.csv','Period,Year,Month')
WHERE  "Period" != 'Period';

CREATE TABLE IF NOT EXISTS MEASURE_SET_SALES
(
	Account VARCHAR(3) not NULL,
	Province VARCHAR(255),
	Product VARCHAR(9) not NULL,
	SCENARIO VARCHAR(7) not NULL,
	PERIOD VARCHAR (6) not NULL,
	VALUE_Quantity DOUBLE,
	VALUE_Sales DOUBLE,
	VALUE_Cost DOUBLE
);

INSERT INTO MEASURE_SET_SALES   ( ACCOUNT ,PROVINCE ,PRODUCT ,SCENARIO ,PERIOD ,VALUE_QUANTITY ,VALUE_SALES ,VALUE_COST  )
SELECT  "Account","Province","Product","Scenario","Period","Quantity","Sales","Cost"
FROM CSVREAD ('demo/demo_data/Sales.csv','Account,Province,Product,Period,Scenario,Quantity,Sales,Cost')
WHERE  "Period" != 'Period';



