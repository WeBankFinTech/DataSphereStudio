<template>
  <div
    ref="view"
    class="we-result-view"
    v-if="result && result.type">
    <we-toolbar
      @on-analysis="$emit('on-analysis')"
      :current-path="result.path"
      :script="script"
      :row="hightLightRow" />
    <Spin
      v-show="isLoading"
      size="large"
      fix />
    <div
      v-if="result.type === '1'"
      :style="{'height': resultHeight}"
      class="text-result-div">
      <div v-if="result.bodyRows">
        <div
          v-for="(row, index) in result.bodyRows.split('\n')"
          :key="index">{{ row }}</div>
      </div>
      <span
        v-else
        class="empty-text">{{ $t('message.workBench.body.script.result.emptyText') }}</span>
    </div>
    <div
      v-else-if="result.type === '2' && tableData.type"
      :class="{'table-box': tableData.type === 'normal'}">
      <template v-if="tableData.type === 'normal'">
        <Table
          border
          highlight-row
          :width="tableData.width"
          :height="tableData.height"
          :columns="data.headRows"
          :data="data.bodyRows"
          @on-sort-change="sortChange"
          @on-current-change="onRowClick"
          class="result-normal-table">
        </Table>
        <we-water-mask
          v-if="isWaterMask"
          :text="watermaskText"
          ref="watermask"></we-water-mask>
      </template>
      <template v-else>
        <we-table
          id="resultTable"
          :data="data"
          :width="tableData.width"
          :height="tableData.height"
          :size="tableData.size"
          :offset-x="offsetX"
          :offset-y="offsetY"
          :variable="getVarW"
          :need-reset="needReset"
          @on-scroll="saveOffset"
          @dbl-click="copyLabel"
          @on-click="onWeTableRowClick"
          @change-status="changeStatus"
          @handleSortClick="handleSortClick"/>
        <we-water-mask
          v-if="isWaterMask"
          :text="watermaskText"
          ref="watermask"></we-water-mask>
      </template>
    </div>
    <div v-else-if="result.type === '4'">
      <iframe
        id="iframeName"
        :style="{'height': resultHeight}"
        :srcdoc="htmlData"
        frameborder="0"
        width="100%"
        @change="iframeOnChange()" />
    </div>
    <div
      v-else-if="result.type === '5'"
      :style="{'height': resultHeight}"
      class="html-result-div"
      v-html="result.bodyRows[0][0]"/>
    <span
      v-else
      class="empty-text">{{ $t('message.workBench.body.script.result.emptyText') }}</span>
    <div class="we-page-container">
      <result-set-list
        v-if="script.resultList && script.resultList.length>1"
        :list="script.resultList"
        @change="changeSet">
      </result-set-list>
      <Page
        v-if="result.type === '2'"
        ref="page"
        :total="tableData.total"
        :page-size-opts="page.sizeOpts"
        :page-size="page.size"
        :current="page.current"
        class-name="page"
        size="small"
        show-total
        show-sizer
        @on-change="change"
        @on-page-size-change="changeSize" />
    </div>
  </div>
