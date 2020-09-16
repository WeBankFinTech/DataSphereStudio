<template>
  <div>
    <Form v-model="apiInfo" :label-width="90">
      <FormItem :label="$t('message.oneService.apiTest.tabs.apiInfo.tag')">
        <span>{{apiInfo.tag}}</span>
      </FormItem>
      <FormItem :label="$t('message.oneService.apiTest.tabs.apiInfo.metrics')">

      </FormItem>
    </Form>
    <h1 class="api-manager-title">{{$t('message.oneService.apiTest.tabs.apiInfo.use')}}</h1>
    <Form
      ref="apiCallQueryForm"
      :label-width="110"
      inline>

      <FormItem
        :label="$t('message.oneService.apiTest.tabs.apiInfo.selectCallTime')">
        <Input ></Input>
      </FormItem>

      <FormItem
        :label="$t('message.oneService.apiTest.tabs.apiInfo.version')">
        <Input ></Input>
      </FormItem>

      <FormItem
        :label="$t('message.oneService.apiTest.tabs.apiInfo.callStatus')">
        <Input ></Input>
      </FormItem>

      <FormItem>
        <Button
          @click="clearBtnClick('apiCallQueryForm')">{{$t('message.oneService.apiTest.tabs.apiInfo.query.clearButtonText')}}</Button>

        <Button
          type="primary"
          style="margin-left: 8px"
          @click="apiCallQueryBtnClick">{{$t('message.oneService.apiTest.tabs.apiInfo.query.buttonText')}}</Button>
      </FormItem>

    </Form>
    <div class="data-service-tbl">
      <DataTable :loading="tableParam.loading" :titles="tableParam.titles" :data="tableParam.data" :totalRecords="tableParam.totalRecords" :pageSize="tableParam.pageSize" :pageNo="tableParam.currentPage" :changePage="changePage" aiTableScroll></DataTable>
    </div>
  </div>
</template>

