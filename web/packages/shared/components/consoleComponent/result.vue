<template>
  <div ref="view" class="we-result-view">
    <we-toolbar
      ref="toolbar"
      :current-path="result.path"
      :script="script"
      :row="hightLightRow"
      :visualShow="visualShow"
      :dispatch="dispatch"
      :getResultUrl="getResultUrl"
      :work="work"
      :result-type="resultType"
      @on-filter="handleFilterView"
      @change-view-type="changeViewType"
    />
    <we-filter-view
      v-if="isFilterViewShow"
      :head-rows="heads"
      :checked="checkedFields"
      :height="resultHeight"
      @on-check="filterList"
    ></we-filter-view>
    <Spin v-show="isLoading" size="large" fix />
    <!-- resultType: 1 -->
    <div
      v-if="resultType === '1'"
      :style="{ height: resultHeight + 'px' }"
      class="text-result-div"
    >
      <div v-if="result.bodyRows">
        <!-- 数据格式不统一，先循环外部数据，再循环内部 -->
        <div v-for="(item, index) in result.bodyRows" :key="index">
          <div v-for="(row, subindex) in item[0].split('\n')" :key="subindex">
            {{ row }}
          </div>
        </div>
      </div>
      <span v-else class="empty-text"></span>
    </div>
    <!-- resultType: 2 table -->
    <div
      v-if="resultType === '2' && visualShow === 'table'"
      class="result-table-content"
      :class="{
        'table-box': tableData.type === 'normal',
        noselectable: baseinfo.resCopyEnable === false,
      }"
    >
      <template v-if="tableData.type === 'normal' && !result.hugeData">
        <wb-table
          ref="resultTable"
          border
          highlight-row
          outer-sort
          :tableHeight="resultHeight"
          :columns="data.headRows"
          :tableList="data.bodyRows"
          :adjust="true"
          :positionMemoryKey="result.path || ''"
          @on-table-scroll-change="tableScrollChange"
          @on-current-change="onRowClick"
          @on-head-dblclick="copyLabel"
          @on-sort-change="sortChange"
          @on-tdcontext-munu="handleTdContextmenu"
          class="result-normal-table"
        >
        </wb-table>
      </template>
      <template v-else-if="!result.hugeData">
        <we-table
          ref="resultTable"
          :data="data"
          :width="tableData.width"
          :height="resultHeight"
          :size="tableData.size"
          :positionMemoryKey="result.path || ''"
          @on-table-scroll-change="tableScrollChange"
          @dbl-click="copyLabel"
          @on-click="onWeTableRowClick"
          @on-sort-change="sortChange"
          @on-tdcontext-munu="handleTdContextmenu"
        />
      </template>
      <div
        v-if="result.hugeData"
        :style="{ height: resultHeight + 'px', padding: '15px' }"
      >
        <p v-if="result.tipMsg">{{ result.tipMsg }}</p>
        <p v-else>{{ $t('message.common.resulttip')}}
          <a
            :href="`/#/results?workspaceId=${
              $route.query.workspaceId
            }&resultPath=${resultPath}&fileName=${
              script.fileName || script.title
            }&from=${$route.name}&taskID=${taskID}`"
            target="_blank"
          >{{ $t('message.common.viewSet') }}</a>
        </p>
      </div>
    </div>
    <!-- resultType: 2 visual|dataWrangler -->
    <template v-for="(comp, index) in extComponents">
      <component
        v-if="visualShow == comp.name"
        :key="comp.name + index"
        :is="comp.component"
        :script="script"
        :work="work"
        :style="{ height: resultHeight + 'px' }"
      />
    </template>
    <!-- resultType: 4 -->
    <div v-if="resultType === '4'">
      <iframe
        id="iframeName"
        :style="{ height: resultHeight + 'px' }"
        :srcdoc="htmlData"
        frameborder="0"
        width="100%"
        @change="iframeOnChange()"
      />
    </div>
    <!-- resultType: 5 -->
    <div
      v-if="resultType === '5'"
      :style="{ height: resultHeight + 'px' }"
      class="html-result-div"
      v-html="result.bodyRows[0][0]"
    />
    <!-- 结果集列表 -->
    <div class="we-page-container" v-if="visualShow === 'table'">
      <result-set-list
        v-if="script.resultList && script.resultList.length > 1"
        :current="script.resultSet - 0"
        :list="script.resultList"
        @change="changeSet"
      >
      </result-set-list>
      <div class="page" v-if="resultType === '2' && !result.hugeData">
        <p v-if="result.tipMsg" :title="result.tipMsg" style="margin-right: 20px;max-width: calc(100% - 250px);overflow: hidden;text-overflow: ellipsis;">{{ result.tipMsg }}</p>
        <Page
          :transfer="true"
          ref="page"
          :total="tableData.total"
          :page-size-opts="page.sizeOpts"
          :page-size="page.size"
          :current="page.current"
          size="small"
          show-total
          show-sizer
          @on-change="change"
          @on-page-size-change="changeSize"
        />
        <span v-if="tableData.total >= 5000">{{
          $t('message.common.only500')
        }}</span>
      </div>
    </div>
    <Modal
      v-model="modal.show"
      :width="600"
      :fullscreen="modal.isFullScreen"
      closable
      footer-hide
      class='modal-detail'
    >
      <div slot="header">
        <span>{{$t('message.common.detail')}}</span>
        <span @click="fullScreenModal" class="full-btn">
          <Icon :type="modal.isFullScreen?'md-contract':'md-expand'" />
          {{modal.isFullScreen?$t('message.scripts.cancelfullscreen'):$t('message.scripts.fullscreen')}}
        </span>
      </div>
      <div class='modal-content'>
        {{modal.content}}
      </div>
    </Modal>
  </div>
</template>
<script>
import {
  dropRight,
  toArray,
} from 'lodash';
import util from '@dataspherestudio/shared/common/util'
import Table from '@dataspherestudio/shared/components/virtualTable'
import WbTable from '@dataspherestudio/shared/components/table'
import WeToolbar from './toolbar.vue'
import resultSetList from './resultSetList.vue'
import filter from './filter.vue'
import mixin from '@dataspherestudio/shared/common/service/mixin'
import storage from '@dataspherestudio/shared/common/helper/storage'
import plugin from '@dataspherestudio/shared/common/util/plugin'
import api from '@dataspherestudio/shared/common/service/api'

const extComponents = plugin.emitHook('script_result_type_component') || []
/**
 * 脚本执行结果集tab面板
 * ! 1. 与工作流节点执行后的管理台console.vue 共用
 */
export default {
  components: {
    WbTable,
    WeTable: Table.WeTable,
    WeToolbar,
    resultSetList,
    WeFilterView: filter,
  },
  props: {
    script: [Object],
    work: Object,
    scriptViewState: Object,
    dispatch: {
      type: Function,
      required: true,
    },
    getResultUrl: {
      type: String,
      defalut: `filesystem`,
    },
  },
  mixins: [mixin],
  data() {
    return {
      modal: {
        show: false,
        isFullScreen: false,
        content: '',
      },
      data: {
        headRows: [],
        bodyRows: [],
      },
      heads: [],
      tableData: {
        type: 'normal',
        width: 1000,
        size: 20,
        total: 100,
      },
      page: {
        current: 1,
        size: 50,
        sizeOpts: [20, 50, 80, 100],
      },
      isLoading: false,
      // 当前高亮的行
      hightLightRow: null,
      isFilterViewShow: false,
      visualShow: 'table',
      extComponents,
      baseinfo: {},
      checkedFields: [],
      result: {
        headRows: [],
        bodyRows: [],
        type: 'EMPTY',
        total: 0,
        path: '',
        cache: {},
      },
      currentSesitiveResult: {
        idx: '0',
        total: 0
      }
    }
  },
  computed: {
    htmlData() {
      return this.result.bodyRows[0] && this.result.bodyRows[0][0]
    },
    resultType() {
      return this.result.type
    },
    resultHeight() {
      return this.scriptViewState.bottomContentHeight - 34 - 35 // 减去tab,分页高度
    },
    resultPath() {
      let path = ''
      if (this.script.resultList && this.script.resultSet !== undefined) {
        path = this.script.resultList[this.script.resultSet].path
      }
      return path
    },
    taskID() {
      return this.work
        ? this.work.taskID ||
            (this.work.data &&
              this.work.data.history &&
              this.work.data.history[0] &&
              this.work.data.history[0].taskID)
        : this.$route.query.taskId || ''
    },
  },
  watch: {
    $route(val) {
      if (val.path === '/home') {
        this.data.headRows = this.data.headRows.slice()
      }
    },
    scriptViewState: {
      deep: true,
      handler: function (n, o) {
        if (n.columnPageNow != o.columnPageNow) {
          storage.remove('column_show_'+ this.result.path)
          this.isFilterViewShow = false;
          this.checkedFields = [];
          this.updateData('changeColumnPage')
        }
      },
    }
  },
  mounted() {
    this.baseinfo = storage.get('baseInfo', 'local') || {}
    this.updateData('mounted')
  },
  beforeDestroy: function () {},
  methods: {
    initPage() {
      if (this.result.current && this.result.size) {
        this.page = Object.assign(
          { ...this.page },
          {
            current: this.result.current,
            size: this.result.size,
          }
        )
      }
      if (this.result.path && sessionStorage.getItem('table_position_' + this.result.path)) {
         let curTablePositionData = JSON.parse(sessionStorage.getItem('table_position_' + this.result.path))
          this.page.current = curTablePositionData.currentPage
          this.page.size = curTablePositionData.pageSize
      }
      this.change(this.page.current)
    },
    sortByCol(col) {
      let sortIndex
      this.data.headRows.map((head, index) => {
        if (head.content === col.content) {
          sortIndex = index
        }
      })
      // 大于50列排序现将要排序的列和原始index保持
      let sortColumnAll = this.originRows.map((row, index) => {
        return {
          originIndex: index,
          value: row[sortIndex],
        }
      })
      // 将找出的列排序
      sortColumnAll = this.arraySortByName(
        sortColumnAll,
        col.columnType,
        'value'
      ) // 从小到大
      let newRow = []
      sortColumnAll.map((item, index) => {
        newRow[index] = this.originRows[item.originIndex]
      })
      return newRow
    },
    sortChange({ column, key, order, cb }) {
      let list = []
      if (order === 'normal') {
        // 恢复原来数据
        if (this.tableData.type === 'normal') {
          list = this.result.bodyRows.map((row) => {
            let newItem = {}
            row.forEach((item, index) => {
              Object.assign(newItem, {
                [`${this.result.headRows[index].columnName}_${index}`]: item,
              })
            })
            return newItem
          })
        } else {
          list = this.result.bodyRows || []
        }
      } else {
        if (this.tableData.type === 'normal') {
          list = this.arraySortByName(this.originRows, column.columnType, key) // 从小到大
        } else {
          list = this.sortByCol(column)
        }

        if (order === 'asc') {
          // 升序
        } else if (order === 'desc') {
          // 降序
          list.reverse()
        }
      }
      if (cb == 'sort') {
        return list
      }
      this.originRows = list
      const tmpResult = this.getResult()
      tmpResult.sortType = order
      tmpResult.sortKey = key
      tmpResult.current = this.page.current
      tmpResult.size = this.page.size
      const resultSet = this.script.resultSet || 0
      this.$set(this.script.resultList[resultSet], 'result', tmpResult)
      this.updateCache({
        resultSet: resultSet,
        showPanel: this.scriptViewState.showPanel,
      })
      this.pageingData()
      if (cb) {
        cb()
      }
    },
    fullScreenModal() {
      this.modal.isFullScreen = !this.modal.isFullScreen
    },
    handleTdContextmenu({ content }) {
      if (content.split(/\n/).length > 1) {
        this.modal.show = true;
        this.modal.content = content;
        this.modal.isFullScreen = false;
      }
    },
    arraySortByName(list, valueType, key) {
      if (list === undefined || list === null) return []
      list.sort((a, b) => {
        let strA = a[key]
        let strB = b[key]
        // 谁为非法值谁在前面
        if (
          strA === undefined ||
          strA === null ||
          strA === '' ||
          strA === ' ' ||
          strA === '　' ||
          strA === 'NULL'
        ) {
          return -1
        }
        if (
          strB === undefined ||
          strB === null ||
          strB === '' ||
          strB === ' ' ||
          strB === '　' ||
          strB === 'NULL'
        ) {
          return 1
        }
        // 如果为整数型大小
        if (
          [
            'int',
            'float',
            'double',
            'long',
            'short',
            'bigint',
            'decimal',
          ].includes(valueType.toLowerCase())
        ) {
          return strA - strB
        }
        if (
          ['timestamp'].includes(valueType.toLowerCase()) &&
          typeof strA === 'string'
        ) {
          return strA.localeCompare(strB)
        }
        const charAry = strA.split('')
        for (const i in charAry) {
          if (this.charCompare(strA[i], strB[i]) !== 0) {
            return this.charCompare(strA[i], strB[i])
          }
        }
        // 如果通过上面的循环对比还比不出来，就无解了，直接返回-1
        return -1
      })
      return list
    },
    charCompare(charA, charB) {
      // 谁为非法值谁在前面
      if (
        charA === undefined ||
        charA === null ||
        charA === '' ||
        charA === ' ' ||
        charA === '　'
      ) {
        return -1
      }
      if (
        charB === undefined ||
        charB === null ||
        charB === '' ||
        charB === ' ' ||
        charB === '　'
      ) {
        return 1
      }
      return charA.localeCompare(charB)
    },
    updateData(type) {
      /**
     * result.type
     * TEXT_TYPE: '1'
     * TABLE_TYPE: '2'
     * IO_TYPE: '3'
     * PICTURE_TYPE: '4'
     * HTML_TYPE: '5'
     */
      if (this.script.result && this.script.result.type === 'RES_EMPTY') { // 单独结果集页面传路径过来
        this.getResultData(1)
        return
      }
      let result = this.result
      if (this.script.resultList && this.script.resultSet !== undefined) {
        result = this.script.resultList[this.script.resultSet].result
      }
      if (!result && this.script.result) {
        result = this.script.result
      }
      if ('dss/apiservice' == this.getResultUrl) {
        // 数据服务
        let columnPageNow = type == 'changeColumnPage' ? this.scriptViewState.columnPageNow : 1;
        this.getResultData(columnPageNow)
      } else if (this.script.nodeId) {
        // 工作流
        this.dispatch('workflowIndexedDB:getNodeCache', {
          nodeId: this.script.nodeId,
          cb: (cache) => {
            if (cache) {
              const {resultList, resultSet} = cache
              if (resultList) {
                this.script.resultList = resultList;
                this.script.resultSet = resultSet;
                let result = this.script.resultList[this.script.resultSet] 
                let columnPageNow = type == 'changeColumnPage' ? this.scriptViewState.columnPageNow : result && result.result ? result.result.columnPageNow : 1;
                this.getResultData(columnPageNow)
                return
              }
            }
            this.getResultData(1)
          }
        });
      } else if(this.script.id) {
        // scriptis
        this.dispatch('IndexedDB:getResult', {
          tabId: this.script.id,
          cb: (resultList) => {
            if (resultList) {
              this.script.resultList = dropRight(toArray(resultList), 3);
              this.script.resultSet = resultList.resultSet;
              let result = this.script.resultList[this.script.resultSet]; 
              let columnPageNow = type == 'changeColumnPage' ? this.scriptViewState.columnPageNow : result && result.result ? result.result.columnPageNow : 1;
              this.getResultData(columnPageNow)
            } else {
              this.getResultData(1)
            }
          }
        });
      }
    },
    checkResult(resultPath) {
      let taskId = this.taskID
      if (!taskId) { // fix dpms 523552
        const dirs = resultPath.split('/')
        taskId = dirs[dirs.length - 2]
      }
      const paths = this.script.resultList.map(item => item.path);
      // 已扣减过流量的直接通过
      let hasDeducted = storage.get('senitive_result_'+ this.baseinfo.username) || [];
      hasDeducted = hasDeducted.some(it => it == taskId);
      let scriptType = '';
      let notCheck = ['ServicesExecute'].includes(this.$route.name);
      // scriptis
      if (this.script.scriptType) {
        scriptType = this.script.runType;
        notCheck = notCheck || !['hive', 'hql', 'jdbc'].includes(this.script.scriptType)
      }
      // 工作流节点
      if (this.script.nodeId && this.$parent.node) {
        const nodeTypeMap = {'linkis.spark.sql': 'sql', 'linkis.hive.hql': 'hql', 'linkis.jdbc.jdbc': 'jdbc'}
        scriptType = nodeTypeMap[this.$parent.node.type]
      }
      if (hasDeducted || notCheck || !scriptType) {
        return Promise.resolve(false);
      }
      // 检查结果集是否需要扣减用量
      const params = {
        taskId: taskId,
        scriptType,
        paths: paths
      };
      return new Promise((resolve, reject) => {
        api.fetch('/dss/datapipe/dataset/checkDatasetSensitive', params, 'post').then(res => {
          const result = res.result || []
          const sensitiveResult = []
          result.forEach((item, idx) => {
            if (item.hasSensitiveData) {
              sensitiveResult.push({
                ...item,
                nameIndex: idx + 1
              })
            }
          })
          if (sensitiveResult.length) {
            this.currentSesitiveResult = {
              ...this.currentSesitiveResult,
              ...sensitiveResult[0]
            }
            const getTableData = (idx) => {
              idx = idx - 0
              this.currentSesitiveResult = {
                idx: `${idx}`,
                ...sensitiveResult[idx]
              }
            }
            this.$Modal.confirm({
              title: '疑似包含企业明文信息',
              render: (h) => {
                const options = [{
                  title: '字段名',
                  key: 'name'
                }, {
                  title: '是否包含企业明文',
                  key: 'isContain'
                }];
                return h('div', [
                  h('p', '疑似包含企业明文信息, 是否确认查看？'),
                  h("Table", {
                    style: {
                      'margin-top': '10px'
                    },
                    props: {
                      size: "small",
                      columns: options,
                      data: Object.keys(this.currentSesitiveResult.metadata).map(it => {
                        return {
                          name: it,
                          isContain: this.currentSesitiveResult.metadata[it]
                        }
                      }),
                      height: Object.keys(this.currentSesitiveResult.metadata).length * 40 > 400 ? 400 : (Object.keys(this.currentSesitiveResult.metadata).length + 1 ) * 40
                    }
                  }),
                  h('div', {
                    style: {
                      'margin-top': '10px'
                    },
                  }, [
                    h("Select", {
                      style: {
                        width: '100px',
                        marginRight: '100px'
                      },
                      props: {
                        size: "small",
                        value: this.currentSesitiveResult.idx
                      },
                      on: {
                        'on-change': (value) => {
                          getTableData(value)
                        }
                      }
                    }, sensitiveResult.map((it,idx) => {
                        return h("Option", {
                          props: {
                            value: `${idx}`,
                            label: `结果集${it.nameIndex}`
                          }
                        })
                      })
                    ),
                    `结果集条数：${this.currentSesitiveResult.total}`
                  ])
                ])
              },
              onOk: () => {
                this.useResult(taskId, paths, resolve)
              },
              onCancel: () => {
                resolve(true)
              }
            });
          } else {
            // 不包含敏感信息
            const hasDeducted = storage.get('senitive_result_'+ this.baseinfo.username) || [];
            storage.set('senitive_result_'+ this.baseinfo.username, [...hasDeducted,  taskId]);
            resolve(false)
          }
        }).catch(() => {
          resolve(false)
        })
      })
    },
    useResult(taskId, paths, resolve) {
      // 扣减用量
      const params = {
        paths,
        taskId
      }
      api.fetch('/dss/datapipe/dataset/deductQuota', params, 'post').then(res => {
        let msg = ''
        if (res.result && !res.result.needDeduct) {
          const hasDeducted = storage.get('senitive_result_'+ this.baseinfo.username) || [];
          storage.set('senitive_result_'+ this.baseinfo.username, [...hasDeducted,  taskId]);
          return resolve(false)
        }
        if (res.result.isSuccess) {
          msg = `包含企业信息明文，扣减用量${res.result.dataSetSize}，剩余用量${res.result.quota}quota`
          this.$Message.success(msg)
          const hasDeducted = storage.get('senitive_result_'+ this.baseinfo.username) || [];
          storage.set('senitive_result_'+ this.baseinfo.username, [...hasDeducted,  taskId]);
          resolve(false)
        } else {
          const fileds = Object.keys(res.result.metadata)
          msg = `用户因如下字段疑似包含企业明文信息（字段清单为 ${fileds.join('、')}），无法直接展示；您当前可查看不超过${res.result.quota}条企业信息明文，如需申请查看更多企业信息明文，请联系CIB部门数据协管员`
          setTimeout(() => {
            this.$Modal.info({
              title: '提示',
              content: `<p style="word-break: break-all;max-height: 470px;overflow-y:auto">${msg}</p>`,
              closable: true,
              width: 650
            });
          }, 400)
          resolve(true)
        }
      }).catch(err => {
        resolve(true)
      })
    },
    async getResultData(columnPageNow) {
      let result = this.script.resultList ? this.script.resultList[this.script.resultSet] : this.script.result
      if (!result || !result.path) {
        return
      }
      this.checkedFields = storage.get('column_show_'+ result.path) || [];
      this.scriptViewState.columnPageNow = columnPageNow
      // 获取列分页数据
      // 表格数据每次从接口拉取，不使用indexedDB的缓存
      const url = `${this.getResultUrl}/openFile`;
      const resultPath = result.path;
      this.isLoading = true;
      let ret = {};
      let hasSorted = false;
      if (result && result.result && result.result.sortType) {
        hasSorted = {
          sortType: result.result.sortType,
          sortKey: result.result.sortKey,
        };
      }
      const params = {
        path: resultPath,
        columnPage: columnPageNow || 1,
        enableLimit: true,
        pageSize: 5000,
      }
      if ('dss/apiservice' == this.getResultUrl) {
        params.taskId = this.taskID
      }
      const checkResultSensitive = await this.checkResult(resultPath)
      // 敏感信息判断 为true 需要被拦截，扣减流量失败或者取消请求结果集
      if (!checkResultSensitive) {
        try {
        ret = await api.fetch(url, params, 'get')
        } catch (error) {
        }
      } else {
        this.isLoading = false
        ret = {
          metadata: [],
          fileContent: [],
          totalLine: 0,
          totalColumn: 0,
          type: '2',
        }
      }
    
      this.isLoading = false;
      if (ret.display_prohibited) {
        result = {
          'headRows': [],
          'bodyRows': [],
          'total': ret.totalLine,
          'type': ret.type,
          'path': resultPath,
          hugeData: true,
          tipMsg: localStorage.getItem("locale") === "en" ? ret.en_msg : ret.zh_msg
        };
      } else if (ret.column_limit_display) {
        result = {
          tipMsg: localStorage.getItem("locale") === "en" ? ret.en_msg : ret.zh_msg,
          'headRows': ret.metadata,
          'bodyRows': ret.fileContent,
          // 如果totalLine是null，就显示为0
          'total': ret.totalLine ? ret.totalLine : 0,
          // 如果内容为null,就显示暂无数据
          'type': ret.fileContent ? ret.type : '0',
          'totalColumn': ret.totalColumn,
          'path': resultPath,
          'current': 1,
          'size': 20,
        };
      } else {
        result = {
          'headRows': ret.metadata,
          'bodyRows': ret.fileContent,
          // 如果totalLine是null，就显示为0
          'total': ret.totalLine ? ret.totalLine : 0,
          // 如果内容为null,就显示暂无数据
          'type': ret.fileContent ? ret.type : '0',
          'totalColumn': ret.totalColumn,
          'path': resultPath,
          'current': 1,
          'size': 20,
        };
      }
      this.result = result;
      this.script.result = result;
      this.originRows = result.bodyRows || []
      if (result.type === '2') {
        this.tableData.type =
          this.result.headRows.length > 50 ? 'virtual' : 'normal'
        let headRows = this.result.headRows || []
        let heads = []
        let bodyRows = []
        this.tableData.total = this.result.total
        if (this.tableData.type === 'normal') {
          headRows.forEach((item, index) => {
            heads.push({
              title: item.columnName,
              key: `${item.columnName}_${index}`,
              minWidth: 120,
              width: 120,
              maxWidth: 240,
              className: 'columnClass',
              sortable: 'custom',
              columnType: item.dataType,
              colHeadHoverTitle: item.columnName + '\n' + item.dataType,
            })

          })
          this.originRows = this.originRows.map((row, i) => {
            let newItem = {}
            row.forEach((item, index) => {
              try {
                Object.assign(newItem, {
                  [heads[index].key]: item,
                })
              } catch (error) {
                console.error(error, row.length, i)
              }
            })
            return newItem
          })
        } else {
          for (let i = 0; i < headRows.length; i++) {
            heads.push({
              content: headRows[i].columnName,
              title: headRows[i].columnName,
              key: `${headRows[i].columnName}_${i}`,
              sortable: true,
              columnType: headRows[i].dataType,
              colHeadHoverTitle:
                headRows[i].columnName + '\n' + headRows[i].dataType,
            })
          }
        }
        const cols = []
        let dataHeads = heads
        if (this.checkedFields.length) {
          if (this.checkedFields.length <= 50) {
            dataHeads = heads
              .filter((it, index) => {
                if (this.checkedFields.includes(it.key)) {
                  cols.push(index)
                  return true
                }
                return false
              })
              .map((it) => {
                return {
                  key: it.key,
                  minWidth: 120,
                  width: 120,
                  maxWidth: 240,
                  ...it,
                }
              })
            this.tableData.type = 'normal'
          } else {
            this.tableData.type = 'virtual'
          }
        }
        this.heads = heads
        this.data = {
          headRows: dataHeads,
          bodyRows,
        }
        this.cols = cols;
        if (hasSorted && hasSorted.sortType !== 'normal') {
          const col = dataHeads.find(it => it.key === hasSorted.sortKey)
          if (col) {
            this.originRows = this.sortChange({
              column: col, key: hasSorted.sortKey, order: hasSorted.sortType, cb: 'sort' 
            })
          }
        }
        this.pageingData()
        this.initPage()
        this.$nextTick(()=>{
          this.$emit('loadDataDone')
        })
      }
    },
    pageingData() {
      if (this.originRows && this.originRows.length) {
        let { current, size } = this.page
        let maxLen = Math.min(this.tableData.total, current * size)
        let minLen = Math.max(0, (current - 1) * size)
        let newArr = this.originRows.slice(minLen, maxLen)
        if (this.cols && this.cols.length && newArr[0] && Array.isArray(newArr[0])) {
          newArr = newArr.map((row) => {
            const list = {}
            this.cols.forEach((it, index) => {
              list[`${this.data.headRows[index].key}`] = row[it]
            })
            return list
          })
        }
        this.data.bodyRows = newArr
      }
    },
    change(page = 1) {
      this.hightLightRow = null
      this.page.current = page
      // 行分页后的数据进行存储
      const tmpResult = this.getResult()
      tmpResult.current = this.page.current
      tmpResult.size = this.page.size
      const resultSet = this.script.resultSet || 0
      if (this.script.resultList && this.script.resultList[resultSet]) {
        this.$set(this.script.resultList[resultSet], 'result', tmpResult)
      }
      this.updateCache({
        resultSet: resultSet,
        showPanel: this.scriptViewState.showPanel,
      })
      this.tableScrollChange(this.result.path)
      this.pageingData()
    },
    updateCache(params) {
      if ('dss/apiservice' == this.getResultUrl) {
      } else if (this.script.nodeId) {
        params.resultList = this.script.resultList;
        this.dispatch('workflowIndexedDB:updateNodeCache', {
          nodeId: this.script.nodeId,
          value: params,
        })
      } else if (this.script.id) {
        this.dispatch('IndexedDB:updateResult', {
          tabId: this.script.id,
          ...this.script.resultList,
          ...params
        })
      }
    },
    changeSize(size) {
      this.hightLightRow = null
      this.page.size = size
      this.page.current = 1
      // 分页后的数据进行存储
      const tmpResult = this.getResult()
      tmpResult.current = this.page.current
      tmpResult.size = this.page.size
      const resultSet = this.script.resultSet || 0
      this.$set(this.script.resultList[resultSet], 'result', tmpResult)
      this.updateCache({
        resultSet: resultSet,
        showPanel: this.scriptViewState.showPanel,
      })
      this.pageingData()
    },
    changeSet(set) {
      if(this.$refs.resultTable && this.$refs.resultTable.handleInitFlag) {
        this.$refs.resultTable.handleInitFlag(true);
      }
      this.isLoading = true
      this.page.current = 1
      this.hightLightRow = null
      this.isFilterViewShow = false
      this.$emit(
        'on-set-change',
        {
          currentSet: set,
        },
        () => {
          this.updateData('changeSet')
        }
      )
    },
    copyLabel(head) {
      util.executeCopy(head.content || head.title)
    },
    getResult() {
      const result = Object.assign(this.result, {
        cache: {},
        current: this.page.current,
        size: this.page.size,
        columnPageNow: this.scriptViewState.columnPageNow,
      })
      return result
    },
    iframeOnChange() {
      this.$nextTick(() => {
        document.all.iframeName.contentWindow.location.reload()
      })
    },
    tableScrollChange(positionMemoryKey, scrollTop, scrollLeft ) {
      let cacheTablePositionData = {
        currentPage: this.page.current,
        pageSize: this.page.size,
      }
      if (scrollLeft !== undefined || scrollTop !== undefined) {
        cacheTablePositionData.scrollLeft = scrollLeft
        cacheTablePositionData.scrollTop = scrollTop
      }
      if (positionMemoryKey && sessionStorage.getItem('table_position_' + positionMemoryKey)) {
        let data = JSON.parse(sessionStorage.getItem('table_position_' + positionMemoryKey))
        cacheTablePositionData = {
          ...data,
          ...cacheTablePositionData,
        }
      }
      sessionStorage.setItem('table_position_' + positionMemoryKey, JSON.stringify(cacheTablePositionData));
    },
    onRowClick(currentRow) {
      // 通过列命名去查找当前的类型
      const row = Object.values(currentRow)
      this.rowToColumnData(row)
    },
    onWeTableRowClick(index) {
      const row = this.data.bodyRows[index]
      this.rowToColumnData(row)
    },
    rowToColumnData(row) {
      this.hightLightRow = []
      this.data.headRows.forEach((subItem, index) => {
        const temObject = {
          columnName: subItem.title,
          dataType: subItem.columnType,
          value: row[index],
        }
        this.hightLightRow.push(temObject)
      })
    },
    handleFilterView() {
      this.isFilterViewShow = !this.isFilterViewShow
    },
    filterList(fields) {
      if (fields === false) {
        this.isFilterViewShow = false
        return
      }
      let rows = []
      let cols = []
      let bodyRows = []
      const checked = {}
      if (fields.length) {
        fields.forEach((key) => {
          checked[key] = true
        })
        this.heads.forEach((it, index) => {
          if (checked[it.key]) {
            rows.push({
              key: it.key,
              minWidth: 120,
              width: 120,
              maxWidth: 240,
              className: 'columnClass',
              sortable: 'custom',
              ...it,
            })
            cols.push(index)
          }
        })
      } else {
        this.heads.forEach((it, index) => {
          rows.push({
            key: it.key,
            minWidth: 120,
            width: 120,
            maxWidth: 240,
            className: 'columnClass',
            sortable: 'custom',
            ...it,
          })
          cols.push(index)
        })
      }
      if (this.result.type === '2') {
        this.tableData.type = rows.length <= 50 ? 'normal' : 'virtual'
      }
      if (this.tableData.type === 'normal') {
        bodyRows = this.result.bodyRows.map((row) => {
          const list = {}
          cols.forEach((it, index) => {
            list[rows[index].key] = row[it]
          })
          return list
        })
      } else {
        bodyRows = this.result.bodyRows.map((row) => {
          return row.filter((it, index) => cols.indexOf(index) > -1)
        })
      }
      this.cols = []
      this.data.headRows = rows
      this.originRows = bodyRows
      this.pageingData()
      this.isFilterViewShow = false
      this.checkedFields = Object.keys(checked)
      storage.set('column_show_'+this.result.path, this.checkedFields)
    },
    changeViewType(type) {
      if (type !== this.visualShow) {
        this.visualShow = type
      }
      this.isFilterViewShow = false
    },
  },
}
</script>
<style lang="scss" scoped>
@import '@dataspherestudio/shared/common/style/variables.scss';
.we-result-view {
  width: 100%;
  height: 100%;
  overflow: hidden;
  padding-left: $toolbarWidth;
  @include bg-color($light-base-color, $dark-base-color);
  .html-result-div {
    overflow-y: auto;
  }
  .result-table-content {
    .ivu-table-header {
      width: calc(100% - 8px);
      tr > th:nth-last-of-type(2) {
        border-right: none;
      }
    }
    .result-normal-table {
      border: none;
      border-right: $border-width-base $border-style-base $border-color-base;
      @include border-color($border-color-base, $dark-border-color-base);
      min-width: 100%;
      .ivu-table {
        min-width: 100%;
        .is-null {
          color: $error-color;
          font-style: italic;
        }
      }
    }
  }
  .text-result-div {
    overflow: auto;
    padding: 10px;
  }
  .empty-text {
    position: $relative;
    top: 20px;
    left: 20px;
  }
  .table-box {
    overflow: hidden;
  }
  .noselectable {
    user-select: none;
  }
  .we-page-container {
    display: flex;
    width: 100%;
    height: 35px;
    text-align: center;
    padding-left: 10px;
    align-items: center;
    justify-content: space-between;
    .page {
      width: 100%;
      display: flex;
      align-items: center;
      justify-content: space-evenly;
    }
    .set {
      width: 90px;
    }
  }
}
.result-normal-table table {
  min-width: 100%;
}

.result-normal-table .columnClass {
  height: 30px;
  line-height: 1.25;
  ::v-deep.ivu-table-cell {
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 2;
    position: relative;
    overflow: hidden;
    .ivu-table-sort {
      position: absolute;
      top: 50%;
      right: 0;
      transform: translate(-50%, -50%);
      z-index: 90;
      i.on {
        color: #ffffff;
      }
    }
  }
}
.modal-detail {
  .full-btn {
    float: right;
    margin-right: 30px;
    padding-top: 5px;
    cursor: pointer;
    color: #2d8cf0
  };
  .modal-content {
    word-break: break-all;
    line-height: 20px;
    white-space: pre-line;
    max-height: 500px;
    overflow: auto;
    margin-top: -16px;
    font-size: 14px;
    color: #515a6e;
  }
}
</style>
<style>
.result-table-content .list-view-item div[title='NULL'],
.result-table-content td[title='NULL'],
.result-table-content span[title='NULL'] {
  color: #ed4014;
}
.bottom-td.null-text {
  color: #ed4014;
  font-style: italic;
}
</style>
