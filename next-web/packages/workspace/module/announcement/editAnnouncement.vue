<template>
  <FForm
    ref="formRef"
    :label-width="100"
    label-position="right"
    :model="formData"
    :rules="formRules"
  >
    <FFormItem :label="$t('announcement.announcementContent')" prop="content">
      <FInput
        v-model="formData.content"
        type="textarea"
        :placeholder="$t('announcement.pleaseEnter')"
        clearable
        show-word-limit
        :maxlength="500"
        :autosize="{ minRows: 3, maxRows: 3 }"
      />
    </FFormItem>
    <FFormItem
      :label="$t('announcement.announcementStartTime')"
      prop="startTime"
    >
      <FDatePicker
        v-model="formData.startTime"
        type="datetime"
        :hour-step="1"
        :minute-step="1"
        :second-step="1"
        :min-date="new Date()"
        :placeholder="$t('announcement.startTime')"
        default-time="00:00:00"
      />
    </FFormItem>
    <FFormItem :label="$t('announcement.announcementEndTime')" prop="endTime">
      <FDatePicker
        v-model="formData.endTime"
        type="datetime"
        :hour-step="1"
        :minute-step="1"
        :second-step="1"
        :min-date="endMinDate"
        :placeholder="$t('announcement.endTime')"
        default-time="00:00:00"
      />
    </FFormItem>
  </FForm>
</template>
<script lang="ts" setup>
import { ref, computed, onMounted } from 'vue';
import { useI18n } from 'vue-i18n';
import { FForm, FMessage } from '@fesjs/fes-design';
import { request } from '@dataspherestudio/shared';

const { t: $t } = useI18n();
interface FormDataType {
  content: string;
  startTime: string;
  endTime: string;
}

const props = defineProps({
  api: {
    type: Object,
    default: () => ({}),
  },
});
const init = (): FormDataType => ({
  content: '',
  startTime: '',
  endTime: '',
});
const formData = ref<FormDataType>(init());

onMounted(() => {
  formData.value = init();
});

const formRules = computed(() => ({
  content: [
    {
      required: true,
      message: $t('announcement.pleaseEnter'),
      trigger: ['input', 'blur'],
    },
  ],
  startTime: [
    {
      required: true,
      message: $t('announcement.pleaseSelect'),
      trigger: ['change', 'blur'],
    },
  ],
  endTime: [
    {
      required: true,
      message: $t('announcement.pleaseSelect'),
      trigger: ['change', 'blur'],
    },
    {
      validator: (rule: unknown, value: number) => {
        if (!formData.value.startTime || value > +formData.value.startTime) {
          return true;
        }
        return false;
      },
      message: '结束时间必须大于起始时间',
      trigger: ['change', 'blur'],
    },
  ],
}));

const endMinDate = computed(() => formData.value.startTime || new Date());

// 自身校验
const formRef = ref<InstanceType<typeof FForm> | null>(null);
async function submit() {
  await formRef.value?.validate();
  const param = {
    content: formData.value.content,
    startTime: formData.value.startTime,
    endTime: formData.value.endTime,
  };
  await request.fetch(props.api.createNotice, param, 'post');
  FMessage.success($t('announcement.addAnnouncementMessage'));
}

defineExpose({ submit });
</script>
