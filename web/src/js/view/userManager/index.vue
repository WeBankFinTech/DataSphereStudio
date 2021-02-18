<template>
  <div class="page-bgc">
    <div class="page-bgc-header">
      <div class="header-info">
        <h1>{{ $t("message.userManager.createUser") }}</h1>
      </div>
    </div>
    <div style="width: 800px; margin: auto">
      <Steps :current="active">
        <Step
          :title="$t('message.userManager.serverSettings')"
          :status="active === 0 ? 'process' : 'finish'"
        ></Step>
        <Step
          :title="$t('message.userManager.userSettings')"
          :status="active === 1 ? 'process' : 'wait'"
        ></Step>
      </Steps>
      <Form
        ref="createServerForm"
        :model="formCreateUser"
        label-position="left"
        :label-width=140
      >
        <div v-show="active === 0" style="margin-top: 20px">
          <div
            v-for="(item, index) in formCreateUser.servers"
            :key="index"
            style="position: relative; padding-top: 20px"
          >
            <div
              style="
                position: absolute;
                top: -10px;
                left: 10px;
              "
              v-show="
                (index === 0 && formCreateUser.servers.length > 1) ||
                  index !== 0
              "
            >
              {{index + 1 }}
            </div>
            <div
              style="
                position: absolute;
                top: -10px;
                right: 10px;
                cursor: pointer;
              "
              v-show="
                (index === 0 && formCreateUser.servers.length > 1) ||
                  index !== 0
              "
            >
              <Poptip
                confirm
                :title="$t('message.userManager.deleteTip')"
                @on-ok="deleteServer(index)"
              >
                <Icon type="md-trash" :size=20 />
              </Poptip>
            </div>
            <Row :gutter=20>
              <Col span="8">
                <FormItem :label="$t('message.userManager.linuxHost')" :label-width=70 :prop=" 'servers.' + index + '.linuxHost'"
                 :rules="{required: true, message: $t('message.userManager.linuxHostNotNull'), trigger: 'blur'}">
                  <Input v-model="item.linuxHost" />
                </FormItem>
              </Col>
              <Col span="8">
                <FormItem :label="$t('message.userManager.linuxLoginUser')" :label-width=100 :prop=" 'servers.' + index + '.linuxLoginUser'"
                 :rules="{required: true, message: $t('message.userManager.linuxLoginUserNotNull'), trigger: 'blur'}">
                  <Input v-model="item.linuxLoginUser" />
                </FormItem>
              </Col>
              <Col span="8">
                <FormItem :label="$t('message.userManager.linuxLoginPassword')" :label-width=110 :prop=" 'servers.' + index + '.linuxLoginPassword'"
                 :rules="{required: true, message: $t('message.userManager.linuxLoginPasswordNotNull'), trigger: 'blur'}">
                  <Input v-model="item.linuxLoginPassword" />
                </FormItem>
              </Col>
            </Row>
            <Divider></Divider>
          </div>
        </div>
      </Form>
      <Form
        ref="createUserForm"
        :model="formCreateUser"
        label-position="left"
        :label-width=140
        :rules="rules"
      >
        <div v-show="active === 1" style="margin-top: 20px">
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
          <FormItem v-for="(item, index) in formCreateUser.paths" :key="item.key" :label="item.key" :prop=" 'paths.' + index + '.value'" 
           :rules="{required: true, message: $t('message.userManager.pathNotNull'), trigger: 'blur'}">
            <Input v-model="item.value" />
          </FormItem>
        </div>
        <FormItem>
          <Row>
            <Col span="19" v-show="active === 0">
              <Button type="primary" @click="addServer">
                {{
                $t("message.userManager.addServer")
                }}
              </Button>
            </Col>
            <Col span="19" v-show="active === 1">
              <Button
                type="primary"
                @click="handleSubmit('createUserForm')"
                :loading="confirmLoading"
              >{{ $t("message.constants.submit") }}</Button>
            </Col>
            <Col span="3" v-show="active === 0">
              <Button @click="setStep('next')">{{ $t("message.userManager.XYB") }}</Button>
            </Col>
            <Col span="3" v-show="active === 1">
              <Button
                @click="setStep('prev')"
                :disabled="confirmLoading"
              >{{ $t("message.userManager.SYB") }}</Button>
            </Col>
            <Col span="2">
              <Button
                @click="handleCancel"
                :disabled="confirmLoading"
              >{{ $t("message.newConst.back") }}</Button>
            </Col>
          </Row>
        </FormItem>

        <FormItem></FormItem>
      </Form>
    </div>
    <Spin v-if="loading" fix />
  </div>
