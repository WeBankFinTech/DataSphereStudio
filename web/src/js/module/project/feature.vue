<template>
  <div class="project-type">
    <div class="project-type-module">
      <div class="project-type-header">
        <svg t="1573033007700" viewBox="0 0 1025 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="113086" width="20" height="20"><path d="M451.310305 529.689024L50.690586 339.234666a72.444333 72.444333 0 0 1 0-135.09716L451.374302 13.619151a144.888665 144.888665 0 0 1 122.041821 0l400.491726 190.518355a72.444333 72.444333 0 0 1 0 135.033163L573.416123 529.753021c-38.71804 17.98309-83.387778 17.98309-122.041821 0z" fill="#5580EB" p-id="113087"></path><path d="M512.299218 784.460127c-29.118526 0-57.853071-6.655663-84.027747-19.519012l-392.940107-195.190119a54.333249 54.333249 0 1 1 48.18956-96.891095l392.556127 195.190119c22.91084 11.135436 49.597489 11.135436 72.444333 0l392.940107-195.190119a54.333249 54.333249 0 1 1 48.189561 96.891095l-393.324088 195.190119a189.43041 189.43041 0 0 1-84.027746 19.519012z m0 229.620375a189.43041 189.43041 0 0 1-84.027747-19.519012l-392.940107-195.190118a54.333249 54.333249 0 1 1 48.18956-96.891095l392.556127 195.190118c22.846843 11.26343 49.597489 11.26343 72.444333 0l392.940107-195.190118a54.333249 54.333249 0 1 1 48.189561 96.827098l-393.324088 195.190119a189.43041 189.43041 0 0 1-84.027746 19.583008z" fill="#5580EB" opacity=".5" p-id="113088"></path></svg>
        <span class="project-type-header-label">{{$t('message.project.applicationStudio')}}</span>
      </div>
      <div
        class="project-type-more"
        :class="{'en': isEnEnv, 'cn': !isEnEnv}">
      </div>
      <div class="project-type-list">
        <div class="project-type-list-item-wrap">
          <svg t="1572508997540" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="3412" xmlns:xlink="http://www.w3.org/1999/xlink" width="30" height="30">
            <path d="M468.89187555 244.16003161L405.84786173 124.9330568H77.68240987v774.65170172h368.68449976c-9.0617679-19.02971259-16.44063605-38.96560197-21.8776968-59.54876049H133.6064632V303.7087921h782.03056989v197.15817876c7.24941431 6.47269136 15.01664395 12.03920592 21.74824296 19.15916642 12.81592889 13.72210569 23.81950419 28.60929581 34.04635654 44.14375507V244.16003161H468.89187555z m-335.28541235-59.67821433h239.74848791l31.45728 59.5487605H133.6064632v-59.5487605z m614.38786371 536.32720592h111.71865284v59.5487605h-111.71865284v119.22697482h-55.92405333v-119.22697482h-111.71865283v-59.5487605h111.71865283v-119.22697481h55.92405333v119.22697481z m-27.83257283-238.32449579c-138.77450272 0-251.39933235 120.00369778-251.39933235 268.09887606 0 148.09517827 112.49537581 268.22832987 251.26987852 268.22832986 138.90395653 0 251.39933235-120.00369778 251.39933234-268.09887605 0-71.07015111-26.40858075-139.29231803-73.52977383-189.64985678s-111.0713837-78.57847309-177.74010468-78.57847309z m-0.12945383 476.64899161c-107.83503803-0.25890765-195.21637136-93.4656632-195.34582519-208.55011555 0.12945383-115.21390617 87.51078717-208.42066173 195.47527902-208.67956939 52.16989235 0 101.36234667 21.74824297 138.2566874 61.10220642 37.02379457 39.35396347 57.21859161 91.78276347 57.21859161 147.57736297-0.12945383 115.21390617-87.64024098 208.55011555-195.60473284 208.55011555z" fill="#2d8cf0" p-id="3413"></path>
          </svg>
          <div
            class="project-type-list-item"
            style="color:#2d8cf0"
            @click="createProject">
            <p class="project-type-list-item-title">{{$t('message.project.createProject')}}</p>
          </div>
        </div>
      </div>
    </div>
    <div class="project-type-module">
      <div class="project-type-header">
        <svg t="1572509911758" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="9923" xmlns:xlink="http://www.w3.org/1999/xlink" width="18" height="18">
          <path d="M447.7 62H94.1C76.4 62 62 74.6 62 90.1v478.1c0 15.5 14.4 28.1 32.1 28.1h353.6c17.8 0 32.1-12.6 32.1-28.1V90.1c0.1-15.5-14.3-28.1-32.1-28.1z m-32.1 478.1H126.3V118.3h289.3v421.8z" fill="#37B1C9" p-id="9924"></path><path d="M447.7 652.6H94.1c-17.8 0-32.1 12.6-32.1 28.1v253.1c0 15.5 14.4 28.1 32.1 28.1h353.6c17.8 0 32.1-12.6 32.1-28.1v-253c0.1-15.6-14.3-28.2-32.1-28.2z m-32.1 253.2H126.3V708.9h289.3v196.9zM929.9 62H576.3c-17.8 0-32.1 12.6-32.1 28.1v253.1c0 15.5 14.4 28.1 32.1 28.1h353.6c17.8 0 32.1-12.6 32.1-28.1V90.1c0-15.5-14.4-28.1-32.1-28.1z m-32.2 253.1H608.4V118.3h289.3v196.8z" fill="#9BD8E4" p-id="9925"></path><path d="M929.9 427.6H576.3c-17.8 0-32.1 12.6-32.1 28.1v478.1c0 15.5 14.4 28.1 32.1 28.1h353.6c17.8 0 32.1-12.6 32.1-28.1v-478c0-15.6-14.4-28.2-32.1-28.2z m-32.2 478.2H608.4V483.9h289.3v421.9z" fill="#37B1C9" p-id="9926"></path>
        </svg>
        <span class="project-type-header-label">{{$t('message.project.integratedSystem')}}</span>
        <div class="project-type-header-help" @click="linkTo">
          <Icon type="ios-help-circle-outline help"></Icon>
          <span>{{$t('message.project.howToIntegratedsystem')}}</span>
        </div>
      </div>
      <div class="project-type-list">
        <div
          class="project-type-list-item-wrap"
          v-for="item in systemMap"
          :key="item.name"
          @click="goto(item.path)">
          <i class="project-type-list-icon" :class="item.font"
            :style="{'color': item.color, 'font-size': item.iconSize || '30px'}"
            v-if="item.font"></i>
          <img class="project-type-list-img" :src="item.icon" alt=""
            :style="{'width': item.iconSize || '30px', 'height': item.iconSize || '30px'}"
            v-else>
          <div class="project-type-list-item">
            <p class="project-type-list-item-title" :style="{color: item.color}">{{item.name}}</p>
            <p class="project-type-list-item-sub">{{item.subName}}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import storage from '@/js/helper/storage';
import config from './config';
export default {
  data() {
    return {
      systemMap: config.systemMap,
      setting: config.setting,
    }
  },
  computed: {
    isEnEnv() {
      return localStorage.getItem('locale') === 'en';
    }
  },
  methods: {
    goto(type) {
      console.log('--->', type)
      this.gotoCommonIframe(type);
    },
    createProject() {
      this.$emit('add-project');
    },
    linkTo() {
      const newTab = window.open('about:blank');
      setTimeout(() => {
        newTab.location.href = `https://github.com/WeBankFinTech/DataSphereStudio/blob/master/docs/zh_CN/ch4/%E7%AC%AC%E4%B8%89%E6%96%B9%E7%B3%BB%E7%BB%9F%E6%8E%A5%E5%85%A5DSS%E6%8C%87%E5%8D%97.md`;
      })
    }
  }
}
</script>
<style lang="scss" src="./index.scss">
</style>

