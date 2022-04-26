<template>
  <Modal title="设置" v-model="visible"
    :mask-closable="closable"
    :closable="closable">
    <div style="padding:10px 0">
      {{ pusers.length ? '请选择代理用户进入或者取消' : '请先申请实名用户允许的代理用户'}}
    </div>
    <div v-if="pusers.length">
      <RadioGroup v-model="proxyUser">
        <Radio v-for="(optionItem) in pusers" :key="optionItem" :label="optionItem" :name="optionItem">{{optionItem}}</Radio>
      </RadioGroup>
    </div>
    <template slot="footer">
      <Button type="primary" :disabled="pusers.length < 1 || !proxyUser" @click="handleOk">确定</Button>
    </template>
  </Modal>
</template>
<script>
import api from '@dataspherestudio/shared/common/service/api';
import storage from '@dataspherestudio/shared/common/helper/storage';
import tree from '@dataspherestudio/scriptis/service/db/tree.js';

export default {
  data() {
    return {
      visible: false,
      proxyUser: '',
      pusers: [],
      closable: false
    }
  },
  mounted() {
  },
  methods: {
    toggle() {
      this.visible = !this.visible
      if (this.visible) {
        this.baseInfo = storage.get("baseInfo", "local") || {}
        this.proxyUser = this.baseInfo.proxyUserName || ''
        this.getPuserData()
      }
    },
    handleOk () {
      if (this.proxyUser!== this.baseInfo.proxyUserName) {
        api.fetch(`/dss/framework/admin/user/proxy/addUserCookie`, {
          userName: this.baseInfo.username,
          proxyUserName: this.proxyUser
        }, 'post').then(() => {
          // 切换代理用户
          storage.set('shareRootPath', '');
          storage.set('hdfsRootPath', '');
          tree.remove('scriptTree');
          tree.remove('hdfsTree');
          tree.remove('hiveTree');
          tree.remove('udfTree');
          tree.remove('functionTree');
          this.baseInfo.proxyUserName = this.proxyUser
          storage.set('baseInfo', this.baseInfo, 'local')
          window.location.reload()
        })
      } else {
        this.toggle()
      }
    },
    getPuserData() {
      api.fetch(`/dss/framework/admin/user/proxy/list`, {
      }, 'get').then(res => {
        this.pusers = res.proxyUserList || []
      })
    }
  }
}
</script>
