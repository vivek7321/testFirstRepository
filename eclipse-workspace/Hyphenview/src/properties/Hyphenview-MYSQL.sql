
CREATE TABLE features  
(  
   feature_id uniqueidentifier DEFAULT NEWSEQUENTIALID(),  
   feature_name varchar(50) NOT NULL,
   feature_description varchar(100) NOT NULL,
   feature_cost int NOT NULL,
   CONSTRAINT PK_feature_id PRIMARY KEY NONCLUSTERED (feature_id)  
);  


insert into features (feature_name,feature_description,feature_cost) values('High Level Reports','Charts or Tables with high level data',1000);
insert into features (feature_name,feature_description,feature_cost) values('Detailed Reports','Tables with detailed data in Tabular format',1000);
insert into features (feature_name,feature_description,feature_cost) values('Export options','Export the report in pdf or excel format',1000);

CREATE TABLE customer  
(  
   customer_id uniqueidentifier DEFAULT NEWSEQUENTIALID(),  
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
   CONSTRAINT PK_customer_id PRIMARY KEY NONCLUSTERED (customer_id)  
);  
  
CREATE TABLE orders  
(  
   order_id uniqueidentifier DEFAULT NEWSEQUENTIALID(),  
   customer_id uniqueidentifier NOT NULL,
   order_date datetime NOT NULL,
   payment_transaction_id varchar(50) not null,
   CONSTRAINT PK_order_id PRIMARY KEY NONCLUSTERED (order_id),
	CONSTRAINT FK_customer_feature_id FOREIGN KEY (customer_id)     
    REFERENCES dbo.customer (customer_id)     
    ON DELETE CASCADE    
    ON UPDATE CASCADE
);  
 
CREATE TABLE orders_features  
(  
   order_feature_id uniqueidentifier DEFAULT NEWSEQUENTIALID(),  
   feature_id uniqueidentifier NOT NULL,
   order_id uniqueidentifier NOT NULL,
   CONSTRAINT PK_order_feature_id PRIMARY KEY NONCLUSTERED (order_feature_id),
   CONSTRAINT FK_order_feature_id1 FOREIGN KEY (feature_id)     
    REFERENCES dbo.features (feature_id)     
    ON DELETE CASCADE    
    ON UPDATE CASCADE,
	CONSTRAINT FK_order_feature_id2 FOREIGN KEY (order_id)     
    REFERENCES dbo.orders (order_id)     
    ON DELETE CASCADE    
    ON UPDATE CASCADE
);  


CREATE TABLE access_group(
	group_id uniqueidentifier DEFAULT NEWSEQUENTIALID(),
	userrolemanage varchar(2) NOT NULL,
	readaccess varchar(2) NOT NULL,
	export varchar(2) NOT NULL,
	group_name varchar(200) NOT NULL,
	searchaccess varchar(2) NOT NULL,
    printaccess varchar(2) NOT NULL,
	CONSTRAINT PK_group_id PRIMARY KEY NONCLUSTERED (group_id)
)



CREATE TABLE user_role(
	role_id uniqueidentifier DEFAULT NEWSEQUENTIALID(),
	role_name varchar(30) NOT NULL,
	group_id uniqueidentifier NOT NULL,
		CONSTRAINT PK_role_id PRIMARY KEY NONCLUSTERED (role_id)
)


insert into access_group (userrolemanage, readaccess, export, group_name,searchaccess,printaccess) 
output inserted.group_id, 'Admin'
into user_role (group_id,role_name)
values ('Y','Y','Y','Administration','Y','Y');

insert into access_group (userrolemanage, readaccess, export, group_name,searchaccess,printaccess) 
output inserted.group_id, 'Employee'
into user_role (group_id,role_name)
values ('N','Y','Y','EmployeeAccess','Y','Y');

insert into access_group (userrolemanage, readaccess, export, searchaccess, printaccess, group_name)
values ('N','Y','N','Y','N', 'ReadOnlyAccess');

insert into user_role (role_name,group_id) 
values ('SuperAdmin' , (select group_id from access_group where group_name = 'Administration'));

insert into user_role (role_name,group_id) 
values ('Guest' , (select group_id from access_group where group_name = 'Administration'));



Alter table user_role ADD CONSTRAINT FK_role_group_id FOREIGN KEY (group_id)     
    REFERENCES dbo.access_group (group_id)     
    ON DELETE CASCADE    
    ON UPDATE CASCADE;


