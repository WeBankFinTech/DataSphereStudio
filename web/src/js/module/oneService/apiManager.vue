/* eslint-disable vue/no-parsing-error */
<template>
  <div class="api-manager">
    <div class="api-query" @keydown.enter="queryBtnClick">
      <!--
      <h1 class="api-manager-title">{{$t('message.oneService.apiManager.title')}}</h1>
      -->
      <Form
        ref="apiQueryForm"
        :model="dataServiceQueryData"
        :label-width="160"
        inline>

        <FormItem
          :label="$t('message.oneService.apiManager.query.apiName')" :label-width="100">
          <Input v-model="dataServiceQueryData.apiName"></Input>
        </FormItem>

        <FormItem
          :label="$t('message.oneService.apiManager.query.status')">
          <Select v-model="dataServiceQueryData.status" clearable>
            <Option v-for="item in query.apiStatusList" :value="item.value" :key="item.value">{{ item.label }}</Option>
          </Select>
        </FormItem>
        <FormItem
          :label="$t('message.oneService.apiManager.query.commiter')">
          <Input v-model="dataServiceQueryData.commiter"></Input>
        </FormItem>

        <FormItem>
          <Button
            @click="clearBtnClick('apiQueryForm')">{{$t('message.oneService.apiManager.query.clearButtonText')}}</Button>
          <Button
            type="primary"
            style="margin-left: 8px"
            @click="queryBtnClick">{{$t('message.oneService.apiManager.query.buttonText')}}</Button>
        </FormItem>
      </Form>
    </div>
    <div class="data-service-tbl">
      <!--卡片显示api应用市场-->

      <Tabs active-key=0 @on-click="tabClick">
        <Tab-pane v-for="(type, index) in tags" :label="type" :key="index" >

          <div class="api-list">
            <div class="api-list-main-left" >
              <Card class="left">
                <div>
                  <div class="api-list-content">
                    <Row ref="row" class="content-item">
                      <i-col
                        ref="col"
                        class="api-list-item"
                        :xs="12" :sm="8" :md="4" :lg="4"
                        v-for="item in apiList"
                        :key="item.id"
                      >
                        <h3 >
                          <div class="name">
                            <img src="../../../assets/images/api.svg" style="width:30px; height:30px;display: inline-block; position: absolute"/>
                            <span class="api-name">{{item.apiName}}</span>
                          </div>
                          <div  v-show="item.status== 1">
                            <div class="right-run"><img src="../../../assets/images/api_status.svg" class="run"/>{{$t("message.oneService.apiStatus.enable")}}</div>
                          </div>
                          <div  v-show="item.status== 0">
                            <div class="right-run"><img src="../../../assets/images/api_status.svg" class="stop"/>{{$t("message.oneService.apiStatus.disable")}}</div>
                          </div>

                        </h3>
                        <!-- <template> -->
                        <!-- </template> -->
                        <span class="describe" v-show="item.describe.length!=0">{{item.describe}}</span>

                        <ul class="lable-list" v-show="item.tagArr!=null">
                          <template v-for="(tagItem, tagIndex) in item.tagArr">
                            <li class="item" :key="tagIndex">{{tagItem}}</li>
                          </template>
                        </ul>
                        <div class="bottom">
                          <!-- <div class="enter" @click="enter(index)">{{$t("message.oneService.apiManager.apiTable.operation.enter")}}</div> -->
                          <router-link class="enter"  :to="{name:'apiTest', query: {id: item.id}}">{{$t("message.oneService.apiManager.apiTable.operation.enter")}}</router-link>
                        </div>
                      </i-col>
                    </Row>
                  </div>
                </div>
              </Card>
            </div>
          </div>
          <!-- </scroller> -->

        </Tab-pane>
      </Tabs>
      <DataBasePage  :totalRecords="totalRecords" :pageSize="pageSize" :pageNo="currentPage" :changePage="changePage" :showTotal="true" :showElevator="true" :on-page-size-change="onPageSizeChange"></DataBasePage>
    </div>
    <ApiCopy v-if="copyModalShow" :show="copyModalShow" :api-data="copyApiData" @on-copy-cancel="copyModalCancel" @on-copy="copyModalOk"></ApiCopy>
    <ApiUpdate :show="updateModalShow" :api-data="updateApiData" @on-update-cancel="updateModalCancel" @on-update="updateModalOk"></ApiUpdate>
  </div>
