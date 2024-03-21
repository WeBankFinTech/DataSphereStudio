<template>
  <div class="main-content">
    <div class="formWrap">
      <Form ref="queries" :model="queries" inline>
        <FormItem
          :label="$t('message.permissions.departName')"
          :label-width="100"
        >
          <Input
            type="text"
            v-model="queries.deptName"
            :placeholder="$t('message.permissions.departPlaceholder')"
            style="width: 272px"
            @on-enter="handleQuery()"
          >
          </Input>
        </FormItem>
        <FormItem>
          <Button @click="handleReset()" style="margin-left: 20px">{{
            $t("message.permissions.reset")
          }}</Button>
          <Button
            type="primary"
            @click="handleQuery()"
            style="margin-left: 8px"
          >{{ $t("message.permissions.query") }}</Button
          >
        </FormItem>
      </Form>
    </div>
    <div class="tableWrap">
      <div class="addWrap">
        <Button type="primary" @click="handleAdd()" icon="md-add">{{
          $t("message.permissions.add")
        }}</Button>
      </div>
      <Table
        :columns="columns"
        :row-class-name="rowClassName"
        :data="departmentList"
        :loading="loading"
      >
        <template slot-scope="{ row, index }" slot="deptName">
          <div
            class="deptName"
            :style="{
              paddingLeft:
                30 * ((row[idChain] && row[idChain].length) || 0) + 'px'
            }"
          >
            <div
              class="iconWrap"
              v-if="row.children && row.children.length > 0"
              @click="handleFold(row, index)"
            >
              <Icon
                :type="!row.unfold ? 'ios-arrow-forward' : 'ios-arrow-up'"
              />
            </div>
            <div>{{ row.deptName }}</div>
          </div>
        </template>
        <template slot-scope="{ row, index }" slot="operation">
          <div class="deptName">
            <div class="operation" @click="edit(row, index)">
              {{ $t("message.permissions.edit") }}
            </div>
            <Divider v-if="!!row[idChain]" type="vertical" />
            <Poptip
              confirm
              :title="$t('message.permissions.deleteTip')"
              @on-ok="() => deleteRow(row, index)"
            >
              <div class="operation" v-if="!!row[idChain]">
                {{ $t("message.permissions.delete") }}
              </div></Poptip
            >
          </div>
        </template>
      </Table>
    </div>
    <Modal
      v-model="modalVisible"
      :title="modalTitle"
      @on-cancel="handleModalCancel"
      footer-hide
    >
      <Form ref="parentForm" :label-width="100" :rules="ruleValidate">
        <FormItem
          :label="$t('message.permissions.superior')"
          v-if="!editingData"
          :error="parentErrorTip"
          required
        >
          <treeselect
            v-model="departForm.parentId"
            :options="departTree"
            :placeholder="
              $t('message.permissions.pleaseInput') +
                $t('message.permissions.superior') +
                $t('message.permissions.forSelect')
            "
            :defaultExpandLevel="Infinity"
            search-nested
            required
            style="width: 300px"
            :noResultsText="$t('message.permissions.noMatch')"
            @close="parentChange"
            @select="checkParentValid"
          >
            <div
              slot="value-label"
              slot-scope="{ node }"
              v-if="node.id || node.id === 0"
            >
              {{ node.id || node.id === 0 ? getParentName(node) : node.id }}
            </div>
            <label slot="option-label" slot-scope="{ node }">
              <Icon
                style="font-size: 16px;margin-right:5px;top: -3px;"
                :type="
                  node.isBranch ? 'ios-folder-outline' : 'ios-paper-outline'
                "
              /><span>{{ node.label }}</span>
            </label>
          </treeselect>
        </FormItem>
      </Form>
      <Form
        ref="departForm"
        :model="departForm"
        :label-width="100"
        :rules="ruleValidate"
      >
        <FormItem
          :label="$t('message.permissions.superior')"
          v-if="editingData && editingData.level !== 0"
          required
          prop="parentName"
        >
          <Input
            type="text"
            v-model="departForm.parentName"
            :placeholder="$t('message.permissions.pleaseInput')"
            style="width: 300px"
            v-if="!editingData"
          >
          </Input>
          <div v-if="!!editingData">{{ departForm.parentName }}</div>
        </FormItem>
        <FormItem
          :label="
            departmentList.length > 0
              ? $t('message.permissions.deptName')
              : $t('message.permissions.company')
          "
          prop="deptName"
        >
          <Input
            type="text"
            v-model="departForm.deptName"
            :placeholder="
              $t('message.permissions.pleaseInput') +
                (departmentList.length > 0
                  ? $t('message.permissions.deptName')
                  : $t('message.permissions.company'))
            "
            style="width: 300px"
          >
          </Input>
        </FormItem>

        <FormItem :label="$t('message.permissions.createBy')" prop="leader">
          <Input
            type="text"
            v-model="departForm.leader"
            :placeholder="$t('message.permissions.pleaseInput')"
            style="width: 300px"
          >
          </Input>
        </FormItem>
        <FormItem :label="$t('message.permissions.phone')" prop="phone">
          <Input
            type="text"
            v-model="departForm.phone"
            :placeholder="$t('message.permissions.pleaseInput')"
            style="width: 300px"
          >
          </Input>
        </FormItem>
        <FormItem :label="$t('message.permissions.email')" prop="email">
          <Input
            type="text"
            v-model="departForm.email"
            :placeholder="$t('message.permissions.pleaseInput')"
            style="width: 300px"
          >
          </Input>
        </FormItem>
      </Form>
      <slot name="footer">
        <div class="modalFooter">
          <Button
            @click="handleModalCancel()"
            size="large"
            class="cancle-btn"
          >{{ $t("message.workspace.cancel") }}</Button
          >
          <Button
            type="primary"
            size="large"
            :loading="confirmLoading"
            @click="handleModalOk()"
            style="margin-left: 10px"
          >{{ $t("message.workspace.ok") }}</Button
          >
        </div></slot
      >
    </Modal>
  </div>