CREATE TABLE user_account(
	user_id uniqueidentifier DEFAULT NEWSEQUENTIALID(),
	user_name varchar(50) NOT NULL,
	role_id uniqueidentifier NOT NULL,
	email_id varchar(50) NOT NULL,
	user_password varchar(300) NOT NULL,
	user_created_on datetime NOT NULL,
	user_status bit NOT NULL,
	customer_id uniqueidentifier NOT NULL,
	CONSTRAINT PK_user_id PRIMARY KEY NONCLUSTERED (user_id),
	CONSTRAINT FK_customer_user_id FOREIGN KEY (customer_id)     
    REFERENCES dbo.customer (customer_id)     
    ON DELETE CASCADE    
    ON UPDATE CASCADE,
	CONSTRAINT FK_role_user_id FOREIGN KEY (role_id)     
    REFERENCES dbo.user_role (role_id)     
    ON DELETE CASCADE    
    ON UPDATE CASCADE
) 


CREATE TABLE database_details  
(  
   db_details_id uniqueidentifier DEFAULT NEWSEQUENTIALID(),  
   rdbms_name varchar(50) NOT NULL,
   domain_name varchar(20) NOT NULL,
   db_port int not null,
   db_user_name varchar(20) not null,
   db_password varchar(50) not null,
   db_schema_name varchar(20) null,
   customer_id uniqueidentifier not null,
   CONSTRAINT PK_db_details_id PRIMARY KEY CLUSTERED (db_details_id),
   CONSTRAINT FK_db_customer_id FOREIGN KEY (customer_id)     
    REFERENCES dbo.customer (customer_id)     
    ON DELETE CASCADE    
    ON UPDATE CASCADE 
);  


CREATE TABLE report_template  
(  
   report_template_id uniqueidentifier DEFAULT NEWSEQUENTIALID(),  
   report_template_name varchar(50) NOT NULL,
   report_type varchar(10) NOT NULL,
   chart_type varchar(20),
   defined_query varchar(max) not null,
   customer_id uniqueidentifier not null,
   db_details_id uniqueidentifier not null,
   enable_drilldown varchar(3) not null default 'no',
   display_order int not null,
   auto_update_interval int null,
   background_color varchar(10) null,
   chart_rect_color varchar(10) null,
   time_period varchar(30) null,
   CONSTRAINT PK_report_template_id PRIMARY KEY CLUSTERED (report_template_id),
   CONSTRAINT FK_customer_id FOREIGN KEY (customer_id)     
    REFERENCES dbo.customer (customer_id)     
    ON DELETE CASCADE    
    ON UPDATE CASCADE,
	 CONSTRAINT FK_db_details_id FOREIGN KEY (db_details_id)     
    REFERENCES dbo.database_details (db_details_id)     
);  

CREATE TABLE audit_reportinfo(
	[SNO] [int] NOT NULL,
	[reportid] [int] NOT NULL,
	[reportname] [varchar](1000) NULL,
	[reportsource] [varchar](50) NULL,
	[reporttype] [varchar](3) NULL,
	[created_time] [datetime] NULL,
	[reportpath] [varchar](100) NULL,
	[reportparam] [varchar](50) NULL,
	[auditStatus] [varchar](100) NULL,
	[auditUser] [varchar](100) NULL,
	[auditDate] [datetime] NULL,
	[RPTFILE] [varbinary](max) NULL,
	[fileDesc] [varchar](1000) NULL
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]



