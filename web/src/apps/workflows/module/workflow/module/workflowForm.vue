<template>
  <Modal
    v-model="ProjectShow"
    :title="actionType === 'add' ? $t('message.orchestratorModes.createOrchestrator') : $t('message.orchestratorModes.modeifyOrchestrator')"
    :closable="false">
    <Form
      :label-width="100"
      label-position="left"
      ref="projectForm"
      :model="workflowDataCurrent"
      :rules="formValid"
      v-if="workflowDataCurrent">
      <FormItem
        :label="$t('message.orchestratorModes.orchestratorName')"
        prop="orchestratorName">
        <Input
          v-model="workflowDataCurrent.orchestratorName"
          :placeholder="$t('message.workflow.inputFlowName')"
        ></Input>
      </FormItem>
      <FormItem :label="$t('message.orchestratorModes.orchestratorMode')" prop="orchestratorMode">
        <Select v-model="workflowDataCurrent.orchestratorMode">
          <Option v-for="item in selectOrchestratorList" :key="item.dicKey" :value="item.dicKey">
            {{ item.dicName}}
          </Option>
        </Select>
      </FormItem>
      <!-- 不同的编排类型显示不同的编排方式， 动态接口获取 -->
      <template v-if="workflowDataCurrent.orchestratorMode">
        <FormItem v-if="selectOrchestrator.dicValue === FORMITEMTYPE.RADIO" :label="$t('message.orchestratorModes.orchestratorMethod')" prop="orchestratorWayString"
          :rules="[{ required: true, trigger: 'blur' }]">
          <RadioGroup v-model="workflowDataCurrent.orchestratorWayString">
            <Radio v-for="item in orchestratorModeList.mapList[workflowDataCurrent.orchestratorMode]" :key="item.dicKey" :label="item.dicKey">
              <span>{{item.dicName}}</span>
            </Radio>
          </RadioGroup>
        </FormItem>
        <FormItem :label="$t('message.orchestratorModes.orchestratorMethod')" v-if="selectOrchestrator.dicValue === FORMITEMTYPE.SELECT" prop="orchestratorWayString" :rules="[{ required: true, trigger: 'blur' }]">
          <Select v-model="workflowDataCurrent.orchestratorWayString">
            <Option v-for="item in orchestratorModeList.mapList[workflowDataCurrent.orchestratorMode]" :key="item.dicKey" :value="item.dicKey">
              {{ item.dicName}}
            </Option>
          </Select>
        </FormItem>
        <FormItem :label="$t('message.orchestratorModes.orchestratorMethod')" v-if="selectOrchestrator.dicValue === FORMITEMTYPE.CHECKBOX" prop="orchestratorWayArray" :rules="[{ required: true, trigger: 'blur', type: 'array' }]">
          <CheckboxGroup v-model="workflowDataCurrent.orchestratorWayArray">
            <Checkbox v-for="item in orchestratorModeList.mapList[workflowDataCurrent.orchestratorMode]" :label="item.dicKey" :key="item.dicKey">
              <span style="margin-left: 10px">{{item.dicName}}</span>
            </Checkbox>
          </CheckboxGroup>
        </FormItem>
      </template>
      <FormItem
        :label="$t('message.workflow.use')"
        prop="uses">
        <we-tag
          :new-label="$t('message.workflow.addUse')"
          :tag-list="workflowDataCurrent.uses"
          @add-tag="addTag"
          @delete-tag="deleteTag"></we-tag>
      </FormItem>
      <FormItem
        :label="$t('message.workflow.workflowDesc')"
        prop="description">
        <Input
          v-model="workflowDataCurrent.description"
          type="textarea"
          :maxlength=201
          :placeholder="$t('message.workflow.inputWorkflowDesc')"></Input>
      </FormItem>
    </Form>
    <div slot="footer">
      <Button
        type="text"
        size="large"
        @click="Cancel">{{$t('message.workflow.cancel')}}</Button>
      <Button
        type="primary"
        size="large"
        @click="Ok">{{$t('message.workflow.ok')}}</Button>
    </div>
  </Modal>
