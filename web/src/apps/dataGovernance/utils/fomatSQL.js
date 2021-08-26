/**
 *  ""
 *
 *
 */
//  "SELECT \n id, \n name, \n sex, \n city, \n sdate \n from  hive_part_test"
//  "<p>SELECT</p><p>id</p><p>name</p>"

export function fomatSQL(sql) {
  sql = typeof sql === 'string' ? sql : ''
  const arr = sql.replaceAll(' ', '').split('\n');
  const res = arr.map(item => `<p>${item}</p>`)
  return res.join('')
}

console.log(fomatSQL("SELECT \n id, \n name, \n sex, \n city, \n sdate \n from  hive_part_test"))
