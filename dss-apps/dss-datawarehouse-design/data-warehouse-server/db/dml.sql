INSERT INTO `dss_datawarehouse_layer` (name,en_name,owner,principal_name,is_available,preset,sort,description,dbs,create_time,update_time,status,lock_version) VALUES
('原数据层（ODS）','ods','admin','ALL',1,1,10,'由业务系统同步到数据仓库的原始数据，一般不经过加工','ALL','2021-09-01 00:00:00','2021-12-08 11:04:38',1,23),
('明细层（DWD）','dwd','admin','ALL',1,1,20,'从ods层经过ETL得到的明细数据，表示具体的事实','ALL','2021-09-01 00:00:00','2021-11-09 15:44:47',1,8),
('汇总层（DWS）','dws','admin','ALL',1,1,30,'由明细数据经过汇总得到的数据，主要由统计维度和指标构成','ALL','2021-09-01 00:00:00','2021-11-09 15:44:47',1,9);