<template>
  <div class="field-list">
    <Input
      v-model="searchText"
      :placeholder="$t('message.scripts.tableDetails.SSZDMC')">
      <Icon
        slot="prefix"
        type="ios-search"/>
    </Input>
    <div class="field-list-header">
      <div class="field-list-item field-list-index">序号</div>
      <div
        class="field-list-item"
        v-for="(item, index) in tableColumns"
        :key="index"
        :class="item.className">{{ item.title }}</div>
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
        :style="{'border-bottom': index === searchColList.length - 1 ? '1px solid #dcdee2' : 'none'}"
        @click="clickItem($event, item)"
      >
        <div class="field-list-item field-list-index">{{ index + 1 }}</div>
        <div
          class="field-list-item"
          :title="formatValue(item, field)"
          v-for="(field, index2) in tableColumns"
          :data-key="field.key"
          :key="index2"
          :class="field.className">{{ formatValue(item, field) }}</div>
      </li>
    </virtual-list>
    <Modal v-model="editModelShow" :title="$t('message.scripts.createTable.titleModel')" :footer-hide="true">
      <Form :model="fieldModel" :label-width="80">
        <Form-item prop="name" label="����">
          <Input v-model="fieldModel.name" placeholder="" :disabled="true"></Input>
        </Form-item>
        <Form-item prop="type" label="����">
          <RadioGroup v-model="fieldModel.type">
            <Radio label="index" disabled>ָ��</Radio>
            <Radio label="dimension" disabled>ά��</Radio>
          </RadioGroup>
        </Form-item>
        <Form-item prop="business" label="ҵ��ھ�">
          <Input v-model="fieldModel.business" placeholder="" :disabled="true"></Input>
        </Form-item>
        <Form-item prop="calculate" label="����ھ�">
          <Input v-model="fieldModel.calculate" placeholder="" :disabled="true"></Input>
        </Form-item>
        <Form-item prop="formula" label="���㹫ʽ">
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
        { title: this.$t('message.scripts.tableDetails.ZDM'), key: 'name', className: 'field-table-name' },
        { title: this.$t('message.scripts.tableDetails.ZDLX'), key: 'type', className: 'field-table-type' },
        { title: this.$t('message.scripts.tableDetails.BM'), key: 'alias', className: 'field-table-alias' },
        { title: this.$t('message.scripts.hiveTableExport.LX'), key: 'modeInfo.type', className: 'field-table-name' },
        { title: this.$t('message.scripts.tableDetails.SFZJ'), key: 'primary', type: 'boolean', className: 'field-table-primary' },
        { title: this.$t('message.scripts.tableDetails.SFFQ'), key: 'partitionField', type: 'boolean', className: 'field-table-part' },
        { title: this.$t('message.scripts.tableDetails.model'), key: 'modeInfo.name', className: 'field-table-mode' },
        { title: this.$t('message.scripts.tableDetails.ZDGZ'), key: 'rule', className: 'field-table-rule' },
        { title: this.$t('message.scripts.tableDetails.MS'), key: 'comment', className: 'field-table-comment' },
      ],
    };
  },
  computed: {
    maxSize() {
      return Math.floor((window.innerHeight - 242) / 46) - 1;
    },
  },
  watch: {
    searchText(val) {
      const reg = /^[\w]*$/;
      if (val && reg.test(val)) {
        this.searchColList = [];
        const regexp = new RegExp(`.*${val}.*`, 'i');
        const tmpList = this.table;
        tmpList.forEach((o) => {
          if (regexp.test(o.name)) {
            this.searchColList.push(o);
          }
        });
      } else {
        this.searchColList = this.table;
      }
    },
  },
  mounted() {
    this.init();
  },
  methods: {
    init() {
      this.searchColList = this.table;
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
          height: 46px;
          line-height: 46px;
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
          background: #fff;
          .field-table-mode {
            color: $primary-color
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
      }
      .field-list-index {
          width: 5%;
          min-width: 50px;
      }
      .field-table-name {
          width: 10%;
      }
      .field-table-mode {
          width: 10%;
      }
      .field-table-type {
          width: 10%;
          min-width: 70px;
      }
      .field-table-alias {
          width: 10%;
      }
      .field-table-primary {
          width: 10%;
          min-width: 70px;
      }
      .field-table-part {
          width: 10%;
          min-width: 70px;
      }
      .field-table-rule {
          width: 10%;
          min-width: 70px;
      }
      .field-table-comment {
          width: 25%;
      }
  }
</style>
