package org.apache.dolphinscheduler.dao.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.dolphinscheduler.dao.entity.ProcessInstance;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * @author enjoyyin
 * @date 2022-02-23
 * @since 0.5.0
 */
public interface ProcessInstanceMetricsMapper {

    IPage<ProcessInstance> queryProcessInstanceListOrderByDuration(Page<ProcessInstance> page,
                                                                   @Param("projectId") int projectId, @Param("processDefinitionId") Integer processDefinitionId,
                                                                   @Param("executorId") Integer executorId, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    List<ProcessInstance> queryProcessInstanceListByStartTime(@Param("projectId") int projectId,
                                                              @Param("processDefinitionId") Integer processDefinitionId, @Param("executorId") Integer executorId,
                                                              @Param("startTime") LocalDate start, @Param("endTime") LocalDate end, @Param("state") int state);

}
