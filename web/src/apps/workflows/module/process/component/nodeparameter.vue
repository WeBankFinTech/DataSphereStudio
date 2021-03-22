<template>
  <div>
    <Form label-position="top" ref="baseInfoForm" :model="currentNode" class="node-parameter-bar">
      <FormItem :label="$t('message.workflow.process.nodeParameter.nodeName')" prop="title" :rules="titleRules">
        <Input v-model="currentNode.title" :placeholder="$t('message.workflow.process.nodeParameter.inputeNodeName')" />
      </FormItem>
      <FormItem
        :label="$t('message.workflow.process.nodeParameter.businessLabel')"
        prop="mybusinessTag">
        <we-tag
          :new-label="$t('message.workflow.process.nodeParameter.addLabel')"
          :tag-list="mybusinessTag"
          @add-tag="businessAddTag"
          @delete-tag="businessDeleteTag"></we-tag>
      </FormItem>
      <FormItem
        :label="$t('message.workflow.process.nodeParameter.applyLabel')"
        prop="myappTag">
        <we-tag
          :new-label="$t('message.workflow.process.nodeParameter.addLabel')"
          :tag-list="myappTag"
          @add-tag="appAddTag"
          @delete-tag="appdDeleteTag"></we-tag>
      </FormItem>
      <FormItem :label="$t('message.workflow.process.nodeParameter.nodeDesc')">
        <Input v-model="currentNode.desc" type="textarea"
          :placeholder="$t('message.workflow.process.nodeParameter.inputeNodeDesc')" />
      </FormItem>
    </Form>
    <div v-if="currentNodeParamsBaseinfoList.length > 0" class="node-module-param-modal-header">
      <h3>{{$t('message.workflow.process.nodeParameter.SXXX')}}</h3>
    </div>
    <Form v-if="currentNodeParamsBaseinfoList.length > 0 && currentNode.jobParams" label-position="top"
      ref="parameterForm"  class="node-parameter-bar" :model="currentNode">
      <template v-for="item in currentNodeParamsBaseinfoList">
        <template v-if="item.position === 'runtime' || item.position === 'startup'">
          <FormItem v-if="['Input', 'Text', 'Disable'].includes(item.uiType)" :rules="paramsValid(item)" :key="poinToLink(item.key)" :label="item.lableName" :prop="'jobParams.'+ poinToLink(item.key)">
            <Input v-model="currentNode.jobParams[poinToLink(item.key)]" :type="filterFormType(item.uiType)" :rows="6"
              :placeholder="item.desc" :disabled="item.uiType === 'Disable'"
            />
          </FormItem>
          <FormItem v-if="item.uiType === 'Select' || item.uiType === 'MultiSelect'" :rules="paramsValid(item)" :key="poinToLink(item.key)" :label="item.lableName" :prop="'jobParams.'+ poinToLink(item.key)">
            <Select
              v-model="currentNode.jobParams[poinToLink(item.key)]"
              :placeholder="item.desc"
              :multiple="item.uiType === 'MultiSelect'">
              <Option v-for="subItem in JSON.parse(item.value)" :value="subItem" :key="subItem">{{subItem}}</Option>
            </Select>
          </FormItem>
          <FormItem v-if="item.uiType === 'MultiBinding'" :rules="paramsValid(item)" :key="poinToLink(item.key)" :label="item.lableName" :prop="'jobParams.'+ poinToLink(item.key)">
            <Select
              v-model="currentNode.jobParams[poinToLink(item.key)]"
              :placeholder="item.desc"
              multiple>
              <Option v-for="(subItem,index) in conditionBindList(item)" :value="subItem.key" :key="index">{{ subItem.name }}</Option>
            </Select>
          </FormItem>
        </template>
        <template v-if="item.uiType === 'Upload'">
          <FormItem :rules="paramsValid(item)" :key="poinToLink(item.key)" :label="item.lableName">
            <resource :resources="resources" :is-ripetition="true" :node-type="nodeData.type" :readonly="readonly"
              @update-resources="updateResources"></resource>
          </FormItem>
        </template>
      </template>
    </Form>
    <div class="save-button" v-if="isShowSaveButton">
      <Button @click="save"
        :disabled="isNodeMap">{{$t('message.workflow.process.nodeParameter.BC')}}
      </Button>
    </div>
  </div>
</template>
<script>
import resource from './resource.vue';
import weTag from '@component/tag/index.vue'
import { isEmpty } from 'lodash';

