import Vue from "vue";
import App from "@/App";
import VueRouter from "vue-router";
import router from "./router";
import api from "@/api";
import store from "@/store";

Vue.config.productionTip = false;
Vue.use(VueRouter);
Vue.prototype.$api = api;

new Vue({
  render: (h) => h(App),
  router,
  store: store,
}).$mount("#app");
