
## 数据服务-服务管理-API监控----过去24小时内每小时的调用量（指定某工作空间）
SELECT t1.hour, IFNULL(t2.cnt,0) AS cnt
FROM (
	SELECT DATE_FORMAT(@cdate := DATE_ADD(@cdate, INTERVAL - 1 HOUR),'%Y-%m-%d %H:00') AS HOUR
	FROM (SELECT @cdate := DATE_ADD(DATE_FORMAT(NOW(), '%Y-%m-%d %H:00'),INTERVAL + 1 HOUR)
	      FROM dss_project) t0   ##记录大于等于24条的任意一张表
	LIMIT 24 ) t1
LEFT JOIN (
	SELECT DATE_FORMAT(time_start,'%Y-%m-%d %H:00') AS HOUR, COUNT(*) AS cnt
	FROM dss_dataapi_config a JOIN dss_dataapi_call b ON a.id =b.api_id
	WHERE a.workspace_id =223 AND time_start >= (NOW() - INTERVAL 24 HOUR)
	GROUP BY DATE_FORMAT(time_start,'%Y-%m-%d %H:00')
	ORDER BY DATE_FORMAT(time_start,'%Y-%m-%d %H:00')
) t2 ON t1.hour = t2.hour

SELECT DATE_FORMAT(time_start,'%Y-%m-%d %H:00') AS k, IFNULL(ROUND(AVG(time_length),0),0) AS v
FROM dss_dataapi_call
WHERE api_id =18 AND time_start >= '2021-08-03 00:00:00' AND time_start <= '2021-08-10 00:00:00'
GROUP BY DATE_FORMAT(time_start,'%Y-%m-%d %H:00')
ORDER BY k


## 数据服务-服务管理-API监控----指定某个api的平均每小时执行时长
SELECT t1.hour, IFNULL(t2.timeLen,0) AS timeLen
FROM (
	SELECT DATE_FORMAT(@cdate := DATE_ADD(@cdate, INTERVAL - 1 HOUR),'%Y-%m-%d %H:00') AS HOUR
	FROM (SELECT @cdate := DATE_ADD(DATE_FORMAT('2021-08-10 00:00:00', '%Y-%m-%d %H:00'),INTERVAL + 1 HOUR)
	      FROM dss_project) t0   ##记录大于等于24条的任意一张表
	LIMIT 40 ) t1
LEFT JOIN (
	SELECT DATE_FORMAT(time_start,'%Y-%m-%d %H:00') AS HOUR, IFNULL(ROUND(AVG(time_length),0),0) AS timeLen
	FROM dss_dataapi_call
	WHERE api_id =18 AND time_start >= '2021-08-03 00:00:00' AND time_start <= '2021-08-10 00:00:00'
	GROUP BY DATE_FORMAT(time_start,'%Y-%m-%d %H:00')
) t2 ON t1.hour = t2.hour
ORDER BY t1.hour



## 数据服务-服务管理-API监控----指定某个api的平均每小时请求次数
SELECT t1.hour, IFNULL(t2.cnt,0) AS cnt
FROM (
	SELECT DATE_FORMAT(@cdate := DATE_ADD(@cdate, INTERVAL - 1 HOUR),'%Y-%m-%d %H:00') AS HOUR
	FROM (SELECT @cdate := DATE_ADD(DATE_FORMAT('2021-08-05 00:00:00', '%Y-%m-%d %H:00'),INTERVAL + 1 HOUR)
	      FROM dss_project) t0   ##记录大于等于24条的任意一张表
	LIMIT 168 ) t1
LEFT JOIN (
	SELECT DATE_FORMAT(time_start,'%Y-%m-%d %H:00') AS HOUR, COUNT(id) AS cnt
	FROM dss_dataapi_call
	WHERE api_id =18 AND time_start >= '2021-07-29 00:00:00' AND time_start <= '2021-08-05 00:00:00'
	GROUP BY DATE_FORMAT(time_start,'%Y-%m-%d %H:00')
) t2 ON t1.hour = t2.hour
ORDER BY t1.hour


SELECT DATE_FORMAT(time_start,'%Y-%m-%d %H:00') AS k, COUNT(id) AS v
FROM dss_dataapi_call
WHERE api_id =18 AND time_start >= '2021-07-29 00:00:00' AND time_start <= '2021-08-05 00:00:00'
GROUP BY DATE_FORMAT(time_start,'%Y-%m-%d %H:00')
ORDER BY k