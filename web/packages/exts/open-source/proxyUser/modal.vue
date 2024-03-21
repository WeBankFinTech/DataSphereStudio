<template>
  <Modal :title="$t('message.scripts.Settings')" v-model="visible"
    :mask-closable="closable"
    :closable="closable">
    <div style="padding:10px 0">
      {{ pusers.length ? $t('message.scripts.operateProxyTip') : $t('message.scripts.applyOperatePorxy')}}
    </div>
    <div v-if="pusers.length">
      <RadioGroup v-model="proxyUser">
        <Radio v-for="(optionItem) in pusers" :key="optionItem" :label="optionItem" :name="optionItem">{{optionItem}}</Radio>
      </RadioGroup>
    </div>
    <template slot="footer">
      <Button @click="handleCancel">{{ $t('message.scripts.cancel') }}</Button>
      <Button type="primary" :disabled="pusers.length < 1 || !proxyUser" @click="handleOk">{{ $t('message.scripts.confirm') }}</Button>
    </template>
  </Modal>
</template>
<script>
import api from '@dataspherestudio/shared/common/service/api';
import storage from '@dataspherestudio/shared/common/helper/storage';
import tree from '@dataspherestudio/scriptis/service/db/tree.js';

export default {
  props: {
    show: Boolean,
    canclose: Boolean
  },
  data() {
    return {
      visible: this.show,
      proxyUser: '',
      pusers: [],
      closable: this.canclose
    }
  },
  mounted() {
    this.baseInfo = storage.get("baseInfo", "local") || {}
    this.getPuserData()
  },
  methods: {
    toggle() {
      this.visible = !this.visible
      if (this.visible) {
        this.proxyUser = this.baseInfo.proxyUserName || ''
        this.getPuserData()
      }
    },
    handleOk () {
      if (this.proxyUser !== this.baseInfo.proxyUserName) {
        api.fetch(`/dss/framework/proxy/setProxyUser`, {
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
          this.baseInfo.proxyUserName = this.proxyUser;
          storage.set('baseInfo', this.baseInfo, 'local');
          storage.remove(this.baseInfo.username + "tabs", "local");
          this.$emit('set-proxy');
          this.visible = false;
          // window.location.reload()
        })
      } else {
        this.visible = false;
        this.$emit('set-proxy');
      }
    },
    handleCancel() {
      api.fetch('/user/logout', {}).then(() => {
        this.visible = false
      });
    },
    getPuserData() {
      api.fetch(`/dss/framework/proxy/listProxyUsers`, {
      }, 'get').then(res => {
        this.pusers = res.proxyUserList || []
      })
    }
  }
}
</script>
