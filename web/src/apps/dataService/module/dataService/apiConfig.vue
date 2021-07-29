<template>
  <div>
    <div class="page-fc-header">
      <p>数据服务</p>
      <div class="divider">/</div>
      <p class="header-subTitle">服务开发</p>
    </div>
    <div class="tabWrap">
      <template v-for="(work, index) in tabDatas">
        <div
          :key="index"
          :class="{ active: work.isActive }"
          class="tab-item"
          ref="work_item"
        >
          <webTab
            :index="index"
            :work="work"
            @on-choose="onChooseWork"
            @on-remove="removeWork"
          />
        </div>
      </template>
    </div>

    <params-config :apiData="choosedData" @showApiForm="showApiModel"/>
  </div>
</template>
<script>
import webTab from "@/apps/workflows/module/common/tabList/tabs.vue";
import paramsConfig from "./paramsConfig.vue";
// import api from "@/common/service/api";
export default {
  components: {
    webTab,
    paramsConfig
  },
  props: {
    tabDatas: {
      type: Array,
      default: () => []
    },
    showApiForm: {
      type: Function
    }
  },
  data() {
    return {
      // apiItems: [
      //   { name: "sdfsaf", id: 1 },
      //   { name: "fdj89", id: 3 }
      // ],
      //apiItems: this.tabDatas,
      toolItems: [
        {
          name: "属性",
          iconSrc: require("../../assets/images/property.svg"),
          type: "property"
        },
        {
          name: "版本",
          iconSrc: require("../../assets/images/version.svg"),
          type: "version"
        },
        {
          name: "保存",
          iconSrc: require("../../assets/images/save.svg"),
          type: "save"
        },
        {
          name: "测试",
          iconSrc: require("../../assets/images/test.svg"),
          type: "test"
        },
        {
          name: "发布",
          iconSrc: require("../../assets/images/release.svg"),
          type: "release"
        }
      ],
      currentTab: { name: "sdfsaf", id: 1 },
      navFold: false,
      confirmLoading: false,
      modalType: "",
      modalVisible: false
    };
  },
  computed: {
    choosedData(){
      console.log(12333);
      return this.tabDatas.find(item => item.isActive)
    }
  },
  created() {
    // 获取api相关数据
    // api.fetch('/dss/apiservice/queryById', {
    //   id: this.$route.query.id,
    // }, 'get').then((rst) => {
    //   if (rst.result) {
    //     // 加工api信息tab的数据
    //     this.apiInfoData = [
    //       { label: this.$t('message.apiServices.label.apiName'), value: rst.result.name },
    //       { label: this.$t('message.apiServices.label.path'), value: rst.result.path },
    //       { label: this.$t('message.apiServices.label.scriptsPath'), value: rst.result.scriptPath },
    //     ]
    //   }
    // }).catch((err) => {
    //   console.error(err)
    // });
  },
  methods: {
    removeWork(tabData) {
      console.log(tabData);
    },
    onChooseWork(tabData) {
      console.log(tabData);
      if(tabData.isActive){
        return;
      }else{
        this.tabDatas = this.tabDatas.map(item => {
          return {...item, isActive: item.name === tabData.name}
        })
      }
    },
    showApiModel(apiData){
      console.log(apiData);
      this.$emit("showApiForm", apiData);
    }
  }
};
</script>

<style lang="scss" scoped>
@import "@/common/style/variables.scss";
.page-fc-header {
  padding: 25px 0px 25px 10px;
  display: flex;
  justify-content: flex-start;
  align-items: center;
  font-size: 21px;
  color: $text-title-color;
  font-family: PingFangSC-Regular;
  .divider {
    padding: 0 10px;
  }
  .header-subTitle {
    font-family: PingFangSC-Medium;
  }
}
.tabWrap {
  padding-left: 10px;
  .tab-item {
    display: inline-block;
    height: 40px;
    line-height: 40px;
    color: $title-color;
    cursor: pointer;
    min-width: 100px;
    max-width: 200px;
    overflow: hidden;
    margin-right: 2px;
    &.active {
      height: 40px;
      color: $primary-color;
      border-radius: 4px 4px 0 0;
      border-bottom: 2px solid $primary-color;
      line-height: 38px;
    }
  }
}
.toolBar {
  width: 100%;
  height: 48px;
  margin-top: -5px;
  background: #f8f9fc;
  border: 1px solid rgba($color: #000000, $alpha: 0.2);
  border-left-width: 0;
  border-right-width: 0;
  box-sizing: border-box;
  display: flex;
  justify-content: flex-start;
  align-items: center;
  padding-left: 10px;
  .toolWrap {
    display: flex;
    justify-content: space-around;
    align-items: center;
    width: 90px;
    cursor: pointer;
    font-family: PingFangSC-Regular;
    font-size: 16px;
    color: rgba(0, 0, 0, 0.65);
    & img {
      width: 16px;
    }
    .divider {
      width: 1px;
      height: 16px;
      background: rgba(0, 0, 0, 0.25);
      &.last-divider {
        background: transparent;
      }
    }
  }
}
</style>
