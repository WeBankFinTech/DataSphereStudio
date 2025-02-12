<template>
    <Tabs @on-click="getDatalist" type="card" class="tagedge-detail-tabs" :animated="false">
        <TabPane :label="title" style="height:100%;">
            <div style="padding: 10px;">
                <h4 class="title">{{ title }}</h4>
                <div class="basic-card" v-for="(type, index1) in info" :key="index1">
                    <span v-for="(item, index2) in type.children" :key="index2" class="basic-card-item">
                        <span class="basic-card-item-title" :style="{ 'width': '120px' }">{{ item.title }}: </span>
                        <span class="basic-card-item-value" :style="{ 'width': 'calc(100% - 120px)' }">{{ formatValue(item)
                        }}</span>
                    </span>
                </div>
            </div>
        </TabPane>
        <TabPane :label="tableTabTitle" style="height:100%;">
            <div style="padding: 10px;">
                <Input v-model="searchText" placeholder="请输搜索属性名称，描述" @on-change="search"  style="margin-bottom: 14px;"/>
                <Table :columns="columnData" :data="tableData" :height="page.totalSize > 10 ? 480 : ''"></Table>
                <Page
                    v-if="page.totalSize > 25"
                    ref="page"
                    :total="page.totalSize"
                    :page-size-opts="page.sizeOpts"
                    :page-size="page.pageSize"
                    :current="page.pageNow"
                    class-name="page"
                    size="small"
                    show-total
                    show-sizer
                    @on-change="change"
                    @on-page-size-change="changeSize" />
                </div>
        </TabPane>
    </Tabs>
</template>
<script>
import { debounce } from 'lodash';
import utils from '@dataspherestudio/shared/common/util';
import api from '@dataspherestudio/shared/common/service/api';

export default {
    props: {
        work: {
            type: Object,
            required: true,
        }
    },
    data() {
        return {
            tagInfo: [
                {
                    children: [{
                        key: 'spaceId',
                        title: '图空间ID',
                    }, {
                        key: 'tagName',
                        title: '标签名',
                    }, {
                        key: 'ttlDuration',
                        title: '存活时间',
                    }, {
                        key: 'ttlCol',
                        title: '存活时间属性'
                    }, {
                        key: 'comment',
                        title: '描述'
                    }],
                }
            ],
            edgeInfo: [
                {
                    children: [{
                        key: 'spaceId',
                        title: '图空间ID',
                    }, {
                        key: 'edgeTypeName',
                        title: '边类型名',
                    }, {
                        key: 'ttlDuration',
                        title: '存活时间',
                    }, {
                        key: 'ttlCol',
                        title: '存活时间属性'
                    }, {
                        key: 'comment',
                        title: '描述'
                    }],
                }
            ],
            columnData: [
                {
                    title: '序号',
                    key: 'idx'
                },
                {
                    title: "属性名称",
                    key: 'propName'
                },
                {
                    title: "数据类型",
                    key: 'dataType'
                },
                {
                    title: "默认值",
                    key: 'defaultValue'
                },
                {
                    title: "描述",
                    key: 'comment'
                }
            ],
            tableData: [],
            page: {
                totalSize: 0,
                sizeOpts: [25, 50, 100],
                pageSize: 25,
                pageNow: 1
            },
            searchText: ''
        };
    },
    computed: {
        title() {
            if (this.work.data.dataType == 'ng_tag') return '标签基本信息'
            if (this.work.data.dataType == 'ng_edge') return '边类型基本信息'
            return '基本信息'
        },
        tableTabTitle() {
            if (this.work.data.dataType == 'ng_tag') return '标签属性'
            if (this.work.data.dataType == 'ng_edge') return '边类型属性'
            return '属性'
        },
        info() {
            if (this.work.data.dataType == 'ng_tag') return this.tagInfo
            if (this.work.data.dataType == 'ng_edge') return this.edgeInfo
            return []
        },
        tablePageData() {
            return this.tableData.slice((this.page.pageNow - 1) * this.page.pageSize, this.thia.page.pageNow * this.page.pageSize)
        }
    },
    methods: {
        formatValue(item) {
            return utils.formatValue(this.work.data, item);
        },
        change(val) {
            this.page.pageNow = val;
        },
        changeSize(val) {
            this.page.pageSize = val;
            this.page.pageNow = 1;
        },
        getPropsData() {
            this.page.pageNow = 1
            this.page.pageSize = 25
            if (this.work.data.dataType == 'ng_tag') {
                this.getTagProp(this.work.data)
            }
            if (this.work.data.dataType == 'ng_edge') {
                this.getEdgeProp(this.work.data)
            }
        },
        getTagProp(item) {
            const { spaceName, clusterCode, tagName } = item
            const params = {
                spaceName,
                clusterCode,
                tagName
            }
            api.fetch('/dss/datapipe/datasource/tag-prop', params, 'get').then(res => {
                if (res) {
                    const data = (res.tagProps || []).map((it, index)=> {
                        it.idx = index + 1
                        return it
                    })
                    this.tableData = data
                    this.backData =  [...data]
                    this.page.totalSize = data.length
                }
            })
        },
        getEdgeProp(item) {
            const { spaceName, clusterCode, edgeTypeName } = item
            const params = {
                spaceName,
                clusterCode,
                edgeTypeName
            }
            api.fetch('/dss/datapipe/datasource/edge-prop', params, 'get').then(res => {
                if (res) {
                    const data = (res.edgeProps || []).map((it, index)=> {
                        it.idx = index + 1
                        return it
                    })
                    this.tableData = data
                    this.backData =  [...data]
                    this.page.totalSize = data.length
                }
            })
        },
        getDatalist(tab) {
            if (tab == 1) {
                this.getPropsData()
            }
        },
        search: debounce(function() {
            const text = this.searchText.trim();
            this.backData = this.backData || []
            if (text) {
                this.tableData = this.backData.filter(it => {
                    return it.propName.includes(text) || it.comment.includes(text)
                })
            } else {
                this.tableData = [...this.backData]
            }
        }, 500),
    },
};
</script>
<style lang="scss" scoped>
.title {
    padding-left: 10px;
    padding-bottom: 10px;
    border-bottom: 1px solid #eee;
    margin-bottom: 5px;
}

.note {
    font-weight: normal;
    padding-left: 10px;
    display: inline-block;
    vertical-align: top;
}

.basic-card {
    margin-bottom: 10px;
    height: calc(100% - 52px);
    overflow: hidden;

    .basic-card-item {
        display: inline-flex;
        width: 50%;
        height: 36px;
        padding-left: 10px;
        align-items: center;

        &.comment {
            width: 100%;
            height: auto;
            align-items: start;
        }

        .basic-card-item-title {
            display: inline-block;
            width: 100px;
            font-weight: bold;
        }

        .basic-card-item-value {
            display: inline-block;
            width: calc(100% - 104px);
            overflow: hidden;

            &.comment {
                word-break: break-all;
            }
        }
    }
}

.basic-card-item {

    .basic-card-item-title,
    .basic-card-item-value {
        font-size: 12px;
        font-weight: 400 !important;
    }
}
</style>
<style lang="scss">
@import '@dataspherestudio/shared/common/style/variables.scss';
.tagedge-detail-tabs {
    .ivu-table th {
        @include font-color(#fff, #000);
        @include bg-color($primary-color, $dark-primary-color);
    }
    .page {
        margin-top: 10px;
        text-align: center;
    }
}
</style>
    