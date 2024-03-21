<template>
  <div class="we-filter-view">
    <div class="select-panel">
      <div @click="hanlderCheck">
        <Input
          v-model="searchName"
          @on-change="filterSearch()"
          class="input"
        ></Input>
        <virtual-list ref="fieldList" wtag="ul" :size="size" :remain="remain">
          <li
            v-for="(item, index) in rowlist"
            :key="item.title + index"
            :data-item="index"
            class="filter-view-item"
          >
            <Icon
              type="ios-checkbox-outline"
              v-if="isSelect(item)"
              size="14"
            ></Icon>
            <Icon type="ios-square-outline" size="14" v-else></Icon>
            <span class="filter-view-item-text" :title="item.title">{{
              item.title
            }}</span>
          </li>
        </virtual-list>
      </div>
      <SvgIcon class="icon-dir-right" icon-class="close" />
      <div>
        <div style="margin-bottom: 10px; margin-top: 2px; font-size: 14">
          {{$t("message.common.dss.selectedcol")}}({{ selectedList.length }})
        </div>
        <div class="selected-list" :style="{ height: height - 53 + 'px' }">
          <div v-if="selected.length < 1">
            {{$t("message.common.dss.colfiltertip")}}<br />
            {{$t("message.common.dss.colfiltertip_1")}}
          </div>
          <div
            v-for="(it, index) in selectedList"
            :key="it.title"
            class="filter-view-item"
          >
            <span class="filter-view-item-text" :title="it.title">{{
              it.title
            }}</span>
            <SvgIcon @click="removeItem(index)" icon-class="close2" />
          </div>
        </div>
      </div>
    </div>
    <div class="buts-warp">
      <Checkbox v-model="checkAll" @on-change="(value) => changeCheckAll(value)"
      >{{ $t("message.common.selectAll") }}</Checkbox
      >
      <Button type="primary" @click="onCancel">{{ $t("message.common.clear") }}</Button>
      <Button type="primary" @click="reset">{{ $t("message.common.reset") }}</Button>
      <Button type="primary" @click="changeHeads()">{{ $t("message.common.ok") }}</Button>
      <Button type="primary" @click="close">{{ $t("message.common.close") }}</Button>
    </div>
  </div>
</template>
<script>
import virtualList from '@dataspherestudio/shared/components/virtualList'
export default {
  components: {
    virtualList,
  },
  props: {
    headRows: {
      type: Array,
      default: () => [],
    },
    checked: {
      type: Array,
      default: () => [],
    },
    height: {
      // 树高度
      type: Number,
      require: true,
    },
  },
  data() {
    return {
      total: 0,
      remain: 0,
      size: 26,
      checkAll: false,
      searchName: '',
      selected: [],
      rowlist: [],
    }
  },
  computed: {
    selectedList() {
      return this.selected.slice().sort((a,b) => a._index - b._index)
    }
  },
  watch: {
    height() {
      this.layout()
    },
    selected(v) {
      this.checkAll =
        v.length === this.rowlist.length &&
        v
          .map((it) => it.title)
          .sort()
          .join() ===
          this.rowlist
            .map((it) => it.title)
            .sort()
            .join()
    }
  },
  mounted() {
    this.filterSearch()
    this.selected = this.headRows.filter(it => this.checked.some(item => item === it.title))
  },
  methods: {
    hanlderCheck(e) {
      const item = e.target.dataset.item || e.target.parentNode.dataset.item
      if (item === undefined) return
      const idx = this.selected.findIndex(
        (it) => it.title === this.rowlist[item].title
      )
      if (idx > -1) {
        this.selected = this.selected.filter((it, index) => index !== idx)
      } else if(this.selected.length < 50) {
        this.selected.push({...this.rowlist[item], _index: item})
      } else if (this.selected.length >= 50) {
        this.$Message.warning(this.$t('message.common.dss.maxcol'))
      }
    },
    filterSearch() {
      this.checkAll = false
      if (!this.searchName) {
        this.rowlist = this.headRows
      }
      this.rowlist = this.headRows
        .filter((it) => it.title.indexOf(this.searchName) > -1)
      this.layout()
    },
    getItemprops(index) {
      return {
        key: index,
        props: {
          item: this.rowlist[index],
          index: index,
        },
      }
    },
    layout() {
      let rows = Math.floor((this.height - 45) / this.size)
      this.total = this.rowlist.length
      this.remain = this.total > rows ? rows : this.total
      this.$refs.fieldList.forceRender()
    },
    changeCheckAll(v) {
      if (v) {
        const len = this.selected.length
        this.rowlist.slice(0, 50).forEach((it, index) => {
          if (len < 1 || !this.isSelect(it)) {
            this.selected.push({ ...it , _index: index})
          }
        })
        if (this.rowlist.length > 50) {
          this.$Message.warning(this.$t('message.common.dss.maxcol'))
        }
      } else {
        this.selected = this.selected.filter((item) => {
          return !this.rowlist.some((it) => it.title === item.title)
        })
      }
    },
    changeHeads() {
      if (this.selectedList.length) {
        this.$emit('on-check', this.selectedList.map(it => it.title))
      } else {
        this.$Message.warning(this.$t('message.common.checkone'))
      }
    },
    reset() {
      this.$emit('on-check', [])
    },
    close() {
      this.$emit('on-check', false)
    },
    onCancel() {
      this.searchName = ''
      this.selected = []
    },
    isSelect(item) {
      const idx = this.selected.findIndex((it) => it.title === item.title)
      return idx > -1
    },
    removeItem(idx) {
      this.selected = this.selected.filter((it, index) => index !== idx)
    },
  },
}
</script>
<style lang="scss" scoped>
@import '@dataspherestudio/shared/common/style/variables.scss';
.we-filter-view {
  width: 450px;
  position: absolute;
  height: 100%;
  padding-top: 10px;
  @include bg-color(#fff, $dark-menu-base-color);
  border: 1px solid #dcdee2;
  @include border-color($border-color-base, #525354);
  border-left: none;
  padding: 10px;
  z-index: 2;
  .select-panel {
    display: flex;
    justify-content: space-between;
  }
  .selected-list {
    width: 170px;
    overflow-y: auto;
  }
  .input {
    margin-bottom: 5px;
  }
  .icon-dir-right {
    color: currentcolor;
    font-size: 1rem;
    padding-top: 25%;
    border-left: 1px solid #eee;
    text-align: center;
    padding-left: 8px;
    border-right: 1px solid #eee;
    @include border-color($border-color-base, #525354);
  }
}
.filter-view-item {
  line-height: 26px;
  font-size: 13px;
  display: flex;
  align-items: center;
  cursor: pointer;
  .filter-view-item-text {
    width: 130px;
    margin-left: 4px;
    display: inline-block;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    vertical-align: middle;
  }
}
.buts-warp {
  display: flex;
  justify-content: space-between;
  margin-top: 10px;
  width: 90%;
  align-items: center;
}
</style>
