<template>
  <div class="assets-index-wrap" ref="assets-index-wrap">
    <!-- top 搜索框 -->
    <div class="assets-index-t">
      <!-- bottom -->
      <div class="assets-index-t-b1">
        <div class="assets-index-t-b1-search">
          <Input
            search
            :enter-button="$t(`message.dataAssetManage.search`)"
            :placeholder="$t(`message.dataAssetManage.pleaseEnterATableName`)"
            size="large"
            @on-search="onSearch"
            v-model="searchToken"
          />
        </div>
      </div>
    </div>

    <!-- bottom 复选框搜索框 + card tabs -->
    <div class="assets-index-b">
      <!-- left -->
      <div class="assets-index-b-l">
        <div class="assets-index-b-l-title">
          <span>筛选条件</span>
        </div>
        <div class="assets-index-b-l-user">
          <span>负责人</span>
          <Select
            v-model="serachOption.owner"
            clearable
            :loading="ownerListLoading"
            style="width: 167px"
          >
            <Option
              v-for="(item, idx) in ownerList"
              :value="item.name"
              :key="idx"
            >
              {{ item.name }}
            </Option>
          </Select>
        </div>
        <div class="assets-index-b-l-label">
          <span>标签</span>
          <Select
            v-model="serachOption.label"
            filterable
            clearable
            :remote-method="remoteSearchLabel"
            :loading="labelSearchLoading"
            style="width: 167px"
          >
            <Option
              v-for="item in labelList"
              :value="item.name"
              :key="item.guid"
            >
              {{ item.name }}
            </Option>
          </Select>
        </div>
      </div>

      <!-- right -->
      <div class="assets-index-b-r" ref="assets-r">
        <Spin v-if="loading" fix></Spin>
        <template v-for="model in cardTabs">
          <tab-card
            :model="model"
            :key="model.guid"
            @on-choose="onChooseCard"
          ></tab-card>
        </template>
      </div>
    </div>
  </div>
</template>
<script>
import tabCard from "../../module/common/tabCard/index.vue";
import { getHiveTbls, getWorkspaceUsers, getLabels } from "../../service/api";
import { storage } from "../../utils/storage";
import {EventBus} from "../../module/common/eventBus/event-bus";
import { throttle } from "lodash";
import mixin from "@/common/service/mixin";
export default {
  components: {
    tabCard,
  },
  mixins: [mixin],
  data() {
    return {
      // 搜索参数
      serachOption: {
        owner: "",
        label: "",
        limit: 10,
        offset: 0,
      },
      // 负责人列表
      ownerList: [],
      // 标签列表
      labelList: [],
      // 数据
      cardTabs: [],
      // 搜索词
      searchToken: "",
      // 负责人加载中
      ownerListLoading: false,
      // 标签加载中
      labelSearchLoading: false,
      // 请求加载中
      loading: false,
      // 所有数据是否全部加载完成
      isLoadDataFinish: false,
      // 节流
      throttleLoad: null,
    };
  },
  created() {
    // 拿到上次搜索历史
    let searchParams = storage.getItem("searchTbls");
    // 判断搜索
    if (searchParams) {
      this.searchToken = JSON.parse(searchParams).query;
      getHiveTbls(JSON.parse(searchParams))
        .then((data) => {
          if (data.result) {
            this.cardTabs = data.result;
          }
        })
        .catch((err) => {
          console.log("Search", err);
        });
    } else {
      this.onSearch();
    }
  },
  mounted() {
    // 获取用户
    this.handleGetUsers();
    // 设置防抖函数
    this.throttleLoad = throttle(() => {
      this.scrollHander();
    }, 300);
    // 添加事件监听
    this.$refs["assets-index-wrap"].addEventListener(
      "scroll",
      this.throttleLoad
    );
  },
  beforeDestroy() {
    // 解除事件监听
    this.$refs["assets-index-wrap"].removeEventListener(
      "scroll",
      this.throttleLoad
    );
  },
  methods: {
    /**
     * 获取用户列表
     */
    handleGetUsers() {
      let id = this.getCurrentWorkspaceId();
      this.ownerListLoading = true;
      getWorkspaceUsers(id)
        .then((res) => {
          this.ownerListLoading = false;
          this.ownerList = res.users;
        })
        .catch(() => {
          this.ownerListLoading = false;
        });
    },
    /**
     * 搜索
     */
    onSearch() {
      this.isLoadDataFinish = false;
      // 构造搜索参数
      const params = {
        query: this.searchToken,
        owner: this.serachOption.owner || "",
        label: this.serachOption.label || "",
        limit: 10,
        offset: 0,
      };
      this.serachOption.limit = 10;
      this.serachOption.offset = 0;
      // 存储搜索参数
      storage.setItem("searchTbls", JSON.stringify(params));
      this.loading = true;
      getHiveTbls(params)
        .then((data) => {
          this.loading = false;
          if (data.result) {
            this.cardTabs = data.result;
          } else {
            this.cardTabs = [];
          }
        })
        .catch((err) => {
          this.loading = false;
          console.log("Search", err);
        });
    },

    /**
     * 下拉到底部加载
     * @returns {Promise<unknown>|void}
     */
    handleReachBottom() {
      // 复制一份
      const res = this.cardTabs.slice(0);
      if (this.isLoadDataFinish) {
        return this.$Message.success("所有数据已加载完成");
      }
      return new Promise((resolve) => {
        const params = {
          query: this.searchToken,
          owner: this.serachOption.owner || "",
          label: this.serachOption.label || "",
          limit: this.serachOption.limit,
          offset: this.serachOption.offset + this.serachOption.limit,
        };
        this.serachOption.offset += this.serachOption.limit;
        this.loading = true;
        getHiveTbls(params)
          .then((data) => {
            this.loading = false;
            if (data.result) {
              this.cardTabs = res.concat(data.result);
            } else {
              this.$Message.success("所有数据已加载完成");
              this.isLoadDataFinish = true;
            }
            resolve();
          })
          .catch((err) => {
            this.loading = false;
            console.log("handleReachBottom", err);
          });
      });
    },

    /**
     * 搜索标签
     * @param query
     */
    remoteSearchLabel(query) {
      if (query !== "") {
        this.labelSearchLoading = true;
        getLabels(query).then((data) => {
          this.labelSearchLoading = false;
          const { result } = data;
          this.labelList = result;
        });
      }
    },
    /**
      跳转
     */
    onChooseCard(model) {
      let that = this;
      EventBus.$emit("on-choose-card", model);
      const workspaceId = that.$route.query.workspaceId;
      const { guid } = model;
      that.$router.push({
        name: "assetsInfo",
        params: { guid },
        query: { workspaceId },
      });
    },

    /**
     * 下拉加載
     */
    scrollHander() {
      const getScrollTop = () => {
        return this.$refs["assets-index-wrap"].scrollTop;
      };
      const getClientHeight = () => {
        return this.$refs["assets-index-wrap"].getBoundingClientRect().height;
      };
      const getScrollHeight = () => {
        return this.$refs["assets-index-wrap"].scrollHeight;
      };
      if (getScrollTop() + getClientHeight() >= getScrollHeight()) {
        this.handleReachBottom();
      }
    },
  },
};
</script>
<style lang="scss" scoped>
@import "@/common/style/variables.scss";

