<template>
  <div>
    <Dropdown trigger="click" @on-click="handlePlugin" placement="bottom-start">
      <Button type="primary">
        {{ $t("plugin.common.addPlugin") }}
        <Icon type="ios-arrow-down"></Icon>
      </Button>
      <DropdownMenu slot="list">
        <Dropdown placement="right-start">
          <DropdownItem>
            source
            <Icon type="ios-arrow-forward"></Icon>
          </DropdownItem>
          <DropdownMenu slot="list">
            <DropdownItem
              v-for="pluginName in plugins.sources"
              :key="`source-${pluginName}`"
              :name="`source##${pluginName}`"
            >
              {{ pluginName }}
            </DropdownItem>
          </DropdownMenu>
        </Dropdown>
        <Dropdown placement="right-start">
          <DropdownItem divided>
            transformation
            <Icon type="ios-arrow-forward"></Icon>
          </DropdownItem>
          <DropdownMenu slot="list">
            <DropdownItem
              v-for="pluginName in plugins.transformations"
              :key="`transformation-${pluginName}`"
              :name="`transformation##${pluginName}`"
            >
              {{ pluginName }}
            </DropdownItem>
          </DropdownMenu>
        </Dropdown>
        <Dropdown placement="right-start">
          <DropdownItem divided>
            sink
            <Icon type="ios-arrow-forward"></Icon>
          </DropdownItem>
          <DropdownMenu slot="list">
            <DropdownItem
              v-for="pluginName in plugins.sinks"
              :key="`sink-${pluginName}`"
              :name="`sink##${pluginName}`"
            >
              {{ pluginName }}
            </DropdownItem>
          </DropdownMenu>
        </Dropdown>
      </DropdownMenu>
    </Dropdown>
  </div>
</template>

<script>
export default {
  name: "TypedPlugin",
  props: {
    addPlugin: {
      type: Function,
      default: () => {},
    },
  },
  data() {
    return {
      plugins: {
        sources: ["jdbc", "managed_jdbc", "file"],
        transformations: ["sql"],
        sinks: ["hive", "jdbc", "managed_jdbc", "file"],
      },
    };
  },
  methods: {
    handlePlugin(item) {
      if (item) {
        let arr = item.split("##");
        if (arr.length == 2) {
          this.addPlugin({ type: arr[0], name: arr[1] });
        }
      }
    },
  },
};
</script>

<style scoped></style>
