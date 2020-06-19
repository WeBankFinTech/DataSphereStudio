<template>
  <div class="page-bgc">
    <div class="page-bgc-header">
      <div class="header-info">
        <h1>{{$t('message.project.infoHeader')}}</h1>
        <p>{{$t('message.project.infoBodyFirstRow')}}</p>
        <p>{{$t('message.project.infoBodySecondRow')}}</p>
      </div>
    </div>

    <div class="workspace-main">
      <Card class="left">
        <h3 class="item-header">
          <span>{{this.$t('message.console.sideNavList.function.name')}}</span>
        </h3>
        <div class="app-list">
          <div class="app-item-wrap">
            <!-- <i class="project-type-list-icon" :class="item.font"
              :style="{'color': item.color, 'font-size': item.iconSize || '30px'}"
              v-if="item.font"></i> -->

            <i class="project-type-list-icon fi-exchange" style="color: rgb(102, 204, 153); font-size: 30px;"></i>
            <span class="label">进入脚本开发</span>
          </div>
        </div>
      </Card>

      <Card class="right">
        <h3 class="item-header">
          <span>{{this.$t('message.console.sideNavList.function.name')}}</span>
        </h3>
        <div class="app-list">
          <div class="app-item-wrap">
            <i class="project-type-list-icon fi-exchange" style="color: rgb(102, 204, 153); font-size: 30px;"></i>
            <span class="label">进入脚本开发</span>
          </div>
        </div>
      </Card>     
    </div>

    <div class="workspace-main">
      <div class="app-list-tabs">
        <Tabs style="flex: 1;" active-key="key1">
          <Tab-pane label="标签一" key="key1">
            <Card class="right">
              <h3 class="item-header">
                <span>{{this.$t('message.console.sideNavList.function.name')}}</span>
              </h3>
              <div class="app-list">
                <div class="app-item-wrap">
                  <i class="project-type-list-icon fi-exchange" style="color: rgb(102, 204, 153); font-size: 30px;"></i>
                  <span class="label">进入脚本开发</span>
                </div>
              </div>
            </Card>    
          </Tab-pane>
          <Tab-pane label="标签二" key="key2">标签二的内容</Tab-pane>
          <Tab-pane label="标签三" key="key3">标签三的内容</Tab-pane>
        </Tabs>

        <div class="input-wrap">
          <i-input icon="ios-search" placeholder="请输入..." style="width: 200px"></i-input>
        </div>
        
      </div>
    </div>

    
  </div>
  
</template>

<script>
export default {
    
}
</script>
<style lang="scss" scoped src="../../../assets/styles/workspace.scss"></style>