package com.webank.wedatasphere.dss.standard.app.development.ref.impl;

import com.webank.wedatasphere.dss.standard.app.development.ref.*;

/**
 * We can find the only difference of these classes between OnlyDevelopmentRequestRef and ThirdlyRequestRef,
 * is that, OnlyDevelopmentRequestRef is used for those third-part AppConns which has no projects, and
 * ThirdlyRequestRef is used for those third-part AppConns which not only contains projects but also contains
 * development process.
 * <br>
 * So, if your system has no project, please use the classes of OnlyDevelopmentRequestRef, otherwise please
 *  use the classes of ThirdlyRequestRef.
 * @author enjoyyin
 * @date 2022-03-09
 * @since 0.5.0
 */
public interface ThirdlyRequestRef {

    class DSSJobContentRequestRefImpl extends DevelopmentRequestRefImpl<DSSJobContentRequestRefImpl>
            implements DSSJobContentRequestRef<DSSJobContentRequestRefImpl>,
            ProjectRefRequestRef<DSSJobContentRequestRefImpl> {}

    class DSSJobContentWithContextRequestRef extends DevelopmentRequestRefImpl<DSSJobContentWithContextRequestRef>
            implements DSSJobContentRequestRef<DSSJobContentWithContextRequestRef>, DSSContextRequestRef<DSSJobContentWithContextRequestRef>,
            ProjectRefRequestRef<DSSJobContentWithContextRequestRef> {}

    class RefJobContentRequestRefImpl extends DevelopmentRequestRefImpl<RefJobContentRequestRefImpl>
            implements RefJobContentRequestRef<RefJobContentRequestRefImpl>, ProjectRefRequestRef<RefJobContentRequestRefImpl> {}

    class RefJobContentWithContextRequestRef extends DevelopmentRequestRefImpl<RefJobContentWithContextRequestRef>
            implements RefJobContentRequestRef<RefJobContentWithContextRequestRef>, DSSContextRequestRef<RefJobContentWithContextRequestRef>,
            ProjectRefRequestRef<RefJobContentWithContextRequestRef> {}

    class CopyRequestRefImpl extends DevelopmentRequestRefImpl<CopyRequestRefImpl>
            implements CopyRequestRef<CopyRequestRefImpl>, ProjectRefRequestRef<CopyRequestRefImpl> {}

    class CopyWitContextRequestRefImpl extends DevelopmentRequestRefImpl<CopyWitContextRequestRefImpl>
            implements CopyRequestRef<CopyWitContextRequestRefImpl>, ProjectRefRequestRef<CopyWitContextRequestRefImpl>,
            DSSContextRequestRef<CopyWitContextRequestRefImpl> {}

    class ImportRequestRefImpl extends DevelopmentRequestRefImpl<ImportRequestRefImpl>
            implements ImportRequestRef<ImportRequestRefImpl>, ProjectRefRequestRef<ImportRequestRefImpl> {}

    class ImportWithStreamRequestRefImpl extends DevelopmentRequestRefImpl<ImportWithStreamRequestRefImpl>
            implements ImportRequestRef<ImportWithStreamRequestRefImpl>, ProjectRefRequestRef<ImportWithStreamRequestRefImpl> {
        @Override
        public boolean isLinkisBMLResources() {
            return false;
        }
    }

    class ImportWitContextRequestRefImpl extends DevelopmentRequestRefImpl<ImportWitContextRequestRefImpl>
            implements ImportRequestRef<ImportWitContextRequestRefImpl>, ProjectRefRequestRef<ImportWitContextRequestRefImpl>,
            DSSContextRequestRef<ImportWitContextRequestRefImpl>{}

    class ImportWitContextAndStreamRequestRefImpl extends DevelopmentRequestRefImpl<ImportWitContextAndStreamRequestRefImpl>
            implements ImportRequestRef<ImportWitContextAndStreamRequestRefImpl>, ProjectRefRequestRef<ImportWitContextAndStreamRequestRefImpl>,
            DSSContextRequestRef<ImportWitContextAndStreamRequestRefImpl>{
        @Override
        public boolean isLinkisBMLResources() {
            return false;
        }
    }

    class UpdateRequestRefImpl extends DevelopmentRequestRefImpl<UpdateRequestRefImpl>
            implements UpdateRequestRef<UpdateRequestRefImpl>, ProjectRefRequestRef<UpdateRequestRefImpl> {}

    class UpdateWitContextRequestRefImpl extends DevelopmentRequestRefImpl<UpdateWitContextRequestRefImpl>
            implements UpdateRequestRef<UpdateWitContextRequestRefImpl>, ProjectRefRequestRef<UpdateWitContextRequestRefImpl>,
            DSSContextRequestRef<UpdateWitContextRequestRefImpl>{}

    class QueryJumpUrlRequestRefImpl extends DevelopmentRequestRefImpl<QueryJumpUrlRequestRefImpl>
            implements QueryJumpUrlRequestRef<QueryJumpUrlRequestRefImpl>, ProjectRefRequestRef<QueryJumpUrlRequestRefImpl> {}

    class QueryJumpUrlWithContextRequestRefImpl extends DevelopmentRequestRefImpl<QueryJumpUrlWithContextRequestRefImpl>
            implements QueryJumpUrlRequestRef<QueryJumpUrlWithContextRequestRefImpl>, ProjectRefRequestRef<QueryJumpUrlWithContextRequestRefImpl>,
            DSSContextRequestRef<QueryJumpUrlWithContextRequestRefImpl> {}
}
