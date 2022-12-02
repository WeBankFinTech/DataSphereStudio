<template>
  <div class="wrap">
    <template v-if="!projectName">
      <div class="emptyGuideWrap">
        <emptyGuide />
      </div>
    </template>
    <template v-else>
      <iframe ref="ifr" :src="openurl" width="100%" frameborder="0"></iframe>
    </template>
    <Spin v-if="projectName && loading" fix>{{ $t('message.common.Loading') }}</Spin>
  </div>
</template>

<script>
import qs from 'qs';
import emptyGuide from "./emptyGuide.vue"
import storage from '@dataspherestudio/shared/common/helper/storage'

export default {
  components: {
    emptyGuide
  },
  mounted() {
    const applications = storage.get('applications') || []
    let applicationItem = {}
    applications.forEach(item => {
      if (item.appconns) {
        const appItem = item.appconns.find(app => app.name == 'realTimeJobCenter')
        if (appItem) applicationItem = appItem
      }
    })
    const ifr = this.$refs.ifr;
    if (ifr) {
      ifr.onload = () => {
        this.loading = false
      }
    }
    this.url = applicationItem.appInstances && applicationItem.appInstances[0] && applicationItem.appInstances[0].homepageUri;
  },
  computed: {
    openurl() {
      if (this.url) {
        const urlObj = new URL(this.url)
        const search = qs.parse(urlObj.search)
        search.projectName = this.projectName
        return decodeURIComponent(urlObj.origin + urlObj.pathname + '?' + qs.stringify(search) + urlObj.hash )
      }
      return ''
    }
  },
  data() {
    return {
      url: '',
      loading: true
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
@import '@dataspherestudio/shared/common/style/variables.scss';

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

