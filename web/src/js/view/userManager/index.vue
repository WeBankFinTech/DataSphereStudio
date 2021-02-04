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
        ref="createUserForm"
        :model="formCreateUser"
        label-position="left"
        :label-width="120"
        :rules="rules"
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
                @on-cancel="cancel"
              >
                <Icon type="md-trash" size="20" />
              </Poptip>
            </div>
            <Row gutter="20">
              <Col span="8">
                <FormItem :label="$t('message.userManager.linuxHost')" label-width="70">
                  <Input v-model="item.linuxHost" />
                </FormItem>
              </Col>
              <Col span="8">
                <FormItem :label="$t('message.userManager.linuxLoginUser')" label-width="90">
                  <Input v-model="item.linuxLoginUser" />
                </FormItem>
              </Col>
              <Col span="8">
                <FormItem :label="$t('message.userManager.linuxLoginPassword')" label-width="110">
                  <Input v-model="item.linuxLoginPassword" />
                </FormItem>
              </Col>
            </Row>
            <Divider></Divider>
          </div>
        </div>
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
          <FormItem v-for="item in formCreateUser.paths" :key="item.value" :label="item.key">
            <Input v-model="item.value" :placeholder="item.value" />
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
        callback(new Error('qqq'))
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
            message: this.$t('message.userManager.usernameNotNull'),
            trigger: 'blur',
          },
        ],
        password: [
          {
            required: true,
            //message: this.$t("message.userManager.passwordNotNull"),
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
      this.confirmLoading = true
      this.$refs[name].validate((valid) => {
        return
        if (valid) {
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
              null,
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
        this.active = this.active + 1
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
