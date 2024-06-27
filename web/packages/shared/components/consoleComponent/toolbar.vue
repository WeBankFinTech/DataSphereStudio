<template>
  <div class="we-toolbar-wrap">
    <ul ref="toolbar" class="we-toolbar">
      <li
        @click="changeViewType('table')"
        :class="{ 'we-toolbar-active': activeTool === 'table' }"
      >
        <Icon type="ios-grid" :size="iconSize" />
        <span
          v-if="isIconLabelShow"
          :title="$t('message.common.toolbar.analysis')"
          class="v-toolbar-icon"
        >{{ $t('message.common.toolbar.resultGroup') }}</span
        >
      </li>
      <li
        v-if="activeTool === 'table' && toolbarShow.filter"
        @click="openPopup('filter')"
        :title="$t('message.common.toolbar.resultGroupLineFilter')"
      >
        <SvgIcon
          :style="{ 'font-size': '20px' }"
          icon-class="export"
          color="#515a6e"
        />
        <span class="v-toolbar-icon" v-if="isIconLabelShow">{{
          $t('message.common.toolbar.lineFilter')
        }}</span>
      </li>
      <li v-if="toolbarShow.download">
        <Poptip
          :transfer="true"
          :width="380"
          v-model="popup.download"
          placement="right-end"
        >
          <div @click.stop="openPopup('download')">
            <SvgIcon
              :style="{ 'font-size': '20px' }"
              icon-class="download"
              color="#515a6e"
            />
            <span
              v-if="isIconLabelShow"
              :title="$t('message.common.download')"
              class="v-toolbar-icon"
            >{{ $t('message.common.download') }}</span
            >
          </div>
          <div slot="content">
            <div>
              <Row class="row-item">
                {{ $t('message.common.toolbar.format') }}
              </Row>
              <Row class="row-item">
                <RadioGroup v-model="download.format">
                  <Col span="10">
                    <Radio label="1">CSV</Radio>
                  </Col>
                  <Col v-if="resultType === '2'" span="10" offset="4">
                    <Radio label="2">Excel</Radio>
                  </Col>
                </RadioGroup>
              </Row>
            </div>
            <div>
              <Row class="row-item">
                {{ $t('message.common.toolbar.coding') }}
              </Row>
              <Row class="row-item">
                <RadioGroup v-model="download.coding">
                  <Col span="10">
                    <Radio label="1">UTF-8</Radio>
                  </Col>
                  <Col span="10" offset="4">
                    <Radio label="2">GBK</Radio>
                  </Col>
                </RadioGroup>
              </Row>
            </div>
            <div>
              <Row class="row-item">
                {{ $t('message.common.toolbar.replace') }}
              </Row>
              <Row class="row-item">
                <RadioGroup v-model="download.nullValue">
                  <Col span="10">
                    <Radio label="1">NULL</Radio>
                  </Col>
                  <Col span="10" offset="4">
                    <Radio label="2">{{
                      $t('message.common.toolbar.emptyString')
                    }}</Radio>
                  </Col>
                </RadioGroup>
              </Row>
            </div>
            <div v-if="download.format == 1">
              <Row class="row-item">
                {{ $t('message.common.toolbar.selectsplit') }}
              </Row>
              <Row class="row-item">
                <RadioGroup v-model="download.splitChar">
                  <Radio
                    v-for="item in splitList"
                    :label="item.value"
                    :name="item.value"
                    :key="item.value"
                  >
                    {{ item.label }}
                  </Radio>
                </RadioGroup>
              </Row>
            </div>
            <div>
              <Row class="row-item">
                {{ $t('message.common.toolbar.keepNewline') }}
              </Row>
              <Row class="row-item">
                <RadioGroup v-model="download.keepNewline">
                  <Radio name="1" label="1">
                    {{ $t('message.scripts.Yes') }}
                  </Radio>
                  <Radio name="0" label="0">
                    {{ $t('message.scripts.No') }}
                  </Radio>
                </RadioGroup>
              </Row>
            </div>
            <Row class="row-item" v-if="download.format == 2">
              <Checkbox v-model="autoFormat">{{
                $t('message.common.toolbar.autoformat')
              }}</Checkbox>
            </Row>
            <div v-if="isApiAll || isNotApiAll || +download.format === 1">
              <Row class="row-item">
                {{ $t('message.common.toolbar.downloadMode') }}
              </Row>
              <Row class="row-item">
                <Checkbox v-model="allDownload">{{
                  $t('message.common.toolbar.all')
                }}</Checkbox>
                <span
                >({{
                  $t('message.common.resultsExport.resultSetNum', {
                    num: resultList.length,
                  })
                }})</span
                >
              </Row>
            </div>
            <Row class="confirm row-item">
              <Col span="10">
                <Button @click="cancelPopup('download')">{{
                  $t('message.common.cancel')
                }}</Button>
              </Col>
              <Col span="10" offset="4">
                <Button type="primary" @click="confirm('download')">{{
                  $t('message.common.submit')
                }}</Button>
              </Col>
            </Row>
          </div>
        </Poptip>
      </li>
      <li @click="openPopup('export')" v-if="toolbarShow.export">
        <SvgIcon
          :style="{ 'font-size': '20px' }"
          icon-class="export"
          color="#515a6e"
        />
        <span class="v-toolbar-icon" v-if="isIconLabelShow">{{
          $t('message.common.toolbar.export')
        }}</span>
      </li>
      <li
        @click="openPopup('rowView')"
        v-if="row"
        :title="$t('message.common.toolbar.rowToColumnTitle')"
      >
        <SvgIcon
          :style="{ 'font-size': '20px' }"
          icon-class="transform"
          color="#515a6e"
        />
        <span v-if="isIconLabelShow" class="v-toolbar-icon">{{
          $t('message.common.toolbar.rowToColumn')
        }}</span>
      </li>
      <template v-for="(comp, index) in extComponents">
        <component
          :key="comp.name + index"
          :is="comp.component"
          :script="script"
          :class="{ 'we-toolbar-active': activeTool === comp.name }"
          @event-from-ext="eventFromExt"
        />
      </template>
    </ul>
    <results-export
      ref="exportToShare"
      :script="script"
      :dispatch="dispatch"
      :current-path="currentPath"
    >
    </results-export>
    <table-row ref="tableRow" :baseinfo="baseinfo" :row="row"> </table-row>
  </div>
