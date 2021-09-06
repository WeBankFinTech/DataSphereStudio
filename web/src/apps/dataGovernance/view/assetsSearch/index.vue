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
          <Select v-model="serachOption.owner" style="width:167px">
            <Option
              v-for="item in ownerList"
              :value="item.value"
              :key="item.value"
              >{{ item.label }}</Option
            >
          </Select>
        </div>
        <div class="assets-index-b-l-env">
          <span>环境</span>
          <Radio v-model="serachOption.env"
            ><label
              style="color: rgba(0,0,0,0.65; fontSize: 14px; fontWeight: normal;"
              >生产环境</label
            ></Radio
          >
        </div>
        <div class="assets-index-b-l-label">
          <span>标签</span>
          <Select v-model="serachOption.label" multiple style="width:167px">
            <Option
              v-for="item in userList"
              :value="item.value"
              :key="item.value"
              >{{ item.label }}</Option
            >
          </Select>
        </div>
      </div>

      <!-- right -->
      <div class="assets-index-b-r">
        <Scroll :on-reach-bottom="handleReachBottom" height="1000">
          <template v-for="model in cardTabs">
            <tab-card
              :model="model"
              :key="model.guid"
              @on-choose="onChooseCard"
            ></tab-card>
          </template>
        </Scroll>
      </div>
    </div>
  </div>
</template>
<script>
//import api from "@/common/service/api";
import tabCard from "../../module/common/tabCard/index.vue";
import { getHiveTbls, getWorkspaceUsers } from "../../service/api";
import { EventBus } from "../../module/common/eventBus/event-bus";
import { storage } from "../../utils/storage";
export default {
  components: {
    tabCard
  },
  data() {
    return {
      serachOption: {
        limit: 10,
        offset: 0
      },
      ownerList: [],
      userList: [
        {
          value: "New York",
          label: "New York"
        },
        {
          value: "London",
          label: "London"
        },
        {
          value: "Sydney",
          label: "Sydney"
        }
      ],
      cardTabs: [],
      queryForTbls: ""
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
    this.getWorkspaceUsers();
  },
  methods: {
    // 搜索
    onSearch() {
      const params = {
        query: this.queryForTbls,
        owner: this.serachOption.owner,
        limit: 10,
        offset: 0
      };
      this.serachOption["limit"] = 10;
      this.serachOption["offset"] = 0;
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
      that.$router.push(
        `${that.$route.path
          .split("/")
          .slice(0, -1)
          .join("/")}/info/${model.guid}`
      );
    },

    // 获取负责人
    getWorkspaceUsers() {
      let that = this;
      // let workspaceId = that.$route.params.workspaceId;
      let workspaceId = 310;
      getWorkspaceUsers(workspaceId)
        .then(data => {
          const { result } = data;
          let _res = [];
          if (result) {
            result.forEach(item => {
              let o = Object.create(null);
              o["value"] = item;
              o["label"] = item;
              _res.push(o);
            });
            that.ownerList = _res;
            console.log("ownerList", that.ownerList);
          }
        })
        .catch(err => {
          console.log("getWorkspaceUsers", err);
        });
    },

    handleReachBottom() {
      let that = this;
      const res = that.cardTabs.slice(0);
      return new Promise(resolve => {
        const params = {
          query: that.queryForTbls,
          owner: that.serachOption.owner,
          limit: that.serachOption.limit,
          offset: that.serachOption.offset + that.serachOption.limit
        };
        that.serachOption.offset += that.serachOption.limit;
        getHiveTbls(params)
          .then(data => {
            if (data.result) {
              that.cardTabs = res.concat(data.result);
            } else {
              that.$Message.success("所有数据已加载完成");
            }
            resolve();
          })
          .catch(err => {
            console.log("handleReachBottom", err);
          });
      });
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
    }
  }
}
</style>
