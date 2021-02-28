import api from "@/api";

const state = {
  accounts: [],
  isLoading: false,
};

const getters = {};

const mutations = {
  SET_ACCOUNTS(state, accounts) {
    state.accounts = accounts;
  },
  SET_IS_LOADING(state, isLoading) {
    state.isLoading = isLoading;
  },
  ADD_ACCOUNT(state, account) {
    state.accounts.push({
      id: account.id,
      name: account.name,
    });
  },
  UPDATE_ACCOUNT(state, account) {
    state.accounts.map((a) =>
      a.id === account.id
        ? {
            id: account.id,
            name: account.name,
          }
        : a
    );
  },
  DELETE_ACCOUNT(state, id) {
    state.accounts.filter((a) => a.id === id);
  },
};

const actions = {
  getAll: async function ({ commit }) {
    try {
      commit("SET_IS_LOADING", true);
      const response = await api.accounts.getAll();
      if (response.data && response.data.accounts) {
        commit("SET_ACCOUNTS", response.data.accounts);
      }
    } catch (e) {
      return Promise.reject(e);
    } finally {
      commit("SET_IS_LOADING", false);
    }
  },
  getSingle: async function ({ commit }, id) {
    try {
      commit("SET_IS_LOADING", true);
      const response = await api.accounts.getSingle(id);
      if (response.data) {
        return response.data;
      }
    } catch (e) {
      return Promise.reject(e);
    } finally {
      commit("SET_IS_LOADING", false);
    }
  },
  create: async function ({ commit }, account) {
    try {
      const response = await api.accounts.create(account);
      if (response && response.data && response.status === 201) {
        commit("ADD_ACCOUNT", response.data);
      }
    } catch (e) {
      return Promise.reject(e);
    }
  },
  update: async function ({ commit }, account) {
    try {
      const response = await api.accounts.update(account);
      if (response && response.data && response.status === 200) {
        commit("UPDATE_ACCOUNT", response.data);
      }
    } catch (e) {
      return Promise.reject(e);
    }
  },
  delete: async function ({ commit }, id) {
    try {
      const response = await api.accounts.delete(id);
      if (response && response.status === 200) {
        commit("DELETE_ACCOUNT", id);
      }
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
