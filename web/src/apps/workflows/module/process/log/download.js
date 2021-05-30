/**
 * download file
 */
import $ from 'jquery'

const downloadFile = ($url, $obj) => {
  const param = {
    url: `/dolphinscheduler/${$url}`,
    obj: $obj
  }

  if (!param.url) {
    this.$Message.warning(this.$t('message.scheduler.noUrl'))
    return
  }

  const generatorInput = function (obj) {
    let result = ''
    const keyArr = Object.keys(obj)
    keyArr.forEach(function (key) {
      result += "<input type='hidden' name = '" + key + "' value='" + obj[key] + "'>"
    })
    return result
  }
  $(`<form action="${param.url}" method="get">${generatorInput(param.obj)}</form>`).appendTo('body').submit().remove()
}

export { downloadFile }
