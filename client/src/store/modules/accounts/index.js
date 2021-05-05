import api from "@/api";

const state = {
  accounts: [],
  isLoading: false,
  currentAccount: null,
  currentIncome: 0,
  currentExpenses: 0,
  currentNet: 0,
};

const getters = {
  accountById: (state) => (id) => state.accounts.find((a) => a.id === id),
};

export const mutations = {
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
    state.accounts = state.accounts.map((a) => {
      return a.id === account.id
        ? {
            id: account.id,
            name: account.name,
          }
        : a;
    });
  },
  DELETE_ACCOUNT(state, id) {
    state.accounts = state.accounts.filter((a) => a.id !== id);
  },
  SET_MONTHLY_STATISTICS(state, obj) {
    state.currentAccount = obj.id;
    state.currentIncome = obj.data.income;
    state.currentExpenses = obj.data.expenses;
    state.currentNet = obj.data.net;
  },
  RESET_STATE(state) {
    state.accounts = [];
    state.isLoading = false;
    state.currentAccount = null;
    state.currentIncome = 0;
    state.currentExpenses = 0;
    state.currentNet = 0;
  },
};

export const actions = {
  getAll: async function ({ commit, state }, withBalance) {
    try {
      commit("SET_IS_LOADING", true);
      const response = await api.accounts.getAll(withBalance);
      if (response && response.data && response.data.accounts) {
        commit("SET_ACCOUNTS", response.data.accounts);
        return Promise.resolve(state.accounts[0] ? state.accounts[0].id : null);
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
      if (response && response.data) {
        return Promise.resolve(response.data);
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
        return Promise.resolve();
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
        return Promise.resolve();
      }
    } catch (e) {
      return Promise.reject(e);
    }
  },
  delete: async function ({ commit }, id) {
    try {
      const response = await api.accounts.delete(id);
      if (response && response.status === 204) {
        commit("DELETE_ACCOUNT", id);
      }
    } catch (e) {
      return Promise.reject(e);
    }
  },
  getMonthly: async function ({ commit }, id) {
    try {
      const response = await api.accounts.getMonthly(id);
      if (response && response.status === 200 && response.data) {
        commit("SET_MONTHLY_STATISTICS", { id, data: response.data });
      }
      return Promise.resolve();
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
