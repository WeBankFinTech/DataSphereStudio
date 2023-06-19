/*
 * Copyright 2019 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

package com.webank.wedatasphere.dss.appconn.sendemail.emailcontent.parser

import java.awt.image.BufferedImage
import java.io.{ByteArrayInputStream, ByteArrayOutputStream, InputStream}
import java.util
import java.util.{Base64, Iterator, UUID}

import com.webank.wedatasphere.dss.appconn.sendemail.email.domain.{AbstractEmail, MultiContentEmail, PdfAttachment, PngAttachment}
import com.webank.wedatasphere.dss.appconn.sendemail.emailcontent.domain.PictureEmailContent
import org.apache.linkis.common.conf.Configuration
import javax.imageio.{ImageIO, ImageReader}
import org.apache.commons.codec.binary.Base64OutputStream
import com.webank.wedatasphere.dss.appconn.sendemail.conf.SendEmailAppConnConfiguration._
import com.webank.wedatasphere.dss.appconn.sendemail.exception.EmailSendFailedException
import javax.imageio.stream.ImageInputStream
import org.apache.commons.io.IOUtils
import org.apache.linkis.common.utils.Utils

object PictureEmailContentParser extends AbstractEmailContentParser[PictureEmailContent] {

  override protected def parseEmailContent(emailContent: PictureEmailContent,
                                           multiContentEmail: MultiContentEmail): Unit = {
    getFirstLineRecord(emailContent).foreach { imageStr =>
      emailContent.getFileType match {
        case "checkData" =>
          //对于邮件校验数据不进行处理
        case "pdf" =>
          val pdfUUID: String = UUID.randomUUID.toString
          val pdfName = pdfUUID + ".pdf"
          val decoder = Base64.getDecoder
          val byteArr = decoder.decode(imageStr)
          multiContentEmail.addAttachment(new PdfAttachment(pdfName, Base64.getEncoder.encodeToString(byteArr)))
        case _ =>
          var inputStream: ByteArrayInputStream = null
          Utils.tryFinally({
            val decoder = Base64.getDecoder
            val byteArr = decoder.decode(imageStr)
            if (CHECK_EMAIL_IMAGE_SWITCH.getValue) {
              checkImageSize(byteArr)
            }
            inputStream = new ByteArrayInputStream(byteArr)
            val image = ImageIO.read(inputStream)
            val contents = generateImage(image, multiContentEmail)
            emailContent.setContent(contents)
          })(IOUtils.closeQuietly(inputStream))

      }
    }
  }

  protected def checkImageSize(byteArr: Array[Byte]): Unit = {
    var reader: ImageReader = null
    val inputStream: InputStream = new ByteArrayInputStream(byteArr)
    var imageInputStream: ImageInputStream = null
    Utils.tryFinally({
      imageInputStream = ImageIO.createImageInputStream(inputStream)
      val imageReaders: util.Iterator[ImageReader] = ImageIO.getImageReaders(imageInputStream)
      if (!imageReaders.hasNext) throw new EmailSendFailedException(80002,"Unsupported image format!")
      reader = imageReaders.next
      reader.setInput(imageInputStream)
      val height = reader.getHeight(0)
      val width = reader.getWidth(0)
      if ((height * width) > EMAIL_IMAGE_MAXSIZE.getValue) {
        throw new EmailSendFailedException(80002, "too large picture size :" + (height * width) + ", expect max picture size is :" + EMAIL_IMAGE_MAXSIZE.getValue)
      }
    })({
      if (reader != null) reader.dispose()
      IOUtils.closeQuietly(imageInputStream)
      IOUtils.closeQuietly(inputStream)
    })

  }

  protected def generateImage(bufferedImage: BufferedImage, email: AbstractEmail): Array[String] = {
    val imageUUID: String = UUID.randomUUID.toString
    val width: Int = bufferedImage.getWidth
    val height: Int = bufferedImage.getHeight
    // 只支持修改visualis图片大小，后续如果有新增其他类型的邮件需要修改图片大小，需要在if中加上该邮件类型
    val imagesCuts = if (height > EMAIL_IMAGE_HEIGHT.getValue) {
      val numOfCut = Math.ceil(height.toDouble / EMAIL_IMAGE_HEIGHT.getValue).toInt
      val realHeight = height / numOfCut
      (0 until numOfCut).map(i => bufferedImage.getSubimage(0, i * realHeight, width, realHeight)).toArray
    } else Array(bufferedImage)
    imagesCuts.indices.map { index =>
      val image = imagesCuts(index)
      val imageName = index + "_" + imageUUID + ".png"
      val os = new ByteArrayOutputStream
      val b64Stream = new Base64OutputStream(os)
      ImageIO.write(image, "png", b64Stream)
      val b64 = os.toString(Configuration.BDP_ENCODING.getValue)
      email.addAttachment(new PngAttachment(imageName, b64))

      var iHeight = image.getHeight
      var iWidth = image.getWidth
      if (iWidth > EMAIL_IMAGE_WIDTH.getValue) {
        iHeight = ((EMAIL_IMAGE_WIDTH.getValue.toDouble / iWidth.toDouble) * iHeight.toDouble).toInt
        iWidth = EMAIL_IMAGE_WIDTH.getValue
      }
      s"""<img width="${iWidth}" height="${iHeight}" src="cid:$imageName"></img>"""
    }.toArray
  }

}
