<template>
    <Drawer :title="$t('message.workflow.publishingWorkflow')" v-model="showDrawer" :width="drawerWidth" :mask-closable="false" draggable
        class-name="custom-drawer-style">
        <div class="flow-wrapper">
            <template v-if="associateGit">
                <div class="diff-title">代码比对</div>
                <div class="diff-wrapper">
                    <div v-if="!isExpand" class="diff-empty">
                        <Button type="text" class="empty-btn" :loading="isLoading" @click="handleToggle(true)">
                            查看版本对比
                        </Button>
                    </div>
                    <template v-else>
                        <Tabs v-model="diffContentType" @on-click="pageStateReset('editor')">
                            <TabPane label="代码" name="code"></TabPane>
                            <TabPane label="元数据" name="meta"></TabPane>
                        </Tabs>
                        <template v-if="currentTree.tree && currentTree.tree.length > 0">
                            <div class="diff-tree">
                                <Tree ref="treeRef" :key="diffContentType" :data="currentTree.tree" expand-node  @on-select-change="handleTreeSelect" />
                            </div>

                            <div v-if="isDiffLoading" class="diff-loading">
                                加载中...
                            </div>

                            <div v-if="compareDetail.visible" class="diff-editor">
                                <div class="editor-title">
                                    <div class="left">
                                        <Icon type="md-lock" />
                                        {{ compareDetail.beforeCommitId }}
                                    </div>
                                    <div class="middle">VS</div>
                                    <div class="right">
                                        当前版本({{ compareDetail.afterCommitId }})
                                    </div>
                                </div>
                                <we-editor-compare 
                                    :key="diffContentType + compareDetail.beforeCommitId + compareDetail.afterCommitId" 
                                    :style="'height:' + editorHeight" 
                                    :value="compareDetail.after"
                                    :original="compareDetail.before" 
                                    :diffEditor="true" 
                                    :readOnly="true" />
                            </div>
                        </template>
                        <div v-else-if="currentTree.tips" style="color: #f29360;margin-bottom: 16px;">{{ currentTree.tips }}</div>
                    </template>

                    <div v-if="compareDetail.visible" class="diff-commit">
                        <div class="commit-left">{{ compareDetail.beforeAnnotate }}</div>
                        <div class="commit-middle"></div>
                        <div class="commit-right">{{ compareDetail.afterAnnotate }}</div>
                    </div>
                </div>
            </template>
            <slot></slot>
        </div>
        <div class="diff-footer">
            <slot name="footer"></slot>
        </div>
    </Drawer>
