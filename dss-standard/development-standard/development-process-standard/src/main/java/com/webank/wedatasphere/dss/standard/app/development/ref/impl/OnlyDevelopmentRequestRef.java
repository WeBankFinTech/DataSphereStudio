package com.webank.wedatasphere.dss.standard.app.development.ref.impl;

import com.webank.wedatasphere.dss.standard.app.development.ref.*;

/**
 * We can find the only difference of these classes between OnlyDevelopmentRequestRef and ThirdlyRequestRef,
 * is that, OnlyDevelopmentRequestRef is used for those third-part AppConns which has no projects, and
 * ThirdlyRequestRef is used for those third-part AppConns which not only contains projects but also contains
 * development process.
 * So, if your system has no project, please use the classes of OnlyDevelopmentRequestRef, otherwise please
 *  use the classes of ThirdlyRequestRef.
 * @author enjoyyin
 * @date 2022-03-09
 * @since 0.5.0
 */
public interface OnlyDevelopmentRequestRef {

    class DSSJobContentRequestRefImpl extends DevelopmentRequestRefImpl<DSSJobContentRequestRefImpl>
            implements DSSJobContentRequestRef<DSSJobContentRequestRefImpl> {}

    class RefJobContentRequestRefImpl extends DevelopmentRequestRefImpl<RefJobContentRequestRefImpl>
            implements RefJobContentRequestRef<RefJobContentRequestRefImpl> {}

    class DSSJobContentWithContextRequestRef extends DevelopmentRequestRefImpl<DSSJobContentWithContextRequestRef>
            implements DSSJobContentRequestRef<DSSJobContentWithContextRequestRef>, DSSContextRequestRef<DSSJobContentWithContextRequestRef> {}

    class RefJobContentWithContextRequestRef extends DevelopmentRequestRefImpl<RefJobContentWithContextRequestRef>
            implements RefJobContentRequestRef<RefJobContentWithContextRequestRef>, DSSContextRequestRef<RefJobContentWithContextRequestRef> {}

    class CopyRequestRefImpl extends DevelopmentRequestRefImpl<ThirdlyRequestRef.CopyRequestRefImpl>
            implements CopyRequestRef<ThirdlyRequestRef.CopyRequestRefImpl> {}

    class CopyWitContextRequestRefImpl extends DevelopmentRequestRefImpl<ThirdlyRequestRef.CopyWitContextRequestRefImpl>
            implements CopyRequestRef<ThirdlyRequestRef.CopyWitContextRequestRefImpl>,
            DSSContextRequestRef<ThirdlyRequestRef.CopyWitContextRequestRefImpl> {}

    class ImportRequestRefImpl extends DevelopmentRequestRefImpl<ThirdlyRequestRef.ImportRequestRefImpl>
            implements ImportRequestRef<ThirdlyRequestRef.ImportRequestRefImpl> {}

    class ImportWitContextRequestRefImpl extends DevelopmentRequestRefImpl<ThirdlyRequestRef.ImportWitContextRequestRefImpl>
            implements ImportRequestRef<ThirdlyRequestRef.ImportWitContextRequestRefImpl>,
            DSSContextRequestRef<ThirdlyRequestRef.ImportWitContextRequestRefImpl>{}

    class UpdateRequestRefImpl extends DevelopmentRequestRefImpl<UpdateRequestRefImpl>
            implements UpdateRequestRef<UpdateRequestRefImpl> {}

    class UpdateWitContextRequestRefImpl extends DevelopmentRequestRefImpl<UpdateWitContextRequestRefImpl>
            implements UpdateRequestRef<UpdateWitContextRequestRefImpl>, DSSContextRequestRef<UpdateWitContextRequestRefImpl>{}
}
