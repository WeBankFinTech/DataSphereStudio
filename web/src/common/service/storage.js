

export const prefix = '';

export function set(key, value){
  if(!(window.localStorage && window.localStorage.setItem)){
    return false;
  }
  try {
    window.localStorage.setItem(prefix + key, JSON.stringify({
      v: value,
      t: +new Date()
    }));
    return true;
  }catch(e){
    console.log(e);
    return false;
  }
}

export function get(key, validTime){
  if(!(window.localStorage && window.localStorage.getItem)){
    return null;
  }
  try {
    let data = (JSON.parse(window.localStorage.getItem(prefix + key) || '{}')),
      t = data.t;
    if(validTime && (+new Date() > t + validTime)){
      window.localStorage.removeItem(prefix + key);
      return null;
    }
    return data.v;
  }catch(e){
    return null;
  }
}

export function remove(key){
  if(!(window.localStorage && window.localStorage.removeItem)){
    return null;
  }
  try {
    window.localStorage.removeItem(prefix + key);
  }catch(e){
    return null;
  }
}

export function clear(){
  if(!(window.localStorage && window.localStorage.clear)){
    return null;
  }
  window.localStorage.clear();
}

export function getCookie(objName){
  var arrStr = document.cookie.split("; ");
  for(var i = 0;i < arrStr.length;i ++){
    var temp = arrStr[i].split("=");
    if(temp[0] == objName) return unescape(temp[1]);
  }
}

export default {
  prefix, get, set, clear , getCookie
}
