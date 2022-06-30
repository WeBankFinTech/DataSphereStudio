package com.webank.wedatasphere.dss.appconn.core.ext;

import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.standard.app.structure.OptionalIntegrationStandard;

/**
 * 这是一个可选的、非强制实现的 AppConn 规范，用于协助第三方 AppConn 提供一些特殊的 Operation 能力，
 * 这些 Operation 与 DSS 的框架逻辑无关，并不是 DSS 框架要求第三方 AppConn 需具备的能力。
 * 这些可选的、非强制的 Operation 能力，通常是提供给 第三方 AppConn 内部的三大规范使用，或是
 * DSS 内置的一些应用工具，在使用这些第三方 AppConn 时，希望第三方 AppConn 提供的能力。
 * <br>
 * 所以，区别于三大规范，该 AppConn 规范的 Operation 不会在类头强制要求 RequestRef 和 ResponseRef 的类型，
 * 内置的应用工具，可以在使用过程中，基于 OptionalOperation 这个顶层基类按需进行次级抽象定义，以及要求
 * 第三方 AppConn 按要求进行继承和实现。
 * @author enjoyyin
 * @date 2022-03-18
 * @since 1.1.0
 */
public interface OptionalAppConn extends AppConn {

    default OptionalIntegrationStandard getOrCreateOptionalStandard() {
        return OptionalIntegrationStandard.getInstance(getAppDesc().getAppName());
    }

}