export default {
  name: 'NodeParams',
  components: {
    resource,
    'we-tag': weTag
  },
  props: {
    nodeData: {
      type: Object,
      default: null,
    },
    name: {
      type: String,
      default: '',
    },
    readonly: {
      type: Boolean,
      default: false,
    },
    isShowSaveButton: {
      type: Boolean,
      default: true,
    },
    nodes: {
      type: Array,
      default: () => [],
    },
    isNodeMap: {
      type: Boolean,
      default: false
    },
    consoleParams: {
      type: Array,
      default: () => []
    }
  },
  data() {
    let validatorName = (rule, value, callback) => {
      if (value === `${this.name}_`) {
        callback(new Error(this.$t('message.workflow.process.nodeParameter.JDMCBNYGZL')));
      } else {
        callback();
      }
    };
    return {
      test: [],
      currentNode: {
      },
      titleRules: [
        { required: true, message: this.$t('message.workflow.process.nodeParameter.TXJDMC'), trigger: 'blur' },
        { type: 'string', pattern: /^[a-zA-Z][a-zA-Z0-9_]*$/, message: this.$t('message.workflow.process.nodeParameter.BXYZMKTXHX'), trigger: 'change' },
        { validator: validatorName, trigger: 'blur' },
        { min: 1, max: 128, message: this.$t('message.workflow.process.nodeParameter.CDZ128ZF'), trigger: 'change' },
      ],
      resources: [],
      paramsIsChange: false,
      timer: null,
      mybusinessTag: '',
      myappTag: ''
    };
  },
  watch: {
    nodeData(val) {
      this.currentNode = val;
      if (!this.currentNode.params || isEmpty(this.currentNode.params.configuration)) {
        // 有params可能是spark节点的variable参数
        this.$set(this.currentNode, 'params', {
          configuration: {
            special: {},
            runtime: {},
            startup: {}
          }
        })
      }
      this.mybusinessTag = val.businessTag
      this.myappTag = val.appTag;
      // 在节点没有保存参数时，需要设置默认值的值,先判断保存的值应该存在哪里starup或者runtime或者是node下
      let jobParams = {};
      this.currentNodeParamsBaseinfoList.map((item) => {
        // 多选框是数组值
        const defaultValue = ['MultiBinding'].includes(item.uiType) ? JSON.parse(item.defaultValue) : this.consoleParamsDefault(item.defaultValue, item.key, this.consoleParams);
        if ((item.position === 'runtime' || item.position === 'startup') && this.currentNode.params) {
          const value = this.currentNode.params.configuration[item.position][item.key] ? this.currentNode.params.configuration[item.position][item.key] : defaultValue;
          jobParams[this.poinToLink(item.key)] = value;
        } else if (item.uiType === 'Upload') {
          if (this.currentNode.jobContent && this.currentNode.jobContent.script) {
            this.resources = this.resources.filter((item) => {
              return item.fileName !== this.currentNode.jobContent.script;
            });
          } else {
            if (this.currentNode.resources) {
              this.resources = this.currentNode.resources;
            } else {
              this.resources = [];
            }
          }
        }
      })
      this.$set(this.currentNode, 'jobParams', jobParams);
    },
    currentNode: {
      handler() {
        this.paramsIsChange = true;
        this.paramsIsChangeAction();
      },
      deep: true,
    },
    paramsIsChange(val) {
      this.$emit('paramsChange', val);
    },
  },
  computed: {
    // 获取当前节点参数的基本信息
    currentNodeParamsBaseinfoList() {
      return this.currentNode.nodeUiVOS ? this.currentNode.nodeUiVOS.filter((item) => !item.baseInfo && item.nodeMenuType) : [];
    }
  },
  methods: {
    // 控制台设置的参数赋值
    consoleParamsDefault(originDefalut, key, rst) {
      let value = originDefalut;
      if (rst.length > 0) {
        const settings = rst[0].fullTree[0].settings;
        if (settings.length > 0) {
          settings.forEach((item) => {
            if (item.key === key) {
              value =  item.value || item.defaultValue;
            }
          });
          if (key === 'wds.linkis.yarnqueue') {
            value = rst[1].fullTree[0].settings[0].value || rst[1].fullTree[0].settings[0].defaultValue;
          }
        }
      }
      return value;
    },
    // linkToPoin
    linkToPoin(key) {
      return key.replace(/-/g, '.');
    },
    //降点装换成短横杠
    poinToLink(key) {
      return key.replace(/\./g, '-');
    },
    // 各参数的校验规则
    paramsValid(param) {
      // 自定义函数的方法先写这里
      // eslint-disable-next-line no-unused-vars
      const validatorTitle = (rule, value, callback) => {
        if (value === `${this.name}_`) {
          callback(new Error(this.$t('message.workflow.process.nodeParameter.JDMCBNYGZL')));
        } else {
          callback();
        }
      }
      // eslint-disable-next-line no-unused-vars
      const validateJson = (rule, value, callback) => {
        const isJsonString = (str) => {
          try {
            JSON.parse(str);
            return true;
          } catch (err) {
            return false;
          }
        };
        if (isJsonString(value)) {
          callback();
        } else {
          callback(new Error(this.$t('message.workflow.process.nodeParameter.QTXZQJSON')));
        }
      };
      const numInterval = (rule, value, callback) => {
        const reg = new RegExp(`[0-9]`);
        // 比较大小
        const valueResult = reg.test(Number(value)) && rule.range[0] <= Number(value) && rule.range[1] >= Number(value);
        if (valueResult || value === '') {
          callback();
        } else {
          callback(new Error())
        }
      };
      let temRule = [];

      if (param.nodeUiValidateVOS) {
        param.nodeUiValidateVOS.map((item) => {
          // 如果是正则类型的就写成正则
          if (item.validateType === 'Required') {
            temRule.push({
              required: true,
              message: item.message,
              trigger: item.trigger,
              type: ['MultiBinding', 'MultiSelect'].includes(param.uiType) ? 'array' : 'string'
            })
          } else if (item.validateType === 'Regex') {
            temRule.push({
              type: 'string',
              pattern: new RegExp(item.validateRange),
              message: item.message,
              trigger: item.trigger
            })
          } else if (item.validateType === 'Function') {
            temRule.push({
              // 只有和上面定义的函数名相同才让执行
              validator: ['validatorTitle', 'validateJson'].includes(item.validateRange) ? eval(item.validateRange) : () => {},
              trigger: item.trigger || 'blur'
            })
          } else if (item.validateType === 'NumInterval') {
            temRule.push({
              validator: numInterval,
              range: JSON.parse(item.validateRange),
              trigger: 'change',
              message: item.message,
            })
          }
        })
      }
      return temRule;
    },
    // 根据返回的添加去获取需要绑定的列表
    conditionBindList(param) {
      let temArry = [];
      if (param.defaultValue === 'empty') {
        temArry.push({
          name: this.$t('message.workflow.process.notBinding'),
          key: 'empty'
        })
      }
      // 对绑定的参数进行过滤
      const conditionResult = (type) => {
        if (param.value && JSON.parse(param.value)) {
          // 如果是通配符就返回true
          const optionsList = JSON.parse(param.value);
          if (optionsList[0] === '*') {
            return true;
          } else {
            return optionsList.includes(type);
          }
        }
      }
      if (this.nodes && this.nodes.length) {
        this.nodes.forEach((node) => {
          if (node.key !== this.currentNode.key && conditionResult(node.type)) {
          // 当sql节点里面没内容时,resources属性值为[]，这种sql节点不放做选项
            const tempObj = {
              name: node.title,
              key: node.key,
            }
            temArry.push(tempObj)
          }
        })
      }
      return temArry;
    },
    businessAddTag(label) {
      if (this.mybusinessTag) {
        this.mybusinessTag += `,${label}`;
      } else {
        this.mybusinessTag = label;
      }
    },
    businessDeleteTag(label) {
      const tmpArr = this.mybusinessTag.split(',');
      const index = tmpArr.findIndex((item) => item === label);
      tmpArr.splice(index, 1);
      this.mybusinessTag = tmpArr.toString();
    },
    appAddTag(label) {
      if (this.myappTag) {
        this.myappTag += `,${label}`;
      } else {
        this.myappTag = label;
      }
    },
    appdDeleteTag(label) {
      const tmpArr = this.myappTag.split(',');
      const index = tmpArr.findIndex((item) => item === label);
      tmpArr.splice(index, 1);
      this.myappTag = tmpArr.toString()
    },
    paramsIsChangeAction() {
      if (this.timer) {
        clearInterval(this.timer);
      }
      this.timer = setInterval(() => {
        if (!this.paramsIsChange) {
          clearInterval(this.timer);
        }
        this.paramsIsChange = false;
      }, 1000 * 60 * 5);
    },
    save() {
      this.resourcesAction();
      // 没有值降默认值回填
      if(this.currentNode.jobParams && this.currentNode.params.configuration) {
        this.currentNodeParamsBaseinfoList.map((item) => {
          if ((item.position === 'runtime' || item.position === 'startup') && this.currentNode.params) {
            const value = this.currentNode.jobParams[this.poinToLink(item.key)] ? this.currentNode.jobParams[this.poinToLink(item.key)] : item.defaultValue;
            this.currentNode.params.configuration[item.position][item.key] = value;
          }
        })
      }
      this.currentNode.businessTag = this.mybusinessTag;
      this.currentNode.appTag = this.myappTag;
      this.validFrom();
    },
    validFrom() {
      this.$refs.baseInfoForm.validate((baseInfoValid) => {
        if (baseInfoValid) {
          if (this.$refs.parameterForm) {
            this.$refs.parameterForm.validate((valid) => {
              if (valid) {
                this.$emit('saveNode', this.currentNode);
              } else {
                this.$Message.warning(this.$t('message.workflow.process.nodeParameter.BCSB'));
              }
            });
          } else {
            this.$emit('saveNode', this.currentNode);
          }
        } else {
          this.$Message.warning(this.$t('message.workflow.process.nodeParameter.JDBDXTXBHF'));
        }
      });
    },
    resourcesAction() {
      let resources = [];
      if (this.currentNode.jobContent && this.currentNode.jobContent.script) {
        resources = this.currentNode.resources.filter((item) => {
          return item.fileName === this.currentNode.jobContent.script;
        });
      }
      this.resources.map((item) => {
        resources.push(item);
      });
      this.currentNode.resources = resources;
    },
    updateResources(res) {
      this.resources = res.map((item) => {
        return {
          fileName: item.fileName,
          resourceId: item.resourceId,
          version: item.version,
        };
      });
      this.resourcesAction();
      this.$emit('saveNode', this.currentNode);
    },
    filterFormType(val) {
      switch (val) {
        case 'Text':
          return 'textarea';
        default:
          return 'text';
      }
    },
  },
};

</script>
