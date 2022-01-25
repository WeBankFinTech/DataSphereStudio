<template>
  <div style="height: 100%;">
    <scriptView
      v-if="work.type=='workspaceScript' || work.type=='hdfsScript' || work.type=='node' || work.type=='historyScript'"
      :readonly="readonly"
      :current="current"
      :node="node"
      :work="work"/>
    <backgroundScriptView
      v-if="work.type=='backgroundScript'"
      :work="work"
      @remove-work="removeWork"/>
    <detailView
      :work="work"
      v-if="work.type=='tableDetails'"
    />
    <createTableView
      :work="work"
      v-if="work.type=='createTable'"/>
    <dbDetails
      v-if="work.type == 'dbDetails'"
      :work="work">
    </dbDetails>

  </div>
</template>
<script>
import scriptView from './script/script.vue';
import backgroundScriptView from './script/backgroundScript.vue';
import detailView from './tableDetails/index.vue';
import createTableView from './createTable/index.vue';
import dbDetails from './dbDetails/index.vue';
export default {
  name: 'workbench',
  components: {
    scriptView,
    backgroundScriptView,
    detailView,
    createTableView,
    dbDetails
  },
  props: {
    work: Object,
    node: Object,
    current: {
      type: String,
      default: ''
    },
    readonly: {
      type: Boolean,
      default: false,
    },
  },
  created() {
  },
  mounted() {
  },
  methods: {
    removeWork() {
      this.$emit('remove-work', this.work);
    },
  },
};
</script>
