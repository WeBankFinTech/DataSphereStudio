<template>
  <div class="login" @keyup.enter.stop.prevent="handleSubmit('loginForm')">
    <i class="login-bg" />
    <div class="login-main">
      <Form ref="loginForm" :model="loginForm" :rules="ruleInline">
        <FormItem>
          <span class="login-title">{{
            $t('message.common.login.loginTitle', {
              app_name: $APP_CONF.app_name,
            })
          }}</span>
        </FormItem>
        <FormItem prop="user">
          <div class="label">{{ $t('message.common.dss.Username') }}</div>
          <Input
            v-model="loginForm.user"
            type="text"
            :placeholder="$t('message.common.login.userName')"
            size="large"
          />
        </FormItem>
        <FormItem prop="password">
          <div class="label">{{ $t('message.common.dss.Password') }}</div>
          <Input
            v-model="loginForm.password"
            type="password"
            :placeholder="$t('message.common.dss.inputPassword')"
            size="large"
          />
        </FormItem>
        <FormItem>
          <Checkbox
            v-model="rememberUserNameAndPass"
            class="remember-user-name"
            style=""
          >{{ $t('message.common.login.remenber') }}
          </Checkbox>
          <Button
            :loading="loading"
            type="primary"
            long
            size="large"
            @click="handleSubmit('loginForm')"
          >{{ $t('message.common.login.login') }}
          </Button>
        </FormItem>
      </Form>
    </div>
  </div>
</template>
<script>
import api from '@dataspherestudio/shared/common/service/api'
import storage from '@dataspherestudio/shared/common/helper/storage'
import mixin from '@dataspherestudio/shared/common/service/mixin'
import { db } from '@dataspherestudio/shared/common/service/db/index.js'
import { config } from '@dataspherestudio/shared/common/config/db.js'
import JSEncrypt from 'jsencrypt'
import util from '@dataspherestudio/shared/common/util/'
import tab from '@/scriptis/service/db/tab.js'
import eventbus from '@dataspherestudio/shared/common/helper/eventbus'
import plugin from '@dataspherestudio/shared/common/util/plugin'