CREATE TABLE reportinfo(
	[reportid] [int] NOT NULL,
	[reportname] [varchar](1000) NULL,
	[reportsource] [varchar](50) NULL,
	[reporttype] [varchar](3) NULL,
	[created_time] [datetime] NULL,
	[reportpath] [varchar](100) NULL,
	[reportparam] [varchar](50) NULL,
	[reportDesc] [varchar](200) NULL,
	[RPTFILE] [varbinary](max) NULL,
	customer_id uniqueidentifier NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[reportid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
CONSTRAINT FK_customer_crystal_report_id FOREIGN KEY (customer_id)     
    REFERENCES dbo.customer (customer_id)
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]



CREATE TABLE Schedulerinfo(
	[Scheduleid] [int] NOT NULL,
	[reportid] [int] NULL,
	[Scheduledtime] [datetime] NULL,
	[ScheduledIntervalDays] [int] NULL,
	[ScheduledIntervalTime] [float] NULL,
	[Status] [varchar](15) NULL,
	[emailid] [varchar](50) NULL,
	[SchedulerPeriod] [varchar](100) NULL,
	[customer_id] [uniqueidentifier] NOT NULL,
	[emailcc] [varchar](3000) NULL,
	[startDate] [datetime] NULL,
	[reportTitle] [varchar](1000) NULL,
	[reportIDEB] [int] NULL,
	[emailBodyContent] [nchar](3000) NULL,
PRIMARY KEY CLUSTERED 
(
	[Scheduleid] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]


-- Insert super Admin data

INSERT INTO dbo.customer (customer_name, email_id) VALUES ('Super Admin', 'super.admin@erasmith.com');

INSERT INTO dbo.user_account (user_name,role_id,email_id,user_password,user_created_on,user_status,customer_id) 
VALUES ('Super Admin',(select role_id from user_role where role_name='SuperAdmin'),'super.admin@erasmith.com','511a19f125a8c56439681101d34563d9e63d3ad6','2018-09-19 18:50:13',1,
(select customer_id from customer where email_id = 'super.admin@erasmith.com'));

CREATE TABLE report_widget  
(  
   widget_id uniqueidentifier DEFAULT NEWSEQUENTIALID(),  
   widget_name varchar(50) NOT NULL,
   widget_type varchar(10) NOT NULL,
   defined_query varchar(max) not null,
   customer_id uniqueidentifier not null,
   db_details_id uniqueidentifier not null,
   display_order int null,
   CONSTRAINT PK_widget_id PRIMARY KEY CLUSTERED (widget_id),
   CONSTRAINT FK_widget_customer_id FOREIGN KEY (customer_id)     
    REFERENCES dbo.customer (customer_id)     
    ON DELETE CASCADE    
    ON UPDATE CASCADE,
                CONSTRAINT FK_widget_db_details_id FOREIGN KEY (db_details_id)     
    REFERENCES dbo.database_details (db_details_id)     
);

GO
/****** Object:  Table SCHEDULER_REPORT_MAP    Script Date: 10/28/2019 4:09:31 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE SCHEDULER_REPORT_MAP(
	[schduler_report_map_id] [uniqueidentifier] NOT NULL,
	[schduler_id] [varchar](100) NOT NULL,
	[report_id_pdf] [int] NULL,
	[report_id_xls] [int] NULL
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table SCHEDULER_REPORT_MAP_PDF    Script Date: 10/28/2019 4:09:31 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE SCHEDULER_REPORT_MAP_PDF(
	[schduler_report_map_id] [uniqueidentifier] NOT NULL,
	[schduler_id] [varchar](100) NOT NULL,
	[report_id_pdf] [int] NULL
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table SCHEDULER_REPORT_MAP_XLS    Script Date: 10/28/2019 4:09:31 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE SCHEDULER_REPORT_MAP_XLS(
	[schduler_report_map_id] [uniqueidentifier] NOT NULL,
	[schduler_id] [varchar](100) NOT NULL,
	[report_id_xls] [int] NULL
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table USER_REPORT_MAP    Script Date: 10/28/2019 4:09:31 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE USER_REPORT_MAP(
	[user_report_map_id] [uniqueidentifier] NOT NULL DEFAULT (newsequentialid()),
	[user_id] [varchar](100) NOT NULL,
	[report_id] [varchar](100) NOT NULL
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table USER_REPORT_TEMP_MAP    Script Date: 10/28/2019 4:09:31 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE USER_REPORT_TEMP_MAP(
	[user_report_temp_map_id] [uniqueidentifier] NOT NULL DEFAULT (newsequentialid()),
	[user_id] [varchar](100) NOT NULL,
	[report_template_id] [varchar](100) NOT NULL
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
ALTER TABLE SCHEDULER_REPORT_MAP ADD  DEFAULT (newsequentialid()) FOR [schduler_report_map_id]
GO
ALTER TABLE SCHEDULER_REPORT_MAP_PDF ADD  DEFAULT (newsequentialid()) FOR [schduler_report_map_id]
GO
ALTER TABLE SCHEDULER_REPORT_MAP_XLS ADD  DEFAULT (newsequentialid()) FOR [schduler_report_map_id]
GO


SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[rpt_database_details](
	[rpt_db_details_id] [uniqueidentifier] NOT NULL DEFAULT (newsequentialid()),
	[rdbms_name] [varchar](50) NOT NULL,
	[domain_name] [varchar](20) NOT NULL,
	[db_port] [int] NOT NULL,
	[db_user_name] [varchar](20) NOT NULL,
	[db_password] [varchar](50) NOT NULL,
	[db_schema_name] [varchar](20) NULL,
	[customer_id] [uniqueidentifier] NOT NULL,
 CONSTRAINT [PK_rpt_db_details_id] PRIMARY KEY CLUSTERED 
(
	[rpt_db_details_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO