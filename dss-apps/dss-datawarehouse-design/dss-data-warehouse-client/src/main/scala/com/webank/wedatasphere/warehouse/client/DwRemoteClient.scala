package com.webank.wedatasphere.warehouse.client

import com.webank.wedatasphere.warehouse.client.action.{ListDwLayerAction, ListDwModifierAction, ListDwStatisticalPeriodAction, ListDwThemeDomainAction}
import com.webank.wedatasphere.warehouse.client.result.{ListLayersResult, ListModifiersResult, ListStatisticalPeriodsResult, ListThemeDomainsResult}

trait DwRemoteClient extends RemoteClient {
  def listLayers(action : ListDwLayerAction) : ListLayersResult

  def listThemeDomains(action : ListDwThemeDomainAction) : ListThemeDomainsResult

  def listModifiers(action : ListDwModifierAction) : ListModifiersResult

  def listStatisticalPeriods(action : ListDwStatisticalPeriodAction) : ListStatisticalPeriodsResult
}
