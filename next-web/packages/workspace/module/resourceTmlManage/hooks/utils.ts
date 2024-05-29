export const utils = () => {
  // 值为数组、对象前的情况未处理
  const paramsObjectToString = (params: object) => {
    const strArray: string[] = [];
    for (const [key, value] of Object.entries(params)) {
      // console.log(`Key: ${key}, Value: ${value}`);
      if (value) {
        strArray.push(key + '=' + value);
      }
    }
    const strParams = strArray.join('&');
    return strParams;
  };

  // 时间戳转为yy-MM-dd hh:mm:ss
  const convertTimeStampToYMDHMS = (time: number | string) => {
    let timeStr = new Date(Number(time)).toLocaleString('zh', {
      hour12: false,
    });
    timeStr = timeStr.replace(/\//g, '-');
    return timeStr;
  };
  return {
    paramsObjectToString,
    convertTimeStampToYMDHMS,
  };
};
