<template>
    <Modal v-model="showModal" :width="800" footer-hide class-name="code-search-modal">
        <div class="code-modal-panel">
            <div class="panel-header">
                <div class="panel-header__left">
                    <span class="panel-header__title" :title="current.path">{{ current.path }}</span>
                    <span class="panel-header__icon">
                        <SvgIcon style="font-size: 14px;" color="#444444" icon-class="fi-workflow1" />
                        <span class="icon-text" :title="current.project">{{  current.project }}</span>
                    </span>
                    <span class="panel-header__icon">
                        {{  current.fileType }}
                    </span>
                </div>
            </div>
            <div class="panel-content" v-if="current.keyLines">
                <ul class="content-item" v-for="code in current.keyLines" :key="code.number">
                    <li class="content-item__num">{{ code.number }}</li>
                    <li class="content-item__code" v-html="code.lineText"></li>
                </ul>
            </div>
        </div>
    </Modal>
</template>
  
<script>
export default {
    name: 'viewMore',
    model: {
        prop: '_visible',
        event: '_changeVisible',
    },
    emits: ['_changeVisible'],
    props: {
        _visible: {
            type: Boolean,
        },
        current: {
            type: Object,
            default: () => ({})
        }
    },
    computed: {
        showModal: {
            get() {
                return this._visible;
            },
            set(val) {
                this.$emit('_changeVisible', val)
            },
        },
    },
    data() {
        return {
        }
    },
    methods: {
    },
}
</script>
  
<style lang="scss" scoped>
.code-modal-panel {
    font-size: 14px;
    border: 1px solid #c5c8ce;
    border-radius: 4px;
    overflow: hidden;

    .panel-header {
        padding: 0 16px;
        background: #f8f8f8;
        height: 32px;
        display: flex;
        justify-content: space-between;
        border-bottom: 1px solid #c5c8ce;
        width: 100%;
        &__left {
            display: flex;
            align-items: center;
            width: calc(100% - 30px);
        }

        &__title {
            margin-right: 15px;
            color: #2d8cf0;
            max-width: 75%;
            overflow: hidden;
            text-overflow: ellipsis;
        }

        &__icon {
            margin-right: 15px;
            display: flex;
            align-items: center;
            .svg-container {
                display: flex;
                align-items: center;
                margin-right: 5px;
            }

            .icon-text {
                max-width: 150px;
                text-overflow: ellipsis;
                overflow: hidden;
            }
        }
    }

    .panel-content {
        padding: 10px 16px 10px 0;
        max-height: 500px;
        overflow: auto;
        .content-item {
            display: flex;

            &__num {
                flex: 0 0 34px;
                line-height: 1.5;
                text-align: right;
                margin-right: 10px;
            }

            &__code {
                flex: 1;
                line-height: 1.5;
            }
        }
    }
}
</style>
<style lang="less">
.code-search-modal {
    .ivu-modal-close {
        top: 2px;
    }

    .ivu-modal-body {
        padding: 0 0;
    }
}
</style> 