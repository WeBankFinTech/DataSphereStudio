<template>
  <div class="container">
    <div class="formWrap">
      <div class="formItemWrap">
        <p>{{ $t("message.permissions.login") }}</p>
        <Input
          type="text"
          v-model="queries.userName"
          :placeholder="
            $t('message.permissions.pleaseInput') +
              $t('message.permissions.login')
          "
          style="width: 272px"
          @on-enter="handleQuery()"
        >
        </Input>
      </div>
      <div class="formItemWrap">
        <p>{{ $t("message.permissions.phone") }}</p>
        <Input
          type="text"
          v-model="queries.phonenumber"
          :placeholder="
            $t('message.permissions.pleaseInput') +
              $t('message.permissions.phone')
          "
          style="width: 272px"
          @on-enter="handleQuery()"
        >
        </Input>
      </div>
      <div class="formItemWrap">
        <p>{{ $t("message.permissions.createTime") }}</p>
        <DatePicker
          format="yyyy-MM-dd"
          type="daterange"
          v-model="queries.createTime"
          :placeholder="
            $t('message.permissions.pleaseInput') +
              $t('message.permissions.createTime')
          "
          style="width: 272px"
        ></DatePicker>
      </div>
      <div class="formItemWrap">
        <Button @click="handleReset()">{{
          $t("message.permissions.reset")
        }}</Button>
        <Button
          type="primary"
          @click="handleQuery()"
          style="margin-left: 8px"
        >{{ $t("message.permissions.query") }}</Button
        >
      </div>
    </div>
    <div class="mainWrap">
      <div class="deptTree">
        <div class="emptyTree" v-if="departTree.length === 0">
          {{ $t("message.permissions.pleaseAddCompany") }}
        </div>
        <div
          v-for="(row, index) in departTree"
          @click="handleDeptChoosed(row)"
          :class="{
            deptName: true,
            deptChoosed: departChooedId === row.id,
            leaf: true || !(row.children && row.children.length > 0)
          }"
          :key="index"
          :style="{
            paddingLeft:
              30 * ((row[idChain] && row[idChain].length) || 0) + 15 + 'px'
          }"
        >
          <div
            class="iconWrap foldIcon"
            v-if="row.children && row.children.length > 0"
            @click="handleFold(row, index)"
          >
            <Icon :type="!row.unfold ? 'ios-arrow-forward' : 'ios-arrow-up'" />
          </div>
          <div class="iconWrap folder">
            <Icon
              :type="
                row.children && row.children.length > 0
                  ? 'ios-folder-outline'
                  : 'ios-paper-outline'
              "
            />
          </div>
          <div class="folder-label">{{ row.label }}</div>
        </div>
      </div>
      <div class="tableWrap">
        <div class="addWrap">
          <p>{{ tableTitle }}</p>
          <Button
            type="primary"
            @click="handleAdd()"
            icon="md-add"
          >{{ $t("message.permissions.add") }}</Button
          >
        </div>
        <Table :columns="columns" :data="userList" :loading="tableLoading">
          <template slot-scope="{ row, index }" slot="operation">
            <div class="deptName">
              <div class="operation" @click="edit(row, index)">
                {{ $t("message.permissions.edit") }}
              </div>
              <Divider v-if="false" type="vertical" />
              <div
                class="operation"
                v-if="false"
                @click="modifyPassword(row, index)"
              >
                {{ $t("message.permissions.modifyPassword") }}
              </div>
              <Divider v-if="false" type="vertical" />
              <div class="operation" v-if="false">
                {{ $t("message.permissions.delete") }}
              </div>
            </div>
          </template>
        </Table>
        <div class="page">
          <Page
            :total="pageData.total"
            :current="pageData.pageNum"
            show-elevator
            show-sizer
            show-total
            @on-change="handlePageChange"
            @on-page-size-change="handlePageSizeChange"
          />
        </div>
      </div>
    </div>
    <Modal
      v-model="modalVisible"
      :title="modalTitle"
      @on-cancel="handleModalCancel"
      footer-hide
    >
      <Form
        ref="departForm"
        :label-width="100"
        v-show="modalType !== 'modifyPassword'"
      >
        <FormItem
          :label="$t('message.permissions.userDepart')"
          :error="departmentErrorTip"
          required
        >
          <treeselect
            v-model="userForm.deptId"
            :options="departTreeOrigin"
            :placeholder="
              $t('message.permissions.pleaseInput') +
                $t('message.permissions.userDepart') +
                $t('message.permissions.forSelect')
            "
            :defaultExpandLevel="Infinity"
            search-nested
            required
            style="width: 300px"
            :noResultsText="$t('message.permissions.noMatch')"
            @close="departmentChange"
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
                style="font-size: 16px; margin-right: 5px; top: -3px"
                :type="
                  node.isBranch ? 'ios-folder-outline' : 'ios-paper-outline'
                "
              /><span>{{ node.label }}</span>
            </label>
          </treeselect>
        </FormItem></Form
        >
      <Form
        ref="userForm"
        :model="userForm"
        :label-width="100"
        :rules="ruleValidate"
        v-show="modalType !== 'modifyPassword'"
      >
        <FormItem :label="$t('message.permissions.username')" prop="name">
          <Input
            type="text"
            v-model="userForm.name"
            :placeholder="
              $t('message.permissions.pleaseInput') +
                $t('message.permissions.username')
            "
            style="width: 300px"
          >
          </Input>
        </FormItem>

        <FormItem
          :label="$t('message.permissions.login')"
          prop="userName"
          v-if="!editingData"
        >
          <Input
            type="text"
            v-model="userForm.userName"
            :placeholder="
              $t('message.permissions.pleaseInput') +
                $t('message.permissions.login')
            "
            style="width: 300px"
          >
          </Input>
        </FormItem>
        <FormItem
          :label="$t('message.permissions.password')"
          prop="password"
          v-if="modalType === 'addUser'"
        >
          <Input
            type="password"
            v-model="userForm.password"
            :placeholder="
              $t('message.permissions.pleaseInput') +
                $t('message.permissions.password')
            "
            style="width: 300px"
          >
          </Input>
        </FormItem>
        <FormItem :label="$t('message.permissions.phone')" prop="phonenumber">
          <Input
            type="text"
            v-model="userForm.phonenumber"
            :placeholder="
              $t('message.permissions.pleaseInput') +
                $t('message.permissions.phone')
            "
            style="width: 300px"
          >
          </Input>
        </FormItem>
        <FormItem :label="$t('message.permissions.email')" prop="email">
          <Input
            type="text"
            v-model="userForm.email"
            :placeholder="
              $t('message.permissions.pleaseInput') +
                $t('message.permissions.email')
            "
            style="width: 300px"
          >
          </Input>
        </FormItem>
      </Form>
      <Form
        ref="passwordForm"
        :model="userForm"
        :rules="passRuleValidate"
        v-show="modalType === 'modifyPassword'"
      >
        <div class="newPassword">
          {{ $t("message.permissions.inputPassword") }}
        </div>
        <FormItem prop="password">
          <Input
            type="password"
            v-model="userForm.password"
            :placeholder="$t('message.permissions.pleaseInput')"
          >
          </Input>
        </FormItem>
      </Form>
      <slot name="footer">
        <div class="modalFooter">
          <Button @click="handleModalCancel()" size="large">{{
            $t("message.workspace.cancel")
          }}</Button>
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
import moment from "moment";
import Treeselect from "@riophae/vue-treeselect";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";
import {
  GetDepartmentTree,
  GetUserList,
  AddNewUser,
  ModifyUser,
  ModifyUserPassword
} from '@dataspherestudio/shared/common/service/permissions';
import {
  ID_CHAIN,
  addIdChain,
  expandAll,
  getParentDepartName,
  removeEmptyChildren,
  testPassword
} from "../util";
export default {
  components: { Treeselect },
  data() {
    const validatePass = (rule, value, callback) => {
      const { valid, tag } = testPassword(value);
      if (valid) {
        callback();
      } else {
        callback(
          new Error(
            tag === "empty"
              ? this.$t("message.permissions.passwordEmpty")
              : tag === "keyboard"
                ? this.$t("message.permissions.pwdKeyboardError")
                : this.$t("message.permissions.pwdCheckError")
          )
        );
      }
    };
    const validateUserNameCheck = (rule, value, callback) => {
      if (!value) {
        callback(new Error(this.$t("message.permissions.userNameEmpty")));
      } else if (value.length > 50) {
        callback(new Error(this.$t("message.permissions.userNameTooLong")));
      } else {
        callback();
      }
    };
    const validateNameCheck = (rule, value, callback) => {
      if (!value) {
        callback(new Error(this.$t("message.permissions.nameEmpty")));
      } else if (value.length > 50) {
        callback(new Error(this.$t("message.permissions.nameTooLong")));
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
        userName: "",
        phonenumber: "",
        createTime: ""
      },
      departTree: [],
      departTreeOrigin: [],
      idChain: ID_CHAIN,
      departChooedId: "",
      tableTitle: "",
      pageData: {
        total: 0,
        pageNum: 1,
        pageSize: 10
      },
      columns: [
        {
          title: "ID",
          key: "id"
        },
        {
          title: this.$t("message.permissions.login"),
          key: "userName"
        },
        {
          title: this.$t("message.permissions.username"),
          key: "name"
        },
        {
          title: this.$t("message.permissions.phone"),
          key: "phonenumber"
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
      userList: [],
      tableLoading: false,
      modalVisible: false,
      modalTitle: "",
      modalType: "",
      userForm: {
        deptId: "",
        userName: "",
        name: "",
        password: "",
        phonenumber: "",
        email: ""
      },
      editingData: "",
      confirmLoading: false,
      departmentErrorTip: "",
      ruleValidate: {
        userName: [
          {
            required: true,
            validator: validateUserNameCheck,
            trigger: "change"
          }
        ],
        name: [
          {
            required: true,
            validator: validateNameCheck,
            trigger: "change"
          }
        ],
        password: [
          {
            required: true,
            validator: validatePass,
            trigger: "blur"
          }
        ],
        phonenumber: [
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
      passRuleValidate: {
        password: [
          {
            required: true,
            validator: validatePass,
            trigger: "blur"
          }
        ]
      }
    };
  },
  mounted() {
    this.getDepartmentTree();
    this.getUserList();
  },
  methods: {
    getDepartmentTree() {
      GetDepartmentTree({}).then(data => {
        const tree = data && data.deptTree;
        if (tree) {
          removeEmptyChildren(tree);
          this.departTreeOrigin = _.cloneDeep(tree);
          tree.forEach(item => addIdChain(item, "id"));
          this.departTree = expandAll(tree);
        }
      });
    },
    getUserList(changeParams = {}) {
      if (!changeParams.pageNum) {
        this.pageData.pageNum = 1;
      }
      const queryParams = {
        deptId: this.departChooedId,
        pageNum: 1,
        pageSize: this.pageData.pageSize,
        ...this.queries,
        ...changeParams
      };
      const params = [];
      Object.keys(queryParams).forEach(key => {
        const value = queryParams[key];
        if (value || value === 0) {
          if (key === "createTime") {
            if (value[0] && value[1]) {
              params.push(`beginTime=${moment(value[0]).format("YYYY-MM-DD")}`);
              params.push(`endTime=${moment(value[1]).format("YYYY-MM-DD")}`);
            }
          } else {
            params.push(`${key}=${value}`);
          }
        }
      });
      const query = params.length > 0 ? "?" + encodeURI(params.join("&")) : "";
      this.tableLoading = true;
      GetUserList(query)
        .then(data => {
          this.tableLoading = false;
          this.pageData.total = data.total;
          this.userList = data.userList.map(item => {
            const user = { ...item };
            if (user.createTime) {
              user.createTime = moment(user.createTime).format(
                "YYYY-MM-DD HH:mm:ss"
              );
            }
            return user;
          });
        })
        .catch(e => {
          console.log(e);
          this.tableLoading = false;
        });
    },
    handleReset() {
      this.departChooedId = "";
      this.queries = {
        userName: "",
        phonenumber: "",
        createTime: ""
      };
      this.getUserList({
        userName: "",
        phone: "",
        createTime: ""
      });
    },
    handleQuery() {
      console.log(this.queries);
      this.departChooedId = "";
      this.getUserList({});
    },
    handleDeptChoosed(depart) {
      console.log(depart);
      this.departChooedId = depart.id;
      const departAr = [];
      depart[ID_CHAIN] &&
        depart[ID_CHAIN].forEach(item => {
          const hit = this.departTree.find(de => de.id === item);
          if (hit) {
            departAr.push(hit.label);
          }
        });
      departAr.push(depart.label);
      this.tableTitle = departAr.join("-");
      this.getUserList({ deptId: depart.id });
    },
    handleAdd() {
      this.editingData = "";
      this.modalType = "addUser";
      this.modalTitle = this.$t("message.permissions.addUser");
      this.modalVisible = true;
      this.userForm.deptId =
        this.departChooedId ||
        (this.departTreeOrigin[0] && this.departTreeOrigin[0].id) ||
        "";
    },
    handleFold(rowData, index) {
      if (rowData.unfold) {
        console.log(index);
        const id = rowData.id;
        const result = this.departTree.filter(item => {
          if (item[ID_CHAIN]) {
            return !item[ID_CHAIN].includes(id);
          } else {
            return true;
          }
        });
        result[index]["unfold"] = false;
        this.departTree = result;
      } else {
        const newAr = _.cloneDeep(this.departTree);
        const ar1 = newAr.slice(0, index + 1);
        const ar2 = newAr.slice(index + 1);
        const children = _.cloneDeep(rowData.children);
        const result = [...ar1, ...children, ...ar2];
        result[index].unfold = true;
        this.departTree = result;
      }
    },
    edit(rowData) {
      rowData.departNameList = this.getParentName({ id: rowData.deptId });
      this.editingData = rowData;
      this.modalType = "updateUser";
      const newObj = {};
      Object.keys(this.userForm).forEach(key => {
        if (key !== "password") {
          newObj[key] = rowData[key];
        }
      });
      this.userForm = newObj;
      this.modalTitle = this.$t("message.permissions.editUser");
      this.modalVisible = true;
    },
    modifyPassword(rowData) {
      this.modalType = "modifyPassword";
      this.modalTitle = this.$t("message.permissions.modifyPassword");
      this.editingData = rowData;
      this.modalVisible = true;
    },
    deleteRow(rowData) {
      if (rowData.children && rowData.children.length > 0) {
        this.$Message.error({
          content: this.$t("message.permissions.deleteUnvalidTip"),
          duration: 10,
          closable: true
        });
      }
    },
    getParentName(data) {
      return getParentDepartName(data, this.departTreeOrigin);
    },
    handlePageSizeChange(pageSize) {
      console.log(pageSize);
      this.pageData.pageSize = pageSize;
      this.getUserList({
        pageSize
      });
    },
    handlePageChange(page) {
      console.log(page);
      this.pageData.pageNum = page;
      this.getUserList({
        pageNum: page
      });
    },
    handleModalCancel() {
      this.modalVisible = false;
      this.resetUserForm();
    },
    handleModalOk() {
      console.log(this.modalType);
      if (this.modalType === "modifyPassword") {
        this.$refs["passwordForm"].validate(valid => {
          if (valid) {
            const params = {
              password: this.userForm.password,
              id: this.editingData.id
            };
            this.confirmLoading = true;
            ModifyUserPassword(params)
              .then(data => {
                console.log(data);
                this.$Message.success(
                  this.$t("message.permissions.modifyPwdSuccess")
                );
                this.modalVisible = false;
                this.confirmLoading = false;
                this.resetUserForm();
              })
              .catch(() => {
                this.confirmLoading = false;
              });
          }
        });
      } else {
        this.$refs["userForm"].validate(valid => {
          console.log(this.userForm);
          const deptId = this.userForm.deptId;
          if (!deptId && deptId !== 0) {
            this.departmentErrorTip = this.$t("message.permissions.deptEmpty");
            return;
          }
          if (valid) {
            const isAdd = !this.editingData;
            const executeMethod = isAdd ? AddNewUser : ModifyUser;
            const { userName, password, ...rest } = this.userForm;
            const params = isAdd
              ? { ...this.userForm, userName: userName.trim(), password }
              : { ...rest, id: this.editingData.id };
            this.confirmLoading = true;
            executeMethod(params)
              .then(data => {
                console.log(data);
                this.$Message.success(
                  isAdd
                    ? this.$t("message.permissions.addUserSucess")
                    : this.$t("message.permissions.updateUserSucess")
                );
                this.modalVisible = false;
                this.confirmLoading = false;
                this.getUserList(
                  isAdd ? {} : { pageNum: this.pageData.pageNum }
                );
                this.resetUserForm();
              })
              .catch(() => {
                this.confirmLoading = false;
              });
          }
        });
      }
    },
    departmentChange(value) {
      this.departmentErrorTip =
        value !== undefined ? "" : this.$t("message.permissions.deptEmpty");
    },
    resetUserForm() {
      if (this.modalType === "modifyPassword") {
        this.$refs["passwordForm"].resetFields();
      } else {
        this.$refs["userForm"].resetFields();
      }
      this.departmentErrorTip = "";
      this.editingData = "";
      this.userForm = {
        deptId: "",
        userName: "",
        name: "",
        password: "",
        phonenumber: "",
        email: ""
      };
      this.passwordForm = {
        password: ""
      };
    }
  }
};
</script>
<style lang="scss" scoped>
@import "@dataspherestudio/shared/common/style/variables.scss";
.container {
  width: 100%;
  // height: 100%;
  display: flex;
  flex-direction: column;
  padding: 0;
  @include bg-color($workspace-background, $dark-workspace-background);
}
.formWrap {
  width: 100%;
  // padding-left: 20px;
  // padding-bottom: 20px;
  // border-bottom: 1px solid #dee4ec;
  // @include border-color($border-color-base, $dark-border-color-base);
  display: flex;
  flex-wrap: wrap;
  border-bottom: 1px solid #dee4ec;
  @include bg-color($workspace-body-bg-color, $dark-workspace-body-bg-color);
  padding-bottom: 24px;
  padding-left: 24px;
  .formItemWrap {
    display: flex;
    align-items: center;
    margin-right: 30px;
    margin-top: 20px;
    & p {
      width: 80px;

      font-size: 14px;
      // color: rgba(0, 0, 0, 0.85);
      @include font-color($workspace-title-color, $dark-workspace-title-color);
    }
  }
}
.mainWrap {
  display: flex;
  flex: 1;
  min-height: 400px;
  @include bg-color($workspace-body-bg-color, $dark-workspace-body-bg-color);
}
.deptTree {
  min-width: 230px;
  height: inherit;
  .emptyTree {
    width: inherit;
    font-size: 14px;
    // color: rgba(0, 0, 0, 0.25);
    @include font-color(rgba(0, 0, 0, 0.25), rgba(255, 255, 255, 0.25));
    text-align: center;
  }
}
.tableWrap {
  flex: 1;
  margin: 0 24px 24px;
  .addWrap {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
    height: 72px;
    & p {

      font-size: 16px;
      @include font-color($workspace-title-color, $dark-workspace-title-color);
    }
  }
  .page {
    display: flex;
    justify-content: flex-end;
    margin-top: 20px;
  }
}
.deptName {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  height: 38px;
  font-size: 14px;

}
.deptChoosed {
  @include bg-color($active-menu-item, $dark-active-menu-item);
  @include font-color($primary-color, $dark-primary-color);
}
.leaf {
  cursor: pointer;
}
.iconWrap {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  width: 26px;
  height: 30px;
  font-size: 18px;
}
.foldIcon {
  cursor: pointer;
}
.folder {
  width: 22px;
  @include font-color($light-text-color, $dark-text-color);
}
.folder-label,
.foldIcon {
  @include font-color($light-text-color, $dark-text-color);
}
.operation {
  font-size: 14px;
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
.newPassword {
  margin-top: 10px;
  margin-bottom: 10px;
  font-size: 14px;
  @include font-color($light-text-color, $dark-text-color);
}
</style>
