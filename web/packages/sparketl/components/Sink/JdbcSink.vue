<!-- eslint-disable vue/no-v-model-argument -->
<!-- eslint-disable vue/no-mutating-props -->
<template>
  <div style="margin-top: 10px">
    <Form ref="formInline" :model="value" :label-width="90">
      <FormItem
        :label="$t('plugin.jdbcUrl')"
        prop="url"
        :rules="{
          required: true,
          message: `${$t('plugin.enter')}${$t('plugin.jdbcUrl')}`,
        }"
      >
        <Input v-model="value.url" />
      </FormItem>
      <Row :gutter="16">
        <Col span="12">
          <FormItem
            :label="$t('plugin.username')"
            prop="user"
            :rules="{
              required: true,
              message: `${$t('plugin.enter')}${$t('plugin.username')}`,
            }"
          >
            <Input v-model="value.user"></Input>
          </FormItem>
        </Col>
        <Col span="12">
          <FormItem
            :label="$t('plugin.password')"
            prop="password"
            :rules="{
              required: true,
              message: `${$t('plugin.enter')}${$t('plugin.password')}`,
            }"
          >
            <Input v-model="value.password" type="password"></Input>
          </FormItem>
        </Col>
      </Row>
      <Row :gutter="16">
        <Col span="12">
          <FormItem
            :label="$t('plugin.driver')"
            prop="driver"
            :rules="{
              required: true,
              message: `${$t('plugin.enter')}${$t('plugin.driver')}`,
            }"
          >
            <Input v-model="value.driver"></Input>
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
      <FormItem :label="$t('plugin.sourceSql')" prop="sourceQuery">
        <Codemirror
          ref="sourceQueryRef"
          v-model:value="value.sourceQuery"
          cmMode="sql"
          @change="editorChange"
        />
      </FormItem>

      <FormItem
        v-for="(item, index) in value.preQueries"
        :key="index"
        :label="$t('plugin.preQueries')"
        :prop="'preQueries.' + index"
      >
        <Row>
          <Col span="18">
            <Input type="text" v-model="value.preQueries[index]"></Input>
          </Col>
          <Col span="4" offset="1">
            <Button @click="() => value.preQueries.splice(index, 1)">{{
              $t('plugin.delete')
            }}</Button>
          </Col>
        </Row>
      </FormItem>
      <FormItem label="">
        <Button type="dashed" @click="() => this.value.preQueries.push('')">{{
          $t('plugin.addPreQueries')
        }}</Button>
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
  name: 'JdbcSink',
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
  methods: {
    addPlugin() {
      console.log(2323)
    },
    formSubmit() {
      return this.$refs['formInline']
    },
    editorChange(val) {
      this.$emit('input', {
        ...this.value,
        sourceQuery: val,
      })
    },
  },
}
</script>