</template>
<script>
import tag from '@component/tag/index.vue'
const FORMITEMTYPE = {
  RADIO: 'radio',
  SELECT: 'select',
  CHECKBOX: 'checkbox'
}
export default {
  components: {
    'we-tag': tag,
  },
  props: {
    workflowData: {
      type: Object,
      default: null,
    },
    addProjectShow: {
      type: Boolean,
      default: false,
    },
    actionType: {
      type: String,
      default: '',
    },
    projectData: {
      type: Object,
      default: () => {}
    },
    orchestratorModeList: {
      type: Object,
      default: () => {}
    },
    selectOrchestratorList: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      ProjectShow: false,
      originBusiness: '',
      FORMITEMTYPE
    };
  },
  computed: {
    formValid() {
      return {
        orchestratorName: [
          { required: true, message: this.$t('message.workflow.enterName'), trigger: 'blur' },
          { message: `${this.$t('message.workflow.nameLength')}128`, max: 128 },
          { type: 'string', pattern: /^[a-zA-Z][a-zA-Z0-9_]*$/, message: this.$t('message.workflow.validNameDesc'), trigger: 'blur' },
        ],
        description: [
          { required: true, trigger: 'blur' },
          { message: `${this.$t('message.workflow.nameLength')}200`, max: 200 },
        ],
        orchestratorMode: [
          { required: true, trigger: 'blur' }
        ]
      }
    },
    // selectOrchestratorList() {
    //   return this.orchestratorModeList.list;
    // },
    selectOrchestrator() {
      return this.orchestratorModeList.list.find((item) => item.dicKey === this.workflowDataCurrent.orchestratorMode);
    },
    workflowDataCurrent() {
      const currentData = this.workflowData;
      if (this.workflowData && this.workflowData.orchestratorWays && this.orchestratorModeList.list.length > 0) {
        if ([this.FORMITEMTYPE.RADIO, this.FORMITEMTYPE.SELECT].includes(this.orchestratorModeList.list.find((item) => item.dicKey === this.workflowData.orchestratorMode).dicValue)) {
          currentData.orchestratorWayString = this.workflowData.orchestratorWays[0];
        } else {
          currentData.orchestratorWayArray = this.workflowData.orchestratorWays;
        }
      }
      return currentData;
    }
  },
  watch: {
    addProjectShow(val) {
      this.ProjectShow = val;
    },
    ProjectShow(val) {
      if (val) {
        this.originBusiness = this.workflowDataCurrent.uses;
      }
      this.$emit('show', val);
    },
  },
  methods: {
    // 处理编排模式值类型不同方法,最终只存数组
    modeValueTypeChange(workflowDataCurrent) {
      let currentData = JSON.parse(JSON.stringify(workflowDataCurrent));
      if ([FORMITEMTYPE.RADIO, FORMITEMTYPE.SELECT].includes(this.selectOrchestrator.dicValue)) {
        currentData.orchestratorWays = [currentData.orchestratorWayString];
        delete currentData.orchestratorWayString;
      } else {
        currentData.orchestratorWays = currentData.orchestratorWayArray;
        delete currentData.orchestratorWayArray;
      }
      return currentData;
    },
    Ok() {
      this.$refs.projectForm.validate((valid) => {
        if (valid) {
          this.$emit('confirm', this.modeValueTypeChange(this.workflowDataCurrent));
          this.ProjectShow = false;
        } else {
          this.$Message.warning(this.$t('message.workflow.failedNotice'));
        }
      });
    },
    Cancel() {
      this.ProjectShow = false;
      this.workflowDataCurrent.uses = this.originBusiness;
    },
    addTag(label) {
      if (this.workflowDataCurrent.uses) {
        this.workflowDataCurrent.uses += `,${label}`;
      } else {
        this.workflowDataCurrent.uses = label;
      }
    },
    deleteTag(label) {
      const tmpArr = this.workflowDataCurrent.uses.split(',');
      const index = tmpArr.findIndex((item) => item === label);
      tmpArr.splice(index, 1);
      this.workflowData.uses = tmpArr.toString();
    }
  },
};
</script>
