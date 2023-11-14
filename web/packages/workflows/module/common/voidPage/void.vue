<template>
  <div class="void-page-wrap">
    <div class="void-page-left">
      <div class="void-page-left-title">
        <span>{{ $t('message.workflow.Development') }}</span>
      </div>
      <div class="void-page-left-main">
        <div class="void-page-left-main-img">
          <SvgIcon icon-class="empty" width="200px" height="200px" />
        </div>
        <div class="void-page-left-main-tip">
          <span>{{ $t('message.workflow.Noopen') }}</span>
        </div>
        <div class="void-page-left-main-button">
          <Button @click.stop="addProject">{{ $t('message.workflow.Add') }}</Button>
          <Button
            type="primary"
            style="margin-left: 8px"
            icon="md-add"
            @click.stop="addWrokFlow"
          >{{ $t('message.workflow.Addworkflow') }}</Button>
          <Button
            type="primary"
            style="margin-left: 8px"
            @click.stop="importWrokFlow"
          >{{ $t('message.workflow.Importworkflow') }}</Button>
        </div>
      </div>
    </div>
    <div class="void-page-right">
      <div class="void-page-right-title" v-if="lastWorkflowList.length">
        <span>{{ $t('message.workflow.Recent') }}</span>
      </div>
      <div class="void-page-right-main">
        <div
          v-for="(item, idx) in lastWorkflowList"
          :key="idx"
          class="void-page-right-list"
        >
          <a class="void-page-right-list-link" @click="goto(item)">{{ item.name }}</a>
        </div>
      </div>
    </div>
    <!--导入弹窗-->
    <Modal v-model="importModalShow" :footer-hide="true" :title="$t('message.workflow.Importworkflow')">
      <Form
        :label-width="100"
        ref="projectForm"
        v-if="importModalShow"
        :model="workflowDataCurrent"
        :rules="formValid">
        <FormItem
          label="dwsCookie"
          prop="dwsCookie">
          <Input
            v-model="workflowDataCurrent.dwsCookie"
            type="text"
            :maxlength=201
            :placeholder="$t('message.workflow.inputImportWorkflowDwsCookie')"></Input>
        </FormItem>
        <Button style="margin-left:100px" type="primary" @click="importConfirm" :loading="isConfirmLoading">导入</Button>
      </Form>
    </Modal>
  </div>
</template>

<script>
import api from '@dataspherestudio/shared/common/service/api';
export default {
  name: "Void",
  props: {
    addProject: {
      type: Function,
      default: () => {}
    },
    addWrokFlow: {
      type: Function,
      default: () => {}
    },
    goto: {
      type: Function,
      default: () => {}
    }
  },
  data() {
    return {
      lastWorkflowList: [],
      isConfirmLoading: false,
      importModalShow: false,
      workflowDataCurrent: {
        dwsCookie: ''
      }
    };
  },
  computed: {
    formValid() {
      return {
        dwsCookie: [
          { required: true, message: this.$t('message.workflow.inputImportWorkflowDwsCookie'), trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    importWrokFlow() {
      this.importModalShow = true
    },
    importConfirm() {
      this.$refs.projectForm.validate((valid) => {
        if (valid) {
          this.isConfirmLoading = true
          api.fetch('/dss/framework/orchestrator/dwsmigrate/migrateProject', {
            dwsCookie: this.workflowDataCurrent.dwsCookie,
            projectName: this.$route.query.projectName || '',
            projectID: this.$route.query.projectID || ''
          }, 'get').then(() => {
            this.$Message.success('导入成功')
            this.isConfirmLoading = false
            this.$emit('importWorkflowSuccess', this.$route.query.projectID)
            this.importModalShow = false
          }).catch(() => {
            this.isConfirmLoading = false
          })
        } else {
          this.isConfirmLoading = false;
          this.$Message.warning(this.$t("message.workflow.failedNotice"));
        }
      });
    }
  }
};
</script>

<style lang="scss" scoped>
@import "@dataspherestudio/shared/common/style/variables.scss";

.void-page-wrap {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  @include bg-color(#fff, $dark-workspace-body-bg-color);
  .void-page-left {
    min-width: 240px;
    margin-right: 106px;
    &-title {
      height: 33px;
      line-height: 33px;
      text-align: left;
      @include font-color($light-text-color, $dark-text-color);


      font-size: 24px;
      @include border-color(rgba(0, 0, 0, 0.65), $dark-border-color-base);
      font-weight: 400;
    }
    &-main {
      &-img {
        text-align: left;
        @include font-color(#ebebeb, #3f434c);
      }
      &-tip {

        font-size: 14px;
        @include border-color(rgba(0, 0, 0, 0.65), $dark-border-color-base);
        letter-spacing: 0;
        text-align: center;
        line-height: 28px;
        height: 28px;
        font-weight: 400;
        margin-bottom: 16px;
        @include font-color($light-text-color, $dark-text-color);
      }
      &-button {
        text-align: left;
        height: 32px;
      }
    }
  }

  .void-page-right {
    margin-left: 106px;
    &-title {
      box-sizing: content-box;
      height: 25px;
      line-height: 25px;
      padding: 16px 0px;
      @include font-color($light-text-color, $dark-text-color);


      font-size: 18px;
      @include border-color(rgba(0, 0, 0, 0.65), $dark-border-color-base);
      font-weight: 400;
    }
    .void-page-right-main {
      .void-page-right-list {
        margin-bottom: 16px;

        font-size: 14px;
        color: #2e92f7;
        font-weight: 400;

        &-link {
          text-decoration: none;
        }
      }
    }
  }
}
</style>
