<template>
  <div>
    <Search @search="search" />
    <Table class="table-content" border :width="tableWidth" :columns="columns" :data="tableData">
      <template slot-scope="{row}" slot="engineInstance">
        <span>{{`${row.engineType}:${row.instance}`}}</span>
      </template>
      <template slot-scope="{row}" slot="usedResource">
        <span>Linkis:({{`${calcCompany(row.resource.usedResource.cores)}vcores,${calcCompany(row.resource.usedResource.memory, true)}G`}})</span>
      </template>
      <template slot-scope="{row}" slot="maxResource">
        <span>Linkis:({{`${calcCompany(row.resource.maxResource.cores)}vcores,${calcCompany(row.resource.maxResource.memory, true)}G`}})</span>
      </template>
      <template slot-scope="{row}" slot="minResource">
        <span>Linkis:({{`${calcCompany(row.resource.minResource.cores)}vcores,${calcCompany(row.resource.minResource.memory, true)}G`}})</span>
      </template>
      <template slot-scope="{row}" slot="startTime">
        <span>{{ timeFormat(row) }}</span>
      </template>
    </Table>
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
    <Modal
      @on-ok="submitTagEdit"
      :title="$t('message.linkis.tagEdit')"
      v-model="isTagEdit"
      :mask-closable="false">
      <Form :model="formItem" :label-width="80">
        <FormItem :label="`${$t('message.linkis.instanceName')}：`">
          <Input disabled v-model="formItem.engineInstance" ></Input>
        </FormItem>
        <FormItem class="addTagClass" :label="`${$t('message.linkis.tableColumns.label')}：`">
          <Tag v-for="(item, index) in formItem.label" :key="item.key" :name="item.key" closable @on-close="handleClose($event, index)">{{item.value}}</Tag>
          <Button v-show="addTag" icon="ios-add" type="dashed" size="small" @click="handleAdd">Add</Button>
          <Form v-show="!addTag" ref="addTagForm" :model="addTagForm" :rules="addTagFormRule">
            <Row>
              <Col span="4">
              <FormItem prop="key">
                <Input v-model="addTagForm.key" placeholder="key" size="small" />
              </FormItem>
              </Col>
              <Col span="1" style="text-align: center;"> : </Col>
              <Col span="4">
              <FormItem prop="value">
                <Input v-model="addTagForm.value" placeholder="value" size="small" />
              </FormItem>
              </Col>
              <Col span="2" offset="1">
              <FormItem>
                <Button type="success" icon="md-checkmark" size="small" @click="confirmAddTag" />
              </FormItem>
              </Col>
              <Col span="2">
              <FormItem>
                <Button type="error" icon="md-close" size="small" @click="handleCancelAdd" />
              </FormItem>
              </Col>
            </Row>
          </Form>
        </FormItem>
        <FormItem :label="`${$t('message.linkis.tableColumns.status')}：`">
          <Select v-model="formItem.status">
            <Option value="beijing">New York</Option>
            <Option value="shanghai">London</Option>
            <Option value="shenzhen">Sydney</Option>
          </Select>
        </FormItem>
      </Form>
    </Modal>
  </div>
