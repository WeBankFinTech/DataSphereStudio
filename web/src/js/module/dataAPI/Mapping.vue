<template>
  <div>
    <Card>
      <p slot="title">
        {{$t(`message.api.${name}`)}}
      </p>
      <Form ref="apiForm">
        <FormItem
          v-for="(item, index) in formData"
          :key="index"
        >
          <Row :gutter="2">
            <Col span="10">
            <Input type="text" v-model="item.name" :placeholder="$t(`message.api.${name}`)" />
            </Col>
            <Col span="10">
            <Select v-model="item.type" :placeholder="$t('message.api.selectType')">
              <Option v-for="item in options" :value="item.value" :key="item.value">{{ item.label }}</Option>
            </Select>
            </Col>
            <Col span="2" offset="1" v-if="isDelete">
            <Button type="error" size="small" @click="remove(index)">{{$t('message.api.delete')}}</Button>
            </Col>
          </Row>
        </FormItem>
      </Form>
    </Card>
  </div>
</template>

<script>
export default {
  props: {
    columns: {
      type: Array,
      default: () => []
    },
    options: {
      type: Array,
      default: () => []
    },
    isDelete: {
      type: Boolean,
      default: false
    },
    name: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      formData: [
        { name: '', type: '' }
      ],
    }
  },
  methods: {
    add() {
      this.formData.push({
        name: '',
        type: ''
      });
    },
    remove(index) {
      this.formData.splice(index, 1);
      this.$emit('remove', index);
    },
    validate() {
      const valid = [];
      for (const item of this.formData) {
        if (!item.name || !item.type) {
          valid.push(false);
        } else {
          valid.push(true);
        }
      }
      if (valid.includes(false)) {
        return false;
      } else {
        return true;
      }
    }
  },
}
</script>

<style>

</style>