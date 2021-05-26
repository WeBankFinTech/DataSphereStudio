import Vue from 'vue'

//过滤空值
Vue.filter('filterNull', value => {
  if (value === null || value === '') {
    return '-'
  } else {
    return value
  }
})
