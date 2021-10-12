<template>
  <div class="assets-index-wrap">
    <!-- top 搜索框 -->
    <div class="assets-index-t">
      <!-- bottom -->
      <div class="assets-index-t-b1">
        <div class="assets-index-t-b1-search">
          <Input
            search
            :enter-button="$t(`message.dataGovernance.search`)"
            :placeholder="$t(`message.dataGovernance.pleaseEnterATableName`)"
            size="large"
            @on-search="onSearch"
            v-model="queryForTbls"
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
            v-model="searchOption.owner"
            filterable
            clearable
            remote
            :remote-method="remoteSearchOwner"
            :loading="owerSerachLoading"
            style="width:167px"
          >
            <Option
              v-for="(item, idx) in ownerList"
              :value="item.value"
              :key="idx"
              >{{ item.label }}</Option
            >
          </Select>
        </div>
        <!--<div class="assets-index-b-l-env">
          <span>环境</span>
          <Radio v-model="searchOption.env"
            ><label
              style="color: rgba(0,0,0,0.65; fontSize: 14px; fontWeight: normal;"
              >生产环境</label
            ></Radio
          >
        </div>-->
        <div class="assets-index-b-l-label">
          <span>主题域/分层</span>
          <Select
            v-model="searchOption.classification"
            clearable
            style="width:167px"
          >
            <OptionGroup label="主题域">
              <Option
                v-for="(item, idx) in subjectList"
                :value="item.name"
                :key="idx"
              >{{ item.name }}</Option>
            </OptionGroup>
            <OptionGroup label="分层">
              <Option
                v-for="(item, idx) in layerList"
                :value="item.name"
                :key="idx"
              >{{ item.name }}</Option>
            </OptionGroup>
          </Select>
        </div>
      </div>

      <!-- right -->
      <div class="assets-index-b-r">
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
//import api from "@/common/service/api";
import tabCard from "../../module/common/tabCard/index.vue";
import { getHiveTbls, getWorkspaceUsers, getLayersAll, getThemedomains } from "../../service/api";
import { EventBus } from "../../module/common/eventBus/event-bus";
import { storage } from "../../utils/storage";
import { throttle } from "lodash";
export default {
  components: {
    tabCard
  },
  data() {
    return {
      searchOption: {
        limit: 10,
        offset: 0
      },
      ownerList: [],
      userList: [],
      cardTabs: [],
      queryForTbls: "",
      owerSerachLoading: false,
      isLoading: false,
      subjectList: [],
      layerList: []
    };
  },
  created() {
    let searchParams = storage.getItem("searchTbls");
    if (searchParams) {
      this.queryForTbls = JSON.parse(searchParams).query;
      getHiveTbls(JSON.parse(searchParams))
        .then(data => {
          if (data.result) {
            this.cardTabs = data.result;
          }
        })
        .catch(err => {
          console.log("Search", err);
        });
    }
  },
  mounted() {
    let _this = this;
    this.throttleLoad = throttle(() => {
      _this.scrollHander();
    }, 300);
    window.addEventListener("scroll", this.throttleLoad);
    getThemedomains().then(res => {
      let { result } = res
      this.subjectList = result
    })
    getLayersAll().then(res => {
      let { result } = res
      this.layerList = result
    })
  },
  destroyed() {
    window.removeEventListener("scroll", this.throttleLoad);
  },
  methods: {
    // 搜索
    onSearch() {
      const params = {
        query: this.queryForTbls,
        owner: this.searchOption.owner,
        classification: this.searchOption.classification,
        limit: 10,
        offset: 0
      };
      this.searchOption["limit"] = 10;
      this.searchOption["offset"] = 0;
      storage.setItem("searchTbls", JSON.stringify(params));
      getHiveTbls(params)
        .then(data => {
          if (data.result) {
            this.cardTabs = data.result;
          } else {
            this.cardTabs = [];
          }
        })
        .catch(err => {
          console.log("Search", err);
        });
    },

    onChooseCard(model) {
      let that = this;
      EventBus.$emit("on-choose-card", model);
      const workspaceId = that.$route.query.workspaceId;
      const { guid } = model;
      that.$router.push({
        name: "dataGovernance/assets/info",
        params: { guid },
        query: { workspaceId }
      });
    },

    // 下拉到底部加载
    handleReachBottom() {
      let that = this;
      const res = that.cardTabs.slice(0);
      if (that.isLoading) {
        return that.$Message.success("所有数据已加载完成");
      }
      return new Promise(resolve => {
        const params = {
          query: that.queryForTbls,
          owner: that.searchOption.owner,
          limit: that.searchOption.limit,
          offset: that.searchOption.offset + that.searchOption.limit,
          classification: that.classification
        };
        that.searchOption.offset += that.searchOption.limit;
        getHiveTbls(params)
          .then(data => {
            if (data.result) {
              that.cardTabs = res.concat(data.result);
            } else {
              that.$Message.success("所有数据已加载完成");
              that.isLoading = true;
            }
            resolve();
          })
          .catch(err => {
            console.log("handleReachBottom", err);
          });
      });
    },

    // 搜索负责人
    remoteSearchOwner(query) {
      let that = this;
      if (query !== "") {
        that.owerSerachLoading = true;
        let workspaceId = that.$route.query.workspaceId;
        getWorkspaceUsers(workspaceId, query)
          .then(data => {
            const { result } = data;
            const _res = [];
            if (result) {
              result.forEach(item => {
                let o = Object.create(null);
                o["value"] = item;
                o["label"] = item;
                _res.push(o);
              });
              that.ownerList = _res;
              that.owerSerachLoading = false;
              console.log("ownerList", that.ownerList);
            }
          })
          .catch(err => {
            console.log("getWorkspaceUsers", err);
          });
      } else {
        that.ownerList = [];
      }
    },

    // 下拉加載
    scrollHander() {
      const getScrollTop = () => {
        var scrollTop = 0;
        if (document.documentElement && document.documentElement.scrollTop) {
          scrollTop = document.documentElement.scrollTop;
        } else if (document.body) {
          scrollTop = document.body.scrollTop;
        }
        return scrollTop;
      };
      const getClientHeight = () => {
        var clientHeight = 0;
        if (
          document.body.clientHeight &&
          document.documentElement.clientHeight
        ) {
          clientHeight = Math.min(
            document.body.clientHeight,
            document.documentElement.clientHeight
          );
        } else {
          clientHeight = Math.max(
            document.body.clientHeight,
            document.documentElement.clientHeight
          );
        }
        return clientHeight;
      };
      const getScrollHeight = () => {
        return Math.max(
          document.body.scrollHeight,
          document.documentElement.scrollHeight
        );
      };
      if (getScrollTop() + getClientHeight() === getScrollHeight()) {
        // 拉数据
        this.handleReachBottom();
      }
    }
  }
};
</script>
<style lang="scss" scoped>
@import "@/common/style/variables.scss";

.assets-index-wrap {
  flex: 1;
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
      padding-bottom: 10px;
    }
  }
}
</style>