</template>

<script>
import api from '@/js/service/api';
import DataTable from '@/js/component/table/dataTable'
import ApiCopy from './apiCopy'
import ApiUpdate from './apiUpdate'
import page from '@/js/component/page/basePage'
import Vue from 'vue';
import vuescroll from 'vuescroll/dist/vuescroll-slide';
// import 'vuescroll/dist/vuescoll.css';

export default {
  name: "apiManager",
  props: {

  },
  components: {
    ApiCopy,
    ApiUpdate,
    'DataBasePage': page
  },
  data() {
    let _this = this;
    return {
      bgc: ['#e961b4', '#ed664b', '#7b6ac7', '#56abd1', '#f7af4c', '#fe5467', '#52c7bd', '#a479b7', '#cb81ce', '#5eabc5'],
      tags: [this.$t("message.oneService.apiManager.apiTable.labelEmpty")],
      loading: false,
      apiList: [],
      tag: this.$t("message.oneService.apiManager.apiTable.labelEmpty"),
      dataServiceQueryData: {
        tag: this.$t("message.oneService.apiManager.apiTable.labelEmpty"),
        currentPage: 1,
        pageSize: 10
      },
      query: {
        apiStatusList: [
          {
            label: this.$t("message.oneService.apiStatus.enable"),
            value: 1
          },
          {
            label: this.$t("message.oneService.apiStatus.disable"),
            value: 0
          }
        ],
        commiterList: [

        ],
        apiNameList: [

        ]
      },
      saveLoading: false,
      copyModalShow: false,
      updateModalShow: false,
      copyApiData: {
      },
      updateApiData: {
      },
      currentPage: 1,
      pageSize: 10,
      totalRecords: 0,
      ops: {
        bar: {
          background: '#5e9de0',
          keepShow: true,
          minSize: 0.3,
          size: '6px',
          disable: false,
          opacity: 1,
          hoverStyle: false
        },
        rail: {
          size: '20px',
          opacity: 0.5,
          background: '#fff',
        },
        scrollButton: {
          enable: true,
          background: '#cecece',
        },
        vuescroll: {
          wheelScrollDuration: 0,
          wheelDirectionReverse: true
        }
      },

    }
  },
  computed: {

  },
  mounted() {
    let _this = this;
    _this.init();
  },
  methods: {
    init() {
      this.setTag(0)
      this.queryBtnClick();
    },
    clearBtnClick(name) {
      this.$refs[name].resetFields();
      let tag =this.dataServiceQueryData.tag;
      this.dataServiceQueryData = {
        pageSize: 10,
        tag: tag,
        currentPage: 1
      }
    },
    changePage(currentPage) {
      this.dataServiceQueryData.currentPage = currentPage
      this.pageList();
    },
    queryBtnClick() {
      let _this = this;
      _this.dataServiceQueryData.currentPage = 1;
      _this.pageList();
    },
    setTag(index){
      this.tag=this.tags[index]
    },
    pageList() {
      let _this = this;
      if(_this.loading){
        return
      }
      _this.loading=true
      api.fetch('/oneservice/tags', 'get').then((rst) => {
        if (rst.tags) {
          _this.tags=[_this.$t("message.oneService.apiManager.apiTable.labelEmpty")]
          rst.tags.forEach((item,index)=> _this.tags.push(item))
        }
      }).catch((err) => {
        console.log(err)
      });

      api.fetch('/oneservice/search', {
        name: _this.dataServiceQueryData.apiName,
        tag: _this.tag==_this.$t("message.oneService.apiManager.apiTable.labelEmpty")?null:_this.tag,
        status: _this.dataServiceQueryData.status,
        creator: _this.dataServiceQueryData.commiter,
        currentPage: _this.dataServiceQueryData.currentPage,
        pageSize: _this.dataServiceQueryData.pageSize
      }, 'get').then((rst) => {
        if (rst.query_list) {
          _this.apiList=[]
          rst.query_list.forEach(function (item) {
            _this.apiList.push({
              id: item.id,
              apiName: item.name,
              apiPath: item.path,
              apiVersion: item.version,
              scriptPath: item.scriptPath,
              tag: item.tag,
              tagArr: item.tag ? item.tag.split(',') : '',
              describe: item.description,
              status: item.status,
              type: item.type,
              responsiblePerson: item.creator
            })
          });
          _this.totalRecords=rst.total
        }
      }).catch((err) => {
        console.log(err)
      });
      _this.loading=false
    },


    disableApi(index, id) {
      let _this = this;
      this.$Modal.confirm({
        title: this.$t("message.oneService.apiManager.apiTable.reconfirm.title"),
        content: '<p>' + this.$t("message.oneService.apiManager.apiTable.reconfirm.disable") + '</p>',
        onOk: () => {
          api.fetch('/oneservice/apiDisable', {
            id: id
          }, 'get').then((rst) => {
            _this.pageList();
          }).catch((err) => {

          });
        }
      });
    },
    enableApi(index, id) {
      let _this = this;
      this.$Modal.confirm({
        title: this.$t("message.oneService.apiManager.apiTable.reconfirm.title"),
        content: '<p>' + this.$t("message.oneService.apiManager.apiTable.reconfirm.enable") + '</p>',
        onOk: () => {
          api.fetch('/oneservice/apiEnable', {
            id: id
          }, 'get').then((rst) => {
            _this.pageList();
          }).catch((err) => {

          });
        }
      });
    },
    // testApi(index, id) {
    //   let _this = this;
    //   this.$router.push({
    //     'path': '/oneService/apiTest',
    //     'query': {
    //       'scriptPath': _this.apiList.data[index].scriptPath
    //     }
    //   });
    // },
    // copyApi(index, id) {
    //   let _this = this;
    //   this.copyModalShow = false;
    //   setTimeout(function () {
    //     _this.copyModalShow = true;
    //     _this.copyApiData = _this.apiList.data[index]
    //   }, 50)
    // },
    // updateApi(index, id) {
    //   this.updateModalShow = true;
    //   this.updateApiData = this.apiList.data[index]
    // },
    onPageSizeChange(){
      console.log(this.currentPage)
    },
    copyModalCancel() {
      this.copyModalShow = false;
    },
    copyModalOk() {
      this.copyModalShow = false;
    },
    updateModalCancel() {
      this.updateModalShow = false;
    },
    updateModalOk() {
      this.updateModalShow = false;
    },
    tabClick(id){
      this.tag=this.tags[id]
      this.dataServiceQueryData.currentPage=1
      this.pageList()
    },
    handleScroll() {
      const vs = this.$refs['vs'];
      const panel = vs.scrollPanelElm;
      const x = Math.random() * panel.scrollWidth;
      const y = Math.random() * panel.scrollHeight;
      vs.scrollTo({
        x,
        y
      });
    },

    enter(index){
      let _this = this;
      this.$router.push({
        'path': '/oneService/apiTest',
        'query': {
          'scriptPath': _this.apiList[index].scriptPath
        }
      });
    }
  }
}
</script>

<style src="./apiManager.scss" lang="scss"></style>

<style scoped>
.env-div-css {
  background-color: #1ebc8d;
}
.fixedBar {
  position: fixed;
  top: 0;
  z-index: 999;
  width: 100%;
 }
</style>
