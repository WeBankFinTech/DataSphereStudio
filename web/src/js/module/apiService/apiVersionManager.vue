<template>
  <div>
    <Form
      ref="apiVersionQueryForm"
      :label-width="110"
      inline>
      <!--
      <FormItem
        :label="$t('message.apiService.apiTest.tabs.apiVersionInfo.versionInfoTable.query.publishDate')">
        <Input ></Input>
      </FormItem>

      <FormItem>
        <Button
          type="primary"
          style="margin-left: 8px"
          @click="apiVersionQueryBtnClick">{{$t('message.apiService.apiTest.tabs.apiVersionInfo.versionInfoTable.query.title')}}</Button>
      </FormItem>
      -->
    </Form>
    <div class="data-service-tbl">
      <Table :columns="versionInfoColumns" :data="apiTestInfo.apiVersionList"></Table>
    </div>
  </div>
</template>

<script>
import api from '@/js/service/api';
import DataTable from '@/js/component/table/dataTable'
export default {
  name: "apiVersionManager",
  props: {
    scriptPath: {
      type: String,
      required: true,
    }
  },
  components: {
    // 'DataTable': DataTable
  },
  data() {
    return {
      versionInfoColumns: [
        {
          title: this.$t('message.apiService.apiTest.tabs.apiVersionInfo.versionInfoTable.version'),
          align: 'center',
          key: 'version'
        },
        {
          title: this.$t('message.apiService.apiTest.tabs.apiVersionInfo.versionInfoTable.status'),
          align: 'center',
          key: 'statusStr',
        },
        {
          title: this.$t('message.apiService.apiTest.tabs.apiVersionInfo.versionInfoTable.source'),
          align: 'center',
          key: 'scriptPath'
        },
        {
          title: this.$t('message.apiService.apiTest.tabs.apiVersionInfo.versionInfoTable.creator'),
          align: 'center',
          key: 'creator'
        },
        {
          title: this.$t('message.apiService.apiTest.tabs.apiVersionInfo.versionInfoTable.publishDate'),
          align: 'center',
          key: 'publishDateStr'
        },
        {
          title: this.$t('message.apiService.apiTest.tabs.apiVersionInfo.versionInfoTable.updateDate'),
          align: 'center',
          key: 'updateDateStr'
        },
        {
          title: this.$t('message.apiService.apiTest.tabs.apiVersionInfo.versionInfoTable.operation'),
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
            //   }, this.$t('message.apiService.apiManager.apiTable.operation.test')));

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
                }, this.$t('message.apiService.apiManager.apiTable.operation.disable')));
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
              }, this.$t('message.apiService.apiManager.apiTable.operation.enable')));
            }
            return h('div', btns)
          }
        },
      ],
      apiTestInfo: {
        apiVersion: null,
        paramList: [

        ],
        apiVersionList: [

        ]
      },
    }
  },
  computed: {

  },
  mounted() {
    this.initApiVersionInfo();
  },
  methods: {
    initApiVersionInfo() {
      let _this = this;
      _this.apiVersionQueryPageList();
    },
    apiVersionQueryBtnClick() {
      this.apiVersionQueryPageList();
    },
    apiVersionQueryPageList() {
      let _this = this;
      api.fetch('/apiservice/apiVersionQuery', {
        scriptPath: _this.scriptPath
      }, 'get').then((rst) => {
        if (rst.result) {
          _this.apiTestInfo.apiVersionList = rst.result;
        }
      }).catch((err) => {

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
            _this.apiVersionQueryPageList();
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
            _this.apiVersionQueryPageList();
          }).catch((err) => {

          });
        }
      });
    },
  }
}
</script>

<style scoped>

</style>
