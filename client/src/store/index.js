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

export default new Vuex.Store({
  modules: { auth, accounts, categories, entries, goals },
  plugins: [persistedState, interceptor],
  strict: debug,
});
