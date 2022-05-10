package com.webank.wedatasphere.dss.standard.app.structure.role.ref;

import com.webank.wedatasphere.dss.standard.app.structure.StructureRequestRef;
import com.webank.wedatasphere.dss.standard.app.structure.StructureRequestRefImpl;

/**
 * RoleRequestRef 为 RoleOperation 的 RequestRef 基类。
 * 主要提供了各个 RoleRequestRef 子接口的实现类以供使用。
 * @author enjoyyin
 * @since 1.1.0
 */
public interface RoleRequestRef<R extends RoleRequestRef<R>> extends StructureRequestRef<R> {

    class DSSRoleContentRequestRefImpl extends StructureRequestRefImpl<DSSRoleContentRequestRefImpl>
        implements DSSRoleContentRequestRef<DSSRoleContentRequestRefImpl>{}

    class RefRoleContentRequestRefImpl extends StructureRequestRefImpl<RefRoleContentRequestRefImpl>
        implements RefRoleContentRequestRef<RefRoleContentRequestRefImpl> {}

    class RoleUpdateRequestRefImpl extends StructureRequestRefImpl<RoleUpdateRequestRefImpl>
        implements RoleUpdateRequestRef<RoleUpdateRequestRefImpl> {}

}
