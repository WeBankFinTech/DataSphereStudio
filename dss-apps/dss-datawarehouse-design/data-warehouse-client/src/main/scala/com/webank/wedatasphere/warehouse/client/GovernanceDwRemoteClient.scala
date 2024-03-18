package com.webank.wedatasphere.warehouse.client

import org.apache.linkis.httpclient.dws.DWSHttpClient
import org.apache.linkis.httpclient.dws.config.DWSClientConfig
import com.webank.wedatasphere.warehouse.client.action.{ListDwLayerAction, ListDwModifierAction, ListDwStatisticalPeriodAction, ListDwThemeDomainAction}
import com.webank.wedatasphere.warehouse.client.result.{ListLayersResult, ListModifiersResult, ListStatisticalPeriodsResult, ListThemeDomainsResult}

class GovernanceDwRemoteClient(clientConfig: DWSClientConfig) extends AbstractDwRemoteClient with DwRemoteClient {
  protected override val dwsHttpClient = new DWSHttpClient(clientConfig, "Governance-DataWarehouse-Client")

  override def listLayers(action: ListDwLayerAction): ListLayersResult = {
    execute(action).asInstanceOf[ListLayersResult]
  }

  override def listThemeDomains(action: ListDwThemeDomainAction): ListThemeDomainsResult = {
    execute(action).asInstanceOf[ListThemeDomainsResult]
  }

  override def listModifiers(action: ListDwModifierAction): ListModifiersResult = {
    execute(action).asInstanceOf[ListModifiersResult]
  }

  override def listStatisticalPeriods(action: ListDwStatisticalPeriodAction): ListStatisticalPeriodsResult = {
    execute(action).asInstanceOf[ListStatisticalPeriodsResult]
  }
}