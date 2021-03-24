import api from "@/api";

const state = {
  categories: new Map(),
  isLoading: false,
};

const getters = {
  getSortedCategories: (state) => {
    const parents = Array.from(state.categories.values()).filter((c) => !c.parent);
    return parents.map((p) => [p, ...Array.from(state.categories.values()).filter((c) => c.parent === p.id)]).flat(2);
  },
};

export const mutations = {
  SET_CATEGORIES(state, categories) {
    state.categories = new Map();
    categories.map((cat) => {
      state.categories.set(cat.id, cat);
    });
  },
  SET_IS_LOADING(state, isLoading) {
    state.isLoading = isLoading;
  },
  ADD_CATEGORY(state, category) {
    state.categories.set(category.id, {
      id: category.id,
      name: category.name,
      parentId: category.parentId,
    });
  },
  UPDATE_CATEGORY(state, category) {
    state.categories.set(category.id, {
      id: category.id,
      name: category.name,
      parentId: category.parentId,
    });
  },
  DELETE_CATEGORY(state, id) {
    state.categories.delete(id);
  },
};

export const actions = {
  getAll: async function ({ commit }) {
    try {
      commit("SET_IS_LOADING", true);
      const response = await api.categories.getAll();
      if (response && response.data && response.data.categories) {
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
      if (response && response.data) {
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
