import Vue from "vue";
import App from "@/App";
import VueRouter from "vue-router";
import router from "./router";
import api from "@/api";
import store from "@/store";
import ToggleButton from "vue-js-toggle-button";
import vSelect from "vue-select";
import "vue-select/dist/vue-select.css";
import "vue2-daterange-picker/dist/vue2-daterange-picker.css";

Vue.config.productionTip = false;
Vue.use(VueRouter);
Vue.use(ToggleButton);
Vue.use(vSelect);
Vue.prototype.$api = api;

new Vue({
  render: (h) => h(App),
  router,
  store: store,
}).$mount("#app");
