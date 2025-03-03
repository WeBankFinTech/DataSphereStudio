<template>
    <Modal v-model="show" width="660" title="生成的Datachecker结果，请核对">
        <div>
            <Table :columns="columns" :data="list" max-height="500" @on-row-dblclick="handleRowDblClick">
                <template slot-scope="{ row, index }" slot="db">
                    <template v-if="editRow && editRow.index == index">
                        <Input v-model="editRow.db" />
                    </template>
                    <template v-else>
                        {{ row.db }}
                    </template>
                </template>
                <template slot-scope="{ row, index }" slot="table">
                    <template v-if="editRow && editRow.index == index">
                        <Input v-model="editRow.table" />
                    </template>
                    <template v-else>
                        {{ row.table }}
                    </template>
                </template>
                <template slot-scope="{ row, index }" slot="partition">
                    <template v-if="editRow && editRow.index == index">
                        <Input v-model="editRow.partition" />
                    </template>
                    <template v-else>
                        {{ row.partition }}
                    </template>
                </template>
                <template slot-scope="{ row, index }" slot="view">
                    {{ row.view === true ? '是' : row.view === false ? '否' : '' }}
                </template>
                <template slot-scope="{ row, index }" slot="action">
                    <span class="table_action error" @click="handleDelete(row, index)">
                        删除
                    </span>
                    <span class="table_action success" @click="handleCopy(row, index)">
                        复制
                    </span>
                    <span v-if="editRow && editRow.index === index" class="table_action warn" @click="handleConfirm(row, index)">
                        确认
                    </span>
                </template>
            </Table>
            <Button type="success" style="margin-top: 10px;margin-right: 20px;" @click="add">添加新行</Button>
            <Button type="success" style="margin-top: 10px;" @click="delView">批量删除视图表</Button>
        </div>
        <div slot="footer">
            <Button @click="cancel">取消</Button>
            <Button type="primary" :loading="loading" @click="ok">确认</Button>
        </div>
    </Modal>
</template>
<script>
import api from '@dataspherestudio/shared/common/service/api';

export default {
    data() {
        return {
            loading: false,
            show: false,
            hasView: false,
            editRow: null,
            columns: [
                { title: "库名", key: "db", slot: "db" },
                { title: "表名", key: "table", slot: "table" },
                { title: "分区名", key: "partition", slot: "partition" },
                { title: "是否视图", key: "view", slot: "view" },
                { title: "操作", slot: 'action', }
            ],
            list: []
        };
    },
    methods: {
        cancel() {
            this.show = false;
            this.list = [];
            this.editRow = null;
        },
        ok() {
            const items = this.list.filter(it => it.db && it.table).map(it => {
                return {
                    db: it.db.trim(),
                    table: it.table.trim(),
                    partition: it.partition ? it.partition.trim() : '',
                }
            })
            if (items.length < 1 || this.list.length !== items.length) {
                this.$Message.error('请检查库表分区信息！')
            } else {
                this.checkIsView(items, () => {
                    this.show = false;
                    this.$emit('confirm', this.node, items)
                    this.list = []
                    this.editRow = null
                })
               
            }
        },
        checkIsView(items, cb) {
            this.loading = true
            api.fetch('/dss/datapipe/datasource/validateTables', {
               tables: items
            }, 'post').then((res) => {
                const hasView = res && res.result && res.result.some(it => it.view)
                if (hasView) {
                    this.list = res.result.sort(it => it.view ? -1 : 1)
                    this.hasView = true
                }
                if (cb) {
                    if (hasView) {
                        this.$Message.error('存在视图表，请先删除视图表！')
                    } else {
                        cb()
                    }
                }
            }).finally(() => {
                this.loading = false;
            })
        },
        handleCopy(row) {
            this.list.push({
                ...row,
                index: this.list.length
            })
            this.editRow = this.list[this.list.length - 1]
        },
        handleDelete(row, index) {
            this.list.splice(index, 1)
        },
        handleRowClick(row, index) {
            this.editRow = null
        },
        handleRowDblClick(row, index) {
            this.editRow = {
                ...row,
                index
            };
        },
        handleConfirm(row, index) {
            this.list[index] = { ...this.editRow }
            this.list = [...this.list]
            this.editRow = null
        },
        add() {
            this.list.push({})
            this.editRow = {
                index: this.list.length - 1
            }
        },
        delView() {
            this.list = this.list.filter(it => !it.view)
            this.hasView = false
        },
        fetchList() {
            if (this.node.resources && this.node.resources.length && this.node.jobContent && this.node.jobContent.script) {
                this.loading = true
                const resourceId = this.node.resources[0].resourceId;
                const fileName = this.node.resources[0].fileName;
                const version = this.node.resources[0].version;
                let config = {
                    method: "get"
                };
                api.fetch("/filesystem/openScriptFromBML", {
                    fileName,
                    resourceId,
                    version,
                    creator: this.node.creator || "",
                    projectName: this.$route.query.projectName || ""
                }, config).then(res => {
                    api.fetch('/dss/datapipe/datasource/explainCodeMeta', {
                        scriptContent: res.scriptContent,
                        nodeType: this.node.type
                    }, 'post').then((rst) => {
                        this.list = rst.meta
                        this.checkIsView(rst.meta)
                    }).catch(() => {
                        this.show = false
                    }).finally(() => {
                        this.loading = false;
                    })
                }).catch(() => {
                    this.loading = false
                })
            } else {
                this.$Message.error('脚本内容为空，无法解析')
            }
        },
        open(node) {
            this.node = node;
            this.show = true;
            this.fetchList();
        }
    },
};
</script>
<style lang="scss" scoped>
@import '@dataspherestudio/shared/common/style/variables.scss';

.table_action{
    display: inline-block;
    cursor: pointer;
    padding: 0 5px;
    &.error {
        color: $error-color;
    }
    &.success {
        color: $success-color;
    }
    &.warn {
        color: $warning-color;
    }
}
</style>
  
  