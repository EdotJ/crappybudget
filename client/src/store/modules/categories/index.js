import api from "@/api";

const state = {
  categories: [],
  isLoading: false,
};

const getters = {};

export const mutations = {
  SET_CATEGORIES(state, categories) {
    state.categories = categories;
  },
  SET_IS_LOADING(state, isLoading) {
    state.isLoading = isLoading;
  },
  ADD_CATEGORY(state, category) {
    state.categories.push({
      id: category.id,
      name: category.name,
      parentId: category.parentId,
    });
  },
  UPDATE_CATEGORY(state, category) {
    state.categories = state.categories.map((c) =>
      c.id === category.id
        ? {
            id: category.id,
            name: category.name,
            parentId: category.parentId,
          }
        : c
    );
  },
  DELETE_CATEGORY(state, id) {
    state.categories = state.categories.filter((c) => c.id === id);
  },
};

export const actions = {
  getAll: async function ({ commit }) {
    try {
      commit("SET_IS_LOADING", true);
      const response = await api.categories.getAll();
      if (response.data && response.data.categories) {
        commit("SET_CATEGORIES", response.data.categories);
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
      const response = await api.categories.getSingle(id);
      if (response.data) {
        return response.data;
      }
    } catch (e) {
      return Promise.reject(e);
    } finally {
      commit("SET_IS_LOADING", false);
    }
  },
  create: async function ({ commit }, category) {
    try {
      const response = await api.categories.create(category);
      if (response && response.data && response.status === 201) {
        commit("ADD_CATEGORY", response.data);
      }
    } catch (e) {
      return Promise.reject(e);
    }
  },
  update: async function ({ commit }, category) {
    try {
      const response = await api.categories.update(category);
      if (response && response.data && response.status === 200) {
        commit("UPDATE_CATEGORY", response.data);
      }
    } catch (e) {
      return Promise.reject(e);
    }
  },
  delete: async function ({ commit }, id) {
    try {
      const response = await api.categories.delete(id);
      if (response && response.status === 204) {
        commit("DELETE_CATEGORY", id);
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
