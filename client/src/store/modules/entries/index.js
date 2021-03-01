import api from "@/api";

const state = {
  entries: [],
  isLoading: false,
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
    state.entries.push({
      id: entry.id,
      name: entry.name,
    });
  },
  UPDATE_ENTRY(state, entry) {
    state.entries = state.entries.map((c) =>
      c.id === entry.id
        ? {
            id: entry.id,
            name: entry.name,
          }
        : c
    );
  },
  DELETE_ENTRY(state, id) {
    state.entries = state.entries.filter((c) => c.id === id);
  },
};

export const actions = {
  getAll: async function ({ commit }) {
    try {
      commit("SET_IS_LOADING", true);
      const response = await api.entries.getAll();
      if (response.data && response.data.entries) {
        commit("SET_ENTRIES", response.data.entries);
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
  create: async function ({ commit }, entry) {
    try {
      const response = await api.entries.create(entry);
      if (response && response.data && response.status === 201) {
        commit("ADD_ENTRY", response.data);
      }
    } catch (e) {
      return Promise.reject(e);
    }
  },
  update: async function ({ commit }, entry) {
    try {
      const response = await api.entries.update(entry);
      if (response && response.data && response.status === 200) {
        commit("UPDATE_ENTRY", response.data);
      }
    } catch (e) {
      return Promise.reject(e);
    }
  },
  delete: async function ({ commit }, id) {
    try {
      const response = await api.entries.delete(id);
      if (response && response.status === 204) {
        commit("DELETE_ENTRY", id);
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