</template>
<script>
import api from '@/js/service/api'
export default {
  data() {
    const validatePwd = (rule, value, callback) => {
      const notEmpty = /\S{8,}/
      const startAlphabet = /^[A-Za-z]/
      const hadNumber = /[0-9]+/
      const specialChar = /[^A-Za-z0-9\s]/
      if (!value) {
        callback(new Error(this.$t('message.userManager.passwordNotNull')))
      } else if (
        !(
          notEmpty.test(value) &&
          startAlphabet.test(value) &&
          hadNumber.test(value) &&
          specialChar.test(value)
        )
      ) {
        callback(new Error(this.$t('message.userManager.passwordNotWeak')))
      } else {
        callback()
      }
    }
    const validateUser = (rule, value, callback) => {
      const userReg = /^[A-Za-z][A-Za-z0-9_]*$/;
      if (!value) {
        callback(new Error(this.$t('message.userManager.usernameNotNull')))
      } else if (!userReg.test(value)) {
        callback(new Error(this.$t('message.userManager.usernameFormat')))
      } else {
        callback()
      }
    }
    return {
      formCreateUser: {
        username: '',
        password: '',
        paths: [{ key: 'rootPath', value: 'hdfs:///dss_workspace/linkis' }],
        dssInstallDir: '',
        azkakanDir: '',
        servers: [
          { linuxHost: '', linuxLoginUser: '', linuxLoginPassword: '' },
        ],
      },
      rules: {
        username: [
          {
            required: true,
            trigger: 'blur',
            validator: validateUser,
          },
        ],
        password: [
          {
            required: true,
            trigger: 'blur',
            validator: validatePwd,
          },
        ],
      },
      active: 0,
      confirmLoading: false,
    }
  },
  created() {
    const servers = localStorage.getItem('serverConfigs')
    if (servers) {
      this.formCreateUser = {
        ...this.formCreateUser,
        servers: JSON.parse(servers),
      }
    }
    api.fetch(`/dss/paths`, null, { method: 'get' }).then((rst) => {
      this.formCreateUser = { ...this.formCreateUser, ...rst }
    })
  },
  methods: {
    handleSubmit(name) {
      const paths = this.formCreateUser.paths;
      paths.forEach(item => {
        item.value = item.value && item.value.trim();
      })
      this.formCreateUser.paths = paths;
      this.confirmLoading = true
      this.$refs[name].validate((valid) => {
        if (valid) {
          this.$Message.info({
              content: this.$t('message.userManager.createTip'),
              duration: 20
          });
          const { servers, ...rest } = this.formCreateUser
          const validServers = servers.filter((item) => {
            return Object.keys(item).every((key) => !!item[key])
          })

          api
            .fetch(
              `dss/user`,
              validServers.length > 0
                ? { ...rest, servers: validServers }
                : rest,
              {method: 'post', timeout: 120000},
              5
            )
            .then((rst) => {
              this.confirmLoading = false
              localStorage.setItem(
                'serverConfigs',
                JSON.stringify(this.formCreateUser.servers)
              )
              if (rst) {
                this.$Message.success(
                  this.$t('message.userManager.createSuccess')
                )
                this.$router.push('/')
              }
            })
            .catch((err) => {
              this.confirmLoading = false
            })
        } else {
          this.confirmLoading = false
        }
      })
    },
    handleCancel() {
      this.$router.push('/')
    },
    setStep(action) {
      if (action === 'next') {
     
        this.formCreateUser.servers =this.formCreateUser.servers.map(server => {
          const obj = {...server};
          Object.keys(server).forEach(key => {obj[key] = obj[key] && obj[key].trim()} );
          return obj

        });
        this.$refs["createServerForm"].validate(valid => {

          if(valid){
            const servers = this.formCreateUser.servers;
            //检测服务器ip与用户名是否有完全相同的
            if(servers.length > 1){
              const serversTemp = servers.map(server => {
                const arr = Object.keys(server).map(key => {
                  if(key !== 'linuxLoginPassword'){
                    return key + server[key];
                  }
                  return "";
                });
                return arr.join("$--$");
              });
              const hadIn = [];
              const repeats = [];
              for(let i =0; i< serversTemp.length; i++){
                if(!hadIn.includes(i)){
                  hadIn.push(i);
                  const str = serversTemp[i];
                  const ar = [i + 1];
                  for(let j = i + 1; j < serversTemp.length; j++){
                    const str2 = serversTemp[j];
                    if(str === str2){
                      ar.push(j + 1);
                      hadIn.push(j);
                    }
                  }
                  if(ar.length > 1){
                    repeats.push(ar.join(','));
                  }
                }

              }
              if(repeats.length > 0){
                this.$Message.error({content: repeats.join(" / ") + this.$t('message.userManager.serverSame'), duration: 6});
                return
              }


            }

            this.active = this.active + 1;

          }else{
            this.$Message.error(this.$t('message.userManager.serverNull'))
          }
        })

      } else {
        this.active = this.active - 1
      }
    },
    addServer() {
      this.formCreateUser.servers.push({
        linuxHost: '',
        linuxLoginUser: '',
        linuxLoginPassword: '',
      })
    },
    deleteServer(index) {
      this.formCreateUser.servers.splice(index, 1)
    },
  },
}
</script>
