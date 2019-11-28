DataSphere Studio快速使用手册
-
## 快速登录
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;为了方便用户使用，系统默认通过使用Linkis的部署用户名进行登录，比如是hadoop部署的可以直接通过 用户：hadoop，密码：hadoop(密码就是用户名)来进行登录。 首先输入前端容器地址：192.168.xx.xx:8888 接着输入用户名密码：hadoop/hadoop
![quick_start00](/images/zh_CN/charpter3/quickstart/quick_start00.png)

__注意：__ 如果要支持多用户登录，DSS的用户登录依赖Linkis，需要在linkis-GateWay的配置里面进行配置，Linkis-GateWay默认支持LDAP。

## 创建工程
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;点击创建工程，填写关键信息即可创建：
![quick_start01](/images/zh_CN/charpter3/quickstart/quick_start01.png)

## 创建工作流
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;点击创建工作流，填写关键信息即可创建：
![quick_start02](/images/zh_CN/charpter3/quickstart/quick_start02.png)

## 工作流编辑
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
1. 点击左边的对应的节点拖入到编辑框
2. 点击节点右边会弹出该节点的配置信息：
![quick_start03](/images/zh_CN/charpter3/quickstart/quick_start03.png)
3. 双击节点，可以跳转对应节点系统的编辑页面：
![quick_start04](/images/zh_CN/charpter3/quickstart/quick_start04.png)


## 工作流运行

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;点击执行按钮，工作流即可开始运行：
![quick_start05](/images/zh_CN/charpter3/quickstart/quick_start05.png)
## 工作流调度

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;在DSS首页的工程展示区域，可以将工程发布到调度系统进行
运行：
![quick_start06](/images/zh_CN/charpter3/quickstart/quick_start06.png)

详细的使用手册建：[使用手册文档]()