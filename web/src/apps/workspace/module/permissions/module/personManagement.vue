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
        >
        </Input>
      </div>
      <div class="formItemWrap">
        <p>{{ $t("message.permissions.phone") }}</p>
        <Input
          type="text"
          v-model="queries.phone"
          :placeholder="
            $t('message.permissions.pleaseInput') +
              $t('message.permissions.phone')
          "
          style="width: 272px"
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
          <div>{{ row.label }}</div>
        </div>
      </div>
      <div class="tableWrap">
        <div class="addWrap">
          <p>{{ tableTitle }}</p>
          <Button type="primary" @click="handleAdd()" icon="md-add">{{
            $t("message.permissions.add")
          }}</Button>
        </div>
        <Table :columns="columns" :data="userList" :loading="tableLoading">
          <template slot-scope="{ row, index }" slot="operation">
            <div class="deptName">
              <div class="operation" @click="edit(row, index)">
                {{ $t("message.permissions.edit") }}
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
      <Form ref="departmentForm" :label-width="100" v-if="!editingData">
        <FormItem
          :label="$t('message.permissions.userDepart')"
          :error="departmentErrorTip"
          required
        >
          <treeselect
            v-model="userForm.deptId"
            :options="departTreeOrigin"
            :disabled="!!editingData"
            :placeholder="
              $t('message.permissions.pleaseInput') +
                $t('message.permissions.userDepart') +
                $t('message.permissions.forSelect')
            "
            defaultExpandLevel="Infinity"
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
                style="font-size: 16px;margin-right:5px;top: -3px;"
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
      >
        <FormItem
          :label="$t('message.permissions.userDepart')"
          v-if="!!editingData"
        >
          <div>{{ editingData.departNameList }}</div>
        </FormItem>
        <FormItem :label="$t('message.permissions.userName')" prop="name">
          <Input
            type="text"
            v-model="userForm.name"
            :placeholder="
              $t('message.permissions.pleaseInput') +
                $t('message.permissions.userName')
            "
            style="width: 300px"
          >
          </Input>
        </FormItem>

        <FormItem
          :label="$t('message.permissions.account')"
          prop="userName"
          v-if="!editingData"
        >
          <Input
            type="text"
            v-model="userForm.userName"
            :placeholder="
              $t('message.permissions.pleaseInput') +
                $t('message.permissions.account')
            "
            style="width: 300px"
          >
          </Input>
        </FormItem>
        <FormItem :label="$t('message.permissions.password')" prop="password">
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
        <FormItem :label="$t('message.permissions.phone')">
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
        <FormItem :label="$t('message.permissions.email')">
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
//import selectTree from "../component/selectTree";
import Treeselect from "@riophae/vue-treeselect";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";
import {
  GetDepartmentTree,
  GetUserList,
  AddNewUser,
  ModifyUser
} from "@/common/service/permissions";
import {
  ID_CHAIN,
  addIdChain,
  expandAll,
  getParentDepartName,
  removeEmptyChildren
} from "../util";
export default {
  components: { Treeselect },
  data() {
    return {
      queries: {
        userName: "",
        phone: "",
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
          title: this.$t("message.permissions.order"),
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
            message: this.$t("message.permissions.userNameEmpty"),
            trigger: "blur"
          }
        ],
        name: [
          {
            required: true,
            message: this.$t("message.permissions.nameEmpty"),
            trigger: "blur"
          }
        ],
        password: [
          {
            required: true,
            message: this.$t("message.permissions.passwordEmpty"),
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
              params.push(
                `params[beginTime]=${moment(value[0]).format("YYYY-MM-DD")}`
              );
              params.push(
                `params[endTime]=${moment(value[1]).format("YYYY-MM-DD")}`
              );
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
      this.queries = {
        userName: "",
        phone: "",
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
          const { userName, ...rest } = this.userForm;
          const params = isAdd
            ? { ...this.userForm, userName }
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
              this.getUserList({});
              this.resetUserForm();
            })
            .catch(() => {
              this.confirmLoading = false;
              // this.$Message.error(
              //   isAdd
              //     ? this.$t("message.permissions.addUserFailed")
              //     : this.$t("message.permissions.updateUserFailed")
              // );
            });
        }
      });
    },
    departmentChange(value) {
      this.departmentErrorTip =
        value !== undefined ? "" : this.$t("message.permissions.deptEmpty");
    },
    resetUserForm() {
      this.$refs["userForm"].resetFields();
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
    }
  }
};
</script>
<style lang="scss" scoped>
.container {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
}
.formWrap {
  width: 100%;
  padding-left: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #dee4ec;
  display: flex;
  flex-wrap: wrap;
  .formItemWrap {
    display: flex;
    align-items: center;
    margin-right: 30px;
    margin-top: 20px;
    & p {
      width: 80px;
      font-family: PingFangSC-Regular;
      font-size: 14px;
      color: rgba(0, 0, 0, 0.85);
    }
  }
}
.mainWrap {
  display: flex;
  flex: 1;
  min-height: 400px;
}
.deptTree {
  min-width: 230px;
  height: inherit;
  border-right: 1px solid #dee4ec;
  padding-top: 24px;
  .emptyTree {
    width: inherit;
    font-size: 14px;
    color: rgba(0, 0, 0, 0.25);
    text-align: center;
  }
}
.tableWrap {
  flex: 1;
  padding: 0 24px;
  .addWrap {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
    height: 72px;
    & p {
      font-family: PingFangSC-Regular;
      font-size: 16px;
      color: rgba(0, 0, 0, 0.85);
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
  font-family: PingFangSC-Medium;
}
.deptChoosed {
  background: #ecf4ff;
  color: #2e92f7;
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
}
.operation {
  font-size: 14px;
  color: #2e92f7;
  cursor: pointer;
}
.modalFooter {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  border-top: 1px solid rgba(0, 0, 0, 0.05);
  padding-top: 10px;
}
</style>
