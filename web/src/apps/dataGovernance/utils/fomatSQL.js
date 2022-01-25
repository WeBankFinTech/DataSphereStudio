/**
 *  转换 后端SQL语句 成 html文本
 *  @param (String)
 *  @return (String)
 */
export function fomatSqlForShow(sql) {
  if (typeof sql !== "string") return "";
  const arr = sql.split("@$");
  const res = arr.map((i, idx) => `<p><span>${idx + 1}</span>${i}</p>`);
  return res.join("");
}

/**
 *
 * @param {String} sql
 * @return {String}
 */
export function fomatSqlForCopy(sql) {
  if (typeof sql !== "string") return "";
  return sql.replaceAll("@$", "\r\n");
  // const arr = sql.replaceAll(" ", "").split("@$");
  // const res = arr.map(i => `${i}\r\n`);
  // return res.join("");
}
