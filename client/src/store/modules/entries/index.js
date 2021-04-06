import api from "@/api";

const state = {
  entries: [],
  isLoading: false,
  balance: null,
  totalPages: null,
};

const getters = {};

export const mutations = {
  SET_ENTRIES(state, entries) {
    state.entries = entries;
  },
  SET_IS_LOADING(state, isLoading) {
    state.isLoading = isLoading;
  },
  ADD_ENTRY(state, entry) {
    if (state.entries.length > 7) {
      state.entries.pop();
    }
    state.entries.push(entry);
    state.entries.sort((a, b) => {
      return new Date(b.date) - new Date(a.date);
    });
  },
  UPDATE_ENTRY(state, entry) {
    state.entries = state.entries.map((c) => (c.id === entry.id ? { ...entry } : c));
  },
  DELETE_ENTRY(state, id) {
    state.entries = state.entries.filter((c) => c.id !== id);
  },
  SET_BALANCE(state, balance) {
    state.balance = balance;
  },
  SET_TOTAL_PAGES(state, totalPages) {
    state.totalPages = totalPages;
  },
};

export const actions = {
  getAll: async function ({ commit }, payload) {
    try {
      commit("SET_IS_LOADING", true);
      const response = payload.accountId
        ? await api.entries.getAllForAccount(payload.accountId, payload.page)
        : await api.entries.getAll(payload.page);
      if (response && response.data && response.data.entries) {
        commit("SET_ENTRIES", response.data.entries);
        commit("SET_TOTAL_PAGES", response.data.totalPages);
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
      const response = await api.entries.getSingle(id);
      if (response.data) {
        return response.data;
      }
    } catch (e) {
      return Promise.reject(e);
    } finally {
      commit("SET_IS_LOADING", false);
    }
  },
  create: async function ({ commit, dispatch }, entry) {
    try {
      const response = await api.entries.create(entry);
      if (response && response.data && response.status === 201) {
        commit("ADD_ENTRY", response.data);
        dispatch("getBalance");
        return Promise.resolve();
      }
    } catch (e) {
      return Promise.reject(e);
    }
  },
  update: async function ({ commit, dispatch }, entry) {
    try {
      const response = await api.entries.update(entry);
      if (response && response.data && response.status === 200) {
        commit("UPDATE_ENTRY", response.data);
        dispatch("getBalance");
      }
    } catch (e) {
      return Promise.reject(e);
    }
  },
  delete: async function ({ commit, dispatch }, id) {
    try {
      const response = await api.entries.delete(id);
      if (response && response.status === 204) {
        commit("DELETE_ENTRY", id);
        dispatch("getBalance");
        return Promise.resolve();
      }
    } catch (e) {
      return Promise.reject(e);
    }
  },
  getBalance: async function ({ commit }) {
    try {
      const response = await api.entries.getBalance();
      if (response && response.status === 200 && (response.data.balance || response.data.balance === 0)) {
        commit("SET_BALANCE", response.data.balance);
        return Promise.resolve();
      }
      return Promise.reject();
    } catch (e) {
      return Promise.reject(e);
    }
  },
  submitReceiptEntries: async function ({ commit }, receipt) {
    try {
      commit("SET_IS_LOADING", true);
      const response = await api.entries.submitReceiptEntries(receipt);
      if (response && response.status === 201 && response.data && response.data.entries) {
        return Promise.resolve();
      }
      return Promise.reject();
    } catch (e) {
      return Promise.reject(e);
    } finally {
      commit("SET_IS_LOADING", false);
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
