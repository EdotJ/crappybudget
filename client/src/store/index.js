import Vue from "vue";
import Vuex from "vuex";
import vuexPersistedState from "vuex-persistedstate";
import interceptor from "@/api/interceptor";
import auth from "./modules/auth";
import accounts from "./modules/accounts";
import categories from "./modules/categories";
import entries from "./modules/entries";
import goals from "./modules/goals";

Vue.use(Vuex);

const debug = process.env.NODE_ENV !== "production";

const persistedState = vuexPersistedState({
  key: "app_state",
  reducer: (state) => ({
    auth: state.auth,
  }),
});

const actions = {
  refreshBalances({ dispatch, state }) {
    if (state.accounts.currentAccount) {
      dispatch("accounts/getMonthly", state.accounts.currentAccount);
    }
    dispatch("goals/getAll");
  },
  async logout({ commit }) {
    commit("auth/CLEAR_TOKENS");
    commit("auth/RESET_STATE");
    commit("accounts/RESET_STATE");
    commit("categories/RESET_STATE");
    commit("entries/RESET_STATE");
    commit("goals/RESET_STATE");
    await localStorage.clear();
    await sessionStorage.clear();
    return Promise.resolve();
  },
};

export default new Vuex.Store({
  modules: { auth, accounts, categories, entries, goals },
  actions: actions,
  plugins: [persistedState, interceptor],
  strict: debug,
});
