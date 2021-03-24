import api from "@/api";
import router from "@/router";

const state = {
  accessToken: "",
  refreshToken: "",
  isLoading: true,
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
  SET_IS_LOADING(state, loading) {
    state.isLoading = loading;
  },
};

export const actions = {
  login: async function ({ commit }, credentials) {
    commit("SET_IS_LOADING", true);
    try {
      const response = await api.auth.login(credentials.username, credentials.password);
      if (response && response.data) {
        commit("SET_ACCESS_TOKEN", response.data.access_token);
        commit("SET_REFRESH_TOKEN", response.data.refresh_token);
      }
      return Promise.resolve();
    } catch (e) {
      return Promise.reject(e);
    } finally {
      commit("SET_IS_LOADING", false);
    }
  },
  register: async function ({ commit }, body) {
    commit("SET_IS_LOADING", true);
    try {
      await api.auth.register(body.username, body.password, body.email);
      return Promise.resolve();
    } catch (e) {
      return Promise.reject(e);
    } finally {
      commit("SET_IS_LOADING", false);
    }
  },
  refreshToken: async function ({ commit, state }) {
    try {
      const response = await api.auth.refreshToken(state.refreshToken);
      if (response && response.data) {
        commit("SET_ACCESS_TOKEN", response.data.access_token);
        commit("SET_REFRESH_TOKEN", response.data.refresh_token);
        return Promise.resolve(true);
      }
    } catch (e) {
      return Promise.reject(e);
    }
  },
  deleteTokens: function ({ commit }) {
    commit("CLEAR_TOKENS");
    localStorage.clear();
    router.push("/login");
  },
};

export default {
  namespaced: true,
  state,
  getters,
  mutations,
  actions,
};
