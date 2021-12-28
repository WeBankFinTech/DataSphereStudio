<template>
  <div class="workflowTabContainer">
    <div class="tap-bar" :class="{ 'tree-fold': treeFold }">
      <!-- 上面主标题Tab 及 切换按钮 -->
      <!-- <div class="topTabList">
        <div class="topLeft">
          <Breadcrumb class="breadcrumb-bar">
            <BreadcrumbItem
              v-for="(item, index) in topTabList"
              :key="index"
              :to="item.url"
            >
              {{ item.name }}
            </BreadcrumbItem>
          </Breadcrumb>
        </div>

        <div class="buttonChange">
          <slot name="topRight">
            <Dropdown @on-click="handleChangeButton">
              <Button type="primary" icon="md-swap">{{
                currentButton.dicName
              }}</Button>
              <Dropdown-menu slot="list">
                <Dropdown-item
                  v-for="item in buttonText"
                  :key="item.dicKey"
                  :name="item.dicValue"
                  >{{ item.dicName }}</Dropdown-item
                >
              </Dropdown-menu>
            </Dropdown>
          </slot>
          <Dropdown class="button-group" @on-click="menuHandleChangeButton">
            <Button icon="md-reorder">{{
              $t("message.orchestratorModes.menu")
            }}</Button>
            <Dropdown-menu slot="list">
              <Dropdown-item
                v-for="item in menuButtonText"
                :key="item.id"
                :name="item.name"
              >
                <Icon type="md-settings"></Icon>
                {{ item.name }}
              </Dropdown-item>
            </Dropdown-menu>
          </Dropdown>
        </div>
      </div> -->
      <!-- 底部工作流Tab -->
      <div class="bottomTapList">
        <slot name="bottom">
          <!-- <div
            class="bottomLeftText"
            :class="{ active: textColor }"
            :style="{ color: textColor }"
            @click="selectProject"
          >
            {{ currentButton.dicName }}
          </div> -->
          <div class="bottomRightContainer">
            <div class="tap-menu">
              <!-- <Dropdown @on-click="swtichMenu">
                <a href="javascript:void(0)" style="text-decoration: none;">
                  {{ currentMenu }}
                  <Icon type="ios-arrow-down"></Icon>
                </a>
                <DropdownMenu slot="list">
                  <DropdownItem name='dev'>开发中心</DropdownItem>
                  <DropdownItem name="scheduler">运维中心</DropdownItem>
                  <DropdownItem name='calc_center'>实时计算中心</DropdownItem>
                </DropdownMenu>
              </Dropdown> -->
              <Dropdown @on-click="handleChangeButton">
                <a href="javascript:void(0)" style="text-decoration: none;">
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
            <template v-for="(work, index) in bottomTapList">
              <div
                :key="work.tabId"
                :class="{
                  active: currentTab.tabId === work.tabId && !textColor
                }"
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
    <Spin v-if="loading" size="large" fix />
  </div>
</template>
<script>
import weTab from "./tabs.vue";
import i18n from "@/common/i18n";
import eventbus from "@/common/helper/eventbus";
export default {
  name: "WorkflowTabList",
  components: {
    weTab
  },
  props: {
    // 上方主标题list数据
    treeFold: {
      type: Boolean,
      default: true
    },
    topTabList: {
      type: Array,
      default: () => [
        { name: "工作空间名称", url: "" },
        { name: "工程名称", url: "" }
      ]
    },
    // 底部工作流list数据
    bottomTapList: {
      type: Array,
      default: () => []
    },
    // 切换tab显示
    tabName: {
      type: [String, Number],
      default: "1"
    },
    buttonText: {
      type: Array,
      default: () => []
    },
    menuButtonText: {
      type: Array,
      default: () => [
        {
          id: 1,
          name: i18n.t("message.orchestratorModes.setting"),
          icon: ""
        }
      ]
    },
    textColor: {
      type: String
    },
    currentTab: {
      type: null
    },
    loading: {
      type: Boolean,
      default: false
    },
    modeOfKey: {
      type: String
    }
  },
  data() {
    return {
      currentButton: {},
      menuList: {
        dev: "开发中心",
        scheduler: "运维中心",
        calc_center: "实时计算中心"
      },
      currentMenu: "开发中心"
    };
  },
  computed: {},
  watch: {
    buttonText(val, old) {
      // 只有新旧值变化的时候才改变
      if (val.length > 0 && JSON.stringify(val) !== JSON.stringify(old)) {
        if (this.$route.name === "Scheduler") {
          val.forEach(item => {
            if (item.dicValue === "scheduler") {
              this.currentButton = item;
            }
          });
        } else {
          this.currentButton = val[0];
        }
        this.$emit("handleChangeButton", this.currentButton);
      }
    },
    modeOfKey(val) {
      if (val)
        this.currentButton = this.buttonText.find(
          item => item.dicValue === val
        );
    }
  },
  methods: {
    removeWork(tabData) {
      this.$emit("handleTabRemove", tabData.tabId);
    },
    onChooseWork(tabData) {
      this.$emit("bandleTapTab", tabData.tabId);
      // tabData.id是编排id
      eventbus.emit("workflow.orchestratorId", {
        orchestratorId: tabData.id,
        mod: "auto"
      });
    },
    handleChangeButton(dicValue) {
      const btn = this.buttonText.find(item => item.dicValue === dicValue);
      this.$emit("handleChangeButton", btn);
    },
    selectProject() {
      this.$emit("selectProject");
    },
    menuHandleChangeButton() {
      this.$emit("menuHandleChangeButton");
    },
    swtichMenu(name) {
      this.handleChangeButton(name);
      this.currentMenu = this.menuList[name];
    }
  }
};
</script>
<style lang="scss" scoped src="./index.scss"></style>
