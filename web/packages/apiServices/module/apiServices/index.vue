<template>
  <div class="main service-cener">
    <div class="tap-bar">
      <h3 class="title">
        <SvgIcon v-if="microModule !== 'apiServices'" style="font-size: 16px;display: inline-block;transform: rotate(180deg);"
          color="#444444"
          @click="goBack"
          icon-class="fi-expand-right"/>
        <span>{{$t('message.apiServices.title')}}</span>
      </h3>
    </div>
    <Row class="search-bar">
      <Col span="5" class="search-item">
        <span class="lable">{{$t("message.apiServices.label.name")}}： </span>
        <Input v-model="searchName" class="input" :placeholder="$t('message.apiServices.placeholder.enterName')"></Input>
      </Col>
      <Col span="5" class="search-item">
        <span class="lable">{{$t("message.apiServices.label.status")}}：</span>
        <Select v-model="searchStatus" clearable class="input">
          <Option value="1">{{$t('message.apiServices.enable')}}</Option>
          <Option value="0">{{$t('message.apiServices.disable')}}</Option>
        </Select>
      </Col>
      <Col span="5" class="search-item">
        <span class="lable">{{$t("message.apiServices.label.submitter")}}：</span>
        <Input v-model="searchCommitter" class="input" :placeholder="$t('message.apiServices.placeholder.inputSubmitter')"></Input>
      </Col>
      <Col span="5">
        <Button class="search" type="primary" @click="getApiData">{{$t("message.apiServices.label.find")}}</Button>
        <Button class="search" type="primary" @click="gotoSubmit">{{$t("message.apiServices.label.approve")}}</Button>
      </Col>
    </Row>
    <div class="workspace-header-right">
      <Tabs class="tabs-content" v-model="currentTag">
        <TabPane label="default" name="default">
        </TabPane>
        <TabPane :label="item" v-for="item in tagList" :key="item" :name="item">
        </TabPane>
      </Tabs>
      <div>
        <span class="showAll" @click="changeShowAll">{{ showAllText ? $t("message.apiServices.viewValid") : $t("message.apiServices.viewAll") }}</span>
        <Dropdown class="change-view" @on-click="changeVisual">
          <Icon type="gear-a" />
          <a>
            {{ $t('message.apiServices.display') }}
          </a>
          <Dropdown-menu slot="list">
            <Dropdown-item v-for="(item, index) in visualCates" :key="index" :name="item.name">{{item.title}}</Dropdown-item>
          </Dropdown-menu>
        </Dropdown>
      </div>
    </div>
    <div ref="row" class="mainContent">
      <template v-if="visual === 'card'">
        <!-- 卡片组件 -->
        <Row :gutter="16" class="api_card">
          <Col
            ref="col"
            :xs="cardXS"
            :sm="cardSM"
            :md="cardMD"
            :lg="cardLG"
            :xl="cardXL"
            :xxl="cardXXL"
            style="margin-bottom: 16px;"
            v-for="(item, index) in pageDatalist"
            :key="item.id + `${index}`"
          >
            <!-- 卡片 -->
            <apiCard
              :title="item"
              :desc="item.description"
              :disabled="item.status === 0 || item.status === 2"
              :status="getStatusButtonTextAndColor(item.status).status"
              :button="getStatusButtonTextAndColor(item.status).button"
              @onButton="holderButton(item)" >
              <template slot="dropdown" v-if="item.creator === username && item.status !== 2">
                <dop :dropdownList="getDropdownList(item, index)" @commonAction="commonAction(item, arguments, index)"></dop>
              </template>
            </apiCard>
          </Col>
        </Row>
      </template>
      <template v-else>
        <!-- 表格组件 -->
        <wb-table
          border
          :loadNum="page.pageSize"
          :columns="tableColumnNum"
          :tableList="pageDatalist"
          class="result-normal-table">
          <template
            slot-scope="{row}"
            slot="name"
          >
            <span>{{ row.aliasName || row.name }}</span>
          </template>
          <template
            slot-scope="{row}"
            slot="status"
          >
            <span v-if="row.status == 0" style="color:red;">{{ $t('message.apiServices.disable') }}</span>
            <span v-else-if="row.status == 2" style="color:#9c9c9c;">{{ $t('message.apiManager.deleted') }}</span>
            <span style="color: rgb(18, 150, 219);" v-else>{{ $t('message.apiServices.enable') }}</span>
          </template>
          <template
            slot-scope="{row}"
            slot="tag"
          >
            <div class="tag-box">
              <span class="tag-item" v-for="item in tagAction(row.tag)" :key="item">{{ item }}</span>
            </div>
          </template>
          <template
            slot-scope="{row,index}"
            slot="action"
          >
            <Button type="primary" :disabled="Number(row.status) === 0 || Number(row.status) === 2" size='small' @click="holderButton(row)">{{getStatusButtonTextAndColor(row.status).button.text}}</Button>
            <dop v-if="row.creator === username && row.status !== 2" :dropdownList="getDropdownList(row)" @commonAction="commonAction(row, arguments, index - 1)"></dop>
          </template>
        </wb-table>
      </template>
    </div>
    <div class="page-bar">
      <Page
        ref="page"
        :total="page.totalSize"
        :page-size-opts="page.sizeOpts"
        :page-size="page.pageSize"
        :current="page.pageNow"
        class-name="page"
        size="small"
        show-total
        show-sizer
        @on-change="change"
        @on-page-size-change="changeSize" />
    </div>
    <dialogForm
      v-if="isShowForm"
      :label-width="120"
      @onConfirm="handleSubmit"
      @onCancel="handleCancel"
      :formMake="formMake"
      :formRules="formRules"
      :openExtend="true"
      :title="formTitle">
      <div style="padding: 20px 0;">
        {{$t('message.apiServices.updateApiModal.paramConfirm')}}
        <Table :columns="paramInfoColumns" :data="paramList"></Table>
      </div>
    </dialogForm>
  </div>
