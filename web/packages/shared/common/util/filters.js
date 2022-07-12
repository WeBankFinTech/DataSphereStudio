import moment from 'moment'

/**
 *  转换时间戳到字符串
 *  @param (Number)
 *  @return (String)
 */
function formatDate (value) {
  if (!value) return ''
  return moment(value).format("YYYY-MM-DD HH:mm")
}

export default {
  formatDate
}
