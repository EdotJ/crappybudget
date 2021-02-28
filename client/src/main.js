import Vue from "vue";
import App from "@/App";
import VueRouter from "vue-router";
import routes from "./routes";
import api from "@/api";
import store from "@/store";

Vue.config.productionTip = false;
Vue.use(VueRouter);
Vue.prototype.$api = api;

const router = new VueRouter({
  mode: "history",
  routes,
});

new Vue({
  render: (h) => h(App),
  router,
  store: store,
}).$mount("#app");
