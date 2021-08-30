<template>
  <div class="assets-info-wrap">
    <!-- top -->
    <div class="assets-info-top">
      <div class="assets-info-top-bgs">
        <Button>
          <SvgIcon icon-class="star" />
          收藏表
        </Button>
        <Button>申请权限</Button>
        <Button>生成API</Button>
      </div>
    </div>

    <!-- bottom -->
    <div class="assets-info-b">
      <!-- left -->
      <div class="assets-info-b-l">
        <div class="assets-info-b-l-title">
          <span>表基础信息</span>
        </div>

        <div class="assets-info-b-l-content">
          <div class="assets-info-b-l-content-item">
            <label for="owner">负责人：</label>
            <span>{{ basicData.owner }}</span>
          </div>
          <div class="assets-info-b-l-content-item">
            <label for="createTime">创建时间：</label>
            <span>{{ basicData.createTime }}</span>
          </div>
          <div class="assets-info-b-l-content-item">
            <label for="lifeCircle">生命周期：</label>
            <span>{{ basicData.lifeCircle }}</span>
          </div>
          <div class="assets-info-b-l-content-item">
            <label for="store">存储量：</label>
            <span>{{ basicData.store }}</span>
          </div>
          <div class="assets-info-b-l-content-item">
            <label for="comment">描述：</label>
            <span>{{ basicData.comment }}</span>
          </div>
          <div class="assets-info-b-l-content-item">
            <label for="labels">描述：</label>
            <span>{{ basicData.labels }}</span>
          </div>
        </div>
      </div>
      <!-- right -->

      <div class="assets-info-b-r">
        <Tabs type="card">
          <TabPane label="字段信息"
            ><field-info
              :fieldInfo="fieldInfo"
              :rangeInfo="rangeFieldInfo"
            ></field-info
          ></TabPane>
          <TabPane label="分区信息"
            ><range-info :rangeInfo="rangeInfo"></range-info
          ></TabPane>
          <TabPane label="数据预览">标签三的内容</TabPane>
          <TabPane label="数据血缘">标签三的内容</TabPane>
        </Tabs>
      </div>
    </div>
  </div>
</template>

<script>
import fieldInfo from "../fieldInfo/index.vue";
import rangeInfo from "../rangeInfo/index.vue";
export default {
  name: "assetsInfo",
  components: {
    fieldInfo,
    rangeInfo
  },
  data() {
    return {
      basicData: {
        owner: "xxx",
        createTime: "xxx",
        lifeCircle: "xxx",
        store: "xxx",
        comment: "xxx",
        labels: ""
      },
      fieldInfo: [
        { id: 1, name: "id", type: "string", comment: "xxx" },
        { id: 2, name: "name", type: "string", comment: "xxx" },
        { id: 3, name: "gender", type: "string", comment: "xxx" }
      ],
      rangeFieldInfo: [{ id: 1, name: "day", type: "string", comment: "xxx" }],
      rangeInfo: [
        {
          partName: "day=20210805",
          reordCnt: 333,
          store: "3.24kb",
          createTime: "2021年8月5日 08:12:36",
          lastAccessTime: "2021年8月5日 08:12:36"
        },
        {
          partName: "day=20210805",
          reordCnt: 333,
          store: "3.24kb",
          createTime: "2021年8月5日 08:12:36",
          lastAccessTime: "2021年8月5日 08:12:36"
        },
        {
          partName: "day=20210805",
          reordCnt: 333,
          store: "3.24kb",
          createTime: "2021年8月5日 08:12:36",
          lastAccessTime: "2021年8月5日 08:12:36"
        },
        {
          partName: "day=20210805",
          reordCnt: 333,
          store: "3.24kb",
          createTime: "2021年8月5日 08:12:36",
          lastAccessTime: "2021年8月5日 08:12:36"
        },
        {
          partName: "day=20210805",
          reordCnt: 333,
          store: "3.24kb",
          createTime: "2021年8月5日 08:12:36",
          lastAccessTime: "2021年8月5日 08:12:36"
        },
        {
          partName: "day=20210805",
          reordCnt: 333,
          store: "3.24kb",
          createTime: "2021年8月5日 08:12:36",
          lastAccessTime: "2021年8月5日 08:12:36"
        },
        {
          partName: "day=20210805",
          reordCnt: 333,
          store: "3.24kb",
          createTime: "2021年8月5日 08:12:36",
          lastAccessTime: "2021年8月5日 08:12:36"
        },
        {
          partName: "day=20210805",
          reordCnt: 333,
          store: "3.24kb",
          createTime: "2021年8月5日 08:12:36",
          lastAccessTime: "2021年8月5日 08:12:36"
        }
      ]
    };
  }
};
</script>

<style lang="scss" scoped>
.assets-info-wrap {
  flex: 1;
  flex-direction: column;
  display: flex;
  .assets-info-top {
    min-height: 80px;
    border-top: 24px solid #f8f9fc;
    border-bottom: 1px solid #dee4ec;
    display: flex;
    align-items: center;
    justify-content: flex-end;
    &-bgs {
      margin-right: 24px;
      button {
        margin-right: 8px;
      }
    }
  }

  .assets-info-b {
    display: flex;
    flex: 1;
    &-l {
      min-width: 249px;
      padding-left: 16px;
      padding-top: 16px;
      border-right: 1px solid #dee4ec;

      &-title {
        font-family: PingFangSC-Medium;
        font-size: 14px;
        color: rgba(0, 0, 0, 0.85);
        font-weight: bold;
      }

      &-content {
        &-item {
          font-family: PingFangSC-Regular;
          font-size: 14px;
          margin-top: 16px;
          label {
            font-weight: normal;
            color: rgba(0, 0, 0, 0.85);
          }
          span {
            color: rgba(0, 0, 0, 0.65);
          }
        }
      }
    }

    &-r {
      flex: 1;
      max-width: calc(100% - 250px);
    }
  }
}
</style>
