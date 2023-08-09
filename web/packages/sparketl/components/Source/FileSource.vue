<!-- eslint-disable vue/no-v-model-argument -->
<!-- eslint-disable vue/no-mutating-props -->
<template>
  <div style="margin-top: 10px">
    <Form ref="formInline" :model="value" :label-width="90">
      <Row :gutter="16">
        <Col span="12">
          <FormItem
            :label="$t('plugin.registryTableName')"
            prop="resultTable"
            :rules="{
              required: true,
              message: `${$t('plugin.enter')}${$t('plugin.registryTableName')}`,
            }"
          >
            <Input v-model="value.resultTable"></Input>
          </FormItem>
        </Col>
        <Col span="12">
          <FormItem
            :label="$t('plugin.fileFormat')"
            prop="serializer"
            :rules="{
              required: true,
              message: `${$t('plugin.enter')}${$t('plugin.fileFormat')}`,
            }"
          >
            <Select v-model="value.serializer">
              <Option
                v-for="item in fileSerializers"
                :value="item"
                :key="item"
              >{{ item }}</Option
              >
            </Select>
          </FormItem>
        </Col>
      </Row>
      <Row :gutter="16">
        <Col span="12">
          <FormItem :label="$t('plugin.enableCache')" prop="persist">
            <i-switch v-model="value.persist" size="large">
              <span slot="open">{{ $t('plugin.open') }}</span>
              <span slot="close">{{ $t('plugin.close') }}</span>
            </i-switch>
          </FormItem>
        </Col>
        <Col span="12" v-show="value.persist">
          <FormItem :label="$t('plugin.storageLevel')" prop="storageLevel">
            <Select v-model="value.storageLevel">
              <Option
                v-for="item in storageLevels"
                :value="item.value"
                :key="item.value"
              >{{ item.label }}</Option
              >
            </Select>
          </FormItem>
        </Col>
      </Row>

      <FormItem
        :label="$t('plugin.path')"
        prop="path"
        :rules="{
          required: true,
          message: `${$t('plugin.enter')}${$t('plugin.path')}`,
        }"
      >
        <Input v-model="value.path"></Input>
      </FormItem>
      <FormItem :label="$t('plugin.columnNames')" prop="columnNames">
        <input-string-array v-model:value="value.columnNames" />
      </FormItem>
      <FormItem :label="$t('plugin.parameter')" prop="options">
        <input-string-map v-model:value="value.options" />
      </FormItem>
    </Form>
  </div>
</template>
<script>
import { StorageLevels, FileSerializers } from '../../utils/BaseConstant'
import InputStringArray from '../InputStringArray'
import InputStringMap from '../InputStringMap'
export default {
  name: 'FileSource',
  props: {
    value: { type: Object, isRequired: true },
    name: String,
  },
  components: {
    InputStringArray,
    InputStringMap,
  },
  data() {
    return {
      storageLevels: StorageLevels,
      fileSerializers: FileSerializers,
    }
  },
  methods: {
    addPlugin() {
      console.log(2323)
    },
    formSubmit() {
      return this.$refs['formInline']
    },
  },
}
</script>
