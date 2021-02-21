import Vue from "vue"
import App from "@/App";
import VueRouter from 'vue-router'

Vue.config.productionTip = false
Vue.use(VueRouter);
const Foo = { template: '<div>foo</div>' }
const Bar = { template: '<div>bar</div>' }

const routes = [
    { path: '/foo', component: Foo },
    { path: '/bar', component: Bar }
]

const router = new VueRouter({
    mode: 'history',
    routes
})

new Vue({
    render: h => h(App),
    router
}).$mount('#app')