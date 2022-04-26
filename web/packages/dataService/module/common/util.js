/*eslint-disable */
function dateFormat(date, suffix = '00:00:00') {
  const dt = date ? date : new Date();
  const format = [
    dt.getFullYear(), dt.getMonth() + 1, dt.getDate()
  ].join('-').replace(/(?=\b\d\b)/g, '0'); // 正则补零
  return `${format} ${suffix}`;
}

export default {
  dateFormat
}