</template>
<script>
import api from '@/common/service/api';
import moment from "moment";
import Search from '@/apps/linkis/module/ECM/search.vue';
export default {
  data() {
    return {
      loading: false,
      formItem: {
        engineInstance: '',
        label: [],
        status: 'male',
      },
      tagTitle: [],
      applicationList: {},
      addTag: true, // 是否显示添加tag按钮
      addTagForm: { // 新增标签的form表单
        key: '',
        value: ''
      },
      currentEngineData: {}, // 当前点击的引擎列表
      currentParentsData: {}, // 当前点击的主引擎
      isShowTable: false,
      addTagFormRule: { // 验证规则
        key: [
          { required: true, message: this.$t('message.linkis.keyTip'), trigger: 'blur' }
        ]
      },
      progressDataList: [],
      tableWidth: 0,
      // 开启标签修改弹框
      isTagEdit: false,
      tableData: [],
      page: {
        totalSize: 0,
        sizeOpts: [15, 30, 45],
        pageSize: 15,
        pageNow: 1
      },
      columns: [
        {
          title: this.$t('message.linkis.tableColumns.engineInstance'),
          key: 'engineInstance',
          minWidth: 150,
          className: 'table-project-column',
          slot: 'engineInstance'
        },
        {
          title: this.$t('message.linkis.tableColumns.engineType'),
          key: 'engineType',
          minWidth: 100,
          className: 'table-project-column'
        },
        {
          title: this.$t('message.linkis.tableColumns.status'),
          key: 'status',
          minWidth: 100,
          className: 'table-project-column',
        },
        {
          title: this.$t('message.linkis.tableColumns.label'),
          key: 'label',
          minWidth: 150,
          className: 'table-project-column',
        },
        {
          title: this.$t('message.linkis.tableColumns.usedResources'),
          key: 'usedResource',
          className: 'table-project-column',
          slot: 'usedResource',
          minWidth: 150,
        },
        {
          title: this.$t('message.linkis.tableColumns.maximumAvailableResources'),
          key: 'maxResource',
          slot: 'maxResource',
          className: 'table-project-column',
          minWidth: 150,
        },
        {
          title: this.$t('message.linkis.tableColumns.minimumAvailableResources'),
          key: 'minResource',
          slot: 'minResource',
          minWidth: 150,
          className: 'table-project-column',
        },
        {
          title: this.$t('message.linkis.tableColumns.requestApplicationName'),
          key: 'owner',
          className: 'table-project-column',
          minWidth: 150,
        },
        {
          title: this.$t('message.linkis.tableColumns.startTime'),
          key: 'startTime',
          className: 'table-project-column',
          slot: 'startTime',
          minWidth: 150,
        },
        {
          title: this.$t('message.linkis.tableColumns.control.title'),
          key: 'action',
          width: '215',
          fixed: 'right',
          align: 'center',
          render: (h, params) => {
            return h('div', [
              h('Button', {
                props: {
                  type: 'error',
                  size: 'small'
                },
                style: {
                  marginRight: '5px'
                },
                on: {
                  click: () => {
                    console.log(params)
                    this.$Modal.confirm({
                      title: this.$t('message.linkis.stop'),
                      content: '请问确认要停止当前引擎？',
                      onOk: () => {
                        let data = [];
                        data.push({
                          engineType: 'EngineConn', // 当期需求是写死此参数
                          engineInstance: params.row.instance,
                        });
                        api.fetch(`/linkisManager/rm/enginekill`, data).then(() => {
                          let obj = this.currentParentsData.id === this.currentEngineData.id ? { item: this.currentParentsData, isChildren: false } : { item: this.currentEngineData, isChildren: true }
                          this.expandChange(obj.item, obj.isChildren);
                          this.$Message.success({
                            background: true,
                            content: 'Delete Success！！'
                          });
                        }).catch((err) => {
                          console.err(err)
                        });
                      }
                    })
                  }
                }
              }, this.$t('message.linkis.stop')),
              h('Button', {
                props: {
                  type: 'primary',
                  size: 'small'
                },
                on: {
                  click: () => {
                    return this.$Message.success({
                      background: true,
                      content: '暂未开放！！'
                    });
                    // 待开启
                    // this.isTagEdit = true;
                    // let obj = {};
                    // obj.engineInstance = params.row.engineInstance;
                    // obj.label = params.row.label;
                    // obj.status = params.row.status;
                    // this.formItem = Object.assign(this.formItem, obj)
                  }
                }
              }, this.$t('message.linkis.tagEdit'))
            ]);
          }
        }
      ],
    }
  },
  components: {
    Search,
  },
  computed: {
    pageDatalist() {// 展示的数据
      return this.tableData.filter((item, index) => {
        return (this.page.pageNow - 1) * this.page.pageSize <= index && index < this.page.pageNow * this.page.pageSize;
      })
    }
  },
  created() {
    this.initExpandList();
  },
  methods: {
    // 刷新进度条
    refreshResource() {
      this.initExpandList();
    },
    // 初始化引擎列表
    async initExpandList() {
      // 获取引擎数据
      this.loading = true;
      try {
        let engines = await api.fetch('/linkisManager/rm/userresources','post') || {};
        // 获取使用的引擎资源列表
        let enginesList = engines.userResources || [];
        enginesList.forEach((userItem, userIndex) => {
          userItem.id = new Date().valueOf() + userIndex * 1000; // 设置唯一id,将时间多乘以2000防止运行过慢导致的id重复
          // 处理engineTypes，计算百分比
          userItem.engineTypes.forEach((engineItem,engineIndex) => {
            engineItem.id = new Date().valueOf() + (engineIndex + 1 + userIndex ) * 2000; // 设置唯一id,将时间多乘以2000防止运行过慢导致的id重复
          })
        })
        this.progressDataList = enginesList;
        this.expandChange(enginesList[0])
        this.loading = false;
      } catch (err) {
        console.log(err)
        this.loading = false;
      }
    },
    // 展开和关闭
    async expandChange(item, isChildren) {
      // 显示表格
      this.isShowTable = true;
      // 如果点击的不为子列表则缓存下当前数据
      // 如果两次点击同一元素则阻止
      if(item.id === this.currentEngineData.id) return;
      // if(item.id === this.currentParentsData.id && !isChildren) return;
      this.currentEngineData = item;
      this.loading = true;
      // 资源标签显示
      this.tagTitle = [];
      if(!isChildren) {
        this.currentParentsData = item;
        this.tagTitle = Object.assign(this.tagTitle, [item.userCreator])
      } else {
        this.tagTitle = Object.assign(this.tagTitle, [this.currentParentsData.userCreator, item.engineType])
      }
      let parameter = {
        userCreator: this.currentParentsData.userCreator,
        engineType: isChildren ? this.currentEngineData.engineType : undefined
      };
      try {
        let engines = await api.fetch('/linkisManager/rm/applicationlist', parameter, 'post') || {};
        this.loading = false;
        let engineInstances = engines.applications[0].applicationList;
        this.applicationList = engineInstances || {};
        this.tableData = engineInstances.engineInstances || [];
        this.page.totalSize = this.tableData.length;
      } catch (errorMsg) {
        console.error(errorMsg);
        this.loading = false;
      }
    },
    // 添加tag
    handleAdd () {
      this.addTagForm = { // 新增标签的form表单
        key: '',
        value: ''
      };
      this.addTag = false;
    },
    // 取消tag添加
    handleCancelAdd() {
      this.addTag = true;
    },
    // 删除tag
    handleClose (event, index) {
      this.formItem.label.splice(index, 1);
    },
    // 提交tag
    confirmAddTag() {
      let key = this.addTagForm.key;
      let value = this.addTagForm.value;
      this.formItem.label.push({ key, value });
      this.addTag = true;
    },
    //  提交修改
    submitTagEdit() {
      console.log('我提交了')
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
    // 搜索
    search(e) {
      console.log(e, '我在搜索中。。。')
    },
    // 时间格式转换
    timeFormat(row) {
      return moment(new Date(row.startTime)).format('YYYY-MM-DD HH:mm:ss')
    },
    calcCompany(num, isCompany = false) {
      let data = num > 0 ? num : 0;
      if(isCompany) {
        return data / 1024 / 1024 / 1024;
      }
      return data;
    }
  }
}
</script>

<style src="./index.scss" lang="scss" scoped></style>