</template>
<script>
import api from '@dataspherestudio/shared/common/service/api';
export default {
    props: {
        projectName: {
            type: String,
            required: true
        },
        orchestratorId: {
            type: [String, Number],
            default: null
        },
        visible: {
            type: Boolean,
            default: false
        },
        associateGit: {
            type: Boolean,
            default: false
        }
    },
    data() {
        return {
            drawerWidth: '33%',
            editorHeight: '300px',
            isLoading: false,
            isExpand: false,
            timeout: null,
            flowFileTree: {
                code: [],
                meta: [],
                codeEmptyTips: '',
                metaEmptyTips: '',
            },
            compareDetail: {
                visible: false,
                before: '',
                after: '',
                beforeCommitId: '',
                afterCommitId: '',
                beforeAnnotate: '',
                afterAnnotate: ''
            },
            diffContentType: 'code',
            isDiffLoading: false
        }
    },
    computed: {
        currentTree() {
            return {
                tree: this.flowFileTree[this.diffContentType],
                tips: this.flowFileTree[this.diffContentType + 'EmptyTips']
            }
        },
        showDrawer: {
            get: function () { return this.visible },
            set: function (show) {
                this.$emit('update:visible', show);
            }
        }
    },
    watch: {
        visible(cur) {
            if (!cur) {
                this.isLoading = false;
                this.timeout = null;
                this.pageStateReset('page');
            }
        }
    },
    mounted() {
        window.addEventListener('resize', this.handleWindowResize);
        this.handleWindowResize();
    },
    beforeDestroy() {
        window.removeEventListener('resize', this.handleWindowResize);
    },
    methods: {
        handleWindowResize() {
            const initWidth = window.innerWidth * 0.33;
            this.drawerWidth = `${initWidth}px`;
        },
        buildTree(data, level = 1) {
            if (!data || ['[]', '{}'].includes(JSON.stringify(data))) return undefined;
            let values = data;
            if (data && String(data) === '[object Object]') {
                values = Object.values(data);
            }
            return values.map((item) => {
                return {
                    title: item.name,
                    expand: true,
                    absolutePath: item.absolutePath,
                    children: this.buildTree(item.children, level + 1),
                };
            });
        },
        delay(time) {
            if (this.timeout) {
                window.clearTimeout(this.timeout);
            }
            return new Promise((resolve) => {
                setTimeout(resolve, time);
            });
        },
        async fetchDiffTree(cb) {
            try {
                this.isLoading = true;
                const param = {
                    projectName: this.projectName,
                    orchestratorId: this.orchestratorId,
                    labels: { route: 'dev' },
                };
                const res = await api.fetch(
                    `dss/framework/orchestrator/diffOrchestratorPublish?projectName=${param.projectName}`,
                    param,
                    'post'
                );
                const { codeTree, metaTree } = res.tree || {};
                this.flowFileTree = {
                    code: this.buildTree(codeTree || []),
                    meta: this.buildTree(metaTree || []),
                    codeEmptyTips:
                        !codeTree && '当前工作流代码发布后的版本与上一版完全相同，没有任何变化',
                    metaEmptyTips:
                        !metaTree &&
                        '当前工作流元数据发布后的版本与上一版完全相同，没有任何变化',
                };
                this.isLoading = false;
                cb && cb();
            } catch (error) {
                this.isLoading = false;
                console.log('get tree error', error);
            }
        },
        async fetchDiffContent(filePath) {
            try {
                this.isDiffLoading = true;
                this.compareDetail.visible = false;
                const param = {
                    projectName: this.projectName,
                    orchestratorId: this.orchestratorId,
                    labels: { route: 'dev' },
                    filePath,
                    publish: true,
                };
                const res = await api.fetch(
                    `/dss/framework/orchestrator/diffFlowContent?projectName=${param.projectName}`,
                    param,
                    'post'
                );
                this.compareDetail = {
                    visible: true,
                    before: res.content.before || '',
                    after: res.content.after || '',
                    beforeCommitId: res.content.beforeCommitId || '',
                    afterCommitId: res.content.afterCommitId || '',
                    beforeAnnotate: res.content.beforeAnnotate || '',
                    afterAnnotate: res.content.afterAnnotate || ''
                };
                this.isDiffLoading = false;
            } catch (error) {
                this.isDiffLoading = false;
                console.log('get content error', error);
            }
        },
        pageStateReset(type) {
            if (['page'].includes(type)) {
                this.diffContentType = 'code';
                this.flowFileTree = {
                    code: [],
                    meta: [],
                    codeEmptyTips: '',
                    metaEmptyTips: '',
                };
                this.isExpand = false;
            }
            this.compareDetail = {
                visible: false,
                before: '',
                after: '',
                beforeCommitId: '',
                afterCommitId: '',
                beforeAnnotate: '',
                afterAnnotate: ''
            };
            this.isDiffLoading = false;
        },
        handleToggle(expand) {
            if (expand) {
                this.fetchDiffTree(() => {
                    this.isExpand = true;
                });
            } else {
                this.pageStateReset('tab');
            }
        },
        handleTreeSelect(node) {
            const current = node[0];
            if (current && !current.children) {
                this.fetchDiffContent(current.absolutePath);
            }
        }
    },
}
</script>
<style lang="less" scoped>
.flow-wrapper {
    height: calc(100% - 32px);
    overflow-y: auto;

    .diff-title {
        margin-bottom: 4px;
        color: #515a6e;
    }

    .diff-wrapper {
        border: 1px solid #e7e7e9;
        padding: 16px 16px;
        margin-bottom: 16px;
        border-radius: 4px;
        .diff-empty {
            .empty-btn {
                box-shadow: none;
                padding: 0 0;
                color: #2d8cf0;
            }
        }

        .diff-tree {
            background: #f8f8f8;
            padding: 8px 16px;
            max-height: 240px;
            overflow-y: auto;
        }

        .diff-loading {
            display: flex;
            align-items: center;
            justify-content: center;
            color: #2d8cf0;
            margin: 16px 0;
        }

        .diff-editor {
            border: 1px solid #e7e7e9;
            padding: 16px 0 1px;
            border-radius: 4px;
            margin-top: 16px;

            .editor-title {
                display: flex;
                margin-bottom: 16px;

                .left {
                    flex: 1;
                    text-align: right;
                    /deep/.ivu-icon {
                        vertical-align: text-top;
                    }
                }

                .right {
                    flex: 1;
                    text-align: left;
                }

                .middle {
                    width: 32px;
                    flex: 0 0 32px;
                    background: #5384ff;
                    color: #fff;
                    text-align: center;
                    border-radius: 4px;
                    margin: 0 32px;
                }
            }

            /deep/.wrap {
                .toolbar {
                    display: none;
                }

                .el-editor {
                    min-height: 300px;
                }
            }

        }

        .diff-commit {
            background: #f8f8f8;
            margin-top: 16px;
            display: flex;
            .commit-left,.commit-right {
                padding: 0 16px;
                min-height: 32px;
                line-height: 32px;
                word-break: break-all;
                flex: 1;
            }
            .commit-middle {
                flex: 0 0 8px;
                background: #fff;
            }
        }
    }
}

.diff-footer {
    height: 32px;
    display: inline-flex;
    gap: 8px;
}
</style>
  