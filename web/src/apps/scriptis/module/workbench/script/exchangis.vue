<template>
  <div class="exchangisJob">
    <Row class-name="exchangisContent" :gutter="32">
      <Col class-name="leftSide" span="2">
        <div class="leftContent">
          <Menu
            mode="vertical"
            theme="light"
            width="auto"
            :active-name="activeProjectId"
            @on-select="projectSelect"
          >
            <MenuGroup title="任务组" class="jobGroup">
              <MenuItem
                :name="project.id"
                :key="project.id"
                v-for="project in projects"
              >
                {{ project.projectName }}
              </MenuItem>
            </MenuGroup>
          </Menu>
        </div>
      </Col>
      <Col class-name="rightSide" span="20">
        <Row>
          <div class="title">
            <span>{{ projectTitle + "-任务列表" }}</span>
            <Button type="primary" @click="handleClick" class="button1"
              >前往Exchangis</Button
            >
          </div>
          <Table
            ref="currentRowTable"
            :columns="columns"
            size="small"
            :data="tableList"
          >
          </Table>
          <Page
            :total="page.total"
            :current="page.current"
            :page-size="page.pageSize"
            @on-change="pageChange"
            show-elevator
            show-total
          />
        </Row>
        <Row class-name="footer">
          <Button @click="cancel">取消</Button>
          <Button
            type="primary"
            class="button2"
            :disabled="!isAllSelected"
            @click="ok"
            >确认</Button
          >
        </Row>
      </Col>
    </Row>
  </div>
</template>
<script>
import { isEmpty, find } from "lodash";
import api from "@/common/service/api";
import util from "@/common/util";
import { Script } from "../modal.js";
import mixin from "@/common/service/mixin";

