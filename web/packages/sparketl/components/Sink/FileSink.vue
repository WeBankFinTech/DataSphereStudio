<!-- eslint-disable vue/no-mutating-props -->
<!-- eslint-disable vue/no-v-model-argument -->
<template>
  <div style="margin-top: 10px">
    <Form ref="formInline" :model="value" :label-width="90">
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
      <Row :gutter="16">
        <Col span="12">
          <FormItem
            :label="$t('plugin.saveMode')"
            prop="saveMode"
            :rules="{
              required: true,
              message: `${$t('plugin.enter')}${$t('plugin.saveMode')}`,
            }"
          >
            <RadioGroup v-model="value.saveMode" type="button">
              <Radio label="append">
                <span>{{ $t('plugin.append') }}</span>
              </Radio>
              <Radio label="overwrite">
                <span>{{ $t('plugin.cover') }}</span>
              </Radio>
            </RadioGroup>
          </FormItem>
        </Col>
        <Col span="12">
          <FormItem :label="$t('plugin.partitions')" prop="numPartitions">
            <Input v-model="value.numPartitions"></Input>
          </FormItem>
        </Col>
      </Row>
      <Row :gutter="16">
        <Col span="12">
          <FormItem
            :label="$t('plugin.sourceTable')"
            prop="sourceTable"
            :rules="{
              required: true,
              message: `${$t('plugin.enter')}${$t('plugin.sourceTable')}`,
            }"
          >
            <Input v-model="value.sourceTable"></Input>
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
      <FormItem :label="$t('plugin.sourceSql')" prop="sourceQuery">
        <Codemirror
          ref="sourceQueryRef"
          v-model:value="value.sourceQuery"
          cmMode="sql"
          @change="editorChange"
        />
      </FormItem>
      <FormItem :label="$t('plugin.partitionsColumn')" prop="partitionBy">
        <input-string-array v-model:value="value.partitionBy" />
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
import Codemirror from '../Codemirror'
export default {
  name: 'FileSink',
  props: {
    value: { type: Object, isRequired: true },
    name: String,
  },
  components: {
    InputStringArray,
    InputStringMap,
    Codemirror,
  },
  data() {
    return {
      form: this.value,
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
    editorChange(value) {
      this.$emit('input', {
        ...this.value,
        sourceQuery: value,
      })
    },
  },
}
</script>