</template>
<script>
import _ from "lodash";
import Treeselect from "@riophae/vue-treeselect";
import moment from "moment";
import {
  GetDepartmentList,
  GetDepartmentTree,
  AddNewDepartment,
  ModifyDepartment,
  DeleteDepartment
} from '@dataspherestudio/shared/common/service/permissions';
import {
  ID_CHAIN,
  addIdChain,
  expandAll,
  getParentDepartName,
  removeEmptyChildren,
  keepTreeNLevel
} from "../util";

/**
 * 构建表格所需的树形结构
 *
 */
function assembleTree(level, datas, result) {
  if (level !== 0) {
    const nexts = datas.filter(item => item.level !== level);
    const children = datas.filter(item => item.level === level);
    children.forEach(item => {
      const parentId = item.parentId;
      const hit = nexts.find(ni => ni.id === parentId);
      if (hit.children) {
        hit.children.push({ ...item });
      } else {
        hit.children = [{ ...item }];
      }
    });
    assembleTree(level - 1, nexts, result);
  } else {
    console.log(datas);
    result.data = [...datas];
  }
}
export default {
  components: { Treeselect },
  data() {
    const validateDepartNameCheck = (rule, value, callback) => {
      if (!value) {
        callback(new Error(this.$t("message.permissions.deptEmpty")));
      } else if (value.length > 50) {
        callback(new Error(this.$t("message.permissions.deptTooLong")));
      } else {
        callback();
      }
    };
    const validateEmail = (rule, value, callback) => {
      if (value) {
        const valid = /^\w+@[a-z0-9]+\.[a-z]{2,4}$/.test(value);
        if (valid) {
          callback();
        } else {
          callback(new Error(this.$t("message.permissions.invalidEmail")));
        }
      } else {
        callback();
      }
    };
    const validatePhone = (rule, value, callback) => {
      if (value) {
        const valid = /^1[3456789]\d{9}$/.test(value);
        if (valid) {
          callback();
        } else {
          callback(new Error(this.$t("message.permissions.invalidPhone")));
        }
      } else {
        callback();
      }
    };
    return {
      queries: {
        deptName: ""
      },
      idChain: ID_CHAIN,
      columns: [
        {
          title: this.$t("message.permissions.deptName"),
          key: "deptName",
          slot: "deptName"
        },
        {
          title: this.$t("message.permissions.level"),
          key: "level"
        },
        {
          title: this.$t("message.permissions.createBy"),
          key: "leader"
        },
        {
          title: this.$t("message.permissions.phone"),
          key: "phone"
        },
        {
          title: this.$t("message.permissions.email"),
          key: "email"
        },
        {
          title: this.$t("message.permissions.createTime"),
          key: "createTime"
        },
        {
          title: this.$t("message.permissions.operation"),
          key: "operation",
          slot: "operation",
          fixed: "right"
        }
      ],
      departmentList: [],
      departmentListOrigin: [],
      loading: false,
      departTree: [],
      confirmLoading: false,
      modalVisible: false,
      modalTitle: "",
      departForm: {
        parentId: "",
        deptName: "",
        leader: "",
        phone: "",
        email: ""
      },
      parentErrorTip: "",
      ruleValidate: {
        deptName: [
          {
            required: true,
            validator: validateDepartNameCheck,
            trigger: "blur"
          }
        ],
        phone: [
          {
            validator: validatePhone,
            trigger: "blur"
          }
        ],
        email: [
          {
            validator: validateEmail,
            trigger: "blur"
          }
        ]
      },
      editingData: ""
    };
  },
  mounted() {
    this.getDepartmentList();
  },
  methods: {
    getDepartmentList(query = "", isQuery = false) {
      this.loading = true;
      GetDepartmentList(query)
        .then(data => {
          this.loading = false;
          const depts = (data && data.deptList) || [];
          if (isQuery && query) {
            if (depts.length == 0) {
              this.departmentList = [];
              return;
            }
            const originList = _.cloneDeep(this.departmentListOrigin);
            const temp = expandAll(originList);
            depts.forEach(item => {
              const index = temp.findIndex(t => t.id === item.id);
              if (index !== -1) {
                temp[index].__HITED__ = true;
              }
            });
            this.departmentList = temp;

            return;
          }

          const departs = [];
          let MAX_LEVEL = 0;
          depts.map(item => {
            const dept = { ...item };
            const level = dept.ancestors.split(",").length - 1;
            if (dept.createTime) {
              dept.createTime = moment(dept.createTime).format(
                "YYYY-MM-DD HH:mm:ss"
              );
            }
            dept.level = level;
            MAX_LEVEL = level > MAX_LEVEL ? level : MAX_LEVEL;
            departs.push(dept);
          });
          let result = {};
          assembleTree(MAX_LEVEL, departs, result);
          const departmentList = result.data;
          departmentList.forEach(item => addIdChain(item, "id"));
          this.departmentListOrigin = [...departmentList];
          this.departmentList = expandAll(departmentList);
        })
        .catch(e => {
          console.log(e);
          this.loading = false;
        });
    },
    handleReset() {
      this.queries = {
        deptName: ""
      };
      this.getDepartmentList();
    },
    handleQuery() {
      console.log("qeuery");
      const deptName = this.queries.deptName;
      const query = deptName ? encodeURI(`?deptName=${deptName}`) : "";
      this.getDepartmentList(query, true);
    },
    handleAdd() {
      GetDepartmentTree({}).then(data => {
        const tree = (data && data.deptTree) || [];
        keepTreeNLevel(tree, 3);
        removeEmptyChildren(tree);
        this.editingData = "";
        this.departTree = tree;
        this.modalTitle =
          tree && tree.length > 0
            ? this.$t("message.permissions.addDepartment")
            : this.$t("message.permissions.addCompany");
        this.modalVisible = true;
        this.departForm.parentId = (tree[0] && tree[0].id) || "";
      });
    },
    handleFold(rowData, index) {
      if (rowData.unfold) {
        console.log(index);
        const id = rowData.id;
        const result = this.departmentList.filter(item => {
          if (item[ID_CHAIN]) {
            return !item[ID_CHAIN].includes(id);
          } else {
            return true;
          }
        });
        result[index]["unfold"] = false;
        this.departmentList = result;
      } else {
        const newAr = _.cloneDeep(this.departmentList);
        const ar1 = newAr.slice(0, index + 1);
        const ar2 = newAr.slice(index + 1);
        const children = _.cloneDeep(rowData.children);
        const result = [...ar1, ...children, ...ar2];
        result[index].unfold = true;
        this.departmentList = result;
      }
    },
    edit(rowData) {
      this.editingData = rowData;
      this.modalTitle = this.$t("message.permissions.editDepartment");
      this.modalVisible = true;
      const newObj = {};
      Object.keys(this.departForm).forEach(key => {
        if (key === "leader") {
          newObj[key] = rowData.createBy;
        }
        if (rowData[key]) {
          newObj[key] = rowData[key];
        }
      });
      const names = [];
      if (rowData[ID_CHAIN]) {
        rowData[ID_CHAIN].forEach(item => {
          const hit = this.departmentList.find(r => r.id === item);
          names.push(hit.deptName);
        });
      }
      newObj.parentName = names.join(" - ") || "—";
      this.departForm = newObj;
    },
    deleteRow(rowData) {
      if (rowData.children && rowData.children.length > 0) {
        this.$Message.error({
          content: this.$t("message.permissions.deleteUnvalidTip"),
          duration: 10,
          closable: true
        });
      }
      this.loading = true;
      DeleteDepartment(rowData.id)
        .then(() => {
          this.loading = false;
          this.$Message.success({
            content: this.$t("message.workspaceManagement.deleteSuccess"),
            duration: 10,
            closable: true
          });
          this.getDepartmentList();
        })
        .catch(() => {
          this.loading = false;
        });
    },
    parentChange(value) {
      console.log(value);
      this.parentErrorTip =
        value !== undefined ? "" : this.$t("message.permissions.parentIdEmpty");
    },
    checkParentValid(node) {
      console.log(node);
    },
    getParentName(data) {
      return getParentDepartName(data, this.departTree);
    },
    handleModalCancel() {
      this.modalVisible = false;
      this.resetDepartForm();
    },
    handleModalOk() {
      this.$refs["departForm"].validate(valid => {
        const isAdd = !this.editingData;
        console.log(this.departForm);
        const parentId = this.departForm.parentId;
        if (isAdd && !parentId && parentId !== 0) {
          this.parentErrorTip = this.$t("message.permissions.deptEmpty");
          return;
        }
        if (valid) {
          const keys = ["parentId", "deptName", "leader", "phone", "email"];
          const executeMethod = isAdd ? AddNewDepartment : ModifyDepartment;
          const params = {};
          keys.forEach(key => {
            params[key] =
              key !== "parentId" && this.departForm[key]
                ? this.departForm[key].trim()
                : this.departForm[key];
          });
          if (!isAdd) {
            params.id = this.editingData.id;
            params.parentId = params.parentId ? params.parentId : 0;
          }
          this.confirmLoading = true;
          executeMethod(params)
            .then(() => {
              this.$Message.success(
                isAdd
                  ? this.$t("message.permissions.addDeptSucess")
                  : this.$t("message.permissions.updateDeptSucess")
              );
              this.getDepartmentList();
              this.resetDepartForm();
              this.confirmLoading = false;
              this.modalVisible = false;
              this.queries = {
                deptName: ""
              };
            })
            .catch(() => {
              this.confirmLoading = false;
            });
        }
      });
    },
    resetDepartForm() {
      this.$refs["departForm"].resetFields();
      this.parentErrorTip = "";
      this.editingData = "";
      this.departForm = {
        parentId: "",
        deptName: "",
        leader: "",
        phone: "",
        email: ""
      };
    },
    rowClassName(row) {
      if (row.__HITED__) {
        return "table-department-hit";
      }
      return "";
    }
  }
};
</script>
<style lang="scss" scoped>
@import "@dataspherestudio/shared/common/style/variables.scss";
.main-content {
  padding-top: 20px;
  @include bg-color($workspace-body-bg-color, $dark-workspace-body-bg-color);
  border: 1px solid $border-color-base;
  @include border-color($border-color-base, $dark-workspace-background);
  .formWrap {
    width: 100%;
    // padding: 20px;
    // padding-bottom: 0px;
    border-bottom: 1px solid #dee4ec;
    @include border-color($border-color-base, $dark-border-color-base);
    ::v-deep.ivu-form-item-label {
      @include font-color($workspace-title-color, $dark-workspace-title-color);
    }
    ::v-deep.ivu-input {
      @include bg-color(
        $workspace-body-bg-color,
        $dark-workspace-body-bg-color
      );
      border: 1px solid $border-color-base;
      @include border-color($border-color-base, $dark-border-color-base);
    }
    .cancle-btn {
      @include border-color($border-color-base, $dark-border-color-base);
      @include font-color($light-text-color, #fff);
      &:hover {
        @include bg-color($active-menu-item, $dark-active-menu-item);
      }
    }
  }
  .tableWrap {
    margin: 0 24px 24px;
    .addWrap {
      display: flex;
      justify-content: flex-end;
      align-items: center;
      width: 100%;
      height: 72px;
    }
  }
  .deptName {
    display: flex;
    justify-content: flex-start;
    align-items: center;
  }
  .iconWrap {
    display: flex;
    justify-content: flex-start;
    align-items: center;
    width: 30px;
    height: 30px;
    font-size: 20px;
    cursor: pointer;
  }
  .operation {
    font-size: 14px;
    // color: #2e92f7;
    @include font-color($primary-color, $dark-primary-color);
    cursor: pointer;
  }
  .modalFooter {
    display: flex;
    justify-content: flex-end;
    align-items: center;
    border-top: 1px solid rgba(0, 0, 0, 0.05);
    padding-top: 10px;
  }
}
</style>
<style>
.ivu-table .table-department-hit td {
  background-color: rgb(45, 183, 245, 0.1);
}
</style>
