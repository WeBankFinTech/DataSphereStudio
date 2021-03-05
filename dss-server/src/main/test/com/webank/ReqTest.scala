package com.webank

import dispatch.Req


object ReqTest extends App{
  private val url: Req = dispatch.url("")
  print(url == url.setMethod("POST"))
}
