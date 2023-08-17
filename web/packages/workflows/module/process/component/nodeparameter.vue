<template>
  <div>
    <Form label-position="top" ref="baseInfoForm" :model="currentNode" class="node-parameter-bar">
      <template v-for="item in curNodeBaseParamsList">
        <FormItem v-if="['Input', 'Text', 'Disable'].includes(item.uiType)" :rules="paramsValid(item)" :key="poinToLink(item.key)" :label="item.lableName" :prop="item.key">
          <Input v-model="currentNode[item.key]" :type="filterFormType(item.uiType)" :rows="6"
            :placeholder="item.desc" :disabled="item.uiType === 'Disable'"
          />
        </FormItem>
        <FormItem
          v-if="item.uiType === 'Tag'"
          :label="item.lableName"
          :key="poinToLink(item.key)"
          :prop="item.key">
          <we-tag
            :new-label="$t('message.workflow.process.nodeParameter.addLabel')"
            :tag-list="currentNode[item.key]"
            @add-tag="addTag(item.key,$event)"
            @delete-tag="deleteTag(item.key,$event)"></we-tag>
        </FormItem>
        <FormItem v-if="item.uiType === 'Select' || item.uiType === 'MultiSelect'" :rules="paramsValid(item)" :key="poinToLink(item.key)" :label="item.lableName" :prop="item.key">
          <Select
            v-model="currentNode[item.key]"
            :placeholder="item.desc"
            :multiple="item.uiType === 'MultiSelect'">
            <Option v-for="subItem in JSON.parse(item.value)" :value="subItem" :key="subItem">{{subItem}}</Option>
          </Select>
        </FormItem>
        <FormItem v-if="item.uiType === 'MultiBinding'" :rules="paramsValid(item)" :key="poinToLink(item.key)" :label="item.lableName" :prop="item.key">
          <Select
            v-model="currentNode[item.key]"
            :placeholder="item.desc"
            multiple>
            <Option v-for="(subItem,index) in conditionBindList(item)" :value="subItem.key" :key="index">{{ subItem.name }}</Option>
          </Select>
        </FormItem>
      </template>
    </Form>
    <div v-if="curNodeParamsList.length > 0" class="node-module-param-modal-header">
      <h5>{{$t('message.workflow.process.nodeParameter.SXXX')}}</h5>
    </div>
    <Form v-if="curNodeParamsList.length > 0 && currentNode.jobParams" label-position="top"
      ref="parameterForm"  class="node-parameter-bar" :model="currentNode">
      <template v-for="item in curNodeParamsList">
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
import weTag from '@dataspherestudio/shared/components/tag/index.vue'
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
    return {
      test: [],
      currentNode: {
      },
      resources: []
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
      // 在节点没有保存参数时，需要设置默认值的值,先判断保存的值应该存在哪里starup或者runtime或者是node下
      let jobParams = {};
      this.curNodeParamsList.map((item) => {
        // 多选框是数组值
        const defaultValue = ['MultiBinding'].includes(item.uiType) ? JSON.parse(item.defaultValue) : this.consoleParamsDefault(item.defaultValue, item.key, this.consoleParams);
        if ((item.position === 'runtime' || item.position === 'startup') && this.currentNode.params) {
          const value = this.currentNode.params.configuration[item.position][item.key] ? this.currentNode.params.configuration[item.position][item.key] : defaultValue;
          jobParams[this.poinToLink(item.key)] = value;
        } else if (item.uiType === 'Upload') {
          if (this.currentNode.resources && this.currentNode.resources.length) {
            this.resources = [...this.currentNode.resources]
            if (this.currentNode.jobContent && this.currentNode.jobContent.script) {
              this.resources = this.currentNode.resources.filter((item) => {
                return item.fileName !== this.currentNode.jobContent.script;
              });
            }
          } else {
            this.resources = [];
          }
        }
      })
      this.$set(this.currentNode, 'jobParams', jobParams);
    }
  },
  computed: {
    // 获取当前节点参数的基本信息
    curNodeParamsList() {
      return this.currentNode.nodeUiVOS ? this.currentNode.nodeUiVOS.filter((item) => !item.baseInfo && item.nodeMenuType) : [];
    },
    curNodeBaseParamsList() {
      return this.currentNode.nodeUiVOS ? this.currentNode.nodeUiVOS.filter((item) => {
        const fields = ['appTag','businessTag','title','desc'].indexOf(item.key) > -1
        return item.baseInfo && fields// && !item.hidden
      }) : [];
    }
  },
  methods: {
    // 控制台设置的参数赋值
    consoleParamsDefault(originDefalut, key, rst) {
      let value = originDefalut;
      if (rst.length > 0) {
        rst[0].fullTree.forEach(item =>{
          if (item && item.settings.length > 0) {
            item.settings.forEach((it) => {
              if (it.key === key) {
                value =  it.configValue || it.defaultValue;
              }
            });
          }
        })
        if (rst[1].fullTree[0] && key === 'wds.linkis.rm.yarnqueue') {
          value = rst[1].fullTree[0].settings[0].configValue || rst[1].fullTree[0].settings[0].defaultValue;
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
        if (value === `${this.name}`) {
          callback(new Error(rule.message));
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
              message: item.message,
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
    addTag(key, label) {
      if (this.currentNode[key]) {
        this.$set(this.currentNode, key, this.currentNode[key] + `,${label}`);
      } else {
        this.$set(this.currentNode, key, label);
      }
    },
    deleteTag(key, label) {
      const tmpArr = this.currentNode[key].split(',');
      const index = tmpArr.findIndex((item) => item === label);
      tmpArr.splice(index, 1);
      this.$set(this.currentNode, key, tmpArr.toString());
    },

    save() {
      this.resourcesAction();
      // 没有值降默认值回填
      if(this.currentNode.jobParams && this.currentNode.params.configuration) {
        this.curNodeParamsList.map((item) => {
          if ((item.position === 'runtime' || item.position === 'startup') && this.currentNode.params) {
            const value = this.currentNode.jobParams[this.poinToLink(item.key)] ? this.currentNode.jobParams[this.poinToLink(item.key)] : item.defaultValue;
            this.currentNode.params.configuration[item.position][item.key] = value;
          }
        })
      }
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
      const mapFlag = {}
      if (this.currentNode.jobContent && this.currentNode.jobContent.script) {
        this.currentNode.resources.forEach(item => {
          if (item.fileName === this.currentNode.jobContent.script && !mapFlag[item.fileName]) {
            resources.push(item)
            mapFlag[item.fileName] = 1
          }
        })
      }
      this.resources.forEach((item) => {
        if (!mapFlag[item.fileName]) {
          resources.push(item);
        }
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
<style>
.node-parameter-bar label.ivu-form-item-label {
  text-align: left;
  line-height: 14px;
  padding-bottom: 6px;
}
</style>
