<template>
  <Form class="user-serchbar" ref="searchBar" :model="searchBar" :rules="ruleInline" inline>
    <Input
      style="width:250px"
      v-model="searchBar.searchText"
      :placeholder="searchBar.title">
      <Icon
        slot="suffix"
        type="ios-search"/>
    </Input>
    <FormItem v-if="optionType.engine" prop="engine" :label="$t('message.workspaceManagement.department') + ':'" class="user-serchbar-dep">
      <Select v-model="searchBar.engine" style="min-width:120px;">
        <Option
          v-for="(item) in optionType.engine"
          :label="item.label"
          :value="item.value"
          :key="item.value"
        />
      </Select>
    </FormItem>
    <FormItem v-if="optionType.status" prop="status" :label="optionType.title" class="user-serchbar-role">
      <Select v-model="searchBar.status" style="min-width:120px;">
        <Option
          v-for="(item) in optionType.status"
          :label="item.label"
          :value="item.value"
          :key="item.value"
        />
      </Select>
    </FormItem>
    <FormItem class='float-right'>
      <Button
        type="primary"
        @click="search"
        style="margin-right:20px;width:80px"
      >{{$t('message.workspaceManagement.find')}}</Button>
      <Button
        type="success"
        @click="createroles"
        style="margin-right:20px;width:80px"
      >{{$t('message.workspaceManagement.create')}}</Button>
      <Button
        type="success"
        @click="setAutoJoin"
        style="width: 100px"
        :title="$t('message.workspaceManagement.autojointip')"
      >{{$t('message.workspaceManagement.autojoin')}}</Button>
    </FormItem>
  </Form>
</template>
<script>
export default {
  props: {
    optionType: Object,
    searchBar: Object,
  },
  data() {
    return {
      ruleInline: {},
    }
  },
  mounted() {

  },
  methods: {
    search(){
      this.$emit("click-serach")
    },
    createroles(){
      this.$emit("click-creater")
    },
    setAutoJoin() {
      this.$emit("click-autojoin")
    }
  },
}
</script>
<style lang="scss" scoped>
  .user-serchbar{
    display: flex;
    justify-content: space-between;
    .user-serchbar-dep{
      display: flex !important;
    }
    .user-serchbar-role{
      display: flex !important;
    }
    .float-right{
      .ivu-form-item-content{
        display: flex;
      }
    }
  }
</style>
