package org.apache.dolphinscheduler.api.controller;

import io.swagger.annotations.*;
import org.apache.dolphinscheduler.api.exceptions.ApiException;
import org.apache.dolphinscheduler.api.service.ProcessInstanceMetricsService;
import org.apache.dolphinscheduler.api.utils.Result;
import org.apache.dolphinscheduler.common.Constants;
import org.apache.dolphinscheduler.common.enums.ExecutionStatus;
import org.apache.dolphinscheduler.dao.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import static org.apache.dolphinscheduler.api.enums.Status.QUERY_PROCESS_INSTANCE_LIST_ORDER_BY_DURATION_ERROR;
import static org.apache.dolphinscheduler.api.enums.Status.QUERY_PROCESS_INSTANCE_STATISTICS_ERROR;

import java.util.List;
import java.util.Map;

/**
 * @author enjoyyin
 * @date 2022-02-23
 * @since 0.5.0
 */
@Api(tags = "PROCESS_INSTANCE_METRICS_TAG")
@RestController
@RequestMapping("/projects/{projectName}/instance")
public class ProcessInstanceMetricsController extends BaseController  {

    private static final Logger logger = LoggerFactory.getLogger(ProcessInstanceMetricsController.class);

    @Autowired
    private ProcessInstanceMetricsService processInstanceService;

    {
        logger.info("dolphinscheduler-prod-metrics module is loaded.");
    }

    @ApiOperation(value = "queryProcessInstanceListOrderByDuration",
                 notes = "QUERY_PROCESS_INSTANCE_LIST_ORDER_BY_DURATION_NOTES")
    @ApiImplicitParams(
                   {@ApiImplicitParam(name = "processDefinitionId", value = "PROCESS_DEFINITION_ID", dataType = "Int",
                        example = "100"), @ApiImplicitParam(name = "executorName", value = "EXECUTOR_NAME", type = "String"),
                        @ApiImplicitParam(name = "startDate", value = "START_DATE", type = "String"),
                        @ApiImplicitParam(name = "endDate", value = "END_DATE", type = "String"),
                        @ApiImplicitParam(name = "pageNo", value = "PAGE_NO", dataType = "Int", example = "1"),
                        @ApiImplicitParam(name = "pageSize", value = "PAGE_SIZE", dataType = "Int", example = "10")})
    @GetMapping(value = "list-order-by-duration")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(QUERY_PROCESS_INSTANCE_LIST_ORDER_BY_DURATION_ERROR)
    public Result queryProcessInstanceListOrderByDuration(
                @ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                @ApiParam(name = "projectName", value = "PROJECT_NAME", required = true) @PathVariable String projectName,
                @RequestParam(value = "processDefinitionId", required = false, defaultValue = "0") Integer processDefinitionId,
                @RequestParam(value = "executorName", required = false) String executorName,
                @RequestParam(value = "startDate", required = false) String startDate,
                @RequestParam(value = "endDate", required = false) String endDate, @RequestParam("pageNo") Integer pageNo,
                @RequestParam("pageSize") Integer pageSize) {
        logger.info("query process instance list order by duration, login user:{}, project name:{}, define id:{}, executor name:{}, start time:{}, end time:{}, page number:{}, page size:{}",
            loginUser.getUserName(), projectName, processDefinitionId, executorName, startDate, endDate, pageNo,
                pageSize);
        Map<String, Object> result = processInstanceService.queryProcessInstanceListOrderByDuration(loginUser,
            projectName, processDefinitionId, executorName, startDate, endDate, pageNo, pageSize);
        return returnDataListPaging(result);
    }

    @ApiOperation(value = "queryProcessInstanceStatistics", notes = "QUERY_PROCESS_INSTANCE_STATISTICS_NOTES")
    @ApiImplicitParams(
        {@ApiImplicitParam(name = "processDefinitionId", value = "PROCESS_DEFINITION_ID", dataType = "Int",
            defaultValue = "0"), @ApiImplicitParam(name = "executorName", value = "EXECUTOR_NAME", type = "String"),
            @ApiImplicitParam(name = "dates", value = "DATES", type = "String"),
            @ApiImplicitParam(name = "stateType", value = "EXECUTION_STATUS", type = "ExecutionStatus"),
            @ApiImplicitParam(name = "step", value = "STEP", type = "Int")})
    @GetMapping(value = "statistics")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(QUERY_PROCESS_INSTANCE_STATISTICS_ERROR)
    public Result queryProcessInstanceStatistics(
            @ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
            @ApiParam(name = "projectName", value = "PROJECT_NAME", required = true) @PathVariable String projectName,
            @RequestParam(value = "processDefinitionId", required = false, defaultValue = "0") Integer processDefinitionId,
            @RequestParam(value = "executorName", required = false) String executorName,
            @RequestParam(value = "dates") List<String> dates, @RequestParam(value = "stateType") ExecutionStatus stateType,
            @RequestParam(value = "step") int step) {
        logger.info(
            "query process instance statistics, login user:{}, project name:{}, define id:{}, executor name:{}, dates:{}, state:{}",
            loginUser.getUserName(), projectName, processDefinitionId, executorName, dates, stateType);
        Map<String, Object> result = processInstanceService.queryProcessInstanceStatistics(loginUser, projectName,
            processDefinitionId, executorName, dates, stateType.ordinal(), step);
        return returnDataList(result);
    }

}
