import moment from 'moment'

/**
 *  转换时间戳到字符串
 *  @param (Number)
 *  @return (String)
 */
function formatDate (value, format = 'YYYY-MM-DD HH:mm') {
  if (!value) return ''
  return moment(value).format(format)
}

export default {
  formatDate
}
