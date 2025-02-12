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
              <template v-for="(work, index) in bottomTapList">
                <we-tab
                  ref="work_item"
                  :key="work.tabId"
                  :index="index"
                  :work="work"
                  :isActive="currentTab.tabId === work.tabId"
                  @on-choose="onChooseWork"
                  @on-remove="removeWork"
                />
              </template>
            </div>
          </div>
          <div v-if="associateGit" @click="$emit('handleChangeButton', 'find')" style="width: 60px;cursor: pointer;">
            <SvgIcon
              icon-class="search"
            />
            <span>{{ $t('message.workflow.Find') }}</span>
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
    // 底部工作流list数据
    bottomTapList: {
      type: Array,
      default: () => [],
    },
    buttonText: {
      type: Array,
      default: () => [],
    },
    currentTab: {
      type: null,
    },
    loading: {
      type: Boolean,
      default: false,
    },
    modeOfKey: {
      type: String,
    },
    associateGit: {
      type: Boolean,
      default: false,
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
    this.schedulerList = JSON.parse(
      sessionStorage.getItem("scheduler_tab_list")
    )
  },
  watch: {
  },
  methods: {
    handleChangeMode() {
      let name = this.currentButton.dicValue || ""
      if (!name) return
      this.handleChangeButton(name)
    },
    removeWork(tabData) {
      this.$emit("handleTabRemove", tabData.tabId)
    },
    onChooseWork(tabData) {
      this.$emit("bandleTapTab", tabData.tabId)
    },
    handleChangeButton(dicValue) {
      const btn = this.buttonText.find((item) => item.dicValue === dicValue)
      this.$emit("handleChangeButton", btn)
    }
  },
}
</script>
<style lang="scss" scoped src="./index.scss"></style>
