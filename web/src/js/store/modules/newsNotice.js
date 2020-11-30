import api from '@/js/service/api';
import { isEmpty } from 'lodash';
import storage from '@/js/helper/storage';
const prefix = process.env.VUE_APP_MN_CONFIG_PREFIX || `${window.location.protocol}//${window.location.host}/operationApi/`;

const state = {
  unreadNewscount: 0,
  isTimer: true,
};

const mutations = {
  SET_UNREAD_NEWS: (state, unreadCount) => {
    state.unreadNewscount = unreadCount;
  },
  SET_IS_TIMER: (state, { isTimer }) => {
    state.isTimer = isTimer;
  }
};

const actions = {
  getUnreadNewsCount({ commit }, { username }) {
    if (isEmpty(username)) {
      const userInfo = storage.get('userInfo');
      username = userInfo.basic.username;
    }
    return new Promise((resolve, reject) => {
      api.fetch(`${prefix}userFeedBacks/commission`, { username }, 'get').then((res) => {
        let unreadCount = 0;
        if (res > 0) {
          unreadCount = res;
        }
        commit('SET_UNREAD_NEWS', unreadCount);
        commit('SET_IS_TIMER', { isTimer: true });
        resolve(res);
      }).catch((err) => {
        commit('SET_IS_TIMER', { isTimer: false });
        reject(err);
      });
    })
  }
};

export default {
  namespaced: true,
  state,
  mutations,
  actions
};
