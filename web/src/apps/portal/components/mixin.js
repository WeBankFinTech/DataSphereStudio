import api from '@/common/service/api';
export default {
  data() {
    return {};
  },
  created() {},
  methods: {
    //获得菜单内容
    getContentList() {
      let url = '/dataportal/menuContent';
      api.fetch(url, 'get').then(res => {
        this.contentList = res.menuContent;
      });
    },
    //获得组件的内容
    getComponentList(content, cb) {
      //todo 获取相应的组件
      let url = '/dataportal/menu/systemProjects';
      let params = {
        system: content
      };
      api.fetch(url, params, 'get').then(res => {
        this.componentList = res.uiList;
        //将下拉列表统一处理成[{id: , name: ''}]
        this.componentList.forEach((item) => {
          //处理组件下面最后一个下拉列表后需要选择前面两个获得，为null的情况
          if (item.returnList === null) {
            let obj = {
              name: '',
              id: ''
            };
            item.list = [obj];
          } else {
            let list = [];
            item.returnList.forEach((it, index) => {
              if (typeof it === 'string') {
                //下拉列表中元素不是对象的情况
                let obj = {
                  name: it,
                  id: index
                };
                list.push(obj);
              } else {
                list.push(it);
              }
            });
            item.list = list;
          }
        });
        cb && cb();
      });
    },
    getPortalList(menuId, cb) {
      let url = '/dataportal/menu';
      api.fetch(url, { menuId }, 'get').then(res => {
        cb && cb(res);
      });
    },
    getAccessUserList(params, cb) {
      let url = '/dataportal/menu/user';
      api.fetch(url, params, 'get').then(res => {
        cb && cb(res);
      });
    }
  }
};
