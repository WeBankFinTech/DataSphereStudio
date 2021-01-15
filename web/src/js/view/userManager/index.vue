<template>
  <div class="page-bgc">
    <div class="page-bgc-header">
      <div class="header-info">
        <h1>{{$t('message.userManager.createUser')}}</h1>
      </div>
    </div>
    <div style="width: 500px; margin: auto;">
      <Form ref="createUserForm" :model="formCreateUser" label-position="left" :label-width=120 :rules="rules">
        <FormItem :label="$t('message.userManager.username')" prop="username">
          <Input v-model="formCreateUser.username" />
        </FormItem>
        <FormItem :label="$t('message.userManager.password')" prop="password">
          <Input v-model="formCreateUser.password" />
        </FormItem>

        <FormItem :label="$t('message.userManager.dssInstallDir')">
          <Input v-model="formCreateUser.dssInstallDir" />
        </FormItem>

        <FormItem :label="$t('message.userManager.azkabanInstallDir')">
          <Input v-model="formCreateUser.azkakanDir" />
        </FormItem>

        <FormItem
          v-for="(item) in formCreateUser.paths"
          :key="item.value"
          :label="item.key"
        >
          <Input v-model="item.value" :placeholder="item.value" />
        </FormItem>
        

        <FormItem>
          <Row>
            <Col span="18"><Button type="primary" @click="handleSubmit('createUserForm')">{{$t('message.constants.submit')}}</Button></Col>
            <Col span="6"><Button @click="handleCancel">{{$t('message.newConst.back')}}</Button></Col>
          </Row>
        
        
        </FormItem>
        <FormItem>
        
        </FormItem>
      </Form>
    </div>
    <Spin
      v-if="loading"
      fix/>
    
  </div>
  
  
</template>
<script>
import api from '@/js/service/api';
export default {
  data () {
    return {
      formCreateUser: {
        username: '',
        password: '',
        paths: [{key: 'rootPath', value: 'hdfs:///dss_workspace/linkis'}],
        dssInstallDir: '',
        azkakanDir: '',
      },
      rules: {
        username: [{required: true, message: this.$t('message.userManager.usernameNotNull'), trigger: 'blur'}],
        password: [{required: true, message: this.$t('message.userManager.passwordNotNull'), trigger: 'blur'}]
      }
    }
  },
  created(){
    api.fetch(`/dss/paths`, null, {method: 'get'}).then((rst)=>{
      this.formCreateUser = {...this.formCreateUser, ...rst};
    })
  },
  methods: {
    handleSubmit(name){
      this.loading = false;
      this.$refs[name].validate((valid) => {
        if (valid) {
          api
            .fetch(`dss/user`, this.formCreateUser)
            .then((rst) => {
              this.loading = false;
              if (rst) {
                this.$Message.success(this.$t('message.userManager.createSuccess'));
              }
            })
            .catch((err) => {
              this.loading = false;
            });
        }else {
          this.loading = false;
        }
      });
    },
    handleCancel(){
      this.$router.push('/');
    }
  }
}
</script>



