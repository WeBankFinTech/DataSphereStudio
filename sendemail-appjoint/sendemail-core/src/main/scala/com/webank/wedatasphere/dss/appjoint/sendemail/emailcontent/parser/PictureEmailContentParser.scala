/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.appjoint.sendemail.emailcontent.parser

import java.awt.image.BufferedImage
import java.io.{ByteArrayInputStream, ByteArrayOutputStream, File}
import java.util.{Base64, UUID}

import javax.imageio.ImageIO
import com.webank.wedatasphere.dss.appjoint.sendemail.conf.SendEmailAppJointConfiguration.EMAIL_IMAGE_HEIGHT
import com.webank.wedatasphere.dss.appjoint.sendemail.email.{AbstractEmail, MultiContentEmail, PngAttachment}
import com.webank.wedatasphere.dss.appjoint.sendemail.emailcontent.PictureEmailContent
import com.webank.wedatasphere.linkis.common.conf.Configuration
import org.apache.commons.codec.binary.Base64OutputStream
import org.springframework.stereotype.Component

/**
  * Created by shanhuang on 2019/10/12.
  */

object PictureEmailContentParser extends AbstractEmailContentParser[PictureEmailContent] {

  override protected def parseEmailContent(emailContent: PictureEmailContent,
                                           multiContentEmail: MultiContentEmail): Unit = {
    getFirstLineRecord(emailContent).foreach { imageStr =>
      val decoder = Base64.getDecoder
      val byteArr = decoder.decode(imageStr)
      val inputStream = new ByteArrayInputStream(byteArr)
      val image = ImageIO.read(inputStream)
      val contents = generateImage(image, multiContentEmail)
      emailContent.setContent(contents)
//      val image = ImageIO.read(new ByteArrayInputStream(imageStr.getBytes(Configuration.BDP_ENCODING.getValue)))
//      val contents = generateImage(image, multiContentEmail)
//      emailContent.setContent(contents)
    }
  }

  protected def generateImage(bufferedImage: BufferedImage, email: AbstractEmail): Array[String] = {
    val imageUUID = UUID.randomUUID.toString
    val width = bufferedImage.getWidth
    val height = bufferedImage.getHeight
    val imagesCuts = if (height > EMAIL_IMAGE_HEIGHT.getValue) {
      val numOfCut = Math.ceil(height.toDouble / EMAIL_IMAGE_HEIGHT.getValue).toInt
      val realHeight = height / numOfCut
      (0 until numOfCut).map(i => bufferedImage.getSubimage(0, i * realHeight, width, realHeight)).toArray
    } else Array(bufferedImage)
    imagesCuts.indices.map { index =>
      val image = imagesCuts(index)
      val imageName = index + "_" + imageUUID
      val os = new ByteArrayOutputStream
      val b64Stream = new Base64OutputStream(os)
      ImageIO.write(image, "png", b64Stream)
      val b64 = os.toString(Configuration.BDP_ENCODING.getValue)
      email.addAttachment(new PngAttachment(imageName, b64))
      s"""<img style="width:${image.getWidth}px; height:${image.getHeight}px;" src="cid:$imageName"></img>"""
    }.toArray
  }

}
