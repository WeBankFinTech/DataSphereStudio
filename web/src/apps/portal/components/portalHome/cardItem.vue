<template>
  <Card class="cardWrap">
    <div class="card">
      <div class="card-title">
        <div>
          <img src="../../../../dss/assets/images/u1439.svg" alt="" class="img">
          <span @click="goPortalList" class="title">{{cardValue.name}}</span>
        </div>
        <div>
          <Icon
            v-if="!notEditable"
            :size="20"
            type="ios-create-outline"
            title="修改内容"
            class="icon-hover"
            @click="newBuildMenu"
          />
          <Icon
            :size="20"
            type="ios-share-outline"
            title="发布"
            class="icon-hover"
            @click="handleOperate(cardValue, 'deliver')"/>
          <Icon
            v-if="!notEditable"
            :size="20"
            type="ios-settings"
            title="更新"
            class="icon-hover"
            @click="handleOperate(cardValue, 'set')"
          />
          <Icon
            :size="20"
            type="ios-trash-outline"
            title="删除"
            class="icon-hover"
            @click="handleOperate(cardValue, 'delete')"/>
        </div>
      </div>
      <template>
        <div class="desc" :data-title="cardValue.description" @click="goPortalList">
          {{ cardValue.description }}
        </div>
      </template>
      <div class="card-footer">
        <div class="bottom-bar">
          <span class="tagItem" v-for="(item, index) in cardValue.tag" :key="index" :title="item">{{ item}}</span>
        </div>
      </div>
    </div>
  </Card>
</template>
<script>
import storage from '@/common/helper/storage';
import util from '@/common/util';
export default {
  props: {
    cardValue: {
      type: Object,
      default: () => ({})
    },
    workspaceId: {
      type: String,
    },
    notEditable: {
      type: Boolean,
      default: false
    }
  },
  created() {
  },
  methods: {
    getUserName() {
      return storage.get("baseInfo", 'local') ? storage.get("baseInfo", 'local').username : null;
    },
    newBuildMenu() {
      let pathObj = {
        path: '/portal/portalsetting',
        query: {portalId: this.cardValue.id, workspaceId: this.workspaceId}
      };
      let editUser = this.cardValue.editUser;
      let creator = this.cardValue.creator;
      editUser.push(creator);
      let userName = this.getUserName();
      let index = editUser.indexOf(userName);
      if (index === -1) {
        this.$Message.error('只有门户的创建者和可编辑用户才有权限新建菜单');
        return;
      }
      this.$router.push(pathObj);
    },
    handleOperate(value, operateType) {
      this.$emit('handleOperate', {value, operateType})
    },
    //携带数据门户id
    goPortalList() {
      util.windowOpen(`http://${window.location.host}/#/portal/portallist?portalId=${this.cardValue.id}&workspaceId=${this.workspaceId}`);
    }
  }
}
</script>
<style scoped lang="scss">
@import '@/common/style/variables.scss';
.cardWrap {
    height: 200px;
    margin: 15px;
    /deep/
    .ivu-card-body {
      height: 100%;
      .card {
        height: 100%;
        display: flex;
        flex-direction: column;
        justify-content: space-between;
      }
  }
}
  .title {
    margin-left: 30px;
    cursor: pointer;
    display: inline-block;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
    word-break: break-all;
    min-width: 50px;
  }
  .img {
    position: absolute;
    width: 20px;
    height: 20px;
  }
  .card-title {
    display: flex;
    justify-content: space-between;
    flex: 1;
    span {
      font-weight: bold;
    }
    .icon-hover:hover{
      color: $link-color;
      cursor: pointer;
    }
    .potalImg {
      cursor: pointer;
      margin-right: 10px;
      &:hover {
        color: $link-color;
      }
    }
  }
.desc {
  width: 100%;
  flex: 11;
  margin-top: 10px;
  vertical-align: middle;
  cursor: pointer;
  word-wrap:break-word;
  overflow-y: auto;
}
  .card-footer {
    margin-bottom: 0px;
    .bottom-bar {
      display: flex;
      justify-content: flex-start;
      align-items: center;
      width: 100%;
      .tagItem {
        color: #fff;
        padding: 0 5px;
        margin-right: 10px;
        border-radius: 3px;
        background-color: #2db7f5;
        white-space: nowrap;
        text-overflow: ellipsis;
        overflow: hidden;
      }
    }
  }
</style>
