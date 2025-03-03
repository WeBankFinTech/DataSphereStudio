<template>
    <Drawer title="查找" v-model="showDrawer" width="80%" :mask-closable="false" class-name="custom-drawer-style">
        <div class="code-search">
            <div class="code-form">
                <Form ref="codeFormRef" inline>
                    <FormItem label="">
                        <Select class="code-form__select flow" v-model="searchForm.projectName" placeholder="请选择"
                            filterable @on-change="getFlow($event)">
                            <template #prefix>项目名:</template>
                            <Option v-for="item in projectList" :value="item.name" :key="item.id">{{ item.name }}</Option>
                        </Select>
                    </FormItem>
                    <FormItem label="">
                        <Select class="code-form__select flow" v-model="searchForm.workflowNameList" placeholder="请选择"
                            multiple filterable>
                            <template #prefix>工作流:</template>
                            <Option v-for="item in flowList" :value="item.name" :key="item.id">{{ item.name }}</Option>
                        </Select>
                    </FormItem>
                    <FormItem label="">
                        <Select class="code-form__select nodeType" v-model="searchForm.typeList" placeholder="请选择" multiple
                            filterable>
                            <template #prefix>节点类型:</template>
                            <Option v-for="(item, index) in nodeTypeList" :value="item.name" :key="'node' + index">{{
                                item.name }}</Option>
                        </Select>
                    </FormItem>
                    <FormItem label="">
                        <Input class="code-form__input file" v-model="searchForm.nodeName" prefix="文件" placeholder="请输入">
                        <template #prepend>文件:</template>
                        </Input>
                    </FormItem>
                    <FormItem label="">
                        <Input class="code-form__input content" v-model="searchForm.searchContent" prefix="查找内容"
                            placeholder="请输入">
                        <template #prepend>查找内容:</template>
                        <template #append>
                            <Select v-model="searchForm.append" style="width: 70px">
                                <Option value="code">代码</Option>
                            </Select>
                        </template>
                        </Input>
                    </FormItem>
                    <FormItem label="">
                        <Button type="primary" @click="pageInit">查找</Button>
                    </FormItem>
                </Form>
            </div>
            <div v-if="pageData.total > 0">
                <div class="code-total">共计匹配: {{ pageData.total }}个文件</div>
                <div class="code-wrapper">
                    <div class="code-panel" v-for="(item, index) in codeResults">
                        <div class="panel-header">
                            <div class="panel-header__left">
                                <span class="panel-header__prefix">
                                    <Icon type="ios-arrow-down"></Icon>
                                </span>
                                <span class="panel-header__title" :title="item.path" @dblclick="gotoFile($event, item)">{{ item.path
                                }}</span>
                                <span class="panel-header__icon">
                                    <SvgIcon style="font-size: 14px;" color="#444444" icon-class="fi-workflow1" />
                                    <span class="icon-text" :title="item.flowName">{{ item.flowName }}</span>
                                </span>
                                <span class="panel-header__icon">
                                    {{ item.fileType }}
                                </span>
                            </div>
                            <div class="panel-header__right" @click="clickShowMore(item)">
                                <SvgIcon style="font-size: 14px;" color="#444444" icon-class="kaifa-icon" />
                                <span class="header-right__icon">查看全部</span>
                            </div>
                        </div>
                        <div class="panel-content" v-if="item.keyLines.length <= 30">
                            <ul class="content-item" v-for="(code, index) in item.keyLines.slice(0, item.end)"
                                :key="code.number"
                                @dblclick="gotoFile($event, item, code)"
                                >
                                <li class="content-item__num">{{ code.number }}</li>
                                <li class="content-item__code" v-html="code.lineText"></li>
                            </ul>
                            <template v-if="item.keyLines.length > 10">
                                <div class="content-btn expand" v-if="item.end === 10" @click="handleClick(item, 30)">展开
                                    <Icon type="ios-arrow-down"></Icon>
                                </div>
                                <div class="content-btn fold" v-if="item.end === 30" @click="handleClick(item, 10)">收起<Icon
                                        type="ios-arrow-up"></Icon>
                                </div>
                            </template>
                        </div>
                        <div class="panel-content" v-else>
                            <div class="content-tips" @click="clickShowMore(item)">
                                <Icon type="ios-alert" />该节点匹配数量过多，请进入全部代码查看匹配项 >
                            </div>
                        </div>
                    </div>
                </div>
                <Page class="code-pagination" :total="pageData.total" show-sizer :current="pageData.currentPage"
                    :page-size="pageData.pageSize" :page-size-opts="pageData.opts" @on-change="pageChange"
                    @on-page-size-change="pageSizeChange"></Page>
            </div>
            <div v-else-if="!loading">
                <div class="code-empty">暂无匹配数据</div>
            </div>
            <div v-else-if="loading" class="code-empty">加载中...</div>
            <!-- 查看更多 -->
            <ViewMore v-model="showMore" :current="currentItem" @gotoFile="gotoFile"/>
        </div>
    </Drawer>
