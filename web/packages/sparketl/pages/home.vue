<!-- eslint-disable vue/no-v-model-argument -->
<template>
  <div class="demo-split">
    <div class="demo-split-pane1">
      <Button type="primary" @click="handleSubmit('formValidate')">提交</Button>
      <Divider>{{ $t("plugin.common.addPlugin") }}</Divider>
      <Badge :count="mapleConfig.plugins.length">
        <TypedPlugin :addPlugin="(type, name) => addPlugin(type, name)" />
      </Badge>

      <div v-for="(item, index) in mapleConfig.plugins" :key="index">
        <div style="border-bottom: 1px solid #efefef; margin-top: 10px">
          <div class="maple-box">
            <div class="maple-title">
              <template v-if="item.type === 'sink'">
                <template v-if="item.name === 'file'">
                  {{ $t("plugin.common.addPath") }}:
                  {{ item.config.path }}</template
                  >
                <template v-else>
                  {{ $t("plugin.common.outTableName") }}:
                  {{ item.config.resultTable }}
                </template>
              </template>
              <template
                v-if="item.type === 'source' || item.type === 'transformation'"
              >
                {{ $t("plugin.common.registryTableName") }}
                {{ item.config.resultTable }}
              </template>
            </div>
            <div>
              {{ `${item.name.replace("_", "-")}-${item.type}` }}
              <Divider type="vertical" />
              <Poptip
                confirm
                title="确定删除吗?"
                @on-ok="() => delPlugin(index)"
              >
                <Button type="error">
                  <Icon type="md-close" />
                </Button>
              </Poptip>
              <Divider type="vertical" />
              <Button type="info" @click="disableDetail(index)">
                <Icon type="md-code" />
              </Button>
            </div>
          </div>

          <component
            :ref="`childForm${index}`"
            v-show="pageConfig[index].expand"
            :is="`${item.name.replace('_', '-')}-${item.type}`"
            v-model:formValue="item.config"
            :name="`${item.type}_${index}`"
          />
        </div>
      </div>
    </div>
    <div class="demo-split-pane">
      <Tabs :value="codeView" @on-click="changeCode">
        <TabPane label="JSON" name="JSON" icon="md-document" />
        <TabPane label="Scala" name="Scala" icon="md-paper" disabled />
      </Tabs>
      <Codemirror
        v-if="codeView == 'JSON'"
        :readOnly="true"
        v-model:value="jsonContent"
      />
    </div>
  </div>
</template>

<script>
import TypedPlugin from "../components/TypedPlugin";
import FileSource from "../components/Source/FileSource";
import JdbcSource from "../components/Source/JdbcSource";
import InputStringMap from "../components/InputStringMap";
import ManagedJdbcSource from "../components/Source/ManagedJdbcSource";
import SqlTransformation from "../components/Transformation/SqlTransformation";
import ManagedJdbcSink from "../components/Sink/ManagedJdbcSink";
import HiveSink from "../components/Sink/HiveSink";
import JdbcSink from "../components/Sink/JdbcSink";
import FileSink from "../components/Sink/FileSink";
import { PluginModels } from "../utils/BaseConstant";
import Codemirror from "../components/Codemirror";
export default {
  name: "HomePage",
  components: {
    TypedPlugin,
    FileSource,
    JdbcSource,
    ManagedJdbcSource,
    SqlTransformation,
    ManagedJdbcSink,
    FileSink,
    JdbcSink,
    HiveSink,
    InputStringMap,
    Codemirror,
  },
  props: {
    msg: String,
  },
  data() {
    return {
      codeView: "JSON",
      codeContent: "",
      collapse: "1",
      mapleConfig: {
        plugins: [],
      },
      pageConfig: [],
    };
  },
  computed: {
    jsonContent() {
      return JSON.stringify(this.mapleConfig, null, 4);
    },
  },
  methods: {
    addPlugin({ type, name, index = -1 }) {
      let plugin = PluginModels[type][name]();
      let plugins = this.mapleConfig.plugins;
      if (index < 0 || index >= plugins.length) {
        plugins.push(plugin);
        this.pageConfig.push({ expand: true });
      } else {
        plugins.splice(index, 0, plugin);
        this.pageConfig.splice(index, 0, { expand: true });
      }
      this.mapleConfig.plugins = plugins;
      //this.jsonContent = JSON.stringify(this.mapleConfig, null, 4);
    },
    delPlugin(index = -1) {
      if (index >= 0) {
        this.mapleConfig.plugins.splice(index, 1);
        this.pageConfig.splice(index, 1);
      }
      //this.jsonContent = JSON.stringify(this.mapleConfig, null, 4);
    },
    changeCode(val) {
      this.codeView = val;
      if (val === "Scala") {
        this.getCode();
      }
    },
    disableDetail(index) {
      this.pageConfig[index].expand = !this.pageConfig[index].expand;
    },
    // 提交
    handleSubmit() {
      let checkLen = 0;
      for (let len = 0; len < this.mapleConfig.plugins.length; len++) {
        // check 表单字段必填
        this.$refs[`childForm${len}`][0].formSubmit().validate((valid) => {
          if (valid) {
            checkLen += 1;
          }
          // 表单提交内容
          if (checkLen == this.mapleConfig.plugins.length) {
            console.log(this.mapleConfig.plugins);
          }
        });
      }
    },
    getCode() {},
  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style>
.maple-box {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #fff;
  border-bottom: 1px solid #eee;
  display: flex;
  height: 36px;
  padding: 0 18px;
  margin-top: 10px;
}
.maple-title {
  font-weight: 700;
  color: #272b3f;
  border-left: 3px solid #2d8cf0;
  padding-left: 8px;
}
.demo-split {
  height: 100%;
  width: 100%;
  overflow: hidden;
  display: flex;
  justify-content: space-between;
}
.demo-split-pane1 {
  flex: 1;
  padding: 10px;
  height: calc(95vh);
  overflow-y: scroll;
  border-right: 1px solid #eee;
}
.demo-split-pane {
  padding: 10px;
  width: 600px;
  height: calc(95vh);
  overflow-y: scroll;
}

.content-editor .CodeMirror {
  height: auto !important;
}
.content-editor .CodeMirror-scroll {
  height: 100%;
  overflow-y: hidden;
  overflow-x: auto;
}
</style>