</template>
<script>
import apiCard from './apiCard.vue'
import api from '@dataspherestudio/shared/common/service/api';
import dop from './dropdownCom.vue';
import storage from '@dataspherestudio/shared/common/helper/storage';
import dialogForm from './form.vue';
import WbTable from '@dataspherestudio/shared/components/table';
import util from '@dataspherestudio/shared/common/util';
const currentModules = util.currentModules();
export default {
  components: {
    apiCard,
    dop,
    dialogForm,
    WbTable
  },
  data() {
    let _this = this;
    return {
      // 显示切换全部列表按钮
      showAllText: false,
      username: '',
      searchName: '',
      searchStatus: '',
      searchCommitter: '',
      visual: 'card', // 视图的类型
      visualCates: [
        { name: 'table', title: this.$t('message.apiServices.tableDisplay') },
        { name: 'card', title: this.$t('message.apiServices.cardDisplay') }
      ],
      page: {
        totalSize: 0,
        sizeOpts: [15, 30, 45],
        pageSize: 15,
        pageNow: 1
      },
      // 模拟卡片数据
      apiData: [],
      // 表单结构和表单数据
      formMake: [],
      // 卡片响应式布局
      cardXS: { span: 24 },
      cardSM: { span: 12 },
      cardMD: { span: 8 },
      cardLG: { span: 6 },
      cardXL: { span: 6 },
      cardXXL: { span: 4 },
      // 表单验证
      formRules: {},
      // 表单标题
      formTitle: "",
      // 更新弹框当前数据
      currentData: {},
      isShowForm: false,
      currentTag: 'default',
      tagList: [],
      tableColumnNum: [
        {
          title: this.$t('message.apiServices.apiTable.apiName'),
          slot: 'name',
          minWidth: 120,
        },
        {
          title: this.$t('message.apiServices.apiTable.apiPath'),
          key: 'path',
          minWidth: 120,
        },
        {
          title: this.$t('message.apiServices.apiTable.status'),
          slot: 'status',
          minWidth: 80,
          align: 'center',
        },
        {
          title: this.$t('message.apiServices.apiTable.type'),
          key: 'type',
          align: 'center',
          tooltip: true,
          minWidth: 80,
        },
        {
          title: this.$t('message.apiServices.apiTable.describe'),
          key: 'description',
          tooltip: true,
          minWidth: 240,
        },
        {
          title: this.$t('message.apiServices.apiTable.calledCount'),
          key: 'calledCount',
          align: 'center',
          tooltip: true,
          minWidth: 80,
        },
        {
          title: this.$t('message.apiServices.apiTable.responsiblePerson'),
          key: 'creator',
          align: 'center',
          tooltip: true,
          minWidth: 80,
        },
        {
          title: this.$t('message.apiServices.apiTable.belongTo'),
          key: 'belongTo',
          align: 'center',
          tooltip: true,
          minWidth: 120,
        },
        {
          title: this.$t('message.apiServices.apiTable.tag'),
          slot: 'tag',
          align: 'center',
          minWidth: 120,
        },
        {
          title: this.$t('message.apiServices.apiTable.operation.title'),
          align: 'center',
          slot: 'action',
          width: 160
        }
      ],
      paramTypeList: [
        {
          label: 'String',
          value: "1"
        },
        {
          label: 'Number',
          value: "2"
        },
        {
          label: 'Date',
          value: "3"
        }
      ],
      requireList: [
        {
          label: this.$t('message.apiServices.paramTable.require.yes'),
          value: 1
        },
        {
          label: this.$t('message.apiServices.paramTable.require.no'),
          value: 0
        },
        {
          label: this.$t('message.apiServices.paramTable.require.hide'),
          value: 2
        }
      ],
      paramList: [],
      // 参数列表
      paramInfoColumns: [
        {
          title: this.$t('message.apiServices.paramTable.paramName'),
          key: 'name'
        },
        {
          title: this.$t('message.apiServices.paramTable.displayName'),
          key: 'displayName',
          render: (h, params) => {
            return h('div', [
              h('Input', {
                props: {
                  value: params.row.displayName
                },
                on: {
                  'on-blur'(event) {
                    _this.paramList[params.row._index].displayName = event.target.value;
                  }
                }
              })
            ]);
          }
        },
        {
          title: this.$t('message.apiServices.paramTable.paramType'),
          key: 'type',
          // width: '100',
          render: (h, params) => {
            return h('div', [
              h('Select', {
                props: {
                  transfer: true,
                  value: params.row.type
                },
                on: {
                  'on-change'(value) {
                    _this.paramList[params.row._index].type = value;
                  }
                }
              },
              _this.paramTypeList.map((item) => {
                return h('Option', {
                  props: {
                    value: item.value,
                    label: item.label
                  }
                })
              }))
            ]);
          }
        },
        {
          title: this.$t('message.apiServices.paramTable.require.title'),
          key: 'required',
          // width: '100',
          render: (h, params) => {
            return h('div', [
              h('Select', {
                props: {
                  transfer: true,
                  value: params.row.required
                },
                on: {
                  'on-change'(value) {
                    _this.paramList[params.row._index].required = value;
                  }
                }
              },
              _this.requireList.map((item) =>{
                return h('Option', {
                  props: {
                    value: item.value,
                    label: item.label
                  }
                })
              }))
            ]);
          }
        },
        {
          title: this.$t('message.apiServices.paramTable.describe'),
          key: 'description',
          width: '200',
          render: (h, params) => {
            return h('div', [
              h('Input', {
                props: {
                  type: 'textarea',
                  value: params.row.description
                },
                on: {
                  'on-blur'(event) {
                    _this.paramList[params.row._index].describe = event.target.value;
                  }
                }
              })
            ]);
          }
        }
      ],
      microModule: currentModules.microModule
    }
  },
  watch: {
    currentTag() {
      // 切换tag, 重新获取数据
      this.getApiData()
    },
  },
  created() {
    this.getApiData();
    this.getTagList();
    this.username = this.getUserName();
  },
  mounted() {
    // 切到非第一页,进入 管理页,再返回数据服务首页,要保留进管理页之前的分页
    if (sessionStorage.getItem('apiservicesCurrentPage') && sessionStorage.getItem('apiservicesCurrentPage') !== 'null' && sessionStorage.getItem('apiservicesCurrentPage') !== 'undefined') {
      this.page.pageNow = Number(sessionStorage.getItem('apiservicesCurrentPage'));
      sessionStorage.removeItem('apiservicesCurrentPage')
    }
  },
  computed: {
    pageDatalist() {// 展示的数据
      let list = this.apiData;
      if (!this.showAllText) {
        list =  this.apiData.filter(item => { return item.status !== 2});
      }
      return list.slice((this.page.pageNow - 1) * this.page.pageSize, this.page.pageNow * this.page.pageSize)
    }
  },
  methods: {
    // 切换是否显示所有列表
    changeShowAll() {
      this.page = { // 切换显示时将页码配置还原
        totalSize: 0,
        sizeOpts: [15, 30, 45],
        pageSize: 15,
        pageNow: 1
      };
      // 如果现在已经是显示所有则切换成屏蔽删除
      if(this.showAllText) {
        this.showAllText = false;
        this.page.totalSize = this.apiData.filter(item => item.status !== 2).length;
      } else {
        this.showAllText = true;
        this.page.totalSize = this.apiData.length;
      }
    },
    tagAction(tags) {
      return tags ? tags.split(',') : []
    },
    // 获取所有tag的列表
    getTagList() {
      api.fetch('/dss/apiservice/tags', {
        workspaceId: this.$route.query.workspaceId
      },'get').then((res) => {
        this.tagList = res.tags;
      })
    },
    // 加工菜单的显示文本
    getDropdownList(e) {
      let dropdownList = [
        {id: 1, title: this.$t('message.apiServices.apiTable.operation.manager')},
        {id: 2, title: this.$t('message.apiServices.apiTable.operation.enable')},
        {id: 4, title: this.$t('message.apiServices.delete')}
      ];
      if(e.status === 1) {
        // 当状态为1时为开启状态，文本显示禁用
        dropdownList[1].title = this.$t('message.apiServices.apiTable.operation.disable')
      } else {
        dropdownList[1].title = this.$t('message.apiServices.apiTable.operation.enable')
      }
      return dropdownList
    },
    // 加工状态的显示文本和颜色
    getStatusButtonTextAndColor(e) {
      const res = {
        '1': { status: { color: "#1296db", text: "Running" }, button: { type: "primary", text: this.$t('message.apiServices.buttonText.enter') } },
        '0': { status: { color: "#F14", text: "Stop" }, button: { type: "error", text: this.$t('message.apiServices.buttonText.maintained') } },
        '2': { status: { color: "#9c9c9c", text: "Deleted" }, button: { type: "error", text: this.$t('message.apiServices.buttonText.maintained') } },
      }
      // 如果没有该状态值则默认返回2
      return res[e] || res[2]
    },
    // 切换分页
    change(val) {
      this.page.pageNow = val;
    },
    // 页容量变化
    changeSize(val) {
      this.page.pageSize = val;
      this.page.pageNow = 1;
    },
    goBack() {
      this.$router.go(-1)
    },
    getApiData() {
      this.page.pageNow = 1
      api.fetch('/dss/apiservice/search', {
        name: this.searchName || undefined,
        tag: this.currentTag === 'default' ? undefined : this.currentTag,
        status: this.searchStatus || undefined,
        creator: this.searchCommitter || undefined,
        workspaceId: this.$route.query.workspaceId
      }, 'get').then((res) => {
        this.apiData = res.query_list;
        if(this.showAllText) {
          this.page.totalSize = this.apiData.length;
        } else {
          this.page.totalSize = this.apiData.filter(item => item.status !== 2).length;
        }
      })
    },
    gotoSubmit() {
      this.$router.push({ name: 'ServicesSubmit', query: { workspaceId: this.$route.query.workspaceId} })
    },
    changeVisual(type) {
      this.visual = type;
    },
    // 点击卡片进入按钮
    holderButton(e) {
      // 跳转到查询页面
      if(e.status === 1) {
        this.$router.push({ name: 'ServicesExecute', query: { workspaceId: this.$route.query.workspaceId, id: e.id } })
      }
    },
    commonAction(row, arg) { // 更多操作的id
      const id = arg[0];
      if (id === 1) { // 跳到管理页
        sessionStorage.setItem('apiservicesCurrentPage', this.page.pageNow)
        this.$router.push({path: '/servicesMangement',query: {apiId: arg[0], workspaceId: this.$route.query.workspaceId, id: row.id, scriptPath: row.scriptPath }})
      } else if(id === 2) { // 禁用、启用
        // 发起请求
        let title = '';
        let content = '';
        let path = '';
        if(Number(!row.status) === 0) {
          title = this.$t('message.apiServices.tip.disableConfirmTitle');
          content = this.$t('message.apiServices.tip.disableConfirm', { name: row.name });
          path = 'apiDisable';
        } else {
          title = this.$t('message.apiServices.tip.enableConfirmTitle');
          content = this.$t('message.apiServices.tip.enableConfirm', { name: row.name });
          path = 'apiEnable';
        }
        this.$Modal.confirm({
          title,
          content,
          onOk: () => {
            api.fetch(`/dss/apiservice/${path}`, {
              id: row.id
            }, 'get').then(() => {
              this.getApiData();
              this.$Message.success("Success")
            }).catch(() => {
              this.$Message.error("Failed")
            });
          }
        });
      } else if(id === 3) { // 更新
        // TODO 由于缺少脚本内容暂时屏蔽此功能
        if(id === 3) return this.$Message.warning("暂未开放！Not open yet!")
        // 表单结构及初始数据value
        // 参考iview动态渲染
        // items.type 为渲染的表单类型，对应iview表单
        // items.label 为左侧名称
        // items.value 为初始值
        // items.prop 对应验证规则的key及返回传参的key
        // items.option 对应多选菜单或单选的渲染，参数形式对应iview的数据结构
        // items.property 对应iview表单的设置参数，例如：'placeholder'、'type' 等
        this.currentData = row;
        // 将参数copy出来不影响原数组
        this.paramList = this.currentData.params.map(item => {
          return { ...item }
        })
        let make = {
          items: [
            { type: "input",
              label: this.$t('message.apiServices.updateApiModal.selectApi'),
              value: this.currentData.name,
              prop: "name",
              property: { placeholder: this.$t('message.apiServices.placeholder.apiName'), disabled: true }
            },
            { type: "input",
              label: this.$t('message.apiServices.updateApiModal.apiPath'),
              value: this.currentData.path,
              prop: "path",
              property: { placeholder: "Rule: /user/info", disabled: true }
            }
          ]
        }
        this.formMake = make;
        // 表单验证
        this.formRules = Object.assign(this.formRules, {
          'name': [
            {
              required: true,
              message: "The name cannot be empty",
              trigger: "blur"
            }
          ],
          'path': [
            {
              required: true,
              message: this.$t('message.apiServices.rule.pathRule')
            },
            {
              message: this.$t('message.apiServices.rule.contentLengthLimit'),
              max: 255
            },
            {
              pattern: /^([\/][\w-]+)*$/,
              message: this.$t('message.apiServices.rule.pathRegRule'),
            }
          ],
        })
        // 表单标题
        this.formTitle = this.$t('message.apiServices.formTitle.dataApiManage');
        this.isShowForm = true
      } else if(id === 4) {
        this.$Modal.confirm({
          title: this.$t('message.apiServices.enterDetele'),
          content: this.$t('message.apiServices.enterDeteleName', { name: row.aliasName }),
          onOk: () => {
            api.fetch(`/dss/apiservice/apiDelete`, {
              id: row.id
            }, 'get').then(() => {
              this.$Message.success("Success")
              this.getApiData();
            }).catch(() => {
              this.$Message.error("Failed")
            });
          }
        });
      }
    },
    handleSubmit(e) {
      if(e) {
        api.fetch('/dss/apiservice/api/' + this.currentData.latestVersionId, {
          ...this.currentData,
          ...e,
          content: undefined,
          params: this.paramList,
        }, 'put').then(() => {
          this.isShowForm = false
          this.$Message.success(this.$t('message.apiServices.notice.publishSuccess'));
        }).catch(() => {
          this.isShowForm = false
        });
      }
    },
    handleCancel() {
      this.isShowForm = false
    },
  }
}
</script>