<script>
import api from '@/js/service/api';
import DataTable from '@/js/component/table/dataTable'
export default {
  name: "apiCallSatistcs",
  props: {

  },
  components: {
    'DataTable': DataTable
  },
  data() {
    return {
      tableParam: {
        loading: false,
        titles: [
          {
            title: this.$t('message.oneService.apiManager.apiTable.apiName'),
            key: 'apiName'
          },
          {
            title: this.$t('message.oneService.apiManager.apiTable.apiPath'),
            key: 'apiPath'
          },
          {
            title: this.$t('message.oneService.apiManager.apiTable.status'),
            key: 'status',
            align: 'center',
            render: (h, params) => {
              let currentRow = params.row;
              var status = currentRow.status;
              if (0 === status) {
                return h('div', this.$t('message.oneService.apiStatus.disable'));
              } else if (1 === status) {
                return h('div', this.$t('message.oneService.apiStatus.enable'));
              } else {
                return h('div', '');
              }
            }
          },
          {
            title: this.$t('message.oneService.apiManager.apiTable.type'),
            key: 'type',
            align: 'center'
          },
          {
            title: this.$t('message.oneService.apiManager.apiTable.describe'),
            key: 'describe'
          },
          {
            title: this.$t('message.oneService.apiManager.apiTable.calledCount'),
            key: 'calledCount',
            align: 'center'
          },
          {
            title: this.$t('message.oneService.apiManager.apiTable.responsiblePerson'),
            key: 'responsiblePerson',
            align: 'center'
          },
          {
            title: this.$t('message.oneService.apiManager.apiTable.belongTo'),
            key: 'belongTo',
            align: 'center'
          },
          {
            title: this.$t('message.oneService.apiManager.apiTable.tag'),
            key: 'tag',
            align: 'center'
          },
          {
            title: this.$t('message.oneService.apiManager.apiTable.operation.title'),
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
                    color: 'blue'
                  },
                  on: {
                    click: () => {
                      this.testApi(currentRow._index, currentRow.id);
                    }
                  }
                }, this.$t('message.oneService.apiManager.apiTable.operation.test')));

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
              //         this.copyApi(currentRow._index, currentRow.id);
              //       }
              //     }
              //   }, this.$t('message.oneService.apiManager.apiTable.operation.copy')));

              if (1 === status) {
                btns.push(
                  h('Button', {
                    props: {
                      type: 'text'
                    },
                    style: {
                      color: 'red'
                    },
                    on: {
                      click: () => {
                        this.disableApi(currentRow._index, currentRow.id);
                      }
                    }
                  }, this.$t('message.oneService.apiManager.apiTable.operation.disable')));
              } else if (0 === status) {
                btns.push(h('Button', {
                  props: {
                    type: 'text'
                  },
                  style: {
                    color: 'green'
                  },
                  on: {
                    click: () => {
                      this.enableApi(currentRow._index, currentRow.id);
                    }
                  }
                }, this.$t('message.oneService.apiManager.apiTable.operation.enable')));
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
              //   }, this.$t('message.oneService.apiManager.apiTable.operation.manager')));

              return h('div', btns)
            }
          }
        ],
        data: [],
        currentPage: 1,
        pageSize: 10,
        totalRecords: 0
      },
      dataServiceQueryData: {
        pageSize: 10
      },
      query: {

      },
      paramInfoColumns: [
        {
          title: this.$t('message.oneService.apiTest.tabs.apiTestInfo.paramTable.paramName'),
          key: 'paramName'
        },
        {
          title: this.$t('message.oneService.apiTest.tabs.apiTestInfo.paramTable.paramType'),
          key: 'paramType',
        },
        {
          title: this.$t('message.oneService.apiTest.tabs.apiTestInfo.paramTable.require.title'),
          align: 'center',
          key: 'require'
        },
        {
          title: this.$t('message.oneService.apiTest.tabs.apiTestInfo.paramTable.defaultValue'),
          key: 'defaultValue',
          width: '200',
          render: (h, params) => {
            return h('div', [
              h('Input', {
                props: {
                  value: params.row.defaultValue
                },
                on: {

                }
              })
            ]);
          }
        }
      ],
      versionInfoColumns: [
        {
          title: this.$t('message.oneService.apiTest.tabs.apiVersionInfo.versionInfoTable.version'),
          align: 'center',
          key: 'version'
        },
        {
          title: this.$t('message.oneService.apiTest.tabs.apiVersionInfo.versionInfoTable.status'),
          align: 'center',
          key: 'statusStr',
        },
        {
          title: this.$t('message.oneService.apiTest.tabs.apiVersionInfo.versionInfoTable.source'),
          align: 'center',
          key: 'scriptPath'
        },
        {
          title: this.$t('message.oneService.apiTest.tabs.apiVersionInfo.versionInfoTable.creator'),
          align: 'center',
          key: 'creator'
        },
        {
          title: this.$t('message.oneService.apiTest.tabs.apiVersionInfo.versionInfoTable.publishDate'),
          align: 'center',
          key: 'publishDateStr'
        },
        {
          title: this.$t('message.oneService.apiTest.tabs.apiVersionInfo.versionInfoTable.updateDate'),
          align: 'center',
          key: 'updateDateStr'
        },
        {
          title: this.$t('message.oneService.apiTest.tabs.apiVersionInfo.versionInfoTable.operation'),
          align: 'center',
          render: (h, params) => {
            let currentRow = params.row;
            var status = currentRow.status;

            let btns = [

            ];
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
            //         this.testApi(currentRow._index, currentRow.id);
            //       }
            //     }
            //   }, this.$t('message.oneService.apiManager.apiTable.operation.test')));

            if (1 === status) {
              btns.push(
                h('Button', {
                  props: {
                    type: 'text'
                  },
                  style: {
                    color: 'red'
                  },
                  on: {
                    click: () => {
                      this.disableApi(currentRow._index, currentRow.id);
                    }
                  }
                }, this.$t('message.oneService.apiManager.apiTable.operation.disable')));
            } else if (0 === status) {
              btns.push(h('Button', {
                props: {
                  type: 'text'
                },
                style: {
                  color: 'green'
                },
                on: {
                  click: () => {
                    this.enableApi(currentRow._index, currentRow.id);
                  }
                }
              }, this.$t('message.oneService.apiManager.apiTable.operation.enable')));
            }
            return h('div', btns)
          }
        },
      ],
      apiInfo: {

      },
      apiTestInfo: {
        apiVersion: null,
        paramList: [

        ],
        apiVersionList: [

        ]
      },
      apiVersionList: [

      ],
      apiVersionInfo: {

      },
      scriptPath: 'hdfs:///tmp/chongchuanbing/jdbc/account_query.jdbc'
    }
  },
  computed: {

  },
  mounted() {

  },
  methods: {
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

    },
    apiCallQueryBtnClick() {

    }
  }
}

</script>

<style scoped>

</style>