export default {
  data() {
    return {
      loading: false,
      loginForm: {
        user: '',
        password: '',
      },
      ruleInline: {
        user: [
          {
            required: true,
            message: this.$t('message.common.login.userName'),
            trigger: 'blur',
          },
        ],
        password: [
          {
            required: true,
            message: this.$t('message.common.login.password'),
            trigger: 'blur',
          },
        ],
      },
      rememberUserNameAndPass: false,
      publicKeyData: null,
    }
  },
  mixins: [mixin],
  created() {
    let userNameAndPass = storage.get('saveUserNameAndPass', 'local')
    if (userNameAndPass) {
      this.rememberUserNameAndPass = true
      this.loginForm.user = userNameAndPass.split('&')[0]
      this.loginForm.password = userNameAndPass.split('&')[1]
    }
    this.getPublicKey()
    // 已登录跳转去首页
    // 未登录停留再登陆页面
    let baseinfo = storage.get('baseInfo', 'local')
    if (baseinfo) {
      this.getIsAdmin(() => {
        window.username = baseinfo.username
        this.afterLogin()
      })
    }
  },
  mounted() {
    storage.set('close_db_table_suggest', false)
    storage.remove('scriptis_execute_req_log')
    const workspaceId = this.getCurrentWorkspaceId()
    sessionStorage.removeItem(`work_flow_lists_${workspaceId}`)
  },
  methods: {
    logout() {
      api.fetch('/user/logout', {}).then(() => {
        this.$emit('clear-session')
        storage.set('need-refresh-proposals-hql', true)
        storage.set('need-refresh-proposals-python', true)
        this.$router.push({ path: '/login' })
      })
    },
    // 获取登录后的url调转
    getPageHomeUrl() {
      const currentModules = util.currentModules()
      return api
        .fetch(
          `${this.$API_PATH.WORKSPACE_PATH}getWorkspaceHomePage`,
          {
            micro_module: currentModules.microModule || 'dss',
          },
          'get'
        )
        .then((res) => {
          storage.set('noWorkSpace', false, 'local')
          return res.workspaceHomePage
        })
        .catch((e) => {
          storage.set('noWorkSpace', true, 'local')
          this.logout()
          throw e
        })
    },
    // 获取公钥接口
    getPublicKey() {
      api.fetch('/user/publicKey', 'get').then((res) => {
        this.publicKeyData = res
      })
    },
    handleSubmit(name) {
      this.$refs[name].validate(async (valid) => {
        if (valid) {
          this.loading = true
          if (!this.rememberUserNameAndPass) {
            storage.remove('saveUserNameAndPass', 'local')
          }
          // this.loginForm.user = this.loginForm.user.toLocaleLowerCase();
          // 需要判断是否需要给密码加密
          let password = this.loginForm.password
          let params = {}
          if (this.publicKeyData && this.publicKeyData.enableLoginEncrypt) {
            const key = `-----BEGIN PUBLIC KEY-----${this.publicKeyData.publicKey}-----END PUBLIC KEY-----`
            const encryptor = new JSEncrypt()
            encryptor.setPublicKey(key)
            password = encryptor.encrypt(this.loginForm.password)
            params = {
              userName: this.loginForm.user,
              password,
            }
          } else {
            params = {
              userName: this.loginForm.user,
              password,
            }
          }
          // 登录清掉本地缓存
          // 保留Scripts页面打开的tab页面
          // 连续两次退出登录后，会导致数据丢失，所以得判断是否已存切没有使用
          let tabs = (await tab.get()) || []
          const tablist = storage.get(this.loginForm.user + 'tabs', 'local')
          if (!tablist || tablist.length <= 0) {
            storage.set(this.loginForm.user + 'tabs', tabs, 'local')
          }
          Object.keys(config.stores).map((key) => {
            db.db[key].clear()
          })
          let rst
          try {
            rst = await api.fetch(`/user/login`, params)
            this.loading = false
            // 保存用户名
            if (this.rememberUserNameAndPass) {
              storage.set(
                'saveUserNameAndPass',
                `${this.loginForm.user}&${this.loginForm.password}`,
                'local'
              )
            }

            if (rst) {
              // 跳转去旧版
              if (rst.redirectLinkisUrl) {
                location.href = rst.redirectLinkisUrl
                return
              }
              this.baseInfo = { username: this.loginForm.user }
              window.username = this.loginForm.user
              storage.set('baseInfo', this.baseInfo, 'local')
              this.getIsAdmin()
              this.afterLogin()
              this.$Message.success(
                this.$t('message.common.login.loginSuccess')
              )
            }
          } catch (error) {
            console.error(error)
            this.loading = false
          }
        } else {
          this.$Message.error(this.$t('message.common.login.vaildFaild'))
        }
      })
    },
    // 清楚本地缓存
    clearSession() {
      storage.clear()
    },
    getIsAdmin(cb) {
      api.fetch(`/jobhistory/governanceStationAdmin`, {}, 'get').then((rst) => {
        if (cb) {
          cb()
        } else {
          this.baseInfo = { username: this.loginForm.user, isAdmin: rst.admin }
          storage.set('baseInfo', this.baseInfo, 'local')
        }
      })
    },
    async afterLogin() {
      // 登录之后需要获取当前用户的调转首页的路径
      const homePageRes = await this.getPageHomeUrl()
      const all_after_login = await plugin.emitHook('after_login', {
        context: this,
        homePageRes,
      })
      eventbus.emit('watermark.refresh')
      if (all_after_login.length) {
        // 有hook返回则hook处理
      } else {
        this.$router.replace({ path: homePageRes.homePageUrl })
      }
    },
  },
}
</script>
<style lang="scss" src="@/dss/assets/styles/login.scss"></style>
