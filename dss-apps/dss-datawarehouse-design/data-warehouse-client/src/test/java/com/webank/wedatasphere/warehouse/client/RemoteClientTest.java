package com.webank.wedatasphere.warehouse.client;

import org.apache.linkis.httpclient.dws.authentication.StaticAuthenticationStrategy;
import org.apache.linkis.httpclient.dws.config.DWSClientConfig;
import org.apache.linkis.httpclient.dws.config.DWSClientConfigBuilder;
import com.webank.wedatasphere.warehouse.client.action.ListDwLayerAction;
import com.webank.wedatasphere.warehouse.client.action.ListDwModifierAction;
import com.webank.wedatasphere.warehouse.client.action.ListDwStatisticalPeriodAction;
import com.webank.wedatasphere.warehouse.client.action.ListDwThemeDomainAction;
import com.webank.wedatasphere.warehouse.client.result.ListLayersResult;
import com.webank.wedatasphere.warehouse.client.result.ListModifiersResult;
import com.webank.wedatasphere.warehouse.client.result.ListStatisticalPeriodsResult;
import com.webank.wedatasphere.warehouse.client.result.ListThemeDomainsResult;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class RemoteClientTest {

    public static void main(String[] args) {
        DWSClientConfig clientConfig = ((DWSClientConfigBuilder) (DWSClientConfigBuilder.newBuilder().addServerUrl("http://dss.shineweng.com:8088")
                .connectionTimeout(30000).discoveryEnabled(true)
                .discoveryFrequency(1, TimeUnit.MINUTES)
                .loadbalancerEnabled(true).maxConnectionSize(5)
                .retryEnabled(false).readTimeout(30000)
                .setAuthenticationStrategy(new StaticAuthenticationStrategy()).setAuthTokenKey("hdfs")
                .setAuthTokenValue("hdfs"))).setDWSVersion("v1").build();
        GovernanceDwRemoteClient governanceDwRemoteClient = new GovernanceDwRemoteClient(clientConfig);

        ListDwStatisticalPeriodAction action = new ListDwStatisticalPeriodAction.Builder().setLayer(null).setTheme(null).setUser("hdfs").setIsAvailable(false).build();
        ListStatisticalPeriodsResult listStatisticalPeriodsResult = governanceDwRemoteClient.listStatisticalPeriods(action);
        System.out.println(listStatisticalPeriodsResult.getAll().size());

        ListDwLayerAction listDwLayerAction = new ListDwLayerAction.Builder().setUser("hdfs").setIsAvailable(false).build();
        String url = listDwLayerAction.getURL();
        System.out.println(url);
        ListLayersResult listLayersResult = governanceDwRemoteClient.listLayers(listDwLayerAction);
        System.out.println(listLayersResult.getAll().size());

        ListDwThemeDomainAction listDwThemeDomainAction = new ListDwThemeDomainAction.Builder().setUser("hdfs").setIsAvailable(false).build();
        ListThemeDomainsResult listThemeDomainsResult = governanceDwRemoteClient.listThemeDomains(listDwThemeDomainAction);
        System.out.println(listThemeDomainsResult.getList().size());

        ListDwModifierAction listDwModifierAction = new ListDwModifierAction.Builder().setUser("hdfs").setIsAvailable(true).build();
        ListModifiersResult listModifiersResult = governanceDwRemoteClient.listModifiers(listDwModifierAction);
        System.out.println(listModifiersResult.getAll().size());

    }


}
