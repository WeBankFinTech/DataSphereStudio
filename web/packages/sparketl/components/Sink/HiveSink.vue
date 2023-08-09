<!-- eslint-disable vue/no-mutating-props -->
<!-- eslint-disable vue/no-v-model-argument -->
<template>
  <div style="margin-top: 10px">
    <Form ref="formInline" :model="value" :label-width="90">
      <Row :gutter="16">
        <Col span="12">
          <FormItem
            :label="$t('plugin.targetDatabase')"
            prop="targetDatabase"
            :rules="{
              required: true,
              message: `${$t('plugin.enter')}${$t('plugin.targetDatabase')}`,
            }"
          >
            <Input v-model="value.targetDatabase"></Input>
          </FormItem>
        </Col>
        <Col span="12">
          <FormItem
            :label="$t('plugin.targetTable')"
            prop="targetTable"
            :rules="{
              required: true,
              message: `${$t('plugin.enter')}${$t('plugin.targetTable')}`,
            }"
          >
            <Input v-model="value.targetTable"></Input>
          </FormItem>
        </Col>
      </Row>
      <Row :gutter="16">
        <Col span="12">
          <FormItem :label="$t('plugin.partitions')" prop="numPartitions">
            <Input v-model="value.numPartitions"></Input>
          </FormItem>
        </Col>
        <Col span="12">
          <FormItem :label="$t('plugin.sourceTable')" prop="sourceTable">
            <Input v-model="value.sourceTable"></Input>
          </FormItem>
        </Col>
      </Row>
      <Row :gutter="16">
        <Col span="12">
          <FormItem
            :label="$t('plugin.filePriority')"
            prop="writeAsFile"
            :rules="{
              required: true,
              message: `${$t('plugin.enter')}${$t('plugin.filePriority')}`,
            }"
          >
            <i-switch v-model="value.writeAsFile">
              <span slot="true">true</span>
              <span slot="false">false</span>
            </i-switch>
          </FormItem>
        </Col>
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
      </Row>
      <FormItem
        :label="$t('plugin.strongCheck')"
        prop="strongCheck"
        :rules="{
          required: true,
          message: `${$t('plugin.enter')}${$t('plugin.strongCheck')}`,
        }"
      >
        <i-switch v-model="value.strongCheck">
          <span slot="true">true</span>
          <span slot="false">false</span>
        </i-switch>
      </FormItem>
      <FormItem :label="$t('plugin.sourceSql')" prop="sourceQuery">
        <Codemirror
          ref="sourceQueryRef"
          v-model:value="value.sourceQuery"
          cmMode="sql"
          @change="editorChange"
        />
      </FormItem>
      <FormItem :label="$t('plugin.parameter')" prop="options">
        <input-string-map v-model:value="value.options" />
      </FormItem>
    </Form>
  </div>
</template>
<script>
import { StorageLevels, FileSerializers } from '../../utils/BaseConstant'
import InputStringMap from '../InputStringMap'
import Codemirror from '../Codemirror'
import { Databases } from '../../utils/BaseConstant'
export default {
  name: 'HiveSink',
  props: {
    value: { type: Object, isRequired: true },
    name: String,
  },
  components: {
    InputStringMap,
    Codemirror,
  },
  data() {
    return {
      storageLevels: StorageLevels,
      fileSerializers: FileSerializers,
      databasesList: Databases,
    }
  },
  // watch: {
  //   value(val) {
  //     this.form = val;
  //   },
  // },
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
