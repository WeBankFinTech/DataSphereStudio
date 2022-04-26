/**
 * download file
 */
import api from '@dataspherestudio/shared/common/service/api';

const downloadFile = ($url, $obj, $fileName) => {
  const downloadBlob = (data, fileNameS = 'json') => {
    if (!data) {
      return
    }
    const blob = new Blob([data])
    const fileName = `${fileNameS}`
    if ('download' in document.createElement('a')) { // 不是IE浏览器
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.style.display = 'none'
      link.href = url
      link.setAttribute('download', fileName)
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link) // 下载完成移除元素
      window.URL.revokeObjectURL(url) // 释放掉blob对象
    } else { // IE 10+
      window.navigator.msSaveBlob(blob, fileName)
    }
  }
  api.fetch($url, $obj, {
    method: 'get',
    responseType: 'blob'
  }).then((res) => {
    downloadBlob(res.data, $fileName || `${Date.now()}.log`)
  })
}

export { downloadFile }
