import api from "@/api";

const state = {
  accessToken: "",
  refreshToken: "",
};

const getters = {
  getAccessToken: (state) => state.accessToken,
  getRefreshToken: (state) => state.refreshToken,
  isLoggedIn: (state) => !!state.accessToken,
};

export const mutations = {
  SET_ACCESS_TOKEN(state, token) {
    state.accessToken = token;
  },
  SET_REFRESH_TOKEN(state, token) {
    state.refreshToken = token;
  },
  CLEAR_TOKENS(state) {
    state.accessToken = "";
    state.refreshToken = "";
  },
};

export const actions = {
  login: async function ({ commit }, credentials) {
    try {
      const response = await api.auth.login(credentials.username, credentials.password);
      if (response.data) {
        commit("SET_ACCESS_TOKEN", response.data.access_token);
        commit("SET_REFRESH_TOKEN", response.data.refresh_token);
      }
    } catch (e) {
      return Promise.reject(e);
    }
  },
  register: async function (state, body) {
    try {
      await api.auth.register(body.username, body.password, body.email);
    } catch (e) {
      return Promise.reject(e);
    }
  },
  refreshToken: async function ({ commit, state }) {
    try {
      const response = await api.auth.refreshToken(state.refreshToken);
      if (response.data) {
        commit("SET_ACCESS_TOKEN", response.data.access_token);
        commit("SET_REFRESH_TOKEN", response.data.refresh_token);
      }
      return Promise.resolve();
    } catch (e) {
      return Promise.reject(e);
    }
  },
  deleteTokens: function ({ commit }) {
    commit("CLEAR_TOKENS");
    localStorage.clear();
    window.location = "/login";
  },
};

export default {
  namespaced: true,
  state,
  getters,
  mutations,
  actions,
};
