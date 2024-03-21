<template>
  <div class="field-list">
    <div class="field-list-search">
      <Input
        v-model="searchText"
        :placeholder="$t('message.scripts.tableDetails.SSZDMC')">
        <Icon
          slot="prefix"
          type="ios-search"/>
      </Input>
      <div class="field-list-search__button">
        <Button type="success" @click="handleCopy">{{ $t('message.scripts.tableDetails.FZBZDXX') }}</Button>
      </div>
    </div>
    <div style="position:relative">
      <div class="field-list-header" id="tbheader" :class="{'ovy': searchColList.length > maxSize}">
        <div
          class="field-list-item"
          v-for="(item, index) in columnCalc"
          :key="index"
          :style="{width: item.width? `${item.width}` : 'auto'}"
          @mousemove.prevent.stop="mousemove"
          @mouseup.prevent.stop="mouseup"
        >{{ item.title }}
          <div
            class="resize-bar"
            :data-col-index="index"
            @mousedown="mousedown"
          ></div>
        </div>
      </div>
      <virtual-list
        ref="columnTables"
        :size="46"
        :remain="searchColList.length > maxSize ? maxSize : searchColList.length"
        wtag="ul"
        class="field-list">
        <li
          v-for="(item, index) in searchColList"
          :key="index"
          class="field-list-body"
          @click="clickItem($event, item)"
        >
          <div
            class="field-list-item"
            :title="formatValue(item, field)"
            v-for="(field, index2) in columnCalc"
            :data-key="field.key"
            :key="index2"
            :style="{width: columnCalc[index2].width? `${columnCalc[index2].width}` : 'auto'}">
            {{ formatValue(item, field) }}
          </div>
        </li>
      </virtual-list>
      <div v-if="dragStartX" class="drag-line" :style="{left: dragLine.left, display: dragLine.show}" />
    </div>
    <Modal v-model="editModelShow" :title="$t('message.scripts.createTable.titleModel')" :footer-hide="true">
      <Form :model="fieldModel" :label-width="80">
        <Form-item prop="name" :label="$t('message.scripts.Name')">
          <Input v-model="fieldModel.name" placeholder="" :disabled="true"></Input>
        </Form-item>
        <Form-item prop="type" :label="$t('message.scripts.Type')">
          <RadioGroup v-model="fieldModel.type">
            <Radio label="index" disabled>{{ $t('message.scripts.Metrics') }}</Radio>
            <Radio label="dimension" disabled>{{ $t('message.scripts.Dimensions') }}</Radio>
          </RadioGroup>
        </Form-item>
        <Form-item prop="business" :label="$t('message.scripts.busst')">
          <Input v-model="fieldModel.business" placeholder="" :disabled="true"></Input>
        </Form-item>
        <Form-item prop="calculate" :label="$t('message.scripts.calcst')">
          <Input v-model="fieldModel.calculate" placeholder="" :disabled="true"></Input>
        </Form-item>
        <Form-item prop="formula" :label="$t('message.scripts.Formula')">
          <Input v-model="fieldModel.formula" type="textarea" placeholder="" :disabled="true"></Input>
        </Form-item>
      </Form>
    </Modal>
  </div>
