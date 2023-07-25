<template>
  <Dropdown :transfer="true" trigger="click" style="margin-left: 20px"  @on-click="commonAction">
    {{$t('message.apiServices.query.more')}}
    <Icon type="ios-arrow-down"></Icon>
    <DropdownMenu slot="list">
      <DropdownItem v-for="(item, index) in dropdownList" :name="item.id" :key="index">{{item.title}}</DropdownItem>
    </DropdownMenu>
  </Dropdown>
</template>
<script>
import i18n from '@dataspherestudio/shared/common/i18n';
export default {
  props: {
    dropdownList: {
      type: Array,
      default: () => {
        return [
          {id: 1, title: i18n.t('message.apiServices.manage')},
          {id: 2, title: i18n.t('message.apiServices.disableenable')},
          // {id: 3, title: '更新'},
        ]
      }
    }
  },
  data() {
    return {
    }
  },
  methods: {
    commonAction(id) {
      this.$emit('commonAction', id)
    }
  }
}
</script>

