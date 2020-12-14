<template>
  <div class="api-manager">
    <div class="api-query" @keydown.enter="queryBtnClick">
      <!--
      <h1 class="api-manager-title">{{$t('message.apiService.apiManager.title')}}</h1>
      -->
      <Form
        ref="apiQueryForm"
        :model="dataServiceQueryData"
        :label-width="110"
        inline>

        <FormItem
          :label="$t('message.apiService.apiManager.query.apiName')">
          <Input v-model="dataServiceQueryData.apiName"></Input>
        </FormItem>

        <FormItem
          :label="$t('message.apiService.apiManager.query.tag')">
          <Input v-model="dataServiceQueryData.tag"></Input>
        </FormItem>

        <FormItem
          :label="$t('message.apiService.apiManager.query.status')">
          <Select v-model="dataServiceQueryData.status" clearable>
            <Option v-for="item in query.apiStatusList" :value="item.value" :key="item.value">{{ item.label }}</Option>
          </Select>
        </FormItem>

        <FormItem
          :label="$t('message.apiService.apiManager.query.commiter')">
          <Input v-model="dataServiceQueryData.commiter"></Input>
        </FormItem>

        <FormItem>
          <Button
            @click="clearBtnClick('apiQueryForm')">{{$t('message.apiService.apiManager.query.clearButtonText')}}</Button>

          <Button
            type="primary"
            style="margin-left: 8px"
            @click="queryBtnClick">{{$t('message.apiService.apiManager.query.buttonText')}}</Button>
        </FormItem>
      </Form>
    </div>
    <div class="data-service-tbl">
      <DataTable :loading="tableParam.loading" :titles="tableParam.titles" :data="tableParam.data" :totalRecords="tableParam.totalRecords" :pageSize="tableParam.pageSize" :pageNo="tableParam.currentPage" :changePage="changePage" aiTableScroll></DataTable>
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
export default {
  name: "apiManager",
  props: {

  },
  components: {
    ApiCopy,
    ApiUpdate,
    'DataTable': DataTable
  },
  data() {
    let _this = this;
    return {
      bgc: ['#e961b4', '#ed664b', '#7b6ac7', '#56abd1', '#f7af4c', '#fe5467', '#52c7bd', '#a479b7', '#cb81ce', '#5eabc5'],
      tableParam: {
        loading: false,
        titles: [
          {
            title: this.$t('message.apiService.apiManager.apiTable.apiName'),
            key: 'apiName'
          },
          {
            title: this.$t('message.apiService.apiManager.apiTable.apiPath'),
            key: 'apiPath'
          },
          {
            title: this.$t('message.apiService.apiManager.apiTable.status'),
            key: 'status',
            align: 'center',
            render: (h, params) => {
              let currentRow = params.row;
              var status = currentRow.status;
              if (0 === status) {
                return h('div', this.$t('message.apiService.apiStatus.disable'));
              } else if (1 === status) {
                return h('div', this.$t('message.apiService.apiStatus.enable'));
              } else {
                return h('div', '');
              }
            }
          },
          {
            title: this.$t('message.apiService.apiManager.apiTable.type'),
            key: 'type',
            align: 'center'
          },
          {
            title: this.$t('message.apiService.apiManager.apiTable.describe'),
            key: 'describe'
          },
          {
            title: this.$t('message.apiService.apiManager.apiTable.calledCount'),
            key: 'calledCount',
            align: 'center'
          },
          {
            title: this.$t('message.apiService.apiManager.apiTable.responsiblePerson'),
            key: 'responsiblePerson',
            align: 'center'
          },
          {
            title: this.$t('message.apiService.apiManager.apiTable.belongTo'),
            key: 'belongTo',
            align: 'center'
          },
          {
            title: this.$t('message.apiService.apiManager.apiTable.tag'),
            key: 'tagArr',
            align: 'center',
            render: (h, params) => {
              let currentRow = params.row;
              var tagArr = currentRow.tagArr;
              if (tagArr) {

                let tagDivArr = [];

                tagArr.forEach(function (item, index) {
                  tagDivArr.push(
                    h('span', {
                      attrs: {
                        class: 'env-div-css'
                      },
                      style: {
                        float: 'left',
                        backgroundColor: _this.bgc[index % _this.bgc.length],
                        borderRadius: '3px',
                        fontSize: '12px',
                        // fontWeight: 'bold',
                        padding: '2px 4px 2px 4px',
                        verticalAlign: 'middle',
                        opacity: 1,
                        border: "1px solid #e9eaec"
                      }
                    }, item)
                  )
                });

                return h('div', tagDivArr);
              }
              return '';
            }
          },
          {
            title: this.$t('message.apiService.apiManager.apiTable.operation.title'),
            align: 'center',
            render: (h, params) => {
              let currentRow = params.row;
              var status = currentRow.status;

              let btns = [

              ];
              btns.push(
                h('Button', {
                  props: {
                    type: 'text'
                  },
                  style: {
                    color: '#1890ff'
                  },
                  on: {
                    click: () => {
                      this.testApi(currentRow._index, currentRow.id);
                    }
                  }
                }, this.$t('message.apiService.apiManager.apiTable.operation.test')));

              btns.push(
                h('Button', {
                  props: {
                    type: 'text'
                  },
                  style: {
                    color: '#14fae8'
                  },
                  on: {
                    click: () => {
                      this.copyApi(currentRow._index, currentRow.id);
                    }
                  }
                }, this.$t('message.apiService.apiManager.apiTable.operation.copy')));

              if (1 === status) {
                btns.push(
                  h('Button', {
                    props: {
                      type: 'text'
                    },
                    style: {
                      color: '#ff4949'
                    },
                    on: {
                      click: () => {
                        this.disableApi(currentRow._index, currentRow.id);
                      }
                    }
                  }, this.$t('message.apiService.apiManager.apiTable.operation.disable')));
              } else if (0 === status) {
                btns.push(h('Button', {
                  props: {
                    type: 'text'
                  },
                  style: {
                    color: '#55fa5e'
                  },
                  on: {
                    click: () => {
                      this.enableApi(currentRow._index, currentRow.id);
                    }
                  }
                }, this.$t('message.apiService.apiManager.apiTable.operation.enable')));
              }

              // btns.push(
              //   h('Button', {
              //     props: {
              //       type: 'text'
              //     },
              //     style: {
              //       color: 'blue'
              //     },
              //     on: {
              //       click: () => {
              //         this.updateApi(currentRow._index, currentRow.id);
              //       }
              //     }
              //   }, this.$t('message.apiService.apiManager.apiTable.operation.manager')));

              return h('div', btns)
            }
          }
        ],
        data: [],
        currentPage: 1,
        pageSize: 8,
        totalRecords: 0
      },
      dataServiceQueryData: {
        pageSize: 8
      },
      query: {
        apiStatusList: [
          {
            label: this.$t("message.apiService.apiStatus.enable"),
            value: 1
          },
          {
            label: this.$t("message.apiService.apiStatus.disable"),
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

      }
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
      this.queryBtnClick();
    },
    clearBtnClick(name) {
      this.$refs[name].resetFields();
      this.dataServiceQueryData = {
        pageSize: 10,
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
    pageList() {
      let _this = this;
      if (this.tableParam.loading) {
        return;
      }
      this.tableParam.loading = true;
      api.fetch('/apiservice/search', {
        name: _this.dataServiceQueryData.apiName,
        tag: _this.dataServiceQueryData.tag,
        status: _this.dataServiceQueryData.status,
        creator: _this.dataServiceQueryData.commiter,
        currentPage: _this.dataServiceQueryData.currentPage,
        pageSize: _this.dataServiceQueryData.pageSize
      }, 'get').then((rst) => {
        _this.tableParam.data = []
        _this.tableParam.loading = false;
        if (rst.query_list) {
          rst.query_list.forEach(function (item) {
            _this.tableParam.data.push({
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
            _this.tableParam.totalRecords = rst.total;
          });
        }
      }).catch((err) => {
        _this.tableParam.loading = false;
      });
    },
    disableApi(index, id) {
      let _this = this;
      this.$Modal.confirm({
        title: this.$t("message.apiService.apiManager.apiTable.reconfirm.title"),
        content: '<p>' + this.$t("message.apiService.apiManager.apiTable.reconfirm.disable") + '</p>',
        onOk: () => {
          api.fetch('/apiservice/apiDisable', {
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
        title: this.$t("message.apiService.apiManager.apiTable.reconfirm.title"),
        content: '<p>' + this.$t("message.apiService.apiManager.apiTable.reconfirm.enable") + '</p>',
        onOk: () => {
          api.fetch('/apiservice/apiEnable', {
            id: id
          }, 'get').then((rst) => {
            _this.pageList();
          }).catch((err) => {

          });
        }
      });
    },
    testApi(index, id) {
      let _this = this;
      this.$router.push({
        'path': '/apiService/apiTest',
        'query': {
          'scriptPath': _this.tableParam.data[index].scriptPath
        }
      });
    },
    copyApi(index, id) {
      let _this = this;
      this.copyModalShow = false;
      setTimeout(function () {
        _this.copyModalShow = true;
        _this.copyApiData = _this.tableParam.data[index]
      }, 50)
    },
    updateApi(index, id) {
      this.updateModalShow = true;
      this.updateApiData = this.tableParam.data[index]
    },
    copyModalCancel() {
      this.copyModalShow = false;
    },
    copyModalOk() {
      this.copyModalShow = false;
    },
    updateModalCancel() {
      console.log('====== updateModalCancel')
      this.updateModalShow = false;
    },
    updateModalOk() {
      console.log('====== updateModalOk')
      this.updateModalShow = false;
    }
  }
}
</script>

<style src="./apiManager.scss" lang="scss"></style>

<style scoped>
.env-div-css {
  background-color: #1ebc8d;
}
</style>
