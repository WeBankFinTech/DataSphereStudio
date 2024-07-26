<template>
    <div class="git-manage">
        <h2 class="git-title">Git账号配置</h2>
        <div class="git-panel">
            <div class="git-panel__title">
                <span>Git只读账号</span>
                <span class="panel-edit" v-if="readForm.mode === 'view'" @click="readForm.mode = 'edit'">编辑</span>
                <span class="panel-cancel" v-else-if="readForm.mode === 'edit'" @click="handleCancel('readForm')">取消</span>
            </div>
            <Form class="git-panel__form" ref="readFormRef" :model="readForm" :label-width="80" :rules="editrule">
                <template v-if="['add', 'edit'].includes(readForm.mode)">
                    <FormItem label="账号名" key="gitUser" prop="gitUser">
                        <Input v-model="readForm.gitUser" placeholder="请输入账号名" />
                    </FormItem>
                    <FormItem label="密码" key="gitPassword" prop="gitPassword">
                        <Input v-model="readForm.gitPassword" placeholder="请输入密码" />
                    </FormItem>
                    <FormItem label="">
                        <Button type="primary" @click="beforeSubmitReadUser(readForm)">确定</Button>
                    </FormItem>
                </template>
                <template v-else>
                    <FormItem label="账号名" key="originGitUser">
                        {{ readForm.originGitUser }}
                    </FormItem>
                    <FormItem label="密码" key="originGitPassword">
                        {{ replaceText(readForm.originGitPassword) }}
                    </FormItem>
                </template>
            </Form>
        </div>
        <div class="git-panel">
            <div class="git-panel__title">
                <span>Git读写账号</span>
                <span class="panel-edit" v-if="writeForm.mode === 'view'" @click="writeForm.mode = 'edit'">编辑</span>
                <span class="panel-cancel" v-else-if="writeForm.mode === 'edit'"
                    @click="handleCancel('writeForm')">取消</span>
            </div>
            <Form class="git-panel__form" ref="writeFormRef" :model="writeForm" :label-width="80" :rules="editrule">
                <template v-if="['add', 'edit'].includes(writeForm.mode)">
                    <FormItem label="账号名" key="gitUser" prop="gitUser">
                        <Input v-model="writeForm.gitUser" placeholder="请输入账号名" :disabled="writeForm.mode === 'edit'" />
                    </FormItem>
                    <FormItem label="账号Token" key="gitToken" prop="gitToken">
                        <Input v-model="writeForm.gitToken" placeholder="请输入账号Token" />
                    </FormItem>
                    <FormItem label="">
                        <Button type="primary" @click="beforeSubmitWriteUser(writeForm)">确定</Button>
                    </FormItem>
                </template>
                <template v-else>
                    <FormItem label="账号名" key="originGitUser">
                        {{ writeForm.originGitUser }}
                    </FormItem>
                    <FormItem label="账号Token" key="originGitToken">
                        {{ replaceText(writeForm.originGitToken) }}
                    </FormItem>
                </template>
            </Form>
        </div>
    </div>
</template>
<script>
import api from '@dataspherestudio/shared/common/service/api';

export default {
    data() {
        return {
            readForm: {
                gitUser: '',
                gitToken: '',
                gitPassword: '',
                originGitUser: '',
                originGitPassword: '',
                originGitToken: '',
                mode: 'add',
                type: 'read',
            },
            writeForm: {
                gitUser: '',
                gitToken: '',
                gitPassword: '',
                originGitUser: '',
                originGitPassword: '',
                originGitToken: '',
                mode: 'add',
                type: 'write',
            },
            editrule: {
                gitUser: [
                    { required: true, message: '不能为空', trigger: "blur" }
                ],
                gitToken: [
                    { required: true, message: '不能为空', trigger: "blur" }
                ],
                gitPassword: [
                    { required: true, message: '不能为空', trigger: "blur" }
                ],
            }
        }
    },
    mounted() {
        this.getUserInfo('read');
        this.getUserInfo('write');
    },
    methods: {
        replaceText(content = '') {
            const len = content.length;
            return new Array(len).fill('*').join('');
        },
        getUserInfo(type) {
            const params = {
                type,
                workspaceId: this.$route.query.workspaceId,
            }
            api.fetch(`/dss/framework/workspace/git`, params, 'get').then((rst) => {
                const userInfo = rst.gitUser || {};
                const tempInfo = {
                    gitUser: userInfo.gitUser || '',
                    gitPassword: userInfo.gitPassword || '',
                    gitToken: userInfo.gitToken || '',
                    originGitUser: userInfo.gitUser || '',
                    originGitPassword: userInfo.gitPassword || '',
                    originGitToken: userInfo.gitToken || '',
                    mode: rst.gitUser ? 'view' : 'add',
                    type
                }
                if (type === 'read') {
                    this.readForm = { ...tempInfo }
                } else {
                    this.writeForm = { ...tempInfo }
                }
            })
        },
        beforeSubmitReadUser(data) {
            this.$refs['readFormRef'].validate((valid) => {
                if (valid) {
                    this.submitUser(data);
                }
            })
        },
        // 测试git读写账号
        beforeSubmitWriteUser(data) {
            this.$refs['writeFormRef'].validate((valid) => {
                if (valid) {
                    if(this.writeForm.mode === 'add') {
                        this.$Modal.confirm({
                            title: 'Git读写账号配置',
                            content: `<div>确认配置Git读写账号?
                                    <p style="color: red;">注意:Git读写账号配置后，账号名在前端页面不允许做
                                    修改(若需修改，请联系大数据生产助手)，请确认无设三再操作</p></div>`,
                            onOk: () => {
                                const params = {
                                    gitUserName: data.gitUser,
                                    gitPassword: data.gitToken
                                }
                                api.fetch(`/dss/framework/workspace/gitConnect`, params, 'get').then(() => {
                                    this.submitUser(data);
                                })
                            }
                        });
                    } else {
                        const params = {
                            gitUserName: data.gitUser,
                            gitPassword: data.gitToken
                        }
                        api.fetch(`/dss/framework/workspace/gitConnect`, params, 'get').then(() => {
                            this.submitUser(data);
                        })
                    }
                }
            })
        },
        submitUser(data) {
            const params = {
                gitUser: data.gitUser,
                gitPassword: data.gitPassword,
                workspaceId: this.$route.query.workspaceId,
                gitToken: data.gitToken,
                type: data.type
            }
            api.fetch(`/dss/framework/workspace/git`, params, 'post').then(() => {
                this.$Message.success("保存成功!");
                this.getUserInfo(data.type);
            });
        },
        handleCancel(key) {
            this[key].mode = 'view';
            this[key].gitUser = this[key].originGitUser;
            this[key].gitToken = this[key].originGitToken;
            this[key].gitPassword = this[key].originGitPassword;
        }
    }
}
</script>
<style lang="less" scoped>
.git-manage {
    padding: 24px 24px;
    font-size: 14px;
    .git-title {
        margin-bottom: 16px;
    }
    .git-panel {
        border: 1px solid #dcdee2;
        border-radius: 4px;
        padding: 16px 16px;
        margin-bottom: 16px;

        &__title {
            display: flex;
            justify-content: space-between;
            margin-bottom: 5px;

            .panel-edit {
                color: #2d8cf0;
                cursor: pointer;
            }

            .panel-cancel {
                cursor: pointer;
            }
        }

        &__form {
            width: 400px;
        }
    }
}
</style>
