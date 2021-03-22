<template>
  <Table border :columns="columns" :data="tableData">
    <template slot-scope="{ row }" slot="name">
      <span @click="goPortalList(row)" class="link">{{ row.name }}</span>
    </template>
    <template slot-scope="{ row}" slot="action">
      <div>
        <a v-if="!notEditable(row)" @click="newBuildMenu(row)" class="operatItem">修改内容</a>
        <a @click="handleOperate(row, 'deliver')" class="operatItem">发布</a>
        <a v-if="!notEditable(row)" @click="handleOperate(row, 'set')" class="operatItem">更新</a>
        <a @click="handleOperate(row, 'delete')" class="operatItem">删除</a>
      </div>
    </template>
  </Table>
</template>
<script>
import util from '@/common/util';
import storage from '@/common/helper/storage';
export default {
  data () {
    return {
      columns: [
        {
          title: '数据门户名称',
          slot: 'name'
        },
        {
          title: '描述',
          key: 'description'
        },
        {
          title: '创建人',
          key: 'creator'
        },
        {
          title: '创建时间',
          key: 'createTime'
        },
        {
          title: '最后修改时间',
          key: 'updateTime'
        },
        {
          title: '操作',
          slot: 'action',
          align: 'center'
        }
      ],
    }
  },
  props: {
    tableData: {
      type: Array,
      default: () => [],
    },
    workspaceId: {
      type: Number,
    }
  },
  created() {

  },
  methods: {
    // 特定demo数据不让修改
    notEditable(data) {
      // 只有强哥的账号下的243数据门户id不能编辑
      return this.getUserName() === 'enjoyyin' && data.id === 243
    },
    getUserName() {
      return storage.get('userInfo', 'session');
    },
    handleOperate(value, operateType) {
      this.$emit('handleOperate', {value, operateType});
    },
    newBuildMenu(row) {
      let pathObj = {
        path: '/portal/portalsetting',
        query: {portalId: row.id, workspaceId: this.workspaceId}
      };
      this.$router.push(pathObj);
    },
    goPortalList(row) {
      util.windowOpen(`http://${window.location.host}/#/portal/portallist?portalId=${row.id}&workspaceId=${this.workspaceId}`);
    }
  }
}
</script>
<style lang="scss" scoped>
@import '@/common/style/variables.scss';
  .operatItem {
    margin-right: 10px;
  }
  .link {
    color: $link-color;
    cursor: pointer;
  }
</style>
