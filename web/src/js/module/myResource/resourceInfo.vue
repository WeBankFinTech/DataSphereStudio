<template>
  <Row :gutter="16">
    <Col span="6">
    <div class="resoure-item">
      <div class="resoure-item-img">
        <img src="../../../assets/images/coreNumber.svg"/>
      </div>
      <div class="resoure-item-content">
        <div class="content-left">
          <div class="text-top left-text">核数</div>
          <div class="left-value">{{ resourceInfo.coreNum ? `${resourceInfo.coreNum}C` : '0' }}</div>
        </div>
        <div class="content-right">
          <div class="right-box" @click="handleExpansion">
            <img src="../../../assets/images/arrow-up-circle.svg" class="operate-img"/>
            <div class="text-top right-text">扩容</div>
          </div>
        </div>
      </div>
    </div>
    </Col>
    <Col span="6">
    <div class="resoure-item">
      <div class="resoure-item-img">
        <img src="../../../assets/images/memory.svg"/>
      </div>
      <div class="resoure-item-content">
        <div class="content-left">
          <div class="text-top left-text">内存</div>
          <div class="left-value">{{ resourceInfo.memSize ? `${resourceInfo.memSize}G` : '0' }}</div>
        </div>
        <div class="content-right">
          <div class="right-box" @click="handleExpansion">
            <img src="../../../assets/images/arrow-up-circle.svg" class="operate-img"/>
            <div class="text-top right-text">扩容</div>
          </div>
        </div>
      </div>
    </div>
    </Col>
    <Col span="6">
    <div class="resoure-item">
      <div class="resoure-item-img">
        <img src="../../../assets/images/storageSpace.svg"/>
      </div>
      <div class="resoure-item-content">
        <div class="content-left">
          <div class="text-top left-text">存储空间</div>
          <div class="left-value">{{ resourceInfo.storage ? `${resourceInfo.storage}T` : '0' }}</div>
        </div>
        <div class="content-right">
          <div class="right-box" @click="handleExpansion">
            <img src="../../../assets/images/arrow-up-circle.svg" class="operate-img"/>
            <div class="text-top right-text">扩容</div>
          </div>
        </div>
      </div>
    </div>
    </Col>
    <Col span="6">
    <div class="resoure-item">
      <div class="resoure-item-img">
        <img src="../../../assets/images/expireTime.svg"/>
      </div>
      <div class="resoure-item-content">
        <div class="content-left">
          <div class="text-top left-text">到期时间</div>
          <div class="left-value">{{ parseDate(resourceInfo.expireTime) }}</div>
        </div>
        <div class="content-right">
          <div class="right-box" @click="handleRenewOrder">
            <img src="../../../assets/images/add-circle.svg" class="operate-img"/>
            <div class="text-top right-text">续订</div>
          </div>
          <div class="right-box">
            <Dropdown trigger="click" class="more-item">
              <img src="../../../assets/images/more.svg" class="more-item-img"/>
              <DropdownMenu slot="list">
                <DropdownItem><span @click="handleCancelOrder">退订</span></DropdownItem>
              </DropdownMenu>
            </Dropdown>
          </div>
        </div>
      </div>
    </div>
    </Col>
  </Row>
</template>
<script>
import moment from 'moment';

export default {
  name: 'ResourceInfo',
  props: {
    resourceInfo: {
      type: Object,
      default: () => {}
    },
  },
  created() {
  },
  mounted() {
  },
  methods: {
    handleExpansion() {
      this.$emit('expansion');
    },
    handleRenewOrder() {
      this.$emit('renewOrder');
    },
    handleCancelOrder() {
      this.$emit('cancelOrder');
    },
    parseDate(date) {
      if (date) {
        return moment(date).format('YYYY-MM-DD');
      } else {
        return '';
      }
    }
  },
};
</script>