</template>
<script>
import api from '../../../../shared/common/service/api';
import ViewMore from './viewMore.vue';

export default {
    components: { ViewMore },
    model: {
        prop: '_visible',
        event: '_changeVisible',
    },
    emits: ['_changeVisible'],
    props: {
        currentMode: {
            type: String,
            default: ''
        },
        _visible: {
            type: Boolean,
        }
    },
    data() {
        return {
            searchForm: {
                projectName: '',
                workflowNameList: [],
                typeList: [],
                nodeName: '',
                searchContent: '',
                append: 'code'
            },
            nodeTypeList: [],
            pageData: {
                currentPage: 1,
                pageSize: 10,
                opts: [10, 30, 50],
                total: 0,
            },
            codeResults: [],
            showMore: false, // 查看更多
            currentItem: {},
            flowList: [],
            projectList: [],
            loading: false
        }
    },
    computed: {
        showDrawer: {
            get() {
                return this._visible
            },
            set(val) {
                this.$emit('_changeVisible', val)
            },
        },
    },
    watch: {
        showDrawer: {
            immediate: true,
            handler: async function (show) {
                if (show) {
                    this.pageReset();
                    await this.getProject();
                    this.searchForm.projectName = this.$route.query.projectName
                    this.getFlow(this.searchForm.projectName)
                }
            }
        }
    },
    created() {
        this.getNodeType();
    },
    methods: {
        setHighlightText(text = '') {
            let keyVal = this.searchForm.searchContent;
            let specialCharacter = ['\\', '$', '(', ')', '*', '+', '.', '[', '?', '^', '{', '|'];
            specialCharacter.map(v => {
                let reg = new RegExp('\\' + v, 'gim');
                keyVal = keyVal.replace(reg, '\\' + v);
            });
            const reg = new RegExp(keyVal, 'g');
            let content = text || '';
            content = content.replaceAll(reg, (match) => {
                if (match === this.searchForm.searchContent) {
                    return `<span style="background:#fde5b3;border-radius: 1px;">${match}</span>`;
                }
                return '';
            });
            return content;
        },
        searchCode() {
            if (!this.searchForm.searchContent) {
                return this.$Message.warning('查找内容不能为空');
            }
            this.loading = true;
            const params = {
                projectName: this.searchForm.projectName,
                workspaceId: this.$route.query.workspaceId,
                workflowNameList: this.searchForm.workflowNameList,
                typeList: this.searchForm.typeList,
                nodeName: this.searchForm.nodeName,
                searchContent: this.searchForm.searchContent,
                pageSize: this.pageData.pageSize,
                pageNow: this.pageData.currentPage
            }
            api.fetch(`/dss/framework/project/searchGit`, params, 'post').then((rst) => {
                this.loading = false;
                this.codeResults = (rst.data.result || []).map(item => {
                    (item.keyLines || []).forEach(code => {
                        code.lineText = this.setHighlightText(code.line);
                    })
                    item.fileType = (item.path.match(/(?<=.+?\.)[^.]+$/g) || [])[0];
                    item.flowName = item.path.split('/')[0];
                    item.nodeName = item.path.split('/')[1];
                    item.end = 10;
                    return item;
                });
                this.pageData.total = rst.data.total;
            }).catch(() =>{
                this.loading = false;
            })
        },
        pageInit() {
            this.pageData.currentPage = 1;
            this.searchCode();
        },
        pageChange(number) {
            this.pageData.currentPage = number;
            this.searchCode();
        },
        pageSizeChange(size) {
            this.pageData.pageSize = size;
            this.pageData.currentPage = 1;
            this.searchCode();
        },
        pageReset() {
            this.searchForm = {
                projectName: this.$route.query.projectName,
                workflowNameList: [],
                typeList: [],
                nodeName: '',
                searchContent: '',
                append: 'code'
            }
            this.codeResults = [];
            this.pageData = {
                currentPage: 1,
                pageSize: 10,
                opts: [10, 30, 50],
                total: 0,
            };
        },
        clickShowMore(item) {
            this.currentItem = item;
            this.showMore = true;
        },
        gotoFile(ev, item, code) {
            const paths = (item.path || '').split('/');
            const curProjectObj = this.projectList.find(item => { return item.name === this.searchForm.projectName})
            // 子工作流节点
            if (paths.length > 3) {
                api.fetch(`/dss/workflow/queryNodeInfoByPath`, {
                    path: item.path,
                    projectId: curProjectObj.id,
                }, 'post').then((res) => {
                    const nodeInfo = {
                        ...res.data,
                        projectName: curProjectObj.name,
                        id: curProjectObj.id
                    }
                    console.log('nodeInfo111', nodeInfo)
                    this.$emit('openSubFlowNode', ev, item, code,nodeInfo)
                })
            } else {
                this.$emit('openFlowNode', ev, item, code, curProjectObj)
            }
        },
        handleClick(item, line) {
            item.end = line;
        },
        // 获取节点类型
        getNodeType() {
            api.fetch(`/dss/framework/orchestrator/allType`, 'get').then((rst) => {
                this.nodeTypeList = (rst.type || []).map(item => ({ name: item }));
            })
        },
        async getProject() {
            await api.fetch(
                `${this.$API_PATH.PROJECT_PATH}getAllProjects`,
                {
                    workspaceId: this.$route.query.workspaceId,
                },
                "post"
            )
                .then((res) => {
                    this.projectList = res.projects.filter((f) => {
                        return f.associateGit
                    });
                });
        },
        getFlow(projectName) {
            const curProjectObj = this.projectList.find(item => { return item.name === projectName})
            if(curProjectObj && curProjectObj.id) {
                api.fetch(
                    `${this.$API_PATH.ORCHESTRATOR_PATH}getAllOrchestrator`,
                    {
                        workspaceId: this.$route.query.workspaceId,
                        orchestratorMode: this.currentMode,
                        projectId: curProjectObj.id,
                    },
                    "post"
                )
                    .then((res) => {
                        this.flowList = res.page.map((f) => {
                            return {
                                ...f,
                                name: f.orchestratorName,
                                type: "flow",
                            };
                        });
                    });
            }
        },
    }
}
</script>
<style lang="less" scoped>
.code-search {
    font-size: 14px;

    .code-title {
        margin-bottom: 5px;
    }

    .code-form {
        &__input {
            /deep/.ivu-input-group-prepend {
                border-right: 0;
                position: absolute;
                top: 1px;
                left: 1px;
                z-index: 9;
                height: 30px;
                line-height: 23px;
                background: transparent;
                border: none;
                font-size: 14px;
            }

            &.content {
                /deep/.ivu-input-with-prefix {
                    padding-left: 74px;
                }
            }

            &.file {
                /deep/.ivu-input-with-prefix {
                    padding-left: 46px;
                }
            }
        }

        &__select {
            &.flow {
                /deep/.ivu-select-prefix {
                    width: 62px;
                    word-break: keep-all;
                }
            }

            &.nodeType {
                /deep/.ivu-select-prefix {
                    width: 84px;
                    word-break: keep-all;
                }
            }
        }
    }

    .code-total {
        margin-bottom: 8px;
    }

    .code-wrapper {
        .code-panel {
            font-size: 14px;
            border: 1px solid #c5c8ce;
            border-radius: 4px;
            overflow: hidden;
            margin-bottom: 16px;

            .panel-header {
                padding: 0 16px;
                background: #f8f8f8;
                height: 32px;
                display: flex;
                justify-content: space-between;
                width: 100%;

                &__left {
                    display: flex;
                    align-items: center;
                    cursor: pointer;
                    width: calc(100% - 75px);
                }

                &__prefix {
                    margin-right: 10px;
                }

                &__title {
                    margin-right: 15px;
                    color: #2d8cf0;
                    max-width: 75%;
                    display: -webkit-box;
                    -webkit-box-orient: vertical;
                    -webkit-line-clamp: 1;
                    overflow: hidden;
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
                        display: -webkit-box;
                        -webkit-box-orient: vertical;
                        -webkit-line-clamp: 1;
                        overflow: hidden;
                    }
                }

                &__right {
                    display: flex;
                    align-items: center;
                    flex: 0 0 75px;

                    .svg-container {
                        display: flex;
                        align-items: center;
                        margin-right: 5px;
                    }

                    .header-right__icon {
                        cursor: pointer;

                        &:hover {
                            color: #2d8cf0;
                        }
                    }
                }
            }

            .panel-content {
                padding: 10px 16px 10px 0;

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

                .content-btn {
                    margin: 10px 0 0 16px;
                    cursor: pointer;

                    &:hover {
                        color: #2d8cf0;
                    }
                }

                .content-tips {
                    margin-left: 16px;
                    color: #ed4014;
                    cursor: pointer;
                }
            }
        }
    }

    .code-pagination {
        text-align: right;
        margin-top: 16px;

        /deep/.ivu-select {
            position: relative;
        }
    }

    .code-empty {
        text-align: center;
        margin-top: 50px;
    }
}
</style>
<style lang="less">
.custom-drawer-style {
    z-index: 1004 !important;
    .ivu-drawer{
        max-width: 78%;
        .ivu-drawer-body {
            height: calc(100% - 100px);
            overflow: auto;
        }
        .ivu-drawer-content {
            top: 55px;
        }
    }
}
</style>