</template>
<script>
import moment from 'moment'
import elementResizeEvent from '@dataspherestudio/shared/common/helper/elementResizeEvent'
import resultsExport from './resultsExport.vue'
import tableRow from './tableRow.vue'
import mixin from '@dataspherestudio/shared/common/service/mixin'
import api from '@dataspherestudio/shared/common/service/api'
import storage from '@dataspherestudio/shared/common/helper/storage'
import plugin from '@dataspherestudio/shared/common/util/plugin'

const extComponents = plugin.emitHook('script_result_toolbar') || []
export default {
  name: 'toolbar',
  components: {
    resultsExport,
    tableRow,
  },
  mixins: [mixin],
  props: {
    currentPath: {
      type: String,
      default: '',
    },
    script: [Object],
    row: {
      type: Array,
      default: () => [],
    },
    dispatch: {
      type: Function,
      required: true,
    },
    getResultUrl: {
      type: String,
      defalut: `filesystem`,
    },
    work: {
      type: Object,
    },
    resultType: {
      type: String,
      defalut: '',
    },
  },
  data() {
    return {
      activeTool: 'table',
      popup: {
        download: false,
        export: false,
        rowView: false,
      },
      baseinfo: {},
      download: {
        format: '1',
        coding: '1',
        nullValue: '1',
        splitChar: '1',
        keepNewline: '0',
      },
      isIconLabelShow: true,
      iconSize: 14,
      allDownload: false, // 是否下载全部结果集
      autoFormat: false,
      extComponents,
      splitList: [
        { label: this.$t('message.scripts.hiveTableExport.DH'), value: '1' },
        { label: this.$t('message.scripts.hiveTableExport.FH'), value: '2' },
        { label: this.$t('message.scripts.hiveTableExport.ZBF'), value: '3' },
        { label: this.$t('message.scripts.hiveTableExport.KG'), value: '4' },
        { label: this.$t('message.scripts.hiveTableExport.SX'), value: '5' },
      ],
    }
  },
  computed: {
    isNotApiAll() {
      return (
        ['hql', 'sql', 'tsql'].includes(this.script.runType) &&
        this.download.format === '2' && this.getResultUrl !== 'dss/apiservice'
      )
    },
    isApiAll() {
      return this.download.format === '2' && this.getResultUrl === 'dss/apiservice'
    },
    toolbarShow() {
      let isScriptis =
        this.$route.name === 'Home' ||
        (this.$route.name === 'results' && this.$route.query.from === 'Home')
      let canDownload = true
      let canFilter = true
      try {
        // display_prohibited、column_limit_display的结果集不支持下载
        if (this.script.resultList[this.script.resultSet].result.tipMsg) {
          canDownload = false
          canFilter = false
        }
      } catch (error) {
        //
      }
      return {
        filter: canFilter,

        export:
          this.baseinfo.exportResEnable !== false &&
          isScriptis &&
          this.activeTool === 'table' &&
          this.resultType === '2',
        download:
          this.activeTool === 'table' &&
          this.baseinfo.downloadResEnable !== false && canDownload,
      }
    },
    resultList() {
      return this.script.resultList || []
    },
  },
  mounted() {
    this.baseinfo = storage.get('baseInfo', 'local') || {}
    elementResizeEvent.bind(this.$el, this.resize)
  },
  methods: {
    openPopup(type) {
      if (type === 'download') {
        this.download = {
          format: '1',
          coding: '1',
          nullValue: '1',
          splitChar: '1',
          keepNewline: '0',
        }
      }
      if (type === 'export') {
        this.$refs.exportToShare.open()
      } else if (type === 'rowView') {
        this.$refs.tableRow.open()
      } else if (type === 'filter') {
        this.$emit('on-filter')
      } else {
        this.popup[type] = true
      }
    },
    cancelPopup(type) {
      this.popup[type] = false
    },
    confirm(type) {
      this[`${type}Confirm`]()
      this.cancelPopup(type)
    },
    pause(msec) {
      return new Promise((resolve) => {
        setTimeout(resolve, msec || 1000)
      })
    },
    async downloadAll(list, cb) {
      const current = list.slice(0, 10)
      cb && cb(current)
      list = list.slice(10, list.length)
      if (list.length > 0) {
        await this.pause()
        this.downloadAll(list, cb)
      }
    },
    downloadConfirm() {
      this.$Modal.confirm({
        title: this.$t('message.common.Prompt'),
        content: this.$t('message.common.safetips'),
        onOk: async () => {
          const splitor = this.download.format === '1' ? 'csv' : 'xlsx'
          const charset = this.download.coding === '1' ? 'utf-8' : 'gbk'
          const nullValue = this.download.nullValue === '1' ? 'NULL' : 'BLANK'
          const keepNewline =
            this.download.keepNewline === '1' ? 'true' : 'false'
          const timestamp = moment.unix(moment().unix()).format('MMDDHHmm')
          let fileName = ''
          if (this.script.fileName && this.script.fileName !== 'undefined') {
            fileName = this.script.fileName
          } else if (this.script.title && this.script.title !== 'undefined') {
            fileName = this.script.title
          }
          const filename = `Result_${fileName}_${timestamp}`
          let temPath = this.currentPath
          let querys = `&charset=${charset}&outputFileType=${splitor}&nullValue=${nullValue}&autoFormat=${this.autoFormat}&keepNewline=${keepNewline}`
          // 如果是api执行页获取结果集，需要带上taskId
          if (this.getResultUrl !== 'filesystem') {
            querys += `&taskId=${this.work.taskID}`
          } else {
            const res = await api.fetch('/dss/scriptis/userLimits', 'get')
            if (res && res.userLimits && res.userLimits.downloadCount) {
              querys += `&limit=${res.userLimits.downloadCount}`
            }
          }
          const splitChar =
            [',', ';', '\t', ' ', '|'][this.download.splitChar - 1] || ','
          if (this.download.format == 1) {
            querys += `&csvSeparator=${encodeURIComponent(splitChar)}`
          }
          // 下载之前条用心跳接口确认是否登录
          await api.fetch('/user/heartbeat', 'get')
          // 全量下载和下载当前结果集
          let apiPath = `${this.getResultUrl}/resultsetToExcel`
          let flag = false
          if (this.allDownload && +this.download.format == 1) {
            const eventList = []
            this.downloadAll(this.resultList, (items) => {
              for (let index = 0; index < items.length; index++) {
                const item = items[index]
                // 数据服务的文件path序号从1开始，script的文件path序号从0开始 无法统一，命名序号直接写死，从1开始递增
                // const name = `ResultSet${
                //   Number(
                //     item.path
                //       .substring(temPath.lastIndexOf('/'))
                //       .split('.')[0]
                //       .split('_')[1]
                //   )+1
                // }`
                const name = `ResultSet${index + 1}`
                const queryStr = `${querys}&outputFileName=${name}&path=${item.path}`
                let url =
                  `${window.location.protocol}//${window.location.host}/api/rest_j/v1/${apiPath}?` +
                  queryStr
                eventList.push(this.downloadFile(url, item.path))
              }
            })
            if (eventList.filter(Boolean).length === this.resultList.length) {
              flag = true
            }
          } else if (
            this.isNotApiAll &&
            this.allDownload &&
            +this.download.format == 2
          ) {
            // 只有script中支持excel的全量下载，数据服务不支持
            temPath = temPath.substring(0, temPath.lastIndexOf('/'))
            apiPath = `${this.getResultUrl}/resultsetsToExcel`
            querys += `&outputFileName=${filename}&path=${temPath}`
            let url =
              `${window.location.protocol}//${window.location.host}/api/rest_j/v1/${apiPath}?` +
              querys
            flag = this.downloadFile(url, temPath)
          } else if (
            this.isApiAll &&
            this.allDownload &&
            +this.download.format == 2
          ) {
            temPath = temPath.substring(0, temPath.lastIndexOf('/'))
            querys += `&outputFileName=${filename}&path=${temPath}&isFullExcel=true`
            let url =
              `${window.location.protocol}//${window.location.host}/api/rest_j/v1/${apiPath}?` +
              querys
            flag = this.downloadFile(url, temPath)
          } else {
            querys += `&outputFileName=${filename}&path=${temPath}`
            let url =
              `${window.location.protocol}//${window.location.host}/api/rest_j/v1/${apiPath}?` +
              querys
            flag = this.downloadFile(url, temPath)
          }
          this.$nextTick(() => {
            if (flag) {
              this.$Message.success(
                this.$t('message.common.toolbar.success.download')
              )
            }
          })
        },
      })
    },
    downloadFile(url, temPath) {
      // 下载记录日志
      api
        .fetch(
          '/dss/scriptis/audit/download/save',
          {
            creator: this.baseinfo.username,
            tenant: this.baseinfo.proxyUserName,
            path: temPath,
            sql: this.script.executionCode || '',
            createTime: Date.now(),
          },
          'post'
        )
        .catch(() => {})
      const link = document.createElement('a')
      link.setAttribute('href', url)
      link.setAttribute('download', '')
      const evObj = document.createEvent('MouseEvents')
      evObj.initMouseEvent(
        'click',
        true,
        true,
        window,
        0,
        0,
        0,
        0,
        0,
        false,
        false,
        true,
        false,
        0,
        null
      )
      return link.dispatchEvent(evObj)
    },
    resize() {
      const height = window.getComputedStyle(this.$refs.toolbar).height
      if (parseInt(height) <= 190) {
        this.isIconLabelShow = false
        this.iconSize = 20
      } else {
        this.isIconLabelShow = true
        this.iconSize = 14
      }
    },
    changeViewType(type, viewType = 'table') {
      if (type !== this.activeTool) {
        this.activeTool = type
      }
      this.$emit('change-view-type', viewType)
    },
    eventFromExt(evt) {
      if (evt && evt.callFn && typeof this[evt.callFn] === 'function') {
        this[evt.callFn](...evt.params)
      }
    },
  },
  beforeDestroy() {
    elementResizeEvent.unbind(this.$el, this.resize)
  },
}
</script>
<style lang="scss" scoped>
@import '@dataspherestudio/shared/common/style/variables.scss';
.we-toolbar-wrap {
  width: $toolbarWidth;
  height: 100%;
  word-break: break-all;
  position: $absolute;
  left: 40px;
  margin-left: -$toolbarWidth;
  @include bg-color($light-base-color, $dark-menu-base-color);
  .we-toolbar {
    width: 100%;
    height: 100%;
    border-right: 1px solid #dcdee2;
    @include border-color($border-color-base, $dark-menu-base-color);
    padding-top: 10px;
    li {
      padding-bottom: 20px;
      text-align: center;
      cursor: pointer;
    }
    span {
      display: block;
      line-height: 24px;
    }
  }
  .we-toolbar-active {
    i,
    span {
      @include font-color($primary-color, $dark-primary-color);
    }
  }
}
</style>
<style>
.ivu-poptip-body-content .row-item {
  margin-top: 5px;
}
</style>

