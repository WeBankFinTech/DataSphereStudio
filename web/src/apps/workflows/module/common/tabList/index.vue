<template>
  <div class="workflowTabContainer">
    <div class="tap-bar">
      <!-- 上面主标题Tab 及 切换按钮 -->
      <div class="topTabList">
        <!-- 主标题面包屑 -->
        <div class="topLeft">
          <Breadcrumb class="breadcrumb-bar">
            <BreadcrumbItem v-for="(item, index) in topTabList" :key="index" :to="item.url">
              {{ item.name }}
            </BreadcrumbItem>
          </Breadcrumb>
        </div>
        <!-- 切换按钮 -->
        <div class="buttonChange">
          <slot name="topRight">
            <Dropdown @on-click="handleChangeButton">
              <Button type="primary" icon="md-swap">{{ currentButton.dicName }}</Button>
              <Dropdown-menu slot="list">
                <Dropdown-item v-for="item in buttonText" :key="item.dicKey" :name="item.dicValue">{{item.dicName}}</Dropdown-item>
              </Dropdown-menu>
            </Dropdown>
          </slot>
          <Dropdown class="button-group" @on-click="menuHandleChangeButton">
            <Button icon="md-reorder">{{$t('message.orchestratorModes.menu')}}</Button>
            <Dropdown-menu slot="list">
              <Dropdown-item v-for="item in menuButtonText" :key="item.id" :name="item.name">
                <Icon type="md-settings"></Icon>
                {{item.name}}
              </Dropdown-item>
            </Dropdown-menu>
          </Dropdown>
        </div>
      </div>
      <!-- 底部工作流Tab -->
      <div class="bottomTapList">
        <slot name="bottom">
          <div class="bottomLeftText" :class="{'active': textColor}" :style="{ color: textColor }" @click="selectProject">{{ currentButton.dicName }}</div>
          <div class="bottomRightContainer">
            <template v-for="(work, index) in bottomTapList">
              <div
                :key="work.tabId"
                :class="{active:currentTab.tabId === work.tabId && !textColor}"
                class="tab-item"
                ref="work_item"
              >
                <we-tab
                  :index="index"
                  :work="work"
                  @on-choose="onChooseWork"
                  @on-remove="removeWork"
                />
              </div>
            </template>
          </div>
        </slot>
      </div>
    </div>
    <div class="defaultSlot">
      <slot></slot>
    </div>
  </div>
</template>
<script>
import weTab from './tabs.vue';
import i18n from '@/common/i18n';
export default {
  name: "WorkflowTabList",
  components: {
    weTab
  },
  props: {
    // 上方主标题list数据
    topTabList: {
      type: Array,
      default: () => [ { name: '工作空间名称', url: '' }, { name: '工程名称', url: '' } ]
    },
    // 底部工作流list数据
    bottomTapList: {
      type: Array,
      default: () => []
    },
    // 切换tab显示
    tabName: {
      type: [String, Number],
      default: '1'
    },
    buttonText: {
      type: Array,
      default: () => [
      ]
    },
    menuButtonText: {
      type: Array,
      default: () => [
        {
          id: 1,
          name: i18n.t('message.orchestratorModes.setting'),
          icon: ''
        }
      ]
    },
    textColor: {
      type: String
    },
    currentTab: {
      type: null
    }
  },
  data() {
    return {
      currentButton: {
      },
    };
  },
  computed: {

  },
  watch: {
    buttonText(val, old) {
      // 只有新旧值变化的时候才改变
      if (val.length > 0 && JSON.stringify(val) !== JSON.stringify(old)) {
        this.currentButton = val[0];
        this.$emit('handleChangeButton', this.currentButton);
      }
    }
  },
  methods: {
    removeWork(tabData) {
      this.$emit('handleTabRemove', tabData.tabId)
    },
    onChooseWork(tabData) {
      this.$emit('bandleTapTab', tabData.tabId)
    },
    handleChangeButton(dicValue) {
      this.currentButton = this.buttonText.find((item) => item.dicValue === dicValue);
      this.$emit('handleChangeButton', this.currentButton);
    },
    selectProject() {
      this.$emit('selectProject');
    },
    menuHandleChangeButton() {
      this.$emit('menuHandleChangeButton')
    }
  }
};
</script>
<style lang="scss" scoped src="./index.scss"></style>
