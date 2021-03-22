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
export default {
  props: {
    dropdownList: {
      type: Array,
      default: () => {
        return [
          {id: 1, title: '管理'},
          {id: 2, title: '禁用/启用'},
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

