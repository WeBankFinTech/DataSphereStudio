package com.webank.wedatasphere.dss.standard.app.structure.project.ref;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webank.wedatasphere.dss.common.entity.project.DSSProject;
import com.webank.wedatasphere.dss.standard.app.structure.StructureRequestRef;
import com.webank.wedatasphere.dss.standard.app.structure.StructureRequestRefImpl;

import java.util.Collections;
import java.util.List;

/**
 * @author enjoyyin
 * @date 2022-03-13
 * @since 0.5.0
 */
public interface DSSProjectContentRequestRef<R extends DSSProjectContentRequestRef<R>>
        extends StructureRequestRef<R> {

    default DSSProject getDSSProject() {
        return (DSSProject) this.getParameter("dssProject");
    }

    default R setDSSProject(DSSProject dssProject) {
        setParameter("dssProject", dssProject);
        return (R) this;
    }

    /**
     * DSS 工程的全量最新权限信息，包含了 DSS 工程所有的最新权限信息
     * @return DSSProjectPrivilege
     */
    default DSSProjectPrivilege getDSSProjectPrivilege() {
        return (DSSProjectPrivilege) this.getParameter("dssProjectPrivilege");
    }

    default R setDSSProjectPrivilege(DSSProjectPrivilege dssProjectPrivilege) {
        setParameter("dssProjectPrivilege", dssProjectPrivilege);
        return (R) this;
    }

    /**
     * DSS 工程的全量最新权数据源列表，包含了 DSS 工程所有的数据源
     * @return DSSProjectPrivilege
     */
    default List<DSSProjectDataSource> getDSSProjectDataSources() {
        String json = (String) this.getParameter("dssProjectDataSources");
        if(json != null) {
          return   new Gson().fromJson(json, new TypeToken<List<DSSProjectDataSource>>(){}.getType());
        }
        return Collections.emptyList();
    }


    default R setDSSProjectDataSources(List<DSSProjectDataSource> dssProjectDataSources) {
        //为了让第三方组件可以不用升级dss依赖包，转成json string再存储
        if(dssProjectDataSources != null) {
            setParameter("dssProjectDataSources", new Gson().toJson(dssProjectDataSources));
        }
        return (R) this;
    }

    class DSSProjectContentRequestRefImpl
            extends StructureRequestRefImpl<DSSProjectContentRequestRefImpl>
            implements DSSProjectContentRequestRef<DSSProjectContentRequestRefImpl> {
    }
    
}
