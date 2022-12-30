import moment from "moment";
/**
 * @description  转换时间戳到字符串
 *  @param value {Number} 时间戳
 *  @return {String}
 */
export default function(value) {
  if (!value) return "";
  return moment(value).format("YYYY-MM-DD HH:mm");
}
