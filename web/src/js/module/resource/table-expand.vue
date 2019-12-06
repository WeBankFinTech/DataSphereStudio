<style scoped>
    .expand-row{
        margin: 10px 0;
    }
    .expand-key {
      font-weight: bold;
    }
</style>
<template>
  <div>
    <Row class="expand-row">
      <Col span="8">
      <span class="expand-key">{{ $t('message.resource.row.applicationName') + '：' }}</span>
      <span class="expand-value">{{ row.applicationName }}</span>
      </Col>
      <Col span="8">
      <span class="expand-key">{{ $t('message.resource.row.usedTime') + '：' }}</span>
      <span class="expand-value">{{ usedTime }}</span>
      </Col>
      <Col span="8">
      <span class="expand-key">{{ $t('message.resource.row.engineStatus') + '：' }}</span>
      <span class="expand-value">{{ row.engineStatus }}</span>
      </Col>
    </Row>
    <Row class="expand-row">
      <Col span="8">
      <span class="expand-key">{{ $t('message.resource.row.engineInstance') + '：' }}</span>
      <span class="expand-value">{{ row.engineInstance }}</span>
      </Col>
      <Col
        span="8"
        v-if="usedResource.yarn">
      <span class="expand-key">{{ $t('message.resource.row.queueName') + '：' }}</span>
      <span class="expand-value">{{ usedResource.yarn.queueName ||  $t('message.constants.noDataText') }}</span>
      </Col>
      <Col span="8">
      <span class="expand-key">{{ $t('message.resource.row.user') + '：' }}</span>
      <span class="expand-value">{{ row.statusInfo.engineInfo.user }}</span>
      </Col>
    </Row>
    <Row
      class="expand-row"
      v-if="usedResource.driver">
      <Col span="8">
      <span class="expand-key">{{ $t('message.resource.row.cpu') + '：' }}</span>
      <span class="expand-value">{{ formatCpu(usedResource.driver.cpu) }}</span>
      </Col>
      <Col span="8">
      <span class="expand-key">{{ $t('message.resource.row.memory') + '：' }}</span>
      <span class="expand-value">{{ usedResource.driver.memory }}</span>
      </Col>
    </Row>
    <Row
      class="expand-row"
      v-else>
      <Col span="8">
      <span class="expand-key">{{ $t('message.resource.row.cpu') + '：' }}</span>
      <span class="expand-value">{{ formatCpu(usedResource.cpu) }}</span>
      </Col>
      <Col span="8">
      <span class="expand-key">{{ $t('message.resource.row.memory') + '：' }}</span>
      <span class="expand-value">{{ usedResource.memory }}</span>
      </Col>
    </Row>
    <Row
      class="expand-row"
      v-if="usedResource.yarn">
      <Col span="8">
      <span class="expand-key">{{ $t('message.resource.row.queueCpu') + '：' }}</span>
      <span class="expand-value">{{ formatCpu(usedResource.yarn.queueCpu) }}</span>
      </Col>
      <Col span="8">
      <span class="expand-key">{{ $t('message.resource.row.queueMemory') + '：' }}</span>
      <span class="expand-value">{{ usedResource.yarn.queueMemory }}</span>
      </Col>
    </Row>
  </div>
</template>
<script>
import moment from 'moment';
export default {
  props: {
    row: Object,
    formatCpu: Function,
  },
  computed: {
    usedResource() {
      return JSON.parse(this.row.usedResource);
    },
    usedTime() {
      return moment(this.row.usedTime).format('YYYY-MM-DD HH:mm:ss');
    },
  },
};
</script>
