<template>
  <div class="workbench workbench-tabs">
    <template v-if="workListLength > 0">
      <div
        class="workbench-tab-wrapper"
        :class="{ 'full-screen': isTopPanelFull }"
        v-if="!node || (node && workListLength > 1)"
      >
        <div
          v-if="$route.name === 'Home'"
          ref="tab-list-scroll"
          class="workbench-tab work-list-tab"
        >
          <draggable
            class="list-group"
            style="height:24px;margin:0"
            tag="ul"
            v-model="worklist"
            v-bind="dragOptions"
            @start="isDragging = true"
            @end="isDragging = false"
            @change="changeOrder"
          >
            <transition-group type="transition" name="flip-list">
              <template v-for="(work, index) in worklist">
                <div
                  :key="work.id"
                  :class="{active:work.id==current}"
                  class="workbench-tab-item"
                  ref="work_item"
                  v-if="work.type !== 'backgroundScript'"
                >
                  <we-title
                    :node="node"
                    :index="index"
                    :work="work"
                    @on-choose="onChooseWork"
                    @on-remove="removeWork"
                  />
                </div>
              </template>
            </transition-group>
          </draggable>
        </div>
        <div v-else ref="tab-list-scroll" class="workbench-tab work-list-tab">
          <template v-for="(work, index) in worklist">
            <div
              :key="work.id"
              :class="{ active: work.id == current }"
              class="workbench-tab-item"
              ref="work_item"
              v-if="work.type !== 'backgroundScript'"
            >
              <we-title
                :node="node"
                :index="index"
                :work="work"
                @on-choose="onChooseWork"
                @on-remove="removeWork"
              />
            </div>
          </template>
        </div>
        <!-- <div class="workbench-tab-control" v-if="isControlBtnShow">
          <Icon
            type="ios-arrow-dropleft-circle"
            :class="{ disable: tabLeft }"
            @click="tabMoving('right')"
          ></Icon>
          <Icon
            type="ios-arrow-dropright-circle"
            :class="{ disable: tabRight }"
            @click="tabMoving('left')"
          ></Icon>
        </div> -->
        <div class="workbench-tab-button">
          <Dropdown
            trigger="click"
            placement="bottom-end"
            @on-click="dropdownClick"
          >
            <Icon type="md-list" />
            <DropdownMenu slot="list">
              <DropdownItem name="other">{{
                $t("message.scripts.container.closeDropDown.others")
              }}</DropdownItem>
              <DropdownItem name="all">{{
                $t("message.scripts.container.closeDropDown.all")
              }}</DropdownItem>
              <DropdownItem name="left">{{
                $t("message.scripts.container.closeDropDown.left")
              }}</DropdownItem>
              <DropdownItem name="right">{{
                $t("message.scripts.container.closeDropDown.right")
              }}</DropdownItem>
              <DropdownItem name="fullScreen" divided v-if="!isTopPanelFull">{{
                $t("message.scripts.constants.logPanelList.fullScreen")
              }}</DropdownItem>
              <DropdownItem name="releaseFullScreen" v-else>{{
                $t("message.scripts.constants.logPanelList.releaseFullScreen")
              }}</DropdownItem>
            </DropdownMenu>
          </Dropdown>
        </div>
      </div>
      <div class="workbench-container" :class="{ node: node }">
        <we-body
          v-for="work in worklist"
          v-show="current == work.id"
          :current="current"
          :key="work.id"
          :work="work"
          @remove-work="removeWork"
          :node="node"
          :readonly="readonly"
        />
      </div>
    </template>
    <template v-if="workListLength == 0 && !loading">
      <div class="bg-page">
        <img class="bg-img" src="./image/bg-img.png" />
        <div v-html="tips" style="line-height:24px"></div>
      </div>
    </template>
    <template>
      <Spin v-if="loading" size="large" fix />
    </template>
    <Modal v-model="showCloseModal" :closable="false" width="360">
      <p slot="header" class="modal-title">
        <Icon type="md-help-circle" />
        <span>{{ $t("message.scripts.container.closeHint") }}</span>
      </p>
      <div class="modal-content">
        <p style="word-break: break-all;">{{ closeModal.text }}</p>
      </div>
      <div slot="footer">
        <Button type="text" @click="closeModal.cancel">{{
          $t("message.scripts.container.footer.cancel")
        }}</Button>
        <Button type="warning" @click="closeModal.close">{{
          $t("message.scripts.container.footer.close")
        }}</Button>
        <Button
          v-if="closeModal.save"
          type="primary"
          @click="closeModal.save"
        >{{ $t("message.scripts.container.footer.save") }}</Button
        >
        <Button
          v-if="closeModal.saveAs"
          type="primary"
          @click="closeModal.saveAs"
        >{{ $t("message.scripts.container.footer.saveAs") }}</Button
        >
      </div>
    </Modal>
    <save-as ref="saveAs" @save-complete="saveAsComplete" />
  </div>
