<template>
  <div class="workspace-table">
    <Table :columns="columns" :data="tableData" style="margin-bottom: 10px;" @on-row-click="gotoWorkspace">
      <div slot="header" @click.stop="createWorkspace">
        <div class="newHome_create">
          <div class="newHome_create_content">
            <SvgIcon :style="{ 'font-size': '20px' }" icon-class="fi-addproject"/>
            <p class="newHome_create_text" >
              {{ $t('message.workspace.createWorkspace') }}
            </p>
          </div>
        </div>
      </div>
      <template slot-scope="{ row }" slot="name">
        <span>{{ row.name }}</span>
      </template>

      <template slot-scope="{ row }" slot="createTime">
        <span>{{ dateFormat(row.createTime) }}</span>
      </template>

      <template slot-scope="{ row }" slot="label" >
        <template v-if="row.label">
          <Tag color="blue" v-for="(label, index) in row.label.split(',')" :key="index">{{label}}</Tag>
        </template>
      </template>

      <template slot-scope="{ row }" slot="description">
        <span class="desc">{{ row.description }}</span>
      </template>

      <template v-if="isAdmin" slot-scope="{ row }" slot="action">
        <div class="buttom-box">
          <Button
            type="primary"
            size="small"
            @click.stop="namagement(row.id)"
          >{{ $t('message.workspace.Management') }}</Button>
        </div>
      </template>
    </Table>
  </div>
</template>

<script>
import moment from 'moment';
import util from '@dataspherestudio/shared/common/util';
export default {
  props: {
    data: {
      type: Array,
      default: () => []
    },
    isAdmin: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      tableData: [],
      columns: [
        {
          title: this.$t('message.workspace.workName'),
          slot: 'name'
        },
        {
          title: this.$t('message.workspace.createTime'),
          slot: 'createTime',
          sortable: true
        },
        {
          title: this.$t('message.workspace.label'),
          slot: 'label'
        },
        {
          title: this.$t('message.workspace.description'),
          slot: 'description',
          ellipsis: true
        },
        {
          title: this.$t('message.workspace.Operation'),
          slot: "action",
          align: "center",
        }
      ]
    }
  },
  watch: {
    data(val) {
      // 保存取到的所有数据
      this.tableData = val;
    }
  },
  methods: {
    dateFormat(date) {
      return moment(date).format('YYYY-MM-DD HH:mm:ss');
    },
    gotoWorkspace(workspace) {
      const workspaceId = workspace.workspaceId || workspace.id;
      const currentModules = util.currentModules();
      if (currentModules.microModule === 'apiServices') {
        this.$router.push({ path: '/apiservices', query: { workspaceId: workspaceId}});
      } else {
        this.$router.push({ path: '/workspaceHome', query: { workspaceId: workspaceId}});
      }
    },
    createWorkspace() {
      this.$emit('createWorkspace')
    },
    namagement(id) {
      this.$emit('namagement', id)
    }
  }
}
</script>

<style lang="scss" scoped>
@import "@dataspherestudio/shared/common/style/variables.scss";
.workspace-table {
  .desc {
    width: 100%;
    display: block;
    overflow: hidden;
    text-overflow: ellipsis;
  }
}
.newHome_create {
  cursor: pointer;
  box-sizing: border-box;
  border: 1px dashed #d9d9d9;
  @include border-color($border-color-base, $dark-border-color-base);
  border-radius: 2px;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  width: calc(100% - 1px);
  height: 48px;

  .newHome_create_content {
    flex: none;
    display: flex;
    justify-content: center;
    align-items: center;
    .newHome_create_text {
      margin-left: 10px;
      cursor: pointer;
    }
  }
}
</style>
