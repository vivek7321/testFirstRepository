Use HyphenviewDb;
 CREATE TABLE features  
(  
   feature_id int(32) zerofill auto_increment,  
   feature_name varchar(50) NOT NULL,
   feature_description varchar(100) NOT NULL,
   feature_cost int NOT NULL,
   primary key (feature_id)  
);  

insert into features (feature_name,feature_description,feature_cost) values('Crystal Reports','Crystal Reports Viewer',0);
insert into features (feature_name,feature_description,feature_cost) values('Dashboard','Dashboard of Reports',0);
insert into features (feature_name,feature_description,feature_cost) values('Both','Both',0);

CREATE TABLE customer  
(  
   customer_id int(32) zerofill auto_increment,  
   customer_name varchar(50) NOT NULL,
   contact_number varchar(50) NULL,
   first_name varchar(50) NULL,
   last_name varchar(50) NULL,
   address varchar(100) NULL,
   city varchar(20) NULL,
   state varchar(50) NULL,
   country varchar(50) NULL,
   postal_code varchar(8) NULL,
   email_id varchar(50) NOT NULL,
   gender varchar(10) NULL,
   unique_id varchar(50) NULL,
   primary key (customer_id)
);  

CREATE TABLE orders 
(  
   order_id int(32) zerofill auto_increment,  
   customer_id int(32) zerofill not null,
   order_date datetime NOT NULL,
   payment_transaction_id varchar(50) not null,
   primary key (order_id),
   foreign key (customer_id) references customer(customer_id)
);  
 
CREATE TABLE orders_features  
(  
   order_feature_id int(32) zerofill auto_increment,  
   feature_id int(32) zerofill not null,
   order_id int(32) zerofill not null,
   primary key (order_feature_id),
   foreign key (order_id) references orders(order_id),
   foreign key (feature_id) references features(feature_id)
);  


CREATE TABLE access_group(
	group_id int(32) zerofill auto_increment,
	userrolemanage varchar(2) NOT NULL,
	readaccess varchar(2) NOT NULL,
	export varchar(2) NOT NULL,
	group_name varchar(200) NOT NULL,
	searchaccess varchar(2) NOT NULL,
    printaccess varchar(2) NOT NULL,
	primary key (group_id)
);

insert into access_group (userrolemanage, readaccess, export, searchaccess, printaccess, group_name)
values ('Y','Y','Y','Y','Y','Administration');
insert into access_group (userrolemanage, readaccess, export, searchaccess, printaccess, group_name)
values ('N','Y','Y','Y','Y', 'EmployeeAccess');
insert into access_group (userrolemanage, readaccess, export, searchaccess, printaccess, group_name)
values ('N','Y','N','Y','N', 'ReadOnlyAccess');

CREATE TABLE user_role(
	role_id int(32) zerofill auto_increment,
	role_name varchar(30) NOT NULL,
	group_id int(32) zerofill NOT NULL,
	PRIMARY KEY (role_id),
    foreign key (group_id) references access_group(group_id)
);


insert into user_role (role_name,group_id) 
values ('SuperAdmin' , (select group_id from access_group where group_name = 'Administration'));
insert into user_role (role_name,group_id) 
values ('Admin' , (select group_id from access_group where group_name = 'Administration'));
insert into user_role (role_name,group_id) 
values ('Employee' , (select group_id from access_group where group_name = 'EmployeeAccess'));
insert into user_role (role_name,group_id) 
values ('Guest' , (select group_id from access_group where group_name = 'Administration'));


CREATE TABLE user_account(
	user_id int(32) zerofill auto_increment,
	user_name varchar(50) NOT NULL,
	role_id int(32) zerofill NOT NULL,
	email_id varchar(50) NOT NULL,
	user_password varchar(300) NOT NULL,
	user_created_on datetime NOT NULL,
	user_status bit NOT NULL,
	customer_id int(32) zerofill NOT NULL,
	PRIMARY KEY (user_id),
    foreign key (role_id) references user_role(role_id),
    foreign key (customer_id) references customer(customer_id)
) ;


CREATE TABLE database_details  
(  
   db_details_id int(32) zerofill auto_increment,  
   rdbms_name varchar(50) NOT NULL,
   domain_name varchar(20) NOT NULL,
   db_port int not null,
   db_user_name varchar(20) not null,
   db_password varchar(50) not null,
   db_schema_name varchar(20) null,
   customer_id int(32) zerofill not null,
   PRIMARY KEY (db_details_id)
);  


CREATE TABLE report_template  
(  
   report_template_id int(32) zerofill auto_increment,  
   report_template_name varchar(50) NOT NULL,
   report_type varchar(10) NOT NULL,
   chart_type varchar(20),
   defined_query text not null,
   customer_id int(32) zerofill not null,
   db_details_id int(32) zerofill not null,
   enable_drilldown varchar(3) not null default 'no',
   display_order int not null,
   auto_update_interval int null,
   background_color varchar(10) null,
   chart_rect_color varchar(10) null,
   time_period varchar(30) null,
   	`theme` varchar(255) NULL,
	`report_sub_title` varchar(255) NULL,
	`dashboardMapId` int NULL,
   PRIMARY KEY (report_template_id),
   foreign key (customer_id) references customer(customer_id),
   foreign key (db_details_id) references database_details(db_details_id)     
);  