export default {
  components: {},
  inject: ["IdeInstance"], // 获取Ide组件实例
  props: {
    work: {
      type: Object,
      required: true,
    },
    current: {
      type: String,
      default: "",
    },
    node: Object,
    readonly: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      script: {
        data: "",
        oldData: "",
        params: {},
        readOnly: false,
      },
      projects: this.IdeInstance.parameters.projects,
      tableList: [],
      activeProjectId: "",
      activeJobId: "",
      page: {
        total: 0,
        current: 1,
        pageSize: 10,
      },
      columns: [
        {
          slot: "selection",
          key: "selection",
          align: "center",
          maxWidth: 100,
          renderHeader: (h, params) => {
            return h("span", {}, " ");
          },
          render: (h, { row, column, index }) => {
            return h("Checkbox", {
              props: {
                value: row.checkBox,
                disabled: row.jobStatus ? true : false,
              },
              on: {
                "on-change": (e) => {
                  this.tableList.forEach((item) => {
                    this.$set(item, "checkBox", false);
                    this.$set(item, "_highlight", false);
                  });
                  this.tableList[index].checkBox = e;
                  this.tableList[index]._highlight = e;
                  if (!e) {
                    this.activeJobId = "";
                  } else {
                    this.activeJobId = row.id;
                  }
                },
              },
            });
          },
        },
        {
          title: "任务ID",
          key: "id",
          align: "left",
        },
        {
          title: "任务名称",
          key: "jobName",
          align: "left",
        },
        {
          title: "任务描述",
          key: "jobDesc",
          align: "left",
        },
        {
          title: "定时状态",
          key: "jobStatus",
          align: "left",
        },
        {
          title: "创建时间",
          key: "createTime",
          align: "left",
        },
      ],
    };
  },
  mixins: [mixin],
  created() {
    this.init();
    this.userName = this.getUserName();
    // 如果当前work存在data，表示工作已经被打开
    if (this.work.data) {
      delete this.work.data.oldData;
      this.script = this.work.data;
    } else {
      let supportedMode = find(this.getSupportModes(), (p) =>
        p.rule.test(this.work.filename)
      );
      if (this.work.specialSetting) {
        supportedMode = find(
          this.getSupportModes(),
          (p) => p.runType === this.work.specialSetting.runType
        );
      }
      this.work.data = this.script = new Script(
        Object.assign({}, supportedMode, {
          id: this.work.id,
          fileName: this.work.filename,
          data: this.work.code,
          params: this.work.params,
          readOnly: this.readonly,
        })
      );
    }
    // 删掉无用的code和params，因为已经存储在script对象中
    delete this.work.code;
    delete this.work.params;
    this.script.oldData = this.script.data;
  },
  mounted() {
    // 获取Ide组件上的属性index
    this.tabIndex = this.IdeInstance.$attrs["in-flows-index"];
  },
  methods: {
    async init() {
      // 获取上次保存的项目ID、任务ID和当前页, 如果不存在则指定项目列表中的第一个值，对应任务列表里的第一个非定时状态任务以及页码
      this.activeProjectId =
        this.node.params &&
        this.node.params.variable &&
        this.node.params.variable.projectId &&
        this.node.params.variable.projectId !== ""
          ? this.node.params.variable.projectId
          : this.projects[0].id;
      let activeProjectId = this.projects.find((item) => {
        return item.id == this.activeProjectId;
      });
      if (!activeProjectId) {
        this.activeProjectId = this.projects[0].id;
      }
      this.page.current =
        this.node.params &&
        this.node.params.variable &&
        this.node.params.variable.current &&
        this.node.params.variable.current !== ""
          ? this.node.params.variable.current
          : 1;
      let params = {
        projectId: this.activeProjectId,
        pageNum: this.page.current,
      };
      await api
        .fetch("/dss/framework/exchangis/task/tree", params, "get")
        .then((res) => {
          this.page.current = res.response.page;
          this.page.total = res.response.totalItems;
          this.tableList = res.response.dssExchangeTaskList;
          this.activeJobId =
            this.node.params &&
            this.node.params.variable &&
            this.node.params.variable.taskId &&
            this.node.params.variable.taskId !== ""
              ? this.node.params.variable.taskId
              : "";
          let activeJobId = this.tableList.find((item) => {
            return item.id == this.activeJobId;
          });
          if (!activeJobId) {
            this.activeJobId = "";
          }
          this.tableList = this.tableList.map((item) => {
            if (item.id === this.activeJobId) {
              item._highlight = true;
              item.checkBox = true;
              return item;
            }
            return item;
          });
        });
    },
    // 页码切换
    pageChange(current) {
      this.activeJobId = "";
      let params = {
        projectId: this.activeProjectId,
        pageNum: current,
      };
      api
        .fetch("/dss/framework/exchangis/task/tree", params, "get")
        .then((res) => {
          this.page.current = res.response.page;
          this.page.total = res.response.totalItems;
          this.tableList = res.response.dssExchangeTaskList;
        });
    },
    // 项目切换
    projectSelect(name) {
      this.activeProjectId = name;
      this.activeJobId = "";
      let params = {
        projectId: name,
        pageNum: 1,
      };
      api
        .fetch("/dss/framework/exchangis/task/tree", params, "get")
        .then((res) => {
          this.page.current = res.response.page;
          this.page.total = res.response.totalItems;
          this.tableList = res.response.dssExchangeTaskList;
        });
    },
    // 节点保存
    nodeSave() {
      if (this.readonly)
        return this.$Message.warning(this.$t("message.scripts.notSave"));
      let params = {
        fileName: this.script.fileName,
        scriptContent: this.script.data,
        creator: this.node.creator,
        projectName: this.$route.query.projectName || "",
        metadata: this.nodeConvertSettingParams(this.script.params),
      };
      const resources = this.node.resources;
      if (
        resources &&
        resources.length &&
        this.node.jobContent &&
        this.node.jobContent.script
      ) {
        params.resourceId = resources[0].resourceId;
      }
      this.node.params = JSON.parse(
        JSON.stringify(this.nodeConvertSettingParams(this.script.params))
      );
      // 删除节点json里的contextId
      delete this.node.params.configuration.runtime.contextID;
      delete this.node.params.configuration.runtime.nodeName;
      // 除了执行，其他的都不需要contextID
      let tempParams = JSON.parse(JSON.stringify(params));
      delete tempParams.metadata.configuration.runtime.contextID;
      delete tempParams.metadata.configuration.runtime.nodeName;
      return api
        .fetch("/filesystem/saveScriptToBML", tempParams, "post")
        .then((res) => {
          this.$Message.success(this.$t("message.scripts.saveSuccess"));
          this.node.isChange = false;
          this.dispatch("IDE:saveNode", {
            resource: {
              fileName: this.script.fileName,
              resourceId: res.resourceId,
              version: res.version,
            },
            node: this.node,
            data: {
              content: this.script.data,
              params: this.script.params,
            },
          });
          this.work.unsave = false;
          // 提交最新的内容，更新script.data和script.oldData
          this.script.oldData = this.script.data;
          // 保存时更新下缓存。
          // 加入用户名来区分不同账户下的tab
          this.dispatch("IndexedDB:recordTab", {
            ...this.work,
            userName: this.userName,
          });
          this.dispatch("IndexedDB:updateGlobalCache", {
            id: this.userName,
            work: this.work,
          });
          return this.node.title;
        })
        .catch(() => {
          this.work.unsave = true;
        });
    },
    async save() {
      if (this.node && Object.keys(this.node).length > 0) {
        await this.nodeSave();
        setTimeout(() => {
          this.cancel();
        }, 3000);
      }
    },
    nodeConvertSettingParams(params) {
      const variable = isEmpty(params.variable)
        ? {}
        : util.convertArrayToObject(params.variable);
      const configuration = isEmpty(params.configuration)? 
        {
          special: {},
          runtime: {},
          startup: {},
        } : {
          special: params.configuration.special || {},
          runtime: params.configuration.runtime || {},
          startup: params.configuration.startup || {},
        };
      return {
        variable,
        configuration,
      };
    },
    convertSettingParams(params) {
      const variable = isEmpty(params.variable)? 
        {} : util.convertArrayToObject(params.variable);
      const configuration = isEmpty(params.variable)? 
        {} : {
          special: {},
          runtime: {},
          startup: {},
        };
      return {
        variable,
        configuration,
      };
    },
    handleClick() {
      this.gotoCommonIframe("Exchangis", {
        workspaceId: this.$route.query.workspaceId,
      });
    },
    cancel() {
      this.IdeInstance.$parent.remove(this.tabIndex);
    },
    ok() {
      api
        .fetch(
          "/dss/framework/exchangis/shell",
          { taskId: this.activeJobId, projectId: this.activeProjectId },
          "get"
        )
        .then((res) => {
          this.script.data = res.response.shell;
          this.script.params.variable[0] = {
            key: "taskId",
            value: this.activeJobId,
          };
          this.script.params.variable[1] = {
            key: "projectId",
            value: this.activeProjectId,
          };
          this.script.params.variable[2] = {
            key: "current",
            value: this.page.current,
          };
          this.save();
        });
    },
  },
  computed: {
    projectTitle() {
      return this.projects.find((item) => {
        return item.id === this.activeProjectId;
      }).projectName;
    },
    isAllSelected() {
      return this.activeProjectId != "" && this.activeJobId != "";
    },
  },
  watch: {
    "script.data": function () {
      if (!this.work.ismodifyByOldTab) {
        this.work.unsave = this.script.data != this.script.oldData;
        if (this.node) {
          this.node.isChange = this.work.unsave;
        }
      }
    },
    "script.oldData": function () {
      if (!this.work.ismodifyByOldTab) {
        this.work.unsave = this.script.data != this.script.oldData;
      }
    },
  },
};
</script>
<style lang="scss" scoped>
.exchangisJob {
  height: 100%;
  position: relative;
  min-width: 800px;
  .exchangisContent {
    min-width: 950px;
    height: 100%;
    .leftSide {
      height: 100%;
      min-width: 152px;
      padding-right: 0 !important;
      .leftContent{
        height: 100%;
        overflow: auto;
        &::after{
          content: "";
          display: block;
          width: 1px;
          height: 100%;
          background: #dcdee2;
          position: absolute;
          top: 0;
          bottom: 0;
          right: 0;
          [data-theme="dark"] & {
            z-index: 1;
          }
          z-index: 1000;
        }
      }

      .ivu-menu-vertical {
        height: 100%;
        overflow: auto;
        &::after{
          content: none;
        }
        /deep/ .ivu-menu-item-group-title {
          padding-left: 0px;
          text-align: center;
          height: 48px;
          line-height: 48px;
          font-size: 14px;
        }
        .ivu-menu-item {
          text-align: center;
        }
      }
    }
    .rightSide {
      min-width: 650px;
      overflow: auto;
      height: 100%;
      .ivu-page {
        margin-top: 20px;
        text-align: center;
      }
      .title {
        margin: 8px 0px 8px 0px;
        line-height: 32px;
        .button1 {
          position: absolute;
          right: 0;
        }
      }
      .footer {
        margin-top: 30px;
        text-align: right;
        .button2 {
          margin-left: 20px;
        }
      }
    }
  }
}
</style>

