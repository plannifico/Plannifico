CREATE TABLE IF NOT EXISTS DIM_Account (
	Account VARCHAR(3) not NULL,
	"Account Name" VARCHAR(255),
	"Customer Flag" VARCHAR(50),
	"Employee" VARCHAR(255),
	"Sector Industry" VARCHAR(255),
	"Open Opportunity" VARCHAR(255),
	PRIMARY KEY ( Account )
);	

INSERT INTO DIM_ACCOUNT (ACCOUNT,	"Account Name",	"Customer Flag",	"Employee",	"Sector Industry",	"Open Opportunity")
SELECT  "Account",	"Account Name",	"Customer Flag",	"Employee",	"Sector Industry",	"Open Opportunity"
FROM CSVREAD ('demo_data/Account.csv','Account,Account Name,Customer Flag,Employee,Sector Industry,Open Opportunity')
WHERE  "Account" != 'Account';


CREATE TABLE IF NOT EXISTS DIM_Province (
	Country VARCHAR(255) not NULL,
	Name VARCHAR(255),
	Province VARCHAR(255),
	PRIMARY KEY ( Province )
);

INSERT INTO DIM_PROVINCE  (COUNTRY, NAME, PROVINCE)
SELECT  "Country", "Name", "Province"
FROM CSVREAD ('demo_data/Province.csv','Country,Name,Province')
WHERE  "Province" != 'Province';


CREATE TABLE IF NOT EXISTS DIM_Product (
	Product VARCHAR(9) not NULL,
	"Product Name" VARCHAR(255),
	Category VARCHAR(255),
	Subcategory VARCHAR(255),
	PRIMARY KEY ( Product )
);

INSERT INTO DIM_PRODUCT   (PRODUCT,"Product Name", CATEGORY, SUBCATEGORY )
SELECT  "Product","Category","Product Name","Subcategory"
FROM CSVREAD ('demo_data/Product.csv','Product,Category,Product Name,Subcategory')
WHERE  "Product" != 'Product';


CREATE TABLE IF NOT EXISTS DIM_SCENARIO (
	SCENARIO VARCHAR(10) not NULL,
	SCENARIO_NAME VARCHAR(255),
	PRIMARY KEY ( SCENARIO )
);

INSERT INTO DIM_SCENARIO   ( SCENARIO , SCENARIO_NAME )
SELECT "Scenario","Name"
FROM CSVREAD ('demo_data/Scenario.csv','Scenario,Name')
WHERE  "Scenario" != 'Scenario';

CREATE TABLE IF NOT EXISTS DIM_PERIOD (
	PERIOD VARCHAR (6) not NULL,
	YEAR VARCHAR (4),
	MONTH VARCHAR (2),
	PRIMARY KEY ( PERIOD )
);

INSERT INTO DIM_PERIOD  (PERIOD ,YEAR ,MONTH )
SELECT  "Period" ,"Year" ,"Month"
FROM CSVREAD ('demo_data/Period.csv','Period,Year,Month')
WHERE  "Period" != 'Period';

CREATE TABLE IF NOT EXISTS MEASURE_SET_Sales
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
FROM CSVREAD ('demo_data/Sales.csv','Account,Province,Product,Period,Scenario,Quantity,Sales,Cost')
WHERE  "Period" != 'Period';



