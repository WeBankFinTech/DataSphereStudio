<template>
  <div class="container">
    <div class="formItemWrap">
      <p>{{ $t("message.permissions.login") }}</p>
      <Input
        type="text"
        v-model="innerValue.label"
        ref="input"
        :placeholder="
          $t('message.permissions.pleaseInput') +
            $t('message.permissions.login')
        "
        style="width: 300px"
        @on-focus="showTree"
        @on-blur="hideTree"
        @on-change="filterTree"
      >
      </Input>
    </div>
    <div class="treeWrap" v-if="treeVisible" @click="handleTreeClick()">
      <div class="emptyTree" v-if="treeDatas.length === 0">
        {{ $t("message.permissions.pleaseAddCompany") }}
      </div>
      <div
        v-for="(row, index) in treeDatas"
        @click="handleChoosed(row)"
        :class="{
          deptName: true,
          deptChoosed: innerValue.id === row.id,
          leaf: !(row.children && row.children.length > 0)
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
  </div>
</template>
<script>
import _ from "lodash";
let CLICK_TREE = false;
let CHOOSED = false;
const ID_CHAIN = "_UPPER_ID_CHAIN_";
function expandAll(datas) {
  const copyData = _.cloneDeep(datas);
  const newAr = [];
  copyData.forEach(item => {
    innerExpand(item, newAr);
  });
  function innerExpand(da, result) {
    const data = _.cloneDeep(da);
    result.push(data);
    if (data.children && data.children.length > 0) {
      data.unfold = true;
      data.children.forEach(item => {
        innerExpand(item, result);
      });
    }
  }
  console.log(newAr);
  return newAr;
}
function findLabel(id, treeDatas) {
  if (!id) {
    return "";
  }
  const depart = treeDatas.find(item => item.id === id);
  const departAr = [];
  depart[ID_CHAIN].forEach(item => {
    const hit = treeDatas.find(de => de.id === item);
    if (hit) {
      departAr.push(hit.label);
    }
  });
  departAr.push(depart.label);
  return departAr.join("-");
}
export default {
  props: {
    treeData: Array,
    onChange: Function,
    label: String,
    labelStyle: Object,
    inputStyle: Object,
    value: String
  },
  data() {
    return {
      ruleInline: {},
      innerValue: {
        id: "",
        label: ""
      },
      treeVisible: false,
      treeDatas: [],
      idChain: ID_CHAIN
    };
  },
  mounted() {
    const datas = expandAll(this.treeData);
    const valueLabel = findLabel(this.value, datas);
    this.treeDatas = datas;
    this.innerValue = {
      id: this.value,
      label: valueLabel
    };
  },
  methods: {
    search() {
      this.$emit("click-serach");
    },
    createroles() {
      this.$emit("click-creater");
    },
    showTree() {
      //this.treeDatas = expandAll(this.treeData);
      this.treeVisible = true;
      console.log(12222);
    },
    hideTree() {
      setTimeout(() => {
        if (CLICK_TREE && !CHOOSED) {
          this.$refs.input.focus();
          return;
        }
        CLICK_TREE = false;
        CHOOSED = false;
        console.log(1442282);
        this.treeVisible = false;
      }, 200);
      console.log(144222);
    },
    handleChoosed(depart) {
      if (depart.children && depart.children.length > 0) {
        return;
      } else {
        this.$emit("updateValue", depart.id);
        this.treeVisible = false;
        CHOOSED = true;
        this.innerValue = {
          label: findLabel(depart.id, this.treeDatas),
          id: depart.id
        };
      }
    },
    handleFold(rowData, index) {
      if (rowData.unfold) {
        console.log(index);
        const id = rowData.id;
        const result = this.treeDatas.filter(item => {
          if (item[ID_CHAIN]) {
            return !item[ID_CHAIN].includes(id);
          } else {
            return true;
          }
        });
        result[index]["unfold"] = false;
        this.treeDatas = result;
      } else {
        const newAr = _.cloneDeep(this.treeDatas);
        const ar1 = newAr.slice(0, index + 1);
        const ar2 = newAr.slice(index + 1);
        const children = _.cloneDeep(rowData.children);
        const result = [...ar1, ...children, ...ar2];
        result[index].unfold = true;
        this.treeDatas = result;
      }
    },
    handleTreeClick() {
      CLICK_TREE = true;
      console.log(144277888);
    },
    filterTree: _.debounce(event => {
      console.log(event.target.value);
    }, 400)
  }
};
</script>
<style lang="scss" scoped>
.container {
  position: relative;
  padding-bottom: 30px;
}
.formItemWrap {
  display: flex;
  align-items: center;
  margin-right: 30px;
  margin-top: 20px;
  & p {
    width: 100px;
    font-family: PingFangSC-Regular;
    font-size: 12px;
    color: rgba(0, 0, 0, 0.85);
    text-align: right;
    padding-right: 10px;
  }
}
.treeWrap {
  width: 300px;
  max-height: 300px;
  background: red;
  position: absolute;
  top: 58px;
  left: 100px;
  z-index: 990;
  padding-top: 10px;
  overflow-y: auto;
  border: 1px solid rgba(0, 0, 0, 0.15);
  box-shadow: 0 3px 6px -4px rgba(0, 0, 0, 0.12),
    0 6px 16px 0 rgba(0, 0, 0, 0.08), 0 9px 28px 8px rgba(0, 0, 0, 0.05);
  border-radius: 2px;
  .emptyTree {
    width: inherit;
    font-size: 14px;
    color: rgba(0, 0, 0, 0.25);
    text-align: center;
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
</style>
