import api from "@/api";

const state = {
  goals: [],
  isLoading: false,
  goalTypes: [],
};

const getters = {};

export const mutations = {
  SET_GOALS(state, goals) {
    state.goals = goals;
  },
  SET_IS_LOADING(state, isLoading) {
    state.isLoading = isLoading;
  },
  ADD_GOAL(state, goal) {
    state.goals.push({
      ...goal,
    });
  },
  UPDATE_GOAL(state, goal) {
    state.goals = state.goals.map((c) =>
      c.id === goal.id
        ? {
            ...goal,
          }
        : c
    );
  },
  DELETE_GOAL(state, id) {
    state.goals = state.goals.filter((c) => c.id !== id);
  },
  SET_GOAL_TYPES(state, types) {
    state.goalTypes = types;
  },
  RESET_STATE(state) {
    state.goals = [];
    state.isLoading = false;
  },
};

export const actions = {
  getAll: async function ({ commit }) {
    try {
      commit("SET_IS_LOADING", true);
      const response = await api.goals.getAll();
      if (response && response.data && response.data.goals) {
        commit("SET_GOALS", response.data.goals);
      }
      return Promise.resolve();
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
      if (response && response.data) {
        return response.data;
      }
    } catch (e) {
      return Promise.reject(e.response);
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
      return Promise.resolve();
    } catch (e) {
      return Promise.reject(e.response);
    }
  },
  update: async function ({ commit }, goal) {
    try {
      const response = await api.goals.update(goal);
      if (response && response.data && response.status === 200) {
        commit("UPDATE_GOAL", response.data);
      }
      return Promise.resolve();
    } catch (e) {
      return Promise.reject(e.response);
    }
  },
  delete: async function ({ commit }, id) {
    try {
      const response = await api.goals.delete(id);
      if (response && response.status === 204) {
        commit("DELETE_GOAL", id);
      }
      return Promise.resolve();
    } catch (e) {
      return Promise.reject(e);
    }
  },
  getGoalTypes: async function ({ commit }) {
    try {
      const response = await api.goals.getGoalTypes();
      if (response && response.data && response.status === 200) {
        commit("SET_GOAL_TYPES", response.data);
      }
    } catch (e) {
      return Promise.reject(e.response);
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