</template>
<script>
import utils from '../utils.js';
import virtualList from '@dataspherestudio/shared/components/virtualList';
export default {
  components: {
    virtualList,
  },
  props: {
    table: {
      type: Array,
    },
    // tableInfo: {
    //   type: Object,
    // },
  },
  data() {
    return {
      searchText: '',
      searchColList: [],
      editModelShow: false,
      fieldModel: {},
      tableColumns: [
        { title: this.$t('message.scripts.Serial'), key: 'index', width: '5%' },
        { title: this.$t('message.scripts.tableDetails.ZDM'), key: 'name', width: '10%' },
        { title: this.$t('message.scripts.tableDetails.ZDLX'), key: 'type', width: '10%'  },
        { title: this.$t('message.scripts.tableDetails.BM'), key: 'alias', width: '10%'  },
        { title: this.$t('message.scripts.hiveTableExport.LX'), key: 'modeInfo.type', width: '10%'  },
        { title: this.$t('message.scripts.tableDetails.SFZJ'), key: 'primary', type: 'boolean', width: '10%' },
        { title: this.$t('message.scripts.tableDetails.SFFQ'), key: 'partitionField', type: 'boolean', width: '10%'  },
        { title: this.$t('message.scripts.tableDetails.model'), key: 'modeInfo.name', width: '10%'  },
        { title: this.$t('message.scripts.tableDetails.ZDGZ'), key: 'rule', width: '10%' },
        { title: this.$t('message.scripts.tableDetails.MS'), key: 'comment', width: '15%' },
      ],
      dragStartX: undefined,
      dragEndX: undefined,
      dragLine: {
        show: 'none',
        diff: 0,
        left: 0
      },
      adjustCol: []
    };
  },
  computed: {
    maxSize() {
      return Math.floor((window.innerHeight - 242) / 46) - 1;
    },
    columnCalc() {
      return this.tableColumns.map((item, index) => {
        return this.adjustCol[index] ? {
          ...item,
          width: this.adjustCol[index]
        } : {
          ...item
        }
      })
    }
  },
  watch: {
    searchText(val) {
      this.searchColList = [];
      let specialCharacter = ['\\', '$', '(', ')', '*', '+', '.', '[', '?', '^', '{', '|'];
      specialCharacter.map(v => {
        let reg = new RegExp('\\' + v, 'gim');
        val = val.replace(reg, '\\' + v);
      });
      const regexp = new RegExp(val, 'i');
      const tmpList = this.table;
      tmpList.forEach((o, index) => {
        if ([o.name, o.comment, o.alias].some(item => regexp.test(item || ''))) {
          o.index = index + 1
          this.searchColList.push(o);
        }
      });
    },
  },
  mounted() {
    this.init();
  },
  methods: {
    init() {
      this.searchColList = this.table.map((o, index)=> {
        o.index = index + 1;
        return o
      });
    },
    formatValue(item, field) {
      return utils.formatValue(item, field);
    },
    clickItem(e, item) {
      if (e && e.target && e.target.dataset.key) {
        if (e.target.dataset.key === 'modeInfo.name') {
          this.fieldModel = item.modeInfo || {}
          if (this.fieldModel.name) this.editModelShow = true
        }
      }
    },
    mousedown(e) {
      if (e && e.target && e.target.dataset.colIndex) {
        this.dragColIndex = e.target.dataset.colIndex - 0
        this.dragStartX = e.clientX
        this.tableLeft = this.$el.getBoundingClientRect().x
      }
    },
    mousemove(e) {
      if (this.dragStartX !== undefined) {
        this.dragEndX = e.clientX
        this.dragLine = {
          left: this.dragEndX - this.tableLeft + 'px',
          diff: this.dragEndX - this.dragStartX,
          show: 'block'
        }
      }
    },
    mouseup(e) {
      if (this.dragStartX !== undefined) {
        this.dragEndX = e.clientX
        this.dragLine = {
          left: this.dragEndX - this.tableLeft + 'px',
          diff: this.dragEndX - this.dragStartX,
          show: 'none'
        }
        this.dragStartX = undefined
        this.adjustColWidth()
      }

    },
    adjustColWidth() {
      const adjustCol = []
      this.$el.querySelectorAll('#tbheader .field-list-item').forEach(item => adjustCol.push(item.getBoundingClientRect().width))
      adjustCol.forEach((item, index) => {
        if (index === this.dragColIndex) {
          adjustCol[index] = adjustCol[index] + this.dragLine.diff + 'px'
        } else {
          adjustCol[index] = adjustCol[index] + this.dragLine.diff / (adjustCol.length - 1) * -1 + 'px'
        }
      })
      this.adjustCol = adjustCol
    },
    handleCopy() {
      const contents = [this.columnCalc.map(item => item.title).join('\t')];
      const columnKeys = this.columnCalc.map(item => item.key);
      this.searchColList.forEach(item => {
        let items = []
        columnKeys.forEach(key => {
          if (['primary','partitionField'].includes(key) && typeof item[key] === 'boolean') {
            items.push(item[key] ? '是' : '否');
          } else {
            items.push(item[key]);
          }
        })
        contents.push(items.join('\t'));
      })
      const text = contents.join('\n');
      const textArea = document.createElement("textarea");
      textArea.value = text;
      document.body.appendChild(textArea);
      textArea.select();
      document.execCommand("copy");
      document.body.removeChild(textArea);
      this.$Message.success(this.$t("message.scripts.paste_successfully"));
    }
  },
};
</script>
<style lang="scss" scoped>
@import '@dataspherestudio/shared/common/style/variables.scss';
  .field-list {
      height: calc(100% - 52px);
      overflow: hidden;
      width: 100%;
      .field-list-header,
      .field-list-body {
          width: 100%;
          display: flex;
          border: 1px solid #dcdee2;
          @include border-color($border-color-base, $dark-border-color-base);
          height: 46px;
          line-height: 46px;
      }
      .field-list-search {
        display: flex;
        &__button {
          flex: 0 0 150px;
          margin-left: 10px;
        }
      }
      .field-list-header {
          background-color: #5e9de0;
          color: #fff;
          font-weight: bold;
          margin-top: 10px;
          border: none;
      }
      .field-list-body {
          border-bottom: none;
          @include bg-color($light-base-color, $dark-base-color);
          .field-table-mode {
            color: $primary-color
          }
          &:last-child {
              border-bottom: 1px solid $border-color-base;
              @include border-color($border-color-base, $dark-border-color-base);
          }
      }
      .field-list-item {
          width: 200px;
          padding: 0 10px;
          display: inline-block;
          height: 100%;
          text-align: center;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
          position: relative;
          min-width: 80px;
          max-width: 30%;
          &:not(:first-child){
              border-left: 1px solid $border-color-base;
              @include border-color($border-color-base, $dark-border-color-base);
          }
      }
  }
  .resize-bar {
    position: absolute;
    width: 10px;
    height: 100%;
    bottom: 0;
    right: -5px;
    cursor: col-resize;
    z-index: 1;
  }
  .drag-line {
    position: absolute;
    top: 0;
    width: 0px;
    border-left: .5px dashed #eee;
    height: 100%;
    z-index: 1;
    display: none;
    pointer-events: none;
  }
  .ovy {
    padding-right: 8px
  }

</style>
