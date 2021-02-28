import api from "@/api";

const state = {
  goals: [],
  isLoading: false,
};

const getters = {};

const mutations = {
  SET_GOALS(state, goals) {
    state.goals = goals;
  },
  SET_IS_LOADING(state, isLoading) {
    state.isLoading = isLoading;
  },
  ADD_GOAL(state, goal) {
    state.goals.push({
      id: goal.id,
      name: goal.name,
    });
  },
  UPDATE_GOAL(state, goal) {
    state.goals.map((c) =>
      c.id === goal.id
        ? {
            id: goal.id,
            name: goal.name,
          }
        : c
    );
  },
  DELETE_GOAL(state, id) {
    state.goals.filter((c) => c.id === id);
  },
};

const actions = {
  getAll: async function ({ commit }) {
    try {
      commit("SET_IS_LOADING", true);
      const response = await api.goals.getAll();
      if (response.data && response.data.goals) {
        commit("SET_GOALS", response.data.goals);
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
      const response = await api.goals.getSingle(id);
      if (response.data) {
        return response.data;
      }
    } catch (e) {
      return Promise.reject(e);
    } finally {
      commit("SET_IS_LOADING", false);
    }
  },
  create: async function ({ commit }, goal) {
    try {
      const response = await api.goals.create(goal);
      if (response && response.data && response.status === 201) {
        commit("ADD_GOAL", response.data);
      }
    } catch (e) {
      return Promise.reject(e);
    }
  },
  update: async function ({ commit }, goal) {
    try {
      const response = await api.goals.update(goal);
      if (response && response.data && response.status === 200) {
        commit("UPDATE_GOAL", response.data);
      }
    } catch (e) {
      return Promise.reject(e);
    }
  },
  delete: async function ({ commit }, id) {
    try {
      const response = await api.goals.delete(id);
      if (response && response.status === 200) {
        commit("DELETE_GOAL", id);
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
