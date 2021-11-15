package com.webank.wedatasphere.warehouse

import com.webank.wedatasphere.dss.data.governance.impl.LinkisDataAssetsRemoteClient
import com.webank.wedatasphere.dss.datamodel.center.client.impl.LinkisDatamodelCenterRemoteClient
import com.webank.wedatasphere.linkis.datasource.client.impl.{LinkisDataSourceRemoteClient, LinkisMetadataSourceRemoteClient}
import com.webank.wedatasphere.linkis.httpclient.dws.authentication.TokenAuthenticationStrategy
import com.webank.wedatasphere.linkis.httpclient.dws.config.{DWSClientConfig, DWSClientConfigBuilder}
import com.webank.wedatasphere.warehouse.client.DwDataSourceConfiguration

import java.lang
import java.util.concurrent.TimeUnit

object LinkisRemoteClientHolder {
  //Linkis Datasource Client Config
  val serverUrl: String = DwDataSourceConfiguration.SERVER_URL.getValue
  val connectionTimeout: lang.Long = DwDataSourceConfiguration.CONNECTION_TIMEOUT.getValue
  val discoveryEnabled: lang.Boolean = DwDataSourceConfiguration.DISCOVERY_ENABLED.getValue
  val discoveryFrequencyPeriod: lang.Long = DwDataSourceConfiguration.DISCOVERY_FREQUENCY_PERIOD.getValue
  val loadbalancerEnabled: lang.Boolean = DwDataSourceConfiguration.LOAD_BALANCER_ENABLED.getValue
  val maxConnectionSize: Integer = DwDataSourceConfiguration.MAX_CONNECTION_SIZE.getValue
  val retryEnabled: lang.Boolean = DwDataSourceConfiguration.RETRY_ENABLED.getValue
  val readTimeout: lang.Long = DwDataSourceConfiguration.READ_TIMEOUT.getValue
  val authTokenKey: String = DwDataSourceConfiguration.AUTHTOKEN_KEY.getValue
  val authTokenValue: String = DwDataSourceConfiguration.AUTHTOKEN_VALUE.getValue
  val dwsVersion: String = DwDataSourceConfiguration.DWS_VERSION.getValue

  val clientConfig: DWSClientConfig = DWSClientConfigBuilder.newBuilder()
    .addServerUrl(serverUrl)
    .connectionTimeout(connectionTimeout)
    .discoveryEnabled(discoveryEnabled)
    .discoveryFrequency(discoveryFrequencyPeriod, TimeUnit.MINUTES)
    .loadbalancerEnabled(loadbalancerEnabled)
    .maxConnectionSize(maxConnectionSize)
    .retryEnabled(retryEnabled)
    .readTimeout(readTimeout)
    .setAuthenticationStrategy(new TokenAuthenticationStrategy())
    .setAuthTokenKey(authTokenKey)
    .setAuthTokenValue(authTokenValue)
    .setDWSVersion(dwsVersion)
    .build()

  val dataSourceClient = new LinkisDataSourceRemoteClient(clientConfig)

  val metadataSourceRemoteClient = new LinkisMetadataSourceRemoteClient(clientConfig)

  var dataModelRemoteClient = new LinkisDatamodelCenterRemoteClient(clientConfig)

  var dataAssetsRemoteClient = new LinkisDataAssetsRemoteClient(clientConfig)

  def getLinkisDataSourceRemoteClient: LinkisDataSourceRemoteClient = {
    dataSourceClient
  }

  def getDataModelRemoteClient: LinkisDatamodelCenterRemoteClient = {
    dataModelRemoteClient
  }

  def getDataAssetsRemoteClient: LinkisDataAssetsRemoteClient = {
    dataAssetsRemoteClient
  }

  def getMetadataSourceRemoteClient: LinkisMetadataSourceRemoteClient = {
    metadataSourceRemoteClient
  }
}
