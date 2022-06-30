<template>
  <div class="workflowTabContainer">
    <div class="tap-bar" :class="{ 'tree-fold': treeFold }">
      <!-- 底部工作流Tab -->
      <div class="bottomTapList">
        <slot name="bottom">
          <div class="bottomRightContainer">
            <div class="tap-menu">
              <Dropdown @on-click="handleChangeButton">
                <a style="text-decoration: none;" @click="handleChangeMode">
                  {{ currentButton.dicName }}
                  <Icon type="ios-arrow-down"></Icon>
                </a>
                <Dropdown-menu slot="list">
                  <Dropdown-item
                    v-for="item in buttonText"
                    :key="item.dicKey"
                    :name="item.dicValue"
                  >{{ item.dicName }}</Dropdown-item
                  >
                </Dropdown-menu>
              </Dropdown>
            </div>
            <div class="top-tap-lists">
              <template v-for="(work, index) in schedulerList">
                <we-tab
                  ref="work_item"
                  :key="work.id"
                  :index="index"
                  :work="work"
                  :isActive="currentTab === work.id"
                  @on-choose="chooseScheduler"
                  @on-remove="removeScheduler"
                />
              </template>
            </div>
          </div>
        </slot>
      </div>
    </div>
    <div class="defaultSlot">
      <slot></slot>
    </div>
    <Spin v-if="loading" size="large" fix />
  </div>
</template>
<script>
import weTab from "@dataspherestudio/shared/components/lubanTab/index.vue"
import eventbus from "@dataspherestudio/shared/common/helper/eventbus"
export default {
  name: "WorkflowTabList",
  components: {
    weTab,
  },
  props: {
    // 上方主标题list数据
    treeFold: {
      type: Boolean,
      default: true,
    },
    buttonText: {
      type: Array,
      default: () => [],
    },
    currentTab: {
      type: [String, Number],
    },
    loading: {
      type: Boolean,
      default: false,
    },
    modeOfKey: {
      type: String,
    },
  },
  data() {
    return {
      schedulerList: []
    }
  },
  computed: {
    currentButton() {
      return this.modeOfKey ? this.buttonText.find(
        (item) => item.dicValue === this.modeOfKey
      ) || {}: {}
    }
  },
  mounted() {
    const that = this
    const SKEY = this.$parent.getTabStorageKey()
    this.schedulerList = JSON.parse(
      sessionStorage.getItem(SKEY)
    )
    eventbus.on('scheduler_tab_list_change', list => {
      that.schedulerList = list
    })
  },
  watch: {
  },
  methods: {
    handleChangeMode() {
      let name = this.currentButton.dicValue || ""
      if (!name) return
      this.handleChangeButton(name)
    },
    handleChangeButton(dicValue) {
      const btn = this.buttonText.find((item) => item.dicValue === dicValue)
      this.$emit("handleChangeButton", btn)
    },
    chooseScheduler(tabData) {
      this.$emit('handleChooseScheduler', tabData)
    },
    removeScheduler(tabData) {
      let curList = this.schedulerList.filter(item => item.id != tabData.id)
      this.schedulerList = curList;
      const SKEY = this.$parent.getTabStorageKey()
      sessionStorage.setItem(SKEY, JSON.stringify(curList))
    }
  },
}
</script>
<style lang="scss" scoped src="./index.scss"></style>
