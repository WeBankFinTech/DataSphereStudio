<template>
  <div class="wrap">
    <template v-if="!projectName">
      <div class="emptyGuideWrap">
        <emptyGuide />
      </div>
    </template>
    <template v-else>
      <iframe :src="`${url}?projectName=${this.projectName}`" width="100%" frameborder="0"></iframe>
    </template>
  </div>
</template>

<script>
import emptyGuide from "./emptyGuide.vue"
export default {
  components: {
    emptyGuide
  },
  mounted() {
    let baseInfo = JSON.parse(localStorage.getItem('baseInfo'))
    let applicationItem = baseInfo.applications.filter(item => item.name == 'realTimeJobCenter')[0]
    this.url = applicationItem.url;
  },
  data() {
    return {
      url: '',
    }
  },
  props: {
    projectName: {
      type: String,
      default: ''
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="scss" scoped>
@import "@/common/style/variables.scss";

.emptyGuideWrap {
  width: 100%;
  min-height: 400px;
  min-width: 600px;
  display: flex;
  justify-content: center;
  align-items: center;
  @include bg-color(#fff, $dark-base-color);
}

.wrap {
  width: 100%;
  min-height: 100vh;
}
</style>

