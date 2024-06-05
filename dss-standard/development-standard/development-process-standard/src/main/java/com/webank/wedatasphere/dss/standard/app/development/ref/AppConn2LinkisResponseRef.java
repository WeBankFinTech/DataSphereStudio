package com.webank.wedatasphere.dss.standard.app.development.ref;

import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRefBuilder;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRefImpl;

import java.util.Map;

public interface AppConn2LinkisResponseRef extends ResponseRef {

    /**
     * 返回可被 Linkis 某个引擎执行的代码
     * @return 可被 Linkis 某个引擎执行的代码
     */
    String getCode();

    /**
     * Linkis Job 的 params，符合 Linkis 的 params 规范，如下：<br/>
     * {
     *   "configuration": {
     *     "special": {
     *         "k1": "v1"
     *     },
     *     "runtime": {
     *       "k2": "v2"
     *     },
     *     "startup": {
     *         "k3": "v2"
     *     }
     *   },
     *   "variable": {
     *       "runDate": "2023-08-10"
     *   }
     * }
     * @return Linkis Job 的 params
     */
    Map<String, Object> getParams();

    /**
     * 希望将 {@code getCode()} 返回的代码提交给哪个 Linkis 引擎
     * @return Linkis 引擎类型
     */
    String getEngineType();

    /**
     * 希望将 {@code getCode()} 返回的代码提交给哪个 Linkis 引擎的哪个代码类型
     * @return 代码类型
     */
    String getRunType();

    static AppConn2LinkisResponseRefBuilder newBuilder() {
        return new AppConn2LinkisResponseRefBuilder();
    }

    class AppConn2LinkisResponseRefBuilder
            extends ResponseRefBuilder.ExternalResponseRefBuilder<AppConn2LinkisResponseRefBuilder, AppConn2LinkisResponseRef> {

        private String code;
        private Map<String, Object> params;
        private String engineType;
        private String runType;

        public AppConn2LinkisResponseRefBuilder setCode(String code) {
            this.code = code;
            return this;
        }

        public AppConn2LinkisResponseRefBuilder setParams(Map<String, Object> params) {
            this.params = params;
            return this;
        }

        public AppConn2LinkisResponseRefBuilder setEngineType(String engineType) {
            this.engineType = engineType;
            return this;
        }

        public AppConn2LinkisResponseRefBuilder setRunType(String runType) {
            this.runType = runType;
            return this;
        }

        class AppConn2LinkisResponseRefImpl extends ResponseRefImpl implements AppConn2LinkisResponseRef{
            public AppConn2LinkisResponseRefImpl() {
                super(AppConn2LinkisResponseRefBuilder.this.responseBody, AppConn2LinkisResponseRefBuilder.this.status,
                        AppConn2LinkisResponseRefBuilder.this.errorMsg, AppConn2LinkisResponseRefBuilder.this.responseMap);
            }

            @Override
            public String getCode() {
                return code;
            }

            @Override
            public Map<String, Object> getParams() {
                return params;
            }

            @Override
            public String getEngineType() {
                return engineType;
            }

            @Override
            public String getRunType() {
                return runType;
            }
        }

        @Override
        protected AppConn2LinkisResponseRef createResponseRef() {
            return new AppConn2LinkisResponseRefImpl();
        }
    }

}
