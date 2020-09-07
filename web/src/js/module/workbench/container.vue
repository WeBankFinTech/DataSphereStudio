<template>
  <div class="workbench workbench-tabs">
    <template v-if="workListLength>0">
      <div
        class="workbench-tab-wrapper"
        :class="{'full-screen': isTopPanelFull}"
        v-if="!node">
        <div class="workbench-tab">
          <template v-for="work in worklist">
            <div
              :key="work.id"
              :class="{active:work.id==current}"
              class="workbench-tab-item"
              v-if="work.isShow && work.type !== 'backgroundScript'">
              <we-title
                :work="work"
                @on-choose="chooseWork"
                @on-remove="removeWork"/>
            </div>
          </template>
        </div>
        <div
          class="workbench-tab-control"
          v-if="isControlBtnShow">
          <Icon
            type="ios-arrow-dropleft-circle"
            @click="tabMoving('right')"></Icon>
          <Icon
            type="ios-arrow-dropright-circle"
            @click="tabMoving('left')"></Icon>
        </div>
        <div class="workbench-tab-button">
          <Dropdown
            trigger="click"
            placement="bottom-end"
            @on-click="dropdownClick">
            <Icon type="md-list" />
            <DropdownMenu slot="list">
              <DropdownItem name="other">{{ $t('message.workBench.container.closeDropDown.others') }}</DropdownItem>
              <DropdownItem name="all">{{ $t('message.workBench.container.closeDropDown.all') }}</DropdownItem>
              <DropdownItem name="left">{{ $t('message.workBench.container.closeDropDown.left') }}</DropdownItem>
              <DropdownItem name="right">{{ $t('message.workBench.container.closeDropDown.right') }}</DropdownItem>
              <DropdownItem
                name="fullScreen"
                divided
                v-if="!isTopPanelFull">{{$t("message.constants.logPanelList.fullScreen")}}</DropdownItem>
              <DropdownItem
                name="releaseFullScreen"
                v-else>{{$t("message.constants.logPanelList.releaseFullScreen")}}</DropdownItem>
            </DropdownMenu>
          </Dropdown>
        </div>
      </div>
      <div
        class="workbench-container"
        :class="{node: node}">
        <we-body
          v-for="work in worklist"
          v-show="current==work.id"
          :key="work.id"
          :work="work"
          @remove-work="removeWork"
          :node="node"
          :readonly="readonly"/>
      </div>
    </template>
    <template v-if="workListLength == 0 && !loading">
      <div class="bg-page">
        <img
          class="bg-img"
          src="./image/bg-img.png">
        <p
          class="bg-text"
          :class="{weight: line.indexOf('？') !== -1, indent: line.indexOf('？') === -1}"
          v-for="(line, index) in tips.split('\n')"
          :key="index">{{ line }}</p>
      </div>
    </template>
    <template>
      <Spin
        v-if="loading"
        size="large"
        fix/>
    </template>
    <Modal
      v-model="showCloseModal"
      :closable="false"
      width="360">
      <p
        slot="header"
        class="modal-title">
        <Icon type="md-help-circle" />
        <span>{{ $t('message.workBench.container.closeHint') }}</span>
      </p>
      <div class="modal-content">
        <p style="word-break: break-all;">{{ closeModal.text }}</p>
      </div>
      <div slot="footer">
        <Button
          type="text"
          @click="closeModal.cancel" >{{ $t('message.workBench.container.footer.cancel') }}</Button>
        <Button
          type="warning"
          @click="closeModal.close">{{ $t('message.workBench.container.footer.close') }}</Button>
        <Button
          v-if="closeModal.save"
          type="primary"
          @click="closeModal.save">{{ $t('message.workBench.container.footer.save') }}</Button>
        <Button
          v-if="closeModal.saveAs"
          type="primary"
          @click="closeModal.saveAs">{{ $t('message.workBench.container.footer.saveAs') }}</Button>
      </div>
    </Modal>
    <save-as
      ref="saveAs"
      @save-complete="saveAsComplete"/>
  </div>
