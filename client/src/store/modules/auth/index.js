import api from "@/api";

const state = {
  accessToken: "",
  // TODO: add refresh token support
};

const getters = {
  getAccessToken: (state) => state.accessToken,
  isLoggedIn: (state) => !!state.accessToken
};

const mutations = {
  SET_ACCESS_TOKEN(state, token) {
    state.accessToken = token;
  },
};

const actions = {
  login: async function ({ commit }, credentials) {
    try {
      const response = await api.users.login(credentials.username, credentials.password);
      if (response.data) {
        commit("SET_ACCESS_TOKEN", response.data.access_token);
      }
    } catch (e) {
      return Promise.reject(e);
    }
  },
  register: async function (state, body) {
    try {
      await api.users.register(body.username, body.password, body.email);
    } catch (e) {
      return Promise.reject(e);
    }
  },
};

export default {
  namespaced: true,
  state,
  getters,
  mutations,
  actions,
};
