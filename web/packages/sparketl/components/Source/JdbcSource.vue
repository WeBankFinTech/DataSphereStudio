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
      <FormItem :label="$t('plugin.querySql')" prop="query">
        <Codemirror
          ref="sqlEditor"
          v-model:value="value.query"
          cmMode="sql"
          @change="
            (value) => {
              this.value.query = value
            }
          "
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
export default {
  name: 'JdbcSource',
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
