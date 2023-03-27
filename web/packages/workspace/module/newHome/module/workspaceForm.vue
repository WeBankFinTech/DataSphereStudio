<template>
  <Modal
    v-model="ProjectShow"
    :title="actionType === 'add' ? $t('message.workspace.newWorkspace') : $t('message.workspace.editorWorkspace')"
    :closable="false">
    <Form
      :label-width="100"
      label-position="left"
      ref="projectForm"
      :model="projectDataCurrent"
      :rules="formValid"
      v-if="ProjectShow">
      <FormItem
        :label="$t('message.workspace.workName')"
        prop="name">
        <Input
          v-model="projectDataCurrent.name"
          :maxlength=21
          :placeholder="$t('message.workspace.enterName')"
          :disabled="actionType === 'modify'" />
      </FormItem>
      <FormItem
        :label="$t('message.workspace.workspaceType')"
        prop="workspace_type">
        <RadioGroup v-model="projectDataCurrent.workspace_type">
          <Radio label="project" :disabled="actionType === 'modify'">
            <span>{{$t('message.workspace.projectOrientation')}}</span>
          </Radio>
          <Radio label="department" :disabled="actionType === 'modify'">
            <span>{{$t('message.workspace.departmentOrientation')}}</span>
          </Radio>
        </RadioGroup>
      </FormItem>
      <FormItem
        v-if="projectDataCurrent.workspace_type === 'department'"
        :label="$t('message.workspace.department')"
        prop="department">
        <treeselect
          v-model="projectDataCurrent.department"
          :placeholder="$t('message.workspace.selectDepartment')"
          searchable
          :options="treeDepartments" />
        <!-- <Select
          v-model="projectDataCurrent.department"
          :placeholder="$t('message.workspace.selectDepartment')">
          <Option
            v-for="(item, index) in departments"
            :label="item.deptName"
            :value="String(item.id)"
            :key="index"/>
        </Select> -->
      </FormItem>
      <FormItem
        :label="$t('message.workspace.label')"
        prop="label">
        <we-tag
          :new-label="$t('message.workspace.addLabel')"
          :tag-list="projectDataCurrent.label"
          @add-tag="addTag"
          @delete-tag="deleteTag"></we-tag>
      </FormItem>
      <FormItem
        :label="$t('message.workspace.description')"
        prop="description">
        <Input
          v-model="projectDataCurrent.description"
          type="textarea"
          :maxlength=200
          :placeholder="$t('message.workspace.pleaseInputWorkspaceDesc')" />
      </FormItem>
    </Form>
    <div slot="footer">
      <Button
        type="text"
        size="large"
        @click="Cancel">{{$t('message.workspace.cancel')}}</Button>
      <Button
        type="primary"
        size="large"
        :disabled="submiting"
        :loading="submiting"
        @click="Ok">{{$t('message.workspace.ok')}}</Button>
    </div>
  </Modal>
</template>
<script>
import tag from '@dataspherestudio/shared/components/tag/index.vue';
import { GetDepartments, CheckWorkspaceNameExist, GetTreeDepartments } from '@dataspherestudio/shared/common/service/apiCommonMethod.js'
// import the component
import Treeselect from '@riophae/vue-treeselect'
// import the styles
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
import util from '@dataspherestudio/shared/common/util';
export default {
  components: {
    'we-tag': tag,
    Treeselect
  },
  props: {
    projectData: {
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
  },
  data() {
    return {
      submiting: false,
      ProjectShow: false,
      departments: [],
      treeDepartments: [],
      projectDataCurrent: {},
    };
  },
  computed: {
    formValid() {
      return {
        name: [
          { required: true, message: this.$t('message.workspace.enterName'), trigger: 'blur' },
          { type: 'string', pattern: /^[a-zA-Z][a-zA-Z0-9_]*$/, message: this.$t('message.workspace.validNameDesc'), trigger: 'blur' },
          { validator: this.checkNameExist, message: this.$t('message.workspace.validNameExist'), trigger: 'blur' },
        ],
        description: [
          { required: true, message: this.$t('message.workspace.pleaseInputWorkspaceDesc'), trigger: 'blur' },
          { message: `${this.$t('message.workspace.nameLength')}200`, max: 200 },
        ],
        workspace_type: [
          { required: true, message: this.$t('message.workspace.selectWorkspaceType'), trigger: 'change' },
        ]
      }
    }
  },
  mounted() {
    GetDepartments().then((res) => {
      this.departments = res.deptList;
    })
    GetTreeDepartments().then(res => {
      let list = res.deptTree
      list && list.length === 1 ? util.deleteEmptyChildren(list[0]) : ''
      this.treeDepartments = list[0] ? list[0].children : list
    })
  },
  watch: {
    addProjectShow(val) {
      this.ProjectShow = val;
    },
    ProjectShow(val) {
      this.$emit('show', val);
    },
    projectData(value){
      this.projectDataCurrent = {
        ...value,
        department: !value.department ? null : value.department
      }
    }
  },
  methods: {
    checkNameExist(rule, value, callback) {
      CheckWorkspaceNameExist({ name: value }).then((res) => {
        if (res.workspaceNameExists) {
          callback(new Error(this.$t('message.workspace.Not')));
        } else {
          callback();
        }
      });
    },
    Ok() {
      this.$refs.projectForm.validate((valid) => {
        if (valid) {
          if (this.projectDataCurrent.workspace_type === 'department' && !this.projectDataCurrent.department) {
            return this.$Message.error(this.$t('message.workspace.selectDepartment'));
          }
          this.submiting = true;
          this.$emit('confirm', this.projectDataCurrent, () => {
            this.ProjectShow = false;
            this.submiting = false;
          });
        } else {
          this.$Message.warning(this.$t('message.workspace.failedNotice'));
        }
      });
    },
    Cancel() {
      this.ProjectShow = false;
    },
    addTag(label) {
      if (this.projectDataCurrent.label) {
        this.projectDataCurrent.label += `,${label}`;
      } else {
        this.projectDataCurrent.label = label;
      }
    },
    deleteTag(label) {
      const tmpArr = this.projectDataCurrent.label.split(',');
      const index = tmpArr.findIndex((item) => item === label);
      tmpArr.splice(index, 1);
      this.projectDataCurrent.label = tmpArr.toString();
    }
  },
};
</script>