CREATE TABLE audit_reportinfo(
	SNO int NOT NULL,
	reportid int NOT NULL,
	reportname varchar(30) NULL,
	reportsource varchar(50) NULL,
	reporttype varchar(3) NULL,
	created_time datetime NULL,
	reportpath varchar(100) NULL,
	reportparam varchar(50) NULL,
	auditStatus varchar(100) NULL,
	auditUser varchar(100) NULL,
	auditDate datetime NULL,
	RPTFILE longblob NULL,
	fileDesc varchar(1000) NULL
);



CREATE TABLE reportinfo(
	reportid int NOT NULL,
	reportname varchar(30) NULL,
	reportsource varchar(50) NULL,
	reporttype varchar(3) NULL,
	created_time datetime NULL,
	reportpath varchar(100) NULL,
	reportparam varchar(50) NULL,
	reportDesc varchar(200) NULL,
	RPTFILE longblob NULL,
	customer_id int(32) zerofill not null,
	PRIMARY KEY (reportid),
    foreign key (customer_id) references customer(customer_id)
);

CREATE TABLE Schedulerinfo(
	`Scheduleid` int NOT NULL,
	`reportid` int NULL,
	`Scheduledtime` datetime(3) NULL,
	`ScheduledIntervalDays` int NULL,
	`ScheduledIntervalTime` Double NULL,
	`Status` varchar(15) NULL,
	`emailid` varchar(50) NULL,
	`SchedulerPeriod` varchar(100) NULL,
	`customer_id` Char(36) NOT NULL,
	`emailcc` varchar(3000) NULL,
	`startDate` datetime(3) NULL,
	`reportTitle` varchar(1000) NULL,
	`reportIDEB` int NULL,
	`emailBodyContent` Nvarchar(3000) NULL,
    PRIMARY KEY (Scheduleid)
);


-- Insert super Admin data

INSERT INTO customer (`customer_name`, `email_id`) 
VALUES ('Super Admin', 'super.admin@erasmith.com');

INSERT INTO user_account (`user_name`,`role_id`,`email_id`,`user_password`,`user_created_on`,`user_status`,`customer_id`) 
VALUES ('Super Admin',(select role_id from user_role where role_name='SuperAdmin'),'super.admin@erasmith.com','511a19f125a8c56439681101d34563d9e63d3ad6',sysdate(),1,
(select customer_id from customer where email_id = 'super.admin@erasmith.com'));


CREATE TABLE report_widget(  
   widget_id int(32) zerofill auto_increment,  
   widget_name varchar(50) NOT NULL,
   widget_type varchar(10) NOT NULL,
   defined_query longtext not null,
   customer_id int(32) not null,
   db_details_id int(32) not null,
     display_order int null,
   CONSTRAINT PK_widget_id PRIMARY KEY CLUSTERED (widget_id)   
);


CREATE TABLE user_report_map(
	`user_report_map_id` Char(36) NOT NULL,
	`user_id` varchar(100) NOT NULL,
	`report_id` varchar(100) NOT NULL
);

CREATE TABLE scheduler_report_map_xls(
	`schduler_report_map_id` Char(36) NOT NULL,
	`schduler_id` varchar(100) NOT NULL,
	`report_id_xls` int NULL
);

CREATE TABLE scheduler_report_map_pdf(
	`schduler_report_map_id` Char(36) NOT NULL,
	`schduler_id` varchar(100) NOT NULL,
	`report_id_pdf` int NULL
);

CREATE TABLE scheduler_report_map(
	`schduler_report_map_id` Char(36) NOT NULL,
	`schduler_id` varchar(100) NOT NULL,
	`report_id_pdf` int NULL,
	`report_id_xls` int NULL
);

CREATE TABLE user_report_temp_map(
	`user_report_temp_map_id` Char(36) NOT NULL,
	`user_id` varchar(100) NOT NULL,
	`report_template_id` varchar(100) NOT NULL
);

CREATE TABLE rpt_database_details(
	`rpt_db_details_id` int(32) zerofill auto_increment,
	`rdbms_name` varchar(50) NOT NULL,
	`domain_name` varchar(20) NOT NULL,
	`db_port` int NOT NULL,
	`db_user_name` varchar(20) NOT NULL,
	`db_password` varchar(50) NOT NULL,
	`db_schema_name` varchar(20) NULL,
	`customer_id` Char(36) NOT NULL,
 CONSTRAINT `PK_rpt_db_details_id` PRIMARY KEY 
(
	`rpt_db_details_id` ASC
) 
);
