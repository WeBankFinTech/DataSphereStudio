import Vue from 'vue';
import Vuex from 'vuex';
import getters from './getters';
import newsNotice from './modules/newsNotice';

Vue.use(Vuex);

const store = new Vuex.Store({
  modules: {
    newsNotice
  },
  getters
});

export default store;
