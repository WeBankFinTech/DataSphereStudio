<template>
  <div class="workbench-body">
    <scriptView
      v-if="work.type=='workspaceScript' || work.type=='hdfsScript'"
      :work="work"/>
    <historyScriptView
      v-if="work.type=='historyScript'"
      :work="work"/>
    <backgroundScriptView
      v-if="work.type=='backgroundScript'"
      :work="work"
      @remove-work="removeWork"/>
    <visualAnalysis
      v-if="work.type=='visualAnalysis'"
      :work="work">
    </visualAnalysis>
    <nodeView
      v-if="work.type=='node'"
      :work="work"
      :node="node"
      :readonly="readonly"/>
    <detailView
      :work="work"
      v-if="work.type=='tableDetails'"
    />
    <createTableView
      :work="work"
      v-if="work.type=='createTable'"/>
  </div>
</template>
<script>
import scriptView from './script/script.vue';
import historyScriptView from './script/historyScript.vue';
import backgroundScriptView from './script/backgroundScript.vue';
import visualAnalysis from './visualAnalysis/visualAnalysis.vue';
import nodeView from './workflow/node.vue';
import detailView from './tableDetails/index.vue';
import createTableView from './createTable/index.vue';
export default {
  components: {
    scriptView,
    historyScriptView,
    visualAnalysis,
    nodeView,
    backgroundScriptView,
    detailView,
    createTableView,
  },
  props: {
    work: Object,
    node: Object,
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
