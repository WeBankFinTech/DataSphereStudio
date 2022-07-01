package org.apache.dolphinscheduler.api.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang.StringUtils;
import org.apache.dolphinscheduler.api.enums.Status;
import org.apache.dolphinscheduler.api.utils.PageInfo;
import org.apache.dolphinscheduler.common.Constants;
import org.apache.dolphinscheduler.common.utils.DateUtils;
import org.apache.dolphinscheduler.dao.entity.ProcessInstance;
import org.apache.dolphinscheduler.dao.entity.Project;
import org.apache.dolphinscheduler.dao.entity.User;
import org.apache.dolphinscheduler.dao.mapper.ProcessInstanceMetricsMapper;
import org.apache.dolphinscheduler.dao.mapper.ProjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author enjoyyin
 * @date 2022-02-23
 * @since 0.5.0
 */
@Service
public class ProcessInstanceMetricsService extends BaseDAGService  {

    private static final Logger logger = LoggerFactory.getLogger(ProcessInstanceMetricsService.class);

    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private UsersService usersService;
    @Autowired
    private ProcessInstanceMetricsMapper processInstanceMapper;


    public Map<String, Object> queryProcessInstanceListOrderByDuration(User loginUser, String projectName,
                                                                       Integer processDefinitionId, String executorName, String startDate, String endDate, Integer pageNo,
                                                                       Integer pageSize) {
        Map<String, Object> result = new HashMap<>(16);
        Project project = projectMapper.queryByName(projectName);

        Map<String, Object> checkResult = projectService.checkProjectAndAuth(loginUser, project, projectName);
        Status resultEnum = (Status)checkResult.get(Constants.STATUS);
        if (resultEnum != Status.SUCCESS) {
            return checkResult;
        }

        Date start = null;
        Date end = null;
        try {
            if (StringUtils.isNotEmpty(startDate)) {
                start = DateUtils.getScheduleDate(startDate);
            }
            if (StringUtils.isNotEmpty(endDate)) {
                end = DateUtils.getScheduleDate(endDate);
            }
        } catch (Exception e) {
            putMsg(result, Status.REQUEST_PARAMS_NOT_VALID_ERROR, "startDate,endDate");
            return result;
        }

        Page<ProcessInstance> page = new Page(pageNo, pageSize);
        PageInfo pageInfo = new PageInfo<ProcessInstance>(pageNo, pageSize);
        int executorId = usersService.getUserIdByName(executorName);

        IPage<ProcessInstance> processInstanceList = processInstanceMapper.queryProcessInstanceListOrderByDuration(page,
            project.getId(), processDefinitionId, executorId, start, end);

        List<ProcessInstance> processInstances = processInstanceList.getRecords();

        for (ProcessInstance processInstance : processInstances) {
            processInstance.setDuration(
                DateUtils.format2Duration(processInstance.getStartTime(), processInstance.getEndTime()));
            User executor = usersService.queryUser(processInstance.getExecutorId());
            if (null != executor) {
                processInstance.setExecutorName(executor.getUserName());
            }
        }

        pageInfo.setTotalCount((int)processInstanceList.getTotal());
        pageInfo.setLists(processInstances);
        result.put(Constants.DATA_LIST, pageInfo);
        putMsg(result, Status.SUCCESS);
        return result;
    }

    public Map<String, Object> queryProcessInstanceStatistics(User loginUser, String projectName,
        Integer processDefinitionId, String executorName, List<String> dateList, int state, int step) {
        Project project = projectMapper.queryByName(projectName);

        Map<String, Object> checkResult = projectService.checkProjectAndAuth(loginUser, project, projectName);
        Status resultEnum = (Status)checkResult.get(Constants.STATUS);
        if (resultEnum != Status.SUCCESS) {
            return checkResult;
        }

        Map<String, Object> dataMap = new HashMap<>(8);
        int executorId = usersService.getUserIdByName(executorName);
        for (String startDate : dateList) {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = start.plusDays(1);
            List<ProcessInstance> processInstanceList = processInstanceMapper.queryProcessInstanceListByStartTime(
                project.getId(), processDefinitionId, executorId, start, end, state);

            List<Integer> counts = new ArrayList<>(Collections.nCopies((int)Math.ceil(24.0 / step), 0));
            Map<Integer, List<ProcessInstance>> map = processInstanceList.stream()
                .collect(Collectors.groupingBy(
                    processInstance -> DateUtils.getHourIndex(processInstance.getStartTime()) / step));
            map.forEach((k, v) -> counts.set(k, v.size()));

            dataMap.put(startDate, counts);
        }

        Map<String, Object> result = new HashMap<>(4);
        result.put(Constants.DATA_LIST, dataMap);
        putMsg(result, Status.SUCCESS);
        return result;
    }


}
