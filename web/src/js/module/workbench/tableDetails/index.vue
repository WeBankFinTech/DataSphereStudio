<template>
  <div class="table-detail">
    <Tabs
      type="card"
      class="table-detail-tabs"
      :animated="false">
      <TabPane
        :label="item"
        v-for="(item, index) in tabList"
        :key="index"
        style="height:100%;">
        <basic
          v-if="item === $t('message.tableDetails.BZBSX')"
          :table-info="work.data"
          :en-env="isEnEnv"></basic>
        <field
          v-if="item === $t('message.tableDetails.BZDXX')"
          :table-info="work.data"></field>
        <statistics
          v-if="item === $t('message.tableDetails.BTJXX')"
          :table-info="work.data"
          :en-env="isEnEnv"></statistics>
      </TabPane>
    </Tabs>
  </div>
</template>
<script>
import basic from './components/basic.vue';
import field from './components/field.vue';
import statistics from './components/statistics.vue';
export default {
  components: {
    basic,
    field,
    statistics,
  },
  props: {
    work: {
      type: Object,
      required: true,
    },
  },
  computed: {
    isEnEnv() {
      return localStorage.getItem('locale') === 'en';
    }
  },
  data() {
    return {
      tabList: [this.$t('message.tableDetails.BZBSX'), this.$t('message.tableDetails.BZDXX'), this.$t('message.tableDetails.BTJXX')],
    };
  },
};
</script>
