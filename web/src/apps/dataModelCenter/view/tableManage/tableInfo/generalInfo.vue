<template>
  <div>
    <Card dis-hover>
      <div slot="title">
        <Icon type="ios-home" :size="24" />
        <span> 表管理 </span>
        /
        <span>table1</span>
        <Tag color="primary">V2</Tag>
      </div>
      <div slot="extra">
        <Icon type="ios-copy" :size="24" />
        <Icon type="ios-create" :size="24" />
        <Icon type="ios-star" :size="24" />
        <Dropdown style="margin-left: 20px">
          <Button type="primary"> 更多 </Button>
          <DropdownMenu slot="list">
            <DropdownItem>查看授权</DropdownItem>
            <DropdownItem>生成api</DropdownItem>
            <DropdownItem>导出表</DropdownItem>
          </DropdownMenu>
        </Dropdown>
      </div>

      <p style="margin-bottom: 16px">这是一段表的简单描述</p>
      <p>
        <Tag color="blue">HIVE</Tag>
        <Tag color="blue">离线表</Tag>
        <Tag color="blue">物理表</Tag>
      </p>
    </Card>
  </div>
</template>

<script>
export default {};
</script>

<style>
</style>
