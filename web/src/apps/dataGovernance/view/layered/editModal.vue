<template>
  <Modal title="新建/编辑分层" v-model="visible" @on-cancel="cancelCallBack">
    <Form
      ref="formRef"
      :rules="ruleValidate"
      :model="formState"
      label-position="top"
    >
      <FormItem label="名称" prop="name">
        <Input v-model="formState.name" placeholder="名称" :disabled="mode === 'edit'" />
      </FormItem>
      <!--<FormItem label="英文名" prop="enName">
        <Input v-model="formState.enName" placeholder="英文名"></Input>
      </FormItem>
      <FormItem label="负责人" prop="owner">
        <Input v-model="formState.owner" placeholder="负责人"></Input>
      </FormItem>
      <FormItem label="排序" prop="order">
        <Input
          v-model="formState.order"
          type="number"
          placeholder="排序"
        ></Input>
      </FormItem>
      <FormItem label="可用角色" prop="principalName">
        <Select
          v-model="formState.principalName"
          multiple
          placeholder="可用角色"
        >
          <Option
            v-for="item in principalNameList"
            :value="item.value"
            :key="item.value"
          >
            {{ item.label }}
          </Option>
        </Select>
      </FormItem>
      <FormItem label="可用库" prop="databases">
        <Select v-model="formState.databases" multiple placeholder="可用库">
          <Option
            v-for="item in databasesList"
            :value="item.value"
            :key="item.value"
          >
            {{ item.label }}
          </Option>
        </Select>
      </FormItem>-->
      <FormItem label="描述" prop="description">
        <Input
          type="textarea"
          v-model="formState.description"
          placeholder="描述"
        ></Input>
      </FormItem>
    </Form>
    <Spin v-if="loading" fix></Spin>
    <template slot="footer">
      <Button @click="handleCancel">取消</Button>
      <Button type="primary" @click="handleOk">确定</Button>
    </template>
  </Modal>
</template>

<script>
import {
  createLayersCustom,
  getLayersById,
  editLayersCustom,
} from '../../service/api'
export default {
  model: {
    prop: '_visible',
    event: '_changeVisible',
  },
  computed: {
    visible: {
      get() {
        return this._visible
      },
      set(val) {
        this.$emit('_changeVisible', val)
      },
    },
  },
  props: {
    // 是否可见
    _visible: {
      type: Boolean,
      required: true,
    },
    // 模式
    mode: {
      type: String,
      required: true,
    },
    id: {
      type: String,
    },
  },
  emits: ['finish', '_changeVisible'],
  data() {
    const validateName = (rule, value, callback) => {
      if (value) {
        const invalid = /\ /.test(value) || !(/^[a-z|A-Z]/.test(value))
        if (value.length > 30) {
          this.formState.name = value.substring(0, 30)
          callback(new Error('最多30字'))
          return
        }
        if (!invalid) {
          callback();
        } else {
          callback(new Error('不能包含空格,且必须以字母开头'))
        }
      } else {
        callback(new Error('不能为空'));
      }
    };
    return {
      // 验证规则
      ruleValidate: {
        name: [
          {
            required: true,
            validator: validateName
          },
        ],
        enName: [
          {
            required: true,
          },
        ],
        owner: [
          {
            required: true,
          },
        ],
      },
      // 是否加载中
      loading: false,
      // 表单数据
      formState: {
        name: '',
        description: ''
      },
      databasesList: [
        {
          value: 'New York',
          label: 'New York',
        },
        {
          value: 'London',
          label: 'London',
        },
      ],
      principalNameList: [
        {
          value: 'New York',
          label: 'New York',
        },
        {
          value: 'London',
          label: 'London',
        },
      ],
    }
  },
  watch: {
    _visible(val) {
      if (val && this.id) this.handleGetById(this.id)
    },
  },
  methods: {
    async handleGetById(id) {
      this.loading = true
      let { result } = await getLayersById(id)
      this.loading = false
      this.formState.name = result.name
      /*this.formState.owner = item.owner
      this.formState.enName = item.enName
      this.formState.principalName = item.principalName.split(',')
      this.formState.order = item.sort
      this.formState.databases = item.dbs.split(',')*/
      this.formState.description = result.description
    },
    cancelCallBack() {
      this.$refs['formRef'].resetFields()
    },
    handleCancel() {
      this.$refs['formRef'].resetFields()
      this.$emit('_changeVisible', false)
    },
    async handleOk() {
      this.$refs['formRef'].validate(async (valid) => {
        if (valid) {
          try {
            const classificationDefs = []
            if (this.mode === 'create') {
              this.loading = true
              classificationDefs.push(
                Object.assign({}, this.formState, {
                  superTypes: ["layer"],
                })
              )
              await createLayersCustom(
                { classificationDefs }
              )
              this.loading = false
            }
            if (this.mode === 'edit') {
              this.loading = true
              classificationDefs.push(
                Object.assign({}, this.formState, {
                  superTypes: ["layer"],
                })
              )
              await editLayersCustom(
                { classificationDefs }
              )
              this.loading = false
            }
            this.$refs['formRef'].resetFields()
            this.$emit('_changeVisible', false)
            this.$emit('finish')
          } catch (error) {
            this.loading = false
            console.log(error)
          }
        }
      })
    },
  },
}
</script>

<style scoped lang="less"></style>
