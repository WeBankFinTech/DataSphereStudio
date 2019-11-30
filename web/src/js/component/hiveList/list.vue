<template>
  <ul>
    <li
      v-for="(item, index) in data"
      v-show="item.isVisible || item.dataType === 'field'"
      :key="index"
      :title="getTitle(item)"
      class="v-hiveColumn"
      @click.stop="dispatch($event, 'we-click', item, index)"
      @contextmenu.prevent.stop="dispatch($event, 'we-contextmenu', item, index)"
      @mousedown.prevent
      @dblclick.prevent.stop="dispatch($event, 'we-dblclick', item, index)">
      <span
        :class="getDBCls(item)"/>
      <div class="inline-box">
        <span class="v-ellipsis">{{ item.name }}</span>
        <ul
          v-for="(table, tableIndex) in item.children"
          v-show="item.isOpen && table.isVisible"
          :key="tableIndex"
          :title="getTitle(table)"
          class="v-hiveColumn"
          @click.stop="dispatch($event, 'we-click', table, tableIndex)"
          @contextmenu.prevent.stop="dispatch($event, 'we-contextmenu', table, tableIndex)"
          @mousedown.prevent
          @dblclick.prevent.stop="dispatch($event, 'we-dblclick', table, tableIndex)">
          <span
            :class="getDBCls(table)"/>
          <div class="inline-box">
            <span class="v-ellipsis">{{ table.name }}</span>
            <li
              v-for="(field, fieldIndex) in table.children"
              v-show="table.isOpen"
              :key="fieldIndex"
              :title="getTitle(table)"
              class="v-hiveColumn"
              @click.stop="dispatch($event, 'we-click', field, fieldIndex)"
              @contextmenu.prevent.stop="dispatch($event, 'we-contextmenu', field, fieldIndex)"
              @mousedown.prevent
              @dblclick.prevent.stop="dispatch($event, 'we-dblclick', field, fieldIndex)">
              <div
                v-if="fieldIndex == 100"
                :title="$t('message.hiveTableDesc.viewTableColumns')">
                <span class="v-hivetable-text">{{$t('message.hiveTableDesc.viewTableColumns')}}</span>
              </div>
              <div v-else-if="fieldIndex > 100" />
              <div v-else>
                <span :class="getDBCls(field)"/>
                <span
                  :title="field.type"
                  class="v-hivetable-type">[{{ field.type }}]</span>
                <span
                  :title="field.name"
                  class="v-hivetable-text">{{ field.name }}</span>
              </div>
            </li>
          </div>
        </ul>
      </div></li>
  </ul>
</template>
<script>
export default {
  name: 'WeHiveList',
  props: {
    data: Array,
    title: {
      type: String,
      default: '',
    },
  },
  data() {
    return {
      root: null,
    };
  },
  created() {
    const parent = this.$parent;
    this.root = parent.isRoot ? parent : parent.root;
  },
  methods: {
    dispatch(ev, name, item, index) {
      this.root.$emit(name, {
        ev, item, index,
      });
    },
    getTitle(item) {
      return this.title ? item[this.title] : '';
    },
    getDBCls(item) {
      let cls = item.isOpen ? (item.iconCls + ' open') : item.iconCls;
      return cls;
    },
  },
};
</script>
<style lang="sass" src="./index.sass"></style>