</template>
<script>
import api from '@/js/service/api';
import storage from '@/js/helper/storage';
import util from '@/js/util';
import title from './title.vue';
import body from './body.vue';
import saveAs from './script/saveAs.vue';
import { Work } from './modal.js';
import { find, uniq, isEmpty, last } from 'lodash';
import { ext } from '@/js/service/nodeType'
export default {
  components: {
    'we-body': body,
    'we-title': title,
    saveAs,
  },
  props: {
    width: Number,
    parameters: {
      type: Object,
      default: () => {
        return {
          content: '',
          params: {},
        };
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
      current: '',
      lastCurrent: '',
      closeModal: {
        text: '',
        cancel: () => {},
        close: () => {},
        save: () => {},
        saveAs: () => {},
      },
      showCloseModal: false,
      userName: '',
      tabMove: {
        maxTabLen: 0,
        leftTab: 0,
        rightTab: 0,
      },
      isControlBtnShow: false,
      tips: this.$t('message.workBench.container.tips'),
      isTopPanelFull: false,
      loading: false,
    };
  },
  computed: {
    workListLength() {
      return this.worklist.length;
    },
  },
  watch: {
    current(val, oldVal) {
      if (oldVal) {
        this.dispatch('Workbench:setResultCache', { id: oldVal });
        this.revealInSidebar();
      }
    },
    '$route': function(val) {
      this.openQueryTab();
    },
  },
  mounted() {
    this.userName = this.getUserName();
    // isCacheInit 是防止切换用户时触发两次mounted
    const isCacheInit = storage.get('isCacheLoading');
    if (!isCacheInit) {
      this.init();
    }
  },
  methods: {
    init() {
      storage.set('isCacheLoading', true);
      this.loading = true;
      // 获取hive库和表的信息，用于在tab页中的.sql脚本进行关键字联想
      // userName作为去本地数据库查找数据库缓存的key，用于区分不同用户的缓存
      this.dispatch('IndexedDB:getGlobalCache', {
        id: this.userName,
      }, (cache) => {
        if (!cache) {
          // 调用hive模块的获取hive库和表的接口
          this.dispatch('HiveSidebar:getAllDbsAndTables', {
            userName: this.userName,
          }, (args) => {
            if (args.isError) {
              this.$Message.warning(this.$t('message.workBench.container.warning.noDBInfo'));
            }
            this.dispatch('fnSidebar:getAllLoadedFunction', (args2) => {
              if (args2.isError) {
                this.$Message.warning(this.$t('message.workBench.container.warning.noUDF'));
              }
              this.dispatch('GlobalValiable:getGlobalVariable', (args3) => {
                if (args3.isError) {
                  this.$Message.warning(this.$t('message.container.notVarible'));
                }
                this.dispatch('IndexedDB:setGlobalCache', {
                  key: this.userName,
                  hiveList: args.list,
                  fnList: args2.list,
                  variableList: args3.list,
                  tabList: [],
                });
                storage.set('isCacheLoading', false);
              });
            });
          });
          // 在获取到hive信息后再打开缓存的tab页信息
          this.getWorkList({ list: [] }).then(() => {
            this.openQueryTab();
            this.$nextTick(() => {
              this.loading = false;
              this.tabMove.maxTabLen = Math.floor(this.width / 100);
              this.changeTabParams('init');
            });
          });
        } else {
          if (!cache.hiveList || !cache.hiveList.length) {
            this.dispatch('HiveSidebar:getAllDbsAndTables', {
              userName: this.userName,
            }, (args) => {
              if (args.isError) {
                this.$Message.warning(this.$t('message.workBench.container.warning.noDBInfo'));
              }
              this.dispatch('IndexedDB:updateGlobalCache', {
                id: this.userName,
                hiveList: args.list,
              });
              storage.set('isCacheLoading', false);
            });
          }
          if (!cache.variableList || !cache.variableList.length) {
            this.dispatch('GlobalValiable:getGlobalVariable', (args) => {
              if (args.isError) {
                this.$Message.warning(this.$t('message.container.notVarible'));
              }
              this.dispatch('IndexedDB:updateGlobalCache', {
                id: this.userName,
                variableList: args.list,
              });
              storage.set('isCacheLoading', false);
            });
          }
          if (!cache.fnList || !cache.fnList.length) {
            this.dispatch('fnSidebar:getAllLoadedFunction', (args) => {
              if (args.isError) {
                this.$Message.warning(this.$t('message.workBench.container.warning.noUDF'));
              }
              this.dispatch('IndexedDB:updateGlobalCache', {
                id: this.userName,
                fnList: args.list,
              });
              storage.set('isCacheLoading', false);
            });
          } else {
            storage.set('isCacheLoading', false);
          }
          // 这种情况适用于初始化系统是/consle，然后打开全局历史的查看
          // 必须打开缓存的tab后再打开从全局历史传递来的query，否在无法被选中
          this.getWorkList({ list: cache.tabList }).then(() => {
            this.openQueryTab();
            this.$nextTick(() => {
              this.loading = false;
              const innerWidth = this.width || this.getInnerWidth();
              this.tabMove.maxTabLen = Math.floor(innerWidth / 100) - 1;
              this.changeTabParams('init');
            });
          });
        }
      });
    },
    getWorkList({ list }) {
      return new Promise((resolve, reject) => {
        if (!this.node) {
          this.dispatch('IndexedDB:getTabs', (worklist) => {
            worklist.forEach((work) => {
              if (work.type !== 'node' && work.owner === this.userName) {
                const isIn = list.indexOf(work.data.id);
                const methodName = 'Workbench:add';
                if (isIn === -1) {
                  this[methodName]({
                    id: work.data.id,
                    filename: work.filename,
                    filepath: work.filepath,
                    code: work.data.data,
                    type: work.type,
                    data: work.data,
                    saveAs: work.saveAs,
                  });
                }
              }
            });
            resolve();
          });
        } else {
          this.tips = '';
          if (this.node.key) {
            this.dispatch('IndexedDB:getTabs', (worklist) => {
              const methodName = 'Workbench:add';
              const supportModes = this.getSupportModes();
              // 由于修改了节点类型所以之前获取方法不行
              const model = ext[this.node.type];
              const match = supportModes.find((s) => s.flowType && s.flowType.toLowerCase() == model);
              const name = '' + new Date().getTime() + Math.ceil(Math.random() * 100) + match.ext;
              this[methodName]({
                id: this.node.key,
                filename: this.node.jobContent && this.node.jobContent.script ? this.node.jobContent.script : name,
                filepath: '',
                type: 'node',
                code: this.parameters.content,
                params: this.parameters.params,
                saveAs: false,
                nodeName: this.node.title
              });
              resolve();
            });
          }
        }
      });
    },
    /**
         * 监听 Workbench:add 事件，触发往worklist塞work的逻辑
         * @param {Work} option
         * @param {Funcion} cb
         * @return {Notice}
         */
    'Workbench:add'(option, cb) {
      if (!option.id || !option.filename) {
        this.$Notice.close('developerWarning');
        return this.$Notice.error({
          title: this.$t('message.workBench.container.developerWarning.notice.title'),
          desc: this.$t('message.workBench.container.developerWarning.notice.desc'),
          name: 'developerWarning',
          duration: 3,
        });
      }
      const supportedMode = find(this.getSupportModes(), (p) => p.rule.test(option.filename) && p.isCanBeOpen);
      if (!supportedMode) {
        this.$Notice.close('unSupport');
        return this.$Notice.warning({
          title: this.$t('message.workBench.container.unSupport.notice.title'),
          desc: this.$t('message.workBench.container.unSupport.notice.desc'),
          name: 'unSupport',
          duration: 3,
        });
      }
      let work = null;
      if (option.type !== 'backgroundScript') {
        // 如果已经在tabs中，则打开
        let repeatWork = find(this.worklist, (work) => work.id == option.id);
        if (!repeatWork) {
          if (this.worklist.length >= 20) {
            this.$Notice.close('boyondQuota');
            cb && cb(false);
            return this.$Notice.warning({
              title: this.$t('message.container.boyondQuota.title'),
              desc: this.$t('message.container.boyondQuota.desc'),
              name: 'boyondQuota',
              duration: 3,
            });
          }
          work = new Work(option);
          // addWay用于判断新建脚本时脚本在tab栏打开的位置
          // follow表示紧跟上一个脚本
          if (option.addWay === 'follow') {
            const index = this.worklist.findIndex((item) => item.id === this.current);
            this.worklist.splice(index === -1 ? 0 : index + 1, 0, work);
          } else {
            this.worklist.push(work);
          }
          repeatWork = work;
        }
        this.chooseWork(repeatWork);
        cb && cb(true);
      } else {
        work = new Work(option);
        this.worklist.push(work);
        cb && cb(true);
      }
    },
    'Workbench:checkExist'(option, cb) {
      api.fetch('/filesystem/isExist', {
        path: option.path,
      }, 'get').then((rst) => {
        // 如果文件已存在，则返回false
        if (rst.isExist) {
          cb(true);
        } else {
          cb(false);
        }
      });
    },
    'Workbench:openFile'(option, cb) {
      const filename = option.filename.slice(option.filename.indexOf('/') + 1, option.filename.length);
      const supportedMode = find(this.getSupportModes(), (p) => p.rule.test(filename) && p.isCanBeOpen);
      if (!supportedMode) {
        this.$Notice.close('unSupport');
        return this.$Notice.warning({
          title: this.$t('message.workBench.container.notice.unSupport.title'),
          desc: this.$t('message.workBench.container.notice.unSupport.desc'),
          name: 'unSupport',
          duration: 3,
        });
      }
      const md5Path = util.md5(option.path);
      const findWork = this.worklist.find((item) => item.id === md5Path);
      if (findWork) {
        return this.chooseWork(findWork);
      }
      const methodName = 'Workbench:add';
      api.fetch('/filesystem/openFile', {
        path: option.path,
      }, 'get').then((rst) => {
        const ismodifyByOldTab = option.code && !rst.fileContent[0][0];
        const params = ismodifyByOldTab ? option.params : this.convertSettingParams(rst.metadata);
        this[methodName]({
          id: md5Path,
          filename: option.filename,
          filepath: option.path,
          code: rst.fileContent[0][0] || option.code,
          params,
          type: option.type,
          saveAs: option.saveAs || false,
          unsave: ismodifyByOldTab,
          ismodifyByOldTab,
        }, (f) => {
          this.changeTabParams('change');
          cb(rst);
        });
      });
    },
    async 'Workbench:deleteDirOrFile'(path, cb) {
      const md5Path = util.md5(path);
      const findWork = find(this.worklist, (work) => {
        return work.id === md5Path;
      });
      if (!findWork) {
        let num = 0;
        const needForClose = [];
        /**
         * 这里是针对层级比较深的情况，删除文件夹
         * 不能边循环边remove，也无法使用Promise.all，因为循环很快
         * 所以只能循环两次
         */
        this.worklist.forEach((work) => {
          if (work.filepath.indexOf(path) === 0) {
            num++;
            if (work.unsave) {
              work.saveAs = true;
            }
            needForClose.push(work);
          }
        });
        if (!num) {
          cb('none');
        } else {
          for (const work of needForClose) {
            await this.removeWork(work);
          }
          cb('save');
        }
      } else if (findWork.unsave) {
        cb('unsave');
      } else {
        this.removeWork(findWork);
        cb('save');
      }
    },
    'Workbench:saveAs'(work) {
      this.$refs.saveAs.open(work);
    },
    'Workbench:updateTab'({ newNode, findWork, oldLabel }, cb) {
      const work = findWork || find(this.worklist, (work) => {
        return work.filename === oldLabel;
      });
      if (work) {
        const newKey = util.md5(newNode.path);
        const modifyLog = this.dispatch('IndexedDB:changeLogKey', { oldKey: work.id, newKey });
        const modifyHistory = this.dispatch('IndexedDB:changeHistoryKey', { oldKey: work.id, newKey });
        const modifyResult = this.dispatch('IndexedDB:changResultKey', { oldKey: work.id, newKey });
        const modifyProgress = this.dispatch('IndexedDB:changProgressKey', { oldKey: work.id, newKey });
        const modifyTab = this.dispatch('IndexedDB:changeTabKey', { oldKey: work.id, newKey });
        Promise.all([modifyLog, modifyHistory, modifyResult, modifyProgress, modifyTab]).then(() => {
          setTimeout(() => {
            // 在重命名成功后重新打开tab；
            // 之前的逻辑是直接修改tab上的名称，并更新indexDb，会引发一个bug
            // 就是在修改完成后没有无法修改work的id，然后可以再次打开一个同名的tab。
            work.unsave = false;
            this.$Modal.remove();
            this.removeWork(work);
            const methodName = 'Workbench:openFile';
            this[methodName]({
              path: newNode.path,
              filename: newNode.name,
              saveAs: false,
              code: findWork && findWork.data.data || '',
              params: findWork && findWork.data.params || {},
            }, () => {
              if (cb) {
                cb();
              }
              setTimeout(() => {
                this.dispatch('Workbench:save', this.worklist[this.worklist.length - 1]);
              }, 500);
            });
          }, 300);
        });
      }
    },
    'Workbench:updateFlowsTab'(node, data) {
      const work = find(this.worklist, (work) => {
        return work.id === node.key;
      });
      this.$set(work.data, 'data', data.content);
      this.$set(work.data, 'params', this.convertSettingParams(data.params));
      this.dispatch('Workbench:resetScriptData', work.data.id);
    },
    'Workbench:updateFlowsNodeName'(node) {
      this.worklist = this.worklist.map((work) => {
        if (work.id === node.key) {
          work.nodeName = node.title
        }
        return work;
      })
    },
    'Workbench:pasteInEditor'(value, node={}) {
      // node页面和scriptis页面操作不同，由于scriptis页面有缓存，所以关闭页面并不会注销组件，所以先判断是node页面触发的还是scriptis页面触发的，然后再判断是有那个编辑器触发的
      if (!this.node && Object.keys(node).length <= 0) {
        const work = find(this.worklist, (work) => work.id === this.current);
        if (!work) return this.$Message.warning(this.$t('message.workBench.container.warning.noSelectedScript'));
        this.dispatch('Workbench:insertValue', {
          id: this.current,
          value,
        });
      } else {
        const work = find(this.worklist, (work) => work.id === node.key);
        if (work && node.key === this.current) {
          this.dispatch('Workbench:insertValue', {
            id: node.key,
            value,
          });
        }
      }
    },
    // 用于获取当前打开的脚本里面有几种语言
    'Workbench:getWorksLangList'(cb) {
      const workLangList = uniq(this.worklist.map((item) => item.data.lang));
      cb(workLangList);
    },
    'Workbench:setTabPanelSize'() {
      this.isTopPanelFull = false;
    },
    'Workbench:removeWork'(work) {
      this.removeWork(work);
    },
    /**
         * 选中一个tab项
         * @param {Work} work
         */
    chooseWork(work) {
      if (work) {
        const type = work.type;
        // 选中work的时候先高亮work所属的模块树，然后去清除翻转过来的另一块文件树；
        // 由于模块间是使用v-if去切换的，所以会触发不到invert后的模块高亮请求
        this.dispatch(this.getMethodName({
          type,
          isInvert: false,
        }), work);
        this.dispatch(this.getMethodName({
          type,
          isInvert: true,
        }), '');
        this.lastCurrent = this.current;
        this.current = work.id;
        this.dispatch('IndexedDB:toggleTab', work.id);
        this.dispatch('Workbench:setParseAction', work.id);
        this.panelControl(this.isTopPanelFull ? 'fullScreen' : 'releaseFullScreen');
      }
    },
    /**
         * 从worklist列表中移除work，对外抛出'Workbench:deleteDirOrFile'事件。
         * 如果work修改过未保存，则提示保存；如果saveAs是true，则提示另存。
         * @param {Work} work
         * @return {Promise}
         */
    removeWork(work) {
      return new Promise((resolve, reject) => {
        let doRemove = () => {
          let index = this.worklist.indexOf(work);
          if (index != -1) {
            this.worklist.splice(index, 1);
            this.dispatch('IndexedDB:removeTab', work.id);
            this.dispatch('IndexedDB:removeGlobalCache', {
              id: this.userName,
              tabId: work.id,
            });
            this.changeTabParams('change');
          }
          if (work.id == this.current) {
            if (this.workListLength > 0) {
              let findWork = last(this.worklist);
              this.current = findWork.id;
              let type = findWork.type;
              // 如果上一次选中的tab没被关闭，就跳回上一次选中的tab，否则则默认选中最后一个
              let index = this.worklist.findIndex((work) => work.id === this.lastCurrent);
              if (index !== -1) {
                this.current = this.lastCurrent;
                type = this.worklist[index].type;
                findWork = this.worklist[index];
              }
              // 先设置当前脚本高亮
              this.dispatch(this.getMethodName({
                type,
                isInvert: false,
              }), findWork);
              /**
               * 清空另外一个模块的高亮，这里和上一次调用时互斥的
               * 因为不知道是工作空间还是HDFS处于打开状态，所以两次操作只执行用户看到的模块
               * 即设置或者清除当前用户能看到的模块高亮
               */
              this.dispatch(this.getMethodName({
                type,
                isInvert: true,
              }), '');
              this.dispatch('IndexedDB:toggleTab', this.current);
            } else {
              this.isTopPanelFull = false;
              this.current = '';
              this.dispatch('WorkSidebar:setHighLight', '');
              this.dispatch('HdfsSidebar:setHighLight', '');
            }
          }
          // 清空从全局历史转过来的脚本时，切换路由到/，否则刷新界面的时候会被一直打开
          if (work.id === this.$route.query.id) {
            this.$router.push('/');
          }
          resolve();
        };
        if (work.unsave) {
          this.chooseWork(work);
          this.showCloseModal = true;
          this.closeModal.cancel = () => {
            this.showCloseModal = false;
            this.close = () => {};
            this.save = () => {};
            this.saveAs = () => {};
            resolve();
          }
          this.closeModal.close = () => {
            this.showCloseModal = false;
            doRemove();
          };
          if (!work.saveAs) {
            this.closeModal.text = `${work.filename}-${this.$t('message.workBench.container.removeWork.normal')}`;
            this.closeModal.save = () => {
              this.showCloseModal = false;
              this.dispatch('Workbench:save', work);
              doRemove();
            };
            this.closeModal.saveAs = null;
          } else {
            const scriptText = work.type === 'hdfsScript' ? `${work.filename}-${this.$t('message.workBench.container.removeWork.readOnly')}` : `${work.filename}-${this.$t('message.workBench.container.removeWork.temporary')}`;
            this.closeModal.text = scriptText;
            this.closeModal.save = null;
            this.closeModal.saveAs = () => {
              this.dispatch('Workbench:saveAs', work);
            };
          }
        } else {
          doRemove();
        }
      });
    },
    /**
         * 按照类型关闭tab
         *  @param {String} name
         */
    async closeTabs(name) {
      let needCloseTabs = [];
      switch (name) {
        case 'other':
          this.worklist.forEach((work) => {
            if (work.id != this.current) {
              needCloseTabs.push(work);
            }
          });
          break;
        case 'all':
          needCloseTabs = this.worklist.slice(0);
          break;
        case 'left':
          for (let i = 0; i < this.workListLength; i++) {
            if (this.worklist[i].id != this.current) {
              needCloseTabs.push(this.worklist[i]);
            } else {
              break;
            }
          }
          break;
        case 'right':
          for (let i = this.workListLength - 1; i > 0; i--) {
            if (this.worklist[i].id != this.current) {
              needCloseTabs.push(this.worklist[i]);
            } else {
              break;
            }
          }
          break;
      }
      for (const work of needCloseTabs) {
        await this.removeWork(work);
      }
    },

    saveAsComplete(isNew, node) {
      const findWork = this.worklist.find((work) => work.id === this.current);
      // 如果是通过关闭模态框打开的，要关闭模态框
      this.closeModal.close();
      const methodName = 'Workbench:updateTab';
      this[methodName]({
        newNode: node,
        findWork,
        oldLabel: null,
      });
    },

    // 打开route.query传过来的tab页
    openQueryTab() {
      const isHistoryIn = !isEmpty(this.$route.query) && this.$route.query.taskID;
      if (this.$route.name === 'Home' && isHistoryIn) {
        const taskID = this.$route.query.taskID;
        const filename = this.$route.query.filename;
        const md5Id = util.md5(filename);
        const params = {
          id: md5Id,
          taskID,
          filename,
          filepath: '',
          saveAs: true,
          type: 'historyScript',
        };
        const methodName = 'Workbench:add';
        this[methodName](params);
        this.$nextTick(() => {
          const findWork = find(this.workList, (item) => {
            return item.taskID === this.$route.query.taskID;
          });
          this.chooseWork(findWork);
        });
      }
    },

    // 获取高亮的方法名
    getMethodName(args) {
      let { type, isInvert } = args;
      const lib = {
        workspaceScript: 'WorkSidebar:setHighLight',
        hdfsScript: 'HdfsSidebar:setHighLight',
      };
      if (isInvert) {
        type = type === 'workspaceScript' ? 'hdfsScript' : 'workspaceScript';
      }
      return lib[type];
    },

    changeTabParams(type) {
      if (this.workListLength > this.tabMove.maxTabLen) {
        if (type === 'init') {
          this.tabMove.rightTab = this.workListLength - this.tabMove.maxTabLen;
          this.tabMove.leftTab = 0;
        } else {
          this.tabMove.leftTab = this.workListLength - this.tabMove.maxTabLen;
          this.tabMove.rightTab = 0;
        }
        this.handleTabShowOrHidden();
        this.isControlBtnShow = true;
      } else {
        this.tabMove.leftTab = 0;
        this.tabMove.rightTab = 0;
        this.isControlBtnShow = false;
        if (type !== 'init') {
          this.handleTabShowOrHidden();
        }
      }
    },

    tabMoving(type) {
      if (type === 'right' && this.tabMove.leftTab >= 1) {
        this.tabMove.leftTab -= 1;
        this.tabMove.rightTab += 1;
      } else if (type === 'left' && this.tabMove.rightTab >= 1) {
        this.tabMove.leftTab += 1;
        this.tabMove.rightTab -= 1;
      }
      this.handleTabShowOrHidden();
    },

    handleTabShowOrHidden() {
      this.worklist.forEach((o, index) => {
        if (this.tabMove.leftTab > 0 && index <= this.tabMove.leftTab - 1) {
          o.isShow = false;
        } else if (this.tabMove.rightTab > 0 && index > this.workListLength - this.tabMove.rightTab - 1) {
          o.isShow = false;
        } else {
          o.isShow = true; ;
        }
      });
    },
    getInnerWidth() {
      const el = document.getElementsByClassName('workbench workbench-tabs')[0];
      return el.offsetWidth;
    },
    panelControl(name) {
      if (name === 'fullScreen') {
        this.isTopPanelFull = true;
      } else {
        this.isTopPanelFull = false;
      }
      this.dispatch('Workbench:setEditorPanelSize', {
        id: this.current,
        status: name,
      });
    },
    dropdownClick(name) {
      const closeMenuList = ['fullScreen', 'releaseFullScreen'];
      if (closeMenuList.indexOf(name) === -1) {
        this.closeTabs(name);
      } else {
        this.panelControl(name);
      }
    },
    revealInSidebar() {
      let currentWork = this.worklist.find((item) => item.id === this.current);
      if (currentWork) {
        this.dispatch('WorkSidebar:revealInSideBar', currentWork);
      }
    },
    convertSettingParams(params) {
      let variable = [];
      let configuration = {
        special: {},
        runtime: {
          args: '',
          env: [],
        },
        startup: {},
      };
      if (!isEmpty(params)) {
        variable = isEmpty(params.variable) ? [] : util.convertObjectToArray(params.variable);
        configuration = isEmpty(params.configuration) ? {} : {
          special: {},
          runtime: {
            args: params.configuration.runtime.args || '',
            env: isEmpty(params.configuration.runtime.env) ? [] : util.convertObjectToArray(params.configuration.runtime.env),
          },
          startup: {},
        };
      }
      return {
        variable,
        configuration,
      };
    },
  },
};
</script>
<style src="./index.scss" lang="scss"></style>