.assets-index-wrap {
  height: 100%;
  display: flex;
  flex-direction: column;

  .assets-index-t-t1 {
    padding: 0px $padding-25;
    border-bottom: $border-width-base $border-style-base $border-color-base;
    @include border-color(
      $background-color-base,
      $dark-workspace-body-bg-color
    );
    @include font-color($workspace-title-color, $dark-workspace-title-color);
    flex: none;
    display: flex;
    align-items: center;
    font-size: $font-size-large;

    .top-l-text {
      cursor: pointer;
      flex: none;
      font-size: $font-size-large;
      padding: 0 15px;
      margin-bottom: -1px;
      line-height: 40px;
      position: relative;

      &::after {
        content: "";
        border-left: 1px solid #dee4ec;
        @include border-color($border-color-base, $dark-border-color-base);
        width: 0;
        position: absolute;
        right: -15px;
        top: 12px;
        height: 16px;
        margin: 0 15px;
      }
    }

    .active {
      border-bottom: 2px solid $primary-color;
      @include border-color($primary-color, $dark-primary-color);
    }

    .top-r-container {
      flex: 1;
      height: 40px;
    }
  }

  .assets-index-t-b1 {
    height: 80px;
    background-color: #edf1f6;
    display: flex;
    justify-content: center;
    align-items: center;

    &-search {
      min-width: 552px;
      min-height: 40px;
    }
  }

  .assets-index-b {
    display: flex;
    flex: 1;

    &-l {
      min-width: 199px;
      padding-left: 16px;
      padding-right: 16px;
      padding-top: 15px;
      border-right: 1px solid #dee4ec;

      &-title {
        font-family: PingFangSC-Medium;
        font-size: 14px;
        color: rgba(0, 0, 0, 0.85);
        font-weight: bold;
      }

      &-user {
        margin-top: 16px;

        span {
          display: block;
          margin-bottom: 8px;
          font-family: PingFangSC-Regular;
          font-size: 14px;
          color: rgba(0, 0, 0, 0.85);
        }
      }

      &-env {
        margin-top: 16px;

        span {
          display: block;
          margin-bottom: 8px;
          font-family: PingFangSC-Regular;
          font-size: 14px;
          color: rgba(0, 0, 0, 0.85);
        }
      }

      &-label {
        margin-top: 16px;

        span {
          display: block;
          margin-bottom: 8px;
          font-family: PingFangSC-Regular;
          font-size: 14px;
          color: rgba(0, 0, 0, 0.85);
        }
      }
    }

    &-r {
      flex: 1;
      position: relative;
      padding-bottom: 10px;
    }
  }
}
</style>