</template>
<script>
import draggable from "vuedraggable"
import api from '@dataspherestudio/shared/common/service/api'
import storage from '@dataspherestudio/shared/common/helper/storage'
import util from '@dataspherestudio/shared/common/util'
import title from "./title.vue"
import body from "./body.vue"
import saveAs from "./script/saveAs.vue"
import { Work } from "./modal.js"
import { find, uniq, isEmpty, last, debounce } from "lodash"
import elementResizeEvent from '@dataspherestudio/shared/common/helper/elementResizeEvent'
import mixin from '@dataspherestudio/shared/common/service/mixin'
import plugin from '@dataspherestudio/shared/common/util/plugin'
const maxTabLen = 20
export default {
  components: {
    "we-body": body,
    "we-title": title,
    saveAs,
    draggable,
  },
  provide() {
    return {
      containerInstance: this,
    }
  },
  props: {
    width: Number,
    parameters: {
      type: Object,
      default: () => {
        return {
          content: "",
          params: {},
        }
      },
    },
    node: Object,
    readonly: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      worklist: [],
      current: "",
      lastCurrent: "",
      closeModal: {
        text: "",
        cancel: () => {},
        close: () => {},
        save: () => {},
        saveAs: () => {},
      },
      showCloseModal: false,
      isControlBtnShow: false,
      tabLeft: false,
      tabRight: false,
      tips: this.$t("message.scripts.container.tips", { book: this.getHandbookUrl() }),
      isTopPanelFull: false,
      loading: false,
    }
  },
  mixins: [mixin],
  computed: {
    workListLength() {
      return this.worklist.length
    },
    dragOptions() {
      return {
        animation: 0,
        group: "description",
        disabled: false,
        draggable: ".workbench-tab-item",
        ghostClass: "ghost",
      }
    },
  },
  watch: {
    current(val, oldVal) {
      if (oldVal) {
        this.dispatch("Workbench:setResultCache", { id: oldVal })
        this.revealInSidebar()
      }
    },
    workListLength() {
      this.toggleCtrlBtn(this)
      this.changeOrder()
    },
    $route: function() {
      this.openQueryTab()
    },
  },
  mounted() {
    this.init()
    elementResizeEvent.bind(this.$el, this.resize)
    this.initListenerCopilotEvent()
  },
  beforeDestroy() {
    elementResizeEvent.unbind(this.$el)
    this.destroyCopilotEvent()
  },
  methods: {
    destroyCopilotEvent() {
      plugin.clear('copilot_web_listener_viewTableData')
      plugin.clear('copilot_web_listener_queryStructure')
    },
    initListenerCopilotEvent() {
      plugin.emitHook('get_copilot_web_listener_event_class', 'table').then(eventClass => {
        const tableEvent = new eventClass({
          dispatch: this.dispatch,
          $t: this.$t
        })
        plugin.on('copilot_web_listener_viewTableData', ({ tableName }) => {
          tableEvent.queryTable(tableName)
        })
        plugin.on('copilot_web_listener_queryStructure', ({ tableName }) => {
          const [dbName, fileName] = tableName.split('.')
          const filenamePath = `${this.$t('message.scripts.hiveTableDesc.tableDetail')}(${fileName})`;
          tableEvent.describeTable({
            dbName,
            fileName,
            filenamePath
          })
        })
      })
      
    },
    init() {
      this.loading = true;
      this.dispatch('IndexedDB:getGlobalCache', {
        id: this.getUserName(),
      }, (cache) => {
        this.updateCache(cache);
      })
      // 获取hive库和表的信息，用于在tab页中的.sql脚本进行关键字联想
      // 首次登录打开时，得重新获取数据
      const worklist = storage.get(this.getUserName() + "tabs", "local")
      if (!this.node && worklist && worklist.length > 0) {
        // getLoginTabs()内部有逻辑用于区分不同用户的缓存
        this.getLoginTabs(worklist).then(() => {
          let work = null
          // 如果记录的没有就取最后一个tab
          if (this.lastActive) {
            work = this.worklist.find((w) => w.id === this.lastActive)
          } else {
            work = last(this.worklist)
          }
          if (work) {
            this.chooseWork(work)
          }
          this.openQueryTab()
          this.$nextTick(() => {
            this.loading = false
            this.scrollIntoView()
          })
        })
        storage.remove(this.getUserName() + "tabs", "local")
        return
      }
      this.getWorkListAndOpen().then(() => {
        let work = last(this.worklist);
        const lastActivedWork = this.worklist.find(w=>w.id === this.lastActive)
        if(lastActivedWork) {
          work = lastActivedWork
        }
        if(work) {
          this.chooseWork(work)
        }
        this.openQueryTab();
        this.$nextTick(() => {
          this.loading = false;
          this.scrollIntoView();
        });
      });
    },
    updateCache(cache) {
      const that = this
      const userName = this.getUserName()
      let hiveList = null
      let variableList = null
      let fnList = null
      const waitList = []
      const getHiveList = () => {
        return that
          .dispatch("HiveSidebar:getAllDbsAndTables", {
            userName,
          })
          .then((args) => {
            hiveList = args
            that.$emit('get-dbtable-length', hiveList.length)
          })
      }

      const getVariableList = () => {
        return that
          .dispatch("GlobalValiable:getGlobalVariable")
          .then((args) => {
            variableList = args
          })
      }
      const getFnList = () => {
        return that.dispatch("fnSidebar:getAllLoadedFunction").then((args) => {
          fnList = args
        })
      }
      if (!cache) {
        waitList.push(getHiveList(), getVariableList(), getFnList())
        Promise.all(waitList)
          .then(() => {
            that.dispatch("IndexedDB:setGlobalCache", {
              key: userName,
              hiveList,
              fnList,
              variableList,
              tabList: [],
            })
          })
          .catch((err) => {
            that.$Message.warning(err)
          })
      } else {
        // 对缺少缓存的部分列表去调取接口依次调用
        if (!cache.hiveList || !cache.hiveList.length) {
          waitList.push(getHiveList())
        }
        if (!cache.variableList || !cache.variableList.length) {
          waitList.push(getVariableList())
        }
        if (!cache.fnList || !cache.fnList.length) {
          waitList.push(getFnList())
        }
        Promise.all(waitList)
          .then(() => {
            that.dispatch("IndexedDB:updateGlobalCache", {
              id: userName,
              hiveList: hiveList || cache.hiveList,
              fnList: fnList || cache.fnList,
              variableList: variableList || cache.variableList,
            })
          })
          .catch((err) => {
            that.$Message.warning(err)
          })
      }
    },
    getLoginTabs(worklist) {
      if (!this.node) {
        let order = this.changeOrder(false)
        let lastActive = worklist.find(
          (w) => w.owner === this.getUserName() && w.actived
        )
        if (lastActive) {
          this.lastActive = lastActive.id
        }
        if (order) {
          worklist.sort((a, b) => {
            return order.indexOf(a.id) - order.indexOf(b.id)
          })
        }
        let asyncList = worklist.map((work) => {
          if (work.type !== "node" && work.owner === this.getUserName()) {
            // 登录之后,更新脚本内容, 当有filePath可从接口获取最新脚本
            return this.openFileAction(work)
          }
        })
        // 异步获取脚本内容之后才能选中work
        return Promise.all(asyncList)
      }
    },
    openFileAction(work) {
      const methodName = "Workbench:add"
      let addParams = {
        id: work.data.id,
        filename: work.filename,
        filepath: work.filepath,
        code: work.data.data,
        type: work.type,
        saveAs: work.saveAs,
        currentNodeKey: this.node ? this.node.key : "",
      }
      if (work.filepath) {
        return api
          .fetch(
            "/filesystem/openFile",
            {
              path: work.filepath,
            },
            "get"
          )
          .then((res) => {
            const params = this.convertSettingParams(res.metadata)
            work.data.data = res.fileContent[0][0]
            addParams.code = work.data.data
            addParams.params = params
            this[methodName](addParams, () => {}, false)
          })
      } else {
        this[methodName](addParams, () => {}, false)
      }
    },
    getWorkListAndOpen() {
      return new Promise((resolve) => {
        if (!this.node) {
          this.dispatch("IndexedDB:getTabs", (worklist) => {
            let order = this.changeOrder(false)
            let lastActive = worklist.find(
              (w) => w.owner === this.getUserName() && w.actived
            )
            if (lastActive) {
              this.lastActive = lastActive.id
            }
            if (order) {
              worklist.sort((a, b) => {
                return order.indexOf(a.id) - order.indexOf(b.id)
              })
            }
            worklist.forEach((work) => {
              if (work.type !== "node" && work.owner === this.getUserName()) {
                // 登录之后,更新脚本内容, 当有filePath可从接口获取最新脚本
                const methodName = "Workbench:add"
                let addParams = {
                  id: work.data.id,
                  filename: work.filename,
                  filepath: work.filepath,
                  code: work.data.data,
                  type: work.type,
                  data: work.data,
                  saveAs: work.saveAs,
                  currentNodeKey: "",
                }
                this[methodName](addParams, () => {}, false)
              }
            })
            resolve()
          })
        } else {
          this.tips = ""
          if (this.node.key) {
            this.dispatch("IndexedDB:getTabs", () => {
              const methodName = "Workbench:add"
              const supportModes = this.getSupportModes()
              const model = this.node.modelType
              const match = supportModes.find(
                (s) => (s.flowType || s.scriptType || '').toLowerCase() == model
              ) || {}
              const name = `${this.node.name || this.node.key}${match.ext || ''}`
              this[methodName]({
                id: this.node.key,
                filename:
                  this.node.jobContent && this.node.jobContent.script
                    ? this.node.jobContent.script
                    : name,
                filepath: "",
                type: "node",
                code: this.parameters.content,
                params: this.parameters.params,
                saveAs: false,
                currentNodeKey: this.node ? this.node.key : "",
              })
              resolve()
            })
          }
        }
      })
    },
    /**
     * 监听 Workbench:add 事件，触发往worklist塞work的逻辑
     * @param {Work} option
     * @param {Funcion} cb
     * @return {Notice}
     */
    "Workbench:add"(option, cb, choose) {
      option.owner = this.getUserName()
      // 先判断当前是否是节点的组件，再来判断那个节点, 由于ide使用keep-alive所以页面关闭并未注销所以还得判断没有节点时阻止
      if (
        (this.node &&
          option.currentNodeKey !== "undefined" &&
          option.currentNodeKey !== this.node.key) ||
        (!this.node && option.currentNodeKey)
      )
        return
      if (!option.id || !option.filename) {
        this.$Notice.close("developerWarning")
        return this.$Notice.error({
          title: this.$t(
            "message.scripts.container.notice.developerWarning.title"
          ),
          desc: this.$t(
            "message.scripts.container.notice.developerWarning.desc"
          ),
          name: "developerWarning",
          duration: 3,
        })
      }
      const supportedMode = find(
        this.getSupportModes(),
        (p) => p.rule.test(option.filename) && p.isCanBeOpen
      )
      if (!supportedMode) {
        this.$Notice.close("unSupport")
        return this.$Notice.warning({
          title: this.$t("message.scripts.container.notice.unSupport.title"),
          desc: this.$t("message.scripts.container.notice.unSupport.desc"),
          name: "unSupport",
          duration: 3,
        })
      }
      let work = null
      if (option.type !== "backgroundScript") {
        // 如果已经在tabs中，则打开
        let repeatWork = find(this.worklist, (work) => work.id == option.id)
        if (!repeatWork) {
          if (this.worklist.length >= maxTabLen) {
            this.$Notice.close("boyondQuota")
            cb && cb(false)
            return this.$Notice.warning({
              title: this.$t("message.scripts.boyondQuota.title"),
              desc: this.$t("message.scripts.boyondQuota.desc"),
              name: "boyondQuota",
              duration: 3,
            })
          }
          work = new Work(option)
          // addWay用于判断新建脚本时脚本在tab栏打开的位置
          // follow表示紧跟上一个脚本
          if (option.addWay === "follow") {
            const index = this.worklist.findIndex(
              (item) => item.id === this.current
            )
            this.worklist.splice(index === -1 ? 0 : index + 1, 0, work)
          } else {
            this.worklist.push(work)
          }
          repeatWork = work
          if (choose !== false) this.chooseWork(repeatWork)
          cb && cb(true)
        } else {
          if (choose !== false) this.chooseWork(repeatWork)
          if (option.action === "export_table") {
            cb && cb(true)
          } else {
            cb && cb(false)
          }
        }
      } else {
        work = new Work(option)
        this.worklist.push(work)
        cb && cb(true)
      }
    },
    "Workbench:checkExist"(option, cb) {
      api
        .fetch(
          "/filesystem/isExist",
          {
            path: option.path,
          },
          "get"
        )
        .then((rst) => {
          // 如果文件已存在，则返回false
          if (rst.isExist) {
            cb(true)
          } else {
            cb(false)
          }
        })
    },
    "Workbench:isOpenTab"({ oldDest }, cb) {
      // 重命名文件、目录，是否存在已打开
      const work = this.worklist.find((work) => work.filepath.indexOf(oldDest) > -1)
      if (work) {
        cb(true)
      } else {
        cb(false)
      }
    },
    "Workbench:openFile"(option, cb) {
      const filename = option.filename.slice(
        option.filename.indexOf("/") + 1,
        option.filename.length
      )
      const supportedMode = find(
        this.getSupportModes(),
        (p) => p.rule.test(filename) && p.isCanBeOpen
      )
      if (!supportedMode) {
        this.$Notice.close("unSupport")
        return this.$Notice.warning({
          title: this.$t("message.scripts.container.notice.unSupport.title"),
          desc: this.$t("message.scripts.container.notice.unSupport.desc"),
          name: "unSupport",
          duration: 3,
        })
      }
      const md5Path = util.md5(option.path)
      const findWork = this.worklist.find((item) => item.id === md5Path)
      if (findWork) {
        return this.chooseWork(findWork)
      }
      const methodName = "Workbench:add"
      let path = option.source ? option.source : option.path
      this.loading = true
      api
        .fetch(
          "/filesystem/openFile",
          {
            path,
          },
          "get"
        )
        .then((rst) => {
          const ismodifyByOldTab = option.code && !rst.fileContent[0][0]
          const params = ismodifyByOldTab
            ? option.params
            : this.convertSettingParams(rst.metadata)
          this[methodName](
            {
              id: md5Path,
              filename: option.filename,
              filepath: option.path,
              code: rst.fileContent[0][0] || option.code,
              params,
              type: option.type,
              saveAs: option.saveAs || false,
              unsave: ismodifyByOldTab,
              ismodifyByOldTab,
              currentNodeKey: this.node ? this.node.key : "",
            },
            () => {
              this.loading = false
              cb(rst)
              if (option.source) {
                this.save(rst, option)
              }
            }
          )
        })
        .catch(() => {
          this.loading = false
        })
    },
    save(rst, option) {
      let variable = rst.metadata.variable
      let params = {
        path: option.path,
        scriptContent: rst.fileContent[0].join(""),
        params: { variable, configuration: {} },
      }
      api.fetch("/filesystem/saveScript", params).then(() => {})
    },
    async "Workbench:deleteDirOrFile"(path, cb) {
      const md5Path = util.md5(path)
      const findWork = find(this.worklist, (work) => {
        return work.id === md5Path
      })
      if (!findWork) {
        let num = 0
        const needForClose = []
        /**
         * 这里是针对层级比较深的情况，删除文件夹
         * 不能边循环边remove，也无法使用Promise.all，因为循环很快
         * 所以只能循环两次
         */
        this.worklist.forEach((work) => {
          if (work.filepath.indexOf(path) === 0) {
            num++
            if (work.unsave) {
              work.saveAs = true
            }
            needForClose.push(work)
          }
        })
        if (!num) {
          cb("none")
        } else {
          for (const work of needForClose) {
            await this.removeWork(work)
          }
          cb("save")
        }
      } else if (findWork.unsave) {
        cb("unsave")
      } else {
        this.removeWork(findWork)
        cb("save")
      }
      this.dispatch("IndexedDB:clearTab", md5Path)
    },
    "Workbench:saveAs"(work) {
      this.$refs.saveAs.open(work)
    },
    "Workbench:updateTab"({ newNode, findWork, oldLabel }, cb) {
      const work =
        findWork ||
        find(this.worklist, (work) => {
          return work.filename === oldLabel
        })
      if (work) {
        const newKey = util.md5(newNode.path)
        const modifyLog = this.dispatch("IndexedDB:changeLogKey", {
          oldKey: work.id,
          newKey,
        })
        const modifyHistory = this.dispatch("IndexedDB:changeHistoryKey", {
          oldKey: work.id,
          newKey,
        })
        const modifyResult = this.dispatch("IndexedDB:changResultKey", {
          oldKey: work.id,
          newKey,
        })
        const modifyProgress = this.dispatch("IndexedDB:changProgressKey", {
          oldKey: work.id,
          newKey,
        })
        const modifyTab = this.dispatch("IndexedDB:changeTabKey", {
          oldKey: work.id,
          newKey,
        })
        Promise.all([
          modifyLog,
          modifyHistory,
          modifyResult,
          modifyProgress,
          modifyTab,
        ]).then(() => {
          setTimeout(() => {
            // 在重命名成功后重新打开tab；
            // 之前的逻辑是直接修改tab上的名称，并更新indexDb，会引发一个bug
            // 就是在修改完成后没有无法修改work的id，然后可以再次打开一个同名的tab。
            work.unsave = false
            this.$Modal.remove()
            this.removeWork(work)
            const methodName = "Workbench:openFile"
            this[methodName](
              {
                path: newNode.path,
                filename: newNode.name,
                saveAs: false,
                code: (findWork && findWork.data.data) || "",
                params: (findWork && findWork.data.params) || {},
              },
              () => {
                if (cb) {
                  cb()
                }
                setTimeout(() => {
                  this.dispatch(
                    "Workbench:save",
                    this.worklist[this.worklist.length - 1]
                  )
                }, 500)
              }
            )
          }, 300)
        })
      }
    },
    "Workbench:updateFlowsTab"(node, data) {
      const work = find(this.worklist, (work) => {
        return work.id === node.key
      })
      this.$set(work.data, "data", data.content)
      this.$set(work.data, "params", this.convertSettingParams(data.params))
      this.dispatch("Workbench:resetScriptData", work.data.id)
    },
    "Workbench:updateFlowsNodeName"(node) {
      this.worklist = this.worklist.map((work) => {
        if (work.id === node.key) {
          work.nodeName = node.title
        }
        return work
      })
    },
    "Workbench:pasteInEditor"(value, node = {}) {
      // node页面和scriptis页面操作不同，由于scriptis页面有缓存，所以关闭页面并不会注销组件，所以先判断是node页面触发的还是scriptis页面触发的，然后再判断是有那个编辑器触发的
      if (!this.node && Object.keys(node).length <= 0) {
        const work = find(this.worklist, (work) => work.id === this.current)
        if (!work)
          return this.$Message.warning(
            this.$t("message.scripts.container.warning.noSelectedScript")
          )
        this.dispatch("Workbench:insertValue", {
          id: this.current,
          value,
        })
      } else {
        const work = find(this.worklist, (work) => work.id === node.key)
        if (work && node.key === this.current) {
          this.dispatch("Workbench:insertValue", {
            id: node.key,
            value,
          })
        }
      }
    },
    // 用于获取当前打开的脚本里面有几种语言
    "Workbench:getWorksLangList"(cb) {
      const workLangList = uniq(this.worklist.map((item) => item.data.lang))
      cb(workLangList)
    },
    "Workbench:setTabPanelSize"() {
      this.isTopPanelFull = false
    },
    "Workbench:removeWork"(work) {
      this.removeWork(work)
    },
    onChooseWork(work) {
      this.chooseWork(work, false)
    },
    /**
     * 选中一个tab项
     * @param {Work} work
     */
    chooseWork(work, intoview = true) {
      if (work) {
        const type = work.type
        // 选中work的时候先高亮work所属的模块树，然后去清除翻转过来的另一块文件树；
        // 由于模块间是使用v-if去切换的，所以会触发不到invert后的模块高亮请求
        this.dispatch(
          this.getMethodName({
            type,
            isInvert: false,
          }),
          work
        )
        this.dispatch(
          this.getMethodName({
            type,
            isInvert: true,
          }),
          ""
        )
        this.lastCurrent = this.current
        this.current = work.id
        this.dispatch("IndexedDB:toggleTab", work.id, this.getUserName())
        this.panelControl(
          this.isTopPanelFull ? "fullScreen" : "releaseFullScreen"
        )
      }
      if (intoview) {
        this.scrollIntoView()
      }
    },
    /**
     * 从worklist列表中移除work，对外抛出'Workbench:deleteDirOrFile'事件。
     * 如果work修改过未保存，则提示保存；如果saveAs是true，则提示另存。
     * @param {Work} work
     * @return {Promise}
     */
    removeWork(work) {
      return new Promise((resolve) => {
        let doRemove = () => {
          let index = this.worklist.indexOf(work)
          if (index != -1) {
            this.worklist.splice(index, 1)
            this.dispatch("IndexedDB:removeTab", work.id)
            this.dispatch("IndexedDB:removeGlobalCache", {
              id: this.getUserName(),
              tabId: work.id,
            })
          }
          if (work.id == this.current) {
            if (this.workListLength > 0) {
              let findWork = last(this.worklist)
              this.current = findWork.id
              let type = findWork.type
              // 如果上一次选中的tab没被关闭，就跳回上一次选中的tab，否则则默认选中最后一个
              let index = this.worklist.findIndex(
                (work) => work.id === this.lastCurrent
              )
              if (index !== -1) {
                this.current = this.lastCurrent
                type = this.worklist[index].type
                findWork = this.worklist[index]
              }
              this.scrollIntoView()
              // 先设置当前脚本高亮
              this.dispatch(
                this.getMethodName({
                  type,
                  isInvert: false,
                }),
                findWork
              )
              /**
               * 清空另外一个模块的高亮，这里和上一次调用时互斥的
               * 因为不知道是工作空间还是HDFS处于打开状态，所以两次操作只执行用户看到的模块
               * 即设置或者清除当前用户能看到的模块高亮
               */
              this.dispatch(
                this.getMethodName({
                  type,
                  isInvert: true,
                }),
                ""
              )
              this.dispatch(
                "IndexedDB:toggleTab",
                this.current,
                this.getUserName()
              )
            } else {
              this.isTopPanelFull = false
              this.current = ""
              this.dispatch("WorkSidebar:setHighLight", "")
              this.dispatch("HdfsSidebar:setHighLight", "")
            }
          }
          // 清空从全局历史转过来的脚本时，切换路由到/，否则刷新界面的时候会被一直打开
          if (work.id === this.$route.query.id) {
            this.$router.push("/")
          }
          resolve()
        }
        if (work.unsave && work.type !== "historyScript") {
          this.chooseWork(work)
          this.showCloseModal = true
          this.closeModal.cancel = () => {
            this.showCloseModal = false
            this.close = () => {}
            this.save = () => {}
            this.saveAs = () => {}
            resolve()
          }
          this.closeModal.close = () => {
            this.showCloseModal = false
            doRemove()
          }
          if (!work.saveAs) {
            this.closeModal.text = `${work.filename}-${this.$t(
              "message.scripts.container.removeWork.normal"
            )}`
            this.closeModal.save = () => {
              this.showCloseModal = false
              this.dispatch("Workbench:save", work)
              doRemove()
            }
            this.closeModal.saveAs = null
          } else {
            const scriptText =
              work.type === "hdfsScript"
                ? `${work.filename}-${this.$t(
                  "message.scripts.container.removeWork.readOnly"
                )}`
                : `${work.filename}-${this.$t(
                  "message.scripts.container.removeWork.temporary"
                )}`
            this.closeModal.text = scriptText
            this.closeModal.save = null
            this.closeModal.saveAs = () => {
              this.dispatch("Workbench:saveAs", work)
            }
          }
        } else {
          doRemove()
        }
        this.dispatch("Workbench:removeTab", this.worklist)
      })
    },
    /**
     * 按照类型关闭tab
     *  @param {String} name
     */
    async closeTabs(name) {
      let needCloseTabs = []
      switch (name) {
        case "other":
          this.worklist.forEach((work) => {
            if (work.id != this.current) {
              needCloseTabs.push(work)
            }
          })
          break
        case "all":
          needCloseTabs = this.worklist.slice(0)
          break
        case "left":
          for (let i = 0; i < this.workListLength; i++) {
            if (this.worklist[i].id != this.current) {
              needCloseTabs.push(this.worklist[i])
            } else {
              break
            }
          }
          break
        case "right":
          for (let i = this.workListLength - 1; i > 0; i--) {
            if (this.worklist[i].id != this.current) {
              needCloseTabs.push(this.worklist[i])
            } else {
              break
            }
          }
          break
      }
      for (const work of needCloseTabs) {
        await this.removeWork(work)
      }
    },

    saveAsComplete(isNew, node) {
      const findWork = this.worklist.find((work) => work.id === this.current)
      // 如果是通过关闭模态框打开的，要关闭模态框
      this.closeModal.close()
      const methodName = "Workbench:updateTab"
      this[methodName]({
        newNode: node,
        findWork,
        oldLabel: null,
      })
    },

    // 打开route.query传过来的tab页
    openQueryTab() {
      const isHistoryIn =
        !isEmpty(this.$route.query) && this.$route.query.taskID
      if (this.$route.name === "Home" && isHistoryIn) {
        const taskID = this.$route.query.taskID
        const filename = this.$route.query.filename
        const md5Id = util.md5(filename)
        const params = {
          id: md5Id,
          taskID,
          filename,
          filepath: "",
          saveAs: true,
          type: "historyScript",
          currentNodeKey: this.node ? this.node.key : "", //避免广播事件和ide做区分
        }
        const methodName = "Workbench:add"
        this[methodName](params)
        this.$nextTick(() => {
          const findWork = find(this.worklist, (item) => {
            return item.taskID === this.$route.query.taskID
          })
          this.chooseWork(findWork)
        })
      }
    },

    // 获取高亮的方法名
    getMethodName(args) {
      let { type, isInvert } = args
      const lib = {
        workspaceScript: "WorkSidebar:setHighLight",
        hdfsScript: "HdfsSidebar:setHighLight",
      }
      if (isInvert) {
        type = type === "workspaceScript" ? "hdfsScript" : "workspaceScript"
      }
      return lib[type]
    },

    tabMoving(type) {
      const tabScroll = this.$refs["tab-list-scroll"]
      let width = tabScroll.getBoundingClientRect().width
      let itemWidth = Math.ceil(width / this.workListLength)
      if (type === "left") {
        tabScroll.scrollLeft += itemWidth
      } else if (type === "right") {
        tabScroll.scrollLeft -= itemWidth
      }
      this.toggleCtrlBtn(this)
    },
    /**
     * 当前work滚动指可视区
     */
    scrollIntoView() {
      let index = this.worklist.findIndex((w) => w.id === this.current)
      if (index < 0) return
      this.$nextTick(() => {
        let item = this.$refs["work_item"]
        if (item && item[index]) {
          item[index].scrollIntoView()
          this.toggleCtrlBtn(this)
        }
      })
    },
    changeOrder(set = true) {
      let key = `work_tabs_order_${this.getUserName()}`
      if (set) {
        let order = []
        this.worklist.forEach((w) => order.push(w.id))
        storage.set(key, order, "local")
      } else {
        let order = storage.get(key, "local")
        return order
      }
    },
    toggleCtrlBtn: debounce((that) => {
      let tabScroll = that.$refs["tab-list-scroll"]
      if (tabScroll) {
        let width = tabScroll.getBoundingClientRect().width
        that.isControlBtnShow = tabScroll.scrollWidth > width
        that.tabRight = tabScroll.scrollLeft >= tabScroll.scrollWidth - width
        that.tabLeft = tabScroll.scrollLeft < 1
      }
    }, 200),
    getInnerWidth() {
      const el = document.getElementsByClassName("workbench workbench-tabs")[0]
      return el.offsetWidth
    },
    panelControl(name) {
      if (name === "fullScreen") {
        this.isTopPanelFull = true
      } else {
        this.isTopPanelFull = false
      }
      this.dispatch("Workbench:setEditorPanelSize", {
        id: this.current,
        status: name,
      })
    },
    dropdownClick(name) {
      const closeMenuList = ["fullScreen", "releaseFullScreen"]
      if (closeMenuList.indexOf(name) === -1) {
        this.closeTabs(name)
      } else {
        this.panelControl(name)
      }
    },
    revealInSidebar() {
      let currentWork = this.worklist.find((item) => item.id === this.current)
      if (currentWork) {
        this.dispatch("WorkSidebar:revealInSideBar", currentWork)
      }
    },
    convertSettingParams(params) {
      const variable = isEmpty(params.variable) ? [] : util.convertObjectToArray(params.variable);
      const configuration = isEmpty(params.configuration) ? {} : {
        special: {},
        runtime: {},
        startup: {},
        ...params.configuration
      };
      return {
        ...params,
        variable,
        configuration,
      };
    },
    resize() {
      this.toggleCtrlBtn(this)
    },
    getUserName() {
      const baseinfo = storage.get("baseInfo", "local")
      if (baseinfo) {
        return baseinfo.proxyEnable && baseinfo.proxyUserName ? baseinfo.proxyUserName : baseinfo.username
      }
      return null
    },
  },
}
</script>
<style lang="scss" scoped>
@import "@dataspherestudio/shared/common/style/variables.scss";
.workbench {
  /deep/.ivu-tabs.ivu-tabs-card {
    border-top: $border-width-base $border-style-base #dcdee2;
    @include border-color($border-color-base, $dark-border-color-base);
    .ivu-tabs-bar .ivu-tabs-tab {
      margin-right: 0px;
      margin-left: -1px;
      border-radius: 0;
      vertical-align: top;
      border-top: 0;
      padding: 5px 16px;
      &.ivu-tabs-tab-active {
        border-top: 2px solid $primary-color !important;
      }
    }
  }
}
// 工作区为空背景设置
.bg-page {
  width: 560px;
  height: 100%;
  margin: 0 auto;
  display: flex;
  justify-content: center;
  flex-direction: column;
  align-items: center;
  .bg-img {
    width: 162px;
    height: 145px;
    margin-bottom: 20px;
    @media screen and (max-height: 600px) {
      display: none;
    }
  }
}
.modal-title {
  font-size: 16px;
  .ivu-icon {
    font-size: 20px;
    color: $warning-color;
    margin-right: 10px;
  }
}
.modal-content {
  padding: 10px 20px;
  font-size: $font-size-base;
  border: none;
  box-shadow: none;
  background-color: transparent;
  p {
    @include font-color($light-text-color, $dark-text-color);
  }
}
.workbench-tabs {
  position: $relative;
  height: 100%;
  overflow: hidden;
  box-sizing: border-box;
  z-index: 3;
  @include font-color($light-text-color, $dark-text-color);
  @include bg-color($light-base-color, $dark-base-color);
  .workbench-tab-wrapper {
    display: flex;
    border-top: $border-width-base $border-style-base #dcdcdc;
    border-bottom: $border-width-base $border-style-base #dcdcdc;
    @include border-color($border-color-base, $dark-background-color-header);
    &.full-screen {
      display: none
    }
    .workbench-tab {
      flex: 1;
      display: flex;
      flex-direction: row;
      flex-wrap: nowrap;
      justify-content: flex-start;
      align-items: center;
      height: 45px;
      @include bg-color($light-base-color, $dark-base-color);
      width: calc(100% - 45px);
      overflow: hidden;
      &.work-list-tab {
        overflow-x: auto;
        overflow-y: hidden;
        padding-left: 16px;
        &::-webkit-scrollbar {
          height: 6px;
        }
        &::-webkit-scrollbar-thumb {
          box-shadow: inset 0 0 2px rgba(0, 0, 0, 0.2);
          border-radius: 3px;
          background-color: #787d8b;
        }
        &::-webkit-scrollbar-track {
          box-shadow: inset 0 0 2px rgba(0, 0, 0, 0.2);
          border-radius: 3px;
        }
        .list-group > span {
          white-space: nowrap;
          display: block;
          height: 0;
        }
      }
      .workbench-tab-item {
        text-align: center;
        border-top: none;
        display: inline-block;
        height: 24px;
        line-height: 24px;
        @include bg-color(#e1e5ea, $dark-workspace-body-bg-color);
        @include font-color(
          $workspace-title-color,
          $dark-workspace-title-color
        );
        cursor: pointer;
        min-width: 100px;
        max-width: 200px;
        overflow: hidden;
        margin-right: 8px;
        border-radius: 12px;
        &.active {
          margin-top: 1px;
          @include bg-color(#e8eef4, $dark-workspace-body-bg-color);
          color: $primary-color;
          @include font-color($primary-color, $dark-primary-color);
        }

        &:hover {
          @include bg-color(#d1d7dd, $dark-workspace-body-bg-color);
        }
      }
    }
    .workbench-tab-control {
      flex: 0 0 45px;
      text-align: right;
      @include bg-color($light-base-color, $dark-base-color);
      border-left: $border-width-base $border-style-base $border-color-split;
      @include border-color($border-color-base, $dark-border-color-base);
      .ivu-icon {
        font-size: $font-size-base;
        margin-top: 8px;
        margin-right: 2px;
        cursor: pointer;
        &:hover {
          // color: $primary-color;
          @include font-color($primary-color, $dark-primary-color);
        }
        &.disable {
          // color: $btn-disable-color;
          @include font-color($btn-disable-color, $dark-disable-color);
        }
      }
    }
    .workbench-tab-button {
      flex: 0 0 30px;
      text-align: center;
      @include bg-color($light-base-color, $dark-base-color);
      &:hover {
        @include bg-color($active-menu-item, $dark-active-menu-item);
      }
      .ivu-icon {
        font-size: $font-size-base;
        margin-top: 8px;
        cursor: pointer;
        @include font-color($light-text-color, $dark-text-color);
      }
    }
  }
  .workbench-container {
    height: calc(100% - 48px);
    &.node {
      height: 100%;
    }
    @keyframes ivu-progress-active {
      0% {
        opacity: 0.3;
        width: 0;
      }
      100% {
        opacity: 0;
        width: 100%;
      }
    }
  }
}
</style>