</template>
<script>
import util from '@/js/util';
import Table from '@js/component/table';
import WeWaterMask from '@js/component/watermark';
import WeToolbar from './toolbar.vue';
import elementResizeEvent from '@js/helper/elementResizeEvent';
import resultSetList from './resultSetList.vue';
import pinyin from 'pinyin';
export default {
  components: {
    WeTable: Table.WeTable,
    WeToolbar,
    WeWaterMask,
    resultSetList,
  },
  props: {
    result: {
      type: [Object],
      default() {
        return {
          headRows: [],
          bodyRows: [],
          type: 'EMPTY',
          total: 0,
          path: '',
          cache: {

          },
        };
      },
    },
    script: [Object],
    height: Number,
  },
  data() {
    let {
      x,
      y,
    } = this.initOffset();
    const isWaterMask = this.getWaterMask();
    const username = this.getUserName();
    return {
      isWaterMask,
      watermaskText: `${this.$t('message.workBench.body.script.result.watermaskText')}-${username}`,
      data: {
        headRows: [],
        bodyRows: [],
      },
      tableData: {
        type: 'normal',
        width: 1000,
        height: 400,
        size: 100,
        total: 100,
        cache: {
          offsetX: 0,
          offsetY: 0,
        },
      },
      offsetX: x,
      offsetY: y,
      resultSet: 0,
      page: {
        current: 1,
        size: 50,
        sizeOpts: [20, 50, 80, 100],
      },
      wCache: [],
      // 用于避免双击和单击的冲突
      dblClickTimer: null,
      isLoading: false,
      // 当前高亮的行
      hightLightRow: null,
      // 用于在路由切换的时候重置结果集数据和滚动条位置
      needReset: false,
    };
  },
  computed: {
    // when mutating (rather than replacing) an Object or an Array, the old value will be the same as new value because they reference the same Object/Array. Vue doesn’t keep a copy of the pre-mutate value.
    scriptViewState: function() {
      let obj = {};
      let state = this.script.scriptViewState;
      Object.keys(state).forEach((key) => {
        obj[key] = state[key];
      });
      return obj;
    },

    resultHeight: function() {
      return this.height - 80 + 'px';
    },
  },
  watch: {
    'result': {
      handler(val) {
        this.updateData();
      },
      deep: true,
      immediate: true,
    },
    'scriptViewState': {
      handler: function(val, oldVal) {
        let result = this.script.result;
        if (oldVal.bottomPanelMin && !val.bottomPanelMin) {
          this.resize();
        }
        // first open tag which has cache
        if (result && val.showPanel === 'result' && oldVal.showPanel !== 'result' && !result.cache.cahceTag) {
          result.cache.cahceTag = true;
          result.cache.offsetX = (result.cache.offsetX || 0) + Math.SQRT1_2;
          result.cache.offsetY = (result.cache.offsetY || 0) + Math.SQRT1_2;
          this.resize();
        }
      },
      deep: true,
    },
    '$route'(val) {
      if (val.path === '/home') {
        this.data.headRows = this.data.headRows.slice();
        this.needReset = true;
      }
    },
  },
  mounted() {
    this.initPage();
    elementResizeEvent.bind(this.$el, this.resize);
  },
  beforeDestroy: function() {
    if (this.script.result) {
      this.script.result.cache.offsetX = this.tableData.cache.offsetX;
      this.script.result.cache.offsetY = this.tableData.cache.offsetY;
    }
    clearTimeout(this.dblClickTimer);
    this.dblClickTimer = null;
    elementResizeEvent.unbind(this.$el);
  },
  methods: {
    'Workbench:setParseAction'(id) {
      this.resize();
    },
    handleSortClick(args) {
      let { reverse,col, colIndex, cb } = args;
      let order = 'normal';
      if (args.reverse === -1) {
        order = 'desc'
      } else if (args.reverse === 1) {
        order = 'asc'
      }
      // 先通过表头的内容找到对应全数据数据的index
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
          value: row[sortIndex]
        }
      })
      // 将找出的列排序
      sortColumnAll = this.arraySortByName(sortColumnAll, col.columnType, 'value');// 从小到大
      let newRow = [];
      if (order === 'asc') {// 升序
        sortColumnAll.map((item, index) => {
          newRow[index] = this.originRows[item.originIndex];
        })
        this.originRows = newRow;
      } else if (order === 'desc') {// 降序
        sortColumnAll.reverse();
        sortColumnAll.map((item, index) => {
          newRow[index] = this.originRows[item.originIndex];
        })
        this.originRows = newRow;
      } else if (order === 'normal') {// 恢复原来数据
        this.originRows = this.result.bodyRows || [];
        this.data.originRows = this.originRows;
      }
      this.pageingData();
    },
    addCellClassName() {
      // 需要将每一行中的NULL挑选出来
      if (this.tableData.type === 'normal') {
        this.originRows = this.originRows.map((row) => {
          row.cellClassName = {};
          Object.keys(row).forEach((key)=> {
            if (row[key] === 'NULL') {
              row.cellClassName[key] = 'is-null';
            }
          })
          return row;
        })
      }
    },
    sortChange({column, key, order}) {
      this.originRows = this.arraySortByName(this.originRows, column.columnType, key);// 从小到大
      if (order === 'asc') {// 升序
        this.addCellClassName();
      } else if (order === 'desc') {// 降序
        this.originRows.reverse();
        this.addCellClassName();
      } else if (order === 'normal') {// 恢复原来数据
        if (this.tableData.type === 'normal') {
          this.originRows = this.result.bodyRows.map((row) => {
            let newItem = {};
            const NullList = [];
            row.forEach((item, index) => {
              Object.assign(newItem, {
                [this.result.headRows[index]]: item,
              });
              if (item === 'NULL') {
                NullList.push(this.result.headRows[index]);
              }
            });
            // 对于NULL值加上高亮样式
            if (NullList.length) {
              newItem.cellClassName = {};
              NullList.forEach((item) => {
                newItem.cellClassName[item] = 'is-null';
              });
            }
            return newItem;
          });
        } else {
          this.originRows = this.result.bodyRows || [];
        }
        this.data.originRows = this.originRows;
      }
      this.pageingData();
    },
    arraySortByName(list, valueType, key) {
      if (list === undefined || list === null) return [];
      list.sort((a, b) => {
        let strA = a[key];
        let strB = b[key];
        // 谁为非法值谁在前面
        if (strA === undefined || strA === null || strA === '' || strA === ' ' || strA === '　' || strA === 'NULL') {
          return -1;
        }
        if (strB === undefined || strB === null || strB === '' || strB === ' ' || strB === '　' || strB === 'NULL') {
          return 1;
        }
        // 如果为整数型大小
        if (['int', 'float', 'double', 'long', 'short', 'bigInt', 'decimal'].includes(valueType.toLowerCase())) {

          return strA - strB;
        }
        const charAry = strA.split('');
        for (const i in charAry) {
          if ((this.charCompare(strA[i], strB[i]) !== 0)) {
            return this.charCompare(strA[i], strB[i]);
          }
        }
        // 如果通过上面的循环对比还比不出来，就无解了，直接返回-1
        return -1;
      });
      return list;
    },
    charCompare(charA, charB) {
      // 谁为非法值谁在前面
      if (charA === undefined || charA === null || charA === '' || charA === ' ' || charA === '　') {
        return -1;
      }
      if (charB === undefined || charB === null || charB === '' || charB === ' ' || charB === '　') {
        return 1;
      }
      if (!this.notChinese(charA)) {
        charA = pinyin(charA)[0][0];
      }
      if (!this.notChinese(charB)) {
        charB = pinyin(charB)[0][0];
      }
      return charA.localeCompare(charB);
    },
    notChinese(char) {
      const charCode = char.charCodeAt(0);
      return charCode >= 0 && charCode <= 128;
    },
    initOffset() {
      let cache = this.script.result.cache;
      let x = 0;
      let y = 0;
      if (cache && cache.offsetX) {
        x = cache.offsetX;
      }
      if (cache && cache.offsetY) {
        y = cache.offsetY;
      }
      return {
        x,
        y,
      };
    },
    initPage() {
      this.page = Object.assign(this.page, {
        current: this.result.current,
        size: this.result.size,
      });
      this.change(this.result.current);
    },
    updateData() {
      /**
         * result.type
         * TEXT_TYPE: '1'
         * TABLE_TYPE: '2'
         * IO_TYPE: '3'
         * PICTURE_TYPE: '4'
         * HTML_TYPE: '5'
         */
      if (this.result.type === '2') {
        this.tableData.type = this.result.headRows.length > 50 ? 'virtual' : 'normal';
        let headRows = this.result.headRows || [];
        this.data.headRows = [];
        this.data.bodyRows = [];

        this.originRows = this.result.bodyRows || [];
        this.tableData.total = this.result.total;
        if (this.tableData.type === 'normal') {
          for (let item of headRows) {
            const title = item.slice(item.indexOf(':') + 1, item.indexOf(','));
            const LEN = 'dataType'.length;
            const columnType = item.slice(item.indexOf(',') + LEN + 2, item.lastIndexOf(','));
            this.data.headRows.push({
              title,
              key: item,
              sortable: 'custom',
              columnType,
              renderHeader: (h, params) => {
                return h('span', {
                  attrs: {
                    title: columnType,
                  },
                  on: {
                    dblclick: (e) => {
                      this.copyLabel({
                        content: params.column.title,
                      });
                      return false;
                    },
                  },
                },
                params.column.title
                );
              },
            });
          }
          this.originRows = this.originRows.map((row) => {
            let newItem = {};
            const NullList = [];
            row.forEach((item, index) => {
              Object.assign(newItem, {
                [headRows[index]]: item,
              });
              if (item === 'NULL') {
                NullList.push(headRows[index]);
              }
            });
            // 对于NULL值加上高亮样式
            if (NullList.length) {
              newItem.cellClassName = {};
              NullList.forEach((item) => {
                newItem.cellClassName[item] = 'is-null';
              });
            }
            return newItem;
          });
        } else {
          for (let i = 0; i < headRows.length; i++) {
            const label = headRows[i].slice(headRows[i].indexOf(':') + 1, headRows[i].indexOf(','));
            const LEN = 'dataType'.length;
            const columnType = headRows[i].slice(headRows[i].indexOf(',') + LEN + 2, headRows[i].lastIndexOf(','));
            this.data.headRows.push({
              content: label,
              sortable: true,
              columnType,
            });
          }
        }
        this.data.originRows = this.originRows;
        this.pageingData();
      }
      // data change result layout change
    },
    pageingData() {
      if (this.originRows && this.originRows.length) {
        let {
          current,
          size,
        } = this.page;
        let maxLen = Math.min(this.tableData.total, current * size);
        let minLen = Math.max(0, (current - 1) * size);
        let newArr = this.originRows.slice(minLen, maxLen);
        this.data.bodyRows = newArr;
      }
    },
    change(page = 1) {
      this.hightLightRow = null;
      this.page.current = page;
      this.pageingData();
    },
    changeSize(size) {
      this.hightLightRow = null;
      this.page.size = size;
      this.page.current = 1;
      this.pageingData();
    },
    resizeWaterMask() {
      if (this.$refs.watermask) {
        this.$refs.watermask.updateCanvas(this.watermaskText);
      }
    },
    resize() {
      if (!this.$refs.page) return;
      let page = this.$refs.page.$el;
      let margin = (page.clientWidth || 0) / 2;
      page.style.marginLeft = -margin + 'px';

      let ele = this.$refs.view;
      let computedStyle = getComputedStyle(ele);
      let h = ele.clientHeight;
      let w = ele.clientWidth;
      h -= parseFloat(computedStyle.paddingTop) + parseFloat(computedStyle.paddingBottom);
      w -= parseFloat(computedStyle.paddingLeft) + parseFloat(computedStyle.paddingRight);
      if (this.tableData.type === 'normal') {
        this.tableData.width = this.data.headRows.length * 120 > w ? this.data.headRows.length * 120 : w;
      } else {
        this.tableData.width = w;
      }
      this.tableData.height = h - 60;
      this.resizeWaterMask();
      return false;
    },
    saveOffset({
      offsetX,
      offsetY,
    }) {
      clearTimeout(this.dblClickTimer);
      this.dblClickTimer = setTimeout(() => {
        let cache = this.tableData.cache;
        cache.offsetX = offsetX;
        cache.offsetY = offsetY;
      }, 300);
    },
    changeSet(set) {
      this.isLoading = true;
      this.$emit('on-set-change', {
        currentSet: set,
        lastResult: this.getResult(),
      }, () => {
        this.isLoading = false;
      });
    },
    getVarW(index) {
      return this.wCache[index];
    },
    copyLabel(head) {
      clearTimeout(this.dblClickTimer);
      util.executeCopy(head.content);
    },
    getResult() {
      const result = Object.assign(this.result, {
        cache: {
          offsetX: this.tableData.cache.offsetX,
          offsetY: this.tableData.cache.offsetY,
        },
        current: this.page.current,
        size: this.page.size,
      });
      return result;
    },
    iframeOnChange() {
      this.$nextTick(() => {
        document.all.iframeName.contentWindow.location.reload();
      });
    },
    onRowClick(currentRow, oldCurrentRow) {
      this.hightLightRow = currentRow;
    },
    onWeTableRowClick(index) {
      const row = this.data.bodyRows[index];
      this.hightLightRow = {};
      this.data.headRows.forEach((item, headIndex) => {
        const columnName = `columnName:${item.content},dataType:${item.columnType},`;
        this.hightLightRow[columnName] = row[headIndex];
      });
    },
    changeStatus() {
      this.needReset = false;
    },
  },
};

</script>
<style>
.result-normal-table table {
  min-width: 100%;
}
</style>
