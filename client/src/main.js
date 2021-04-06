import Vue from "vue";
import App from "@/App";
import VueRouter from "vue-router";
import router from "./router";
import api from "@/api";
import store from "@/store";
import ToggleButton from "vue-js-toggle-button";

Vue.config.productionTip = false;
Vue.use(VueRouter);
Vue.use(ToggleButton);
Vue.prototype.$api = api;

new Vue({
  render: (h) => h(App),
  router,
  store: store,
}).$mount("#app");
