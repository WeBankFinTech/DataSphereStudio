<template>
  <div class="field" :style="{ height: resultHeight + 'px' }">
    <template v-if="codePrecheckRes && codePrecheckRes.checkData && codePrecheckRes.checkData.length > 0">
      <p class="item-content" v-for="(item,index) in codePrecheckRes.checkData" :key="index">
        {{item.detailMessage}}
      </p>
    </template>
  </div>
</template>
<script>
export default {
  props: {
    codePrecheckRes: Object,
    scriptViewState: Object,
  },
  computed: {
    resultHeight() {
      return this.scriptViewState.bottomContentHeight - 36
    },
  }
};
</script>
<style lang="scss" scoped>
.field {
  position: relative;
  overflow: auto;
}
</style>