<style lang="scss" scoped>
@import "@dataspherestudio/shared/common/style/variables.scss";
.main.service-cener {
  padding: 20px 50px;
  box-sizing: border-box;
  height: $percent-all;
  display: flex;
  flex-direction: column;
  overflow: auto;
  .title, .search-bar, .workspace-header-right, .page-bar {
    flex: none;
  }
  .mainContent {
    // flex: 1;
    box-sizing: border-box;
  }
  .tap-bar {
    @include bg-color($light-base-color, $dark-base-color);
    margin-bottom: $padding-25;
    padding-bottom: $padding-25;
    border-bottom: $border-width-base $border-style-base #dcdcdc;
    @include border-color($border-color-base, $dark-menu-base-color);
  }
  .title {
    font-size: $font-size-large;
    font-weight: 600;
    display: flex;
    align-items: $align-center;
    @include font-color(rgba(0, 0, 0, 0.65), $dark-text-color);
    .back {
      margin-right: $margin;
      padding-right: $padding;
      font-size: 1.5rem;
      position: relative;
      cursor: pointer;
      font-weight: 700;
      &::after {
        content: "";
        position: absolute;
        border-right: $border-width-base $border-style-base $border-color-base;
        height: 60%;
        top: 50%;
        right: 0;
        transform: translateY(-50%);
        width: 0;
      }
    }
  }
  .search-bar {
    margin-top: $margin;
    .search-item {
      display: flex;
      justify-content: flex-start;
      align-items: $align-center;
      font-size: $font-size-base;
      margin-right: 30px;
      .lable {
        flex-basis: 100px;
        text-align: $align-center;
      }
    }
    .search {
      margin-right: 15px;
    }
  }
  .page-bar {
    text-align: center;
    padding: 20px 0;
  }
  .workspace-header-right {
    text-align: right;
    padding-top: 20px;
    display: flex;
    align-items: $align-center;
    justify-content: space-between;
    .tabs-content {
      max-width: calc(100% - 150px);
    }
    .showAll {
      margin: 0 10px;
      color: $primary-color;
      cursor: pointer;
    }
    .change-view {
      margin-top: -12px;
      flex-basis: 100px;
    }
  }
  .tag-box {
    height: 100%;
    display: flex;
    justify-content: space-around;
    align-items: $align-center;
    flex-wrap: wrap;
    .tag-item {
      box-sizing: border-box;
      line-height: 1.1;
      border: $border-width-base $border-style-base $border-color-split;
      border-radius: $border-radius-small;
      font-size: $tag-font-size;
      padding: 0 4px 0 4px;
      margin: 2px 0 2px 0;
    }
  }
}
</style>
