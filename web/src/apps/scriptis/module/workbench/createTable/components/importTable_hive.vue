<template>
  <div class="import-table-hive">
    <p class="import-table-hive-item">
      <span class="import-table-hive-item-title">{{$t('message.scripts.createTable.XZK')}}</span>
      <Select
        transfer
        v-model="hive.dbName"
        class="import-table-hive-item-select"
        @on-change="handleHiveChange">
        <Option
          v-for="(db) in dbList"
          :key="db.name"
          :value="db.name">{{ db.name }}</Option>
      </Select>
    </p>
    <p class="import-table-hive-item">
      <span class="import-table-hive-item-title">{{$t('message.scripts.createTable.XZB')}}</span>
      <Select
        transfer
        v-model="hive.tbName"
        class="import-table-hive-item-select"
        @on-change="handleTbChange"
        v-if="currentAcitvedDb">
        <Option
          v-for="(tb) in currentAcitvedDb.children"
          :key="tb.name"
          :value="tb.name">{{ tb.name }}</Option>
      </Select>
    </p>
  </div>
</template>
<script>
export default {
  props: {
    dbList: {
      type: Array,
      required: true,
    },
    hive: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      currentDbIndex: 0,
      currentAcitvedTb: null,
      currentAcitvedDb: null,
    };
  },
  mounted() {
    this.init();
  },
  methods: {
    init() {
      const personalDbName = `${this.getUserName()}_ind`;
      this.handleHiveChange(personalDbName);
      this.currentDbIndex = this.dbList.findIndex((item) => item.name === personalDbName);
      this.currentAcitvedDb = this.dbList[this.currentDbIndex];
      this.hive.dbName = this.currentAcitvedDb.name;
    },
    handleTbChange(val) {
      if (!val) {
        return;
      }
      this.hive.tbName = val;
      const tbList = this.dbList[this.currentDbIndex].children;
      this.currentAcitvedTb = tbList.find((tb) => tb.name === val);
      this.$emit('get-fields', this.currentAcitvedTb);
    },
    handleHiveChange(val) {
      this.$emit('get-tables', val);
      this.currentDbIndex = this.dbList.findIndex((item) => item.name === val);
      this.currentAcitvedDb = this.dbList[this.currentDbIndex];
    },
  },
};
</script>
<style lang="scss" scoped>
.import-table-hive {
    .import-table-hive-item {
        padding: 10px 0 10px 20px;
        .import-table-hive-item-title {
            display: inline-block;
            font-weight: bold;
            margin-right: 10px;
        }
        .import-table-hive-item-select {
            width: 300px;
        }
    }
}
</style>
