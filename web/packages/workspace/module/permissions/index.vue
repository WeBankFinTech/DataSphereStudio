<template>
  <div class="permission-wrap">
    <div class="page-bgc-header">
      <div class="header-info">
        {{ $t("message.workspace.permissions") }}
      </div>
    </div>
    <div class="main">
      <div class="slider">
        <div
          v-for="item in permissionsMenus"
          :key="item.name"
          :class="{ menuItem: true, menuChoosed: choosed === item.name }"
          @click="changePermission(item)"
        >
          {{ item.title }}
        </div>
      </div>
      <div class="content">
        <component v-bind:is="choosedComponent"></component>
      </div>
    </div>
    <div class="footer"></div>
  </div>
</template>
<script>
import departManagement from "./module/departManagement";
import personManagement from "./module/personManagement";
import i18n from '@dataspherestudio/shared/common/i18n';
const permissionsMenus = [
  {
    title: i18n.t("message.permissions.departManagement"),
    name: "departManagement"
  },
  {
    title: i18n.t("message.permissions.personManagement"),
    name: "personManagement"
  }
];
export default {
  components: {
    departManagement,
    personManagement
  },
  data() {
    return {
      permissionsMenus,
      choosed: "departManagement",
      choosedComponent: "departManagement"
    };
  },
  created() {},
  methods: {
    changePermission(item) {
      this.choosed = item.name;
      this.choosedComponent = item.name;
    }
  }
};
</script>
<style lang="scss" scoped src="./index.scss"></style>
