import Home from "@/pages/Home";
import Login from "@/pages/Login";
import Register from "@/pages/Register";
import ForgotPassword from "@/pages/ForgotPassword";
import VueRouter from "vue-router";
import store from "@/store";
import ChartsPage from "@/pages/charts/ChartsPage";
import Accounts from "@/pages/accounts/Accounts";
import ExternalData from "@/pages/ExternalData";
import Settings from "@/pages/Settings";
import AccountForm from "@/pages/accounts/AccountForm";
import Categories from "@/pages/categories/Categories";
import CategoriesForm from "@/pages/categories/CategoriesForm";
import EntryForm from "@/pages/EntryForm";
import CsvImportForm from "@/pages/CsvImportForm";
import YnabImportForm from "@/pages/YnabImportForm";
import ForgotPasswordConfirm from "@/pages/ForgotPasswordConfirm";
import CompleteVerification from "@/pages/CompleteVerification";

const routes = [
  { path: "/", component: Home },
  { path: "/entries/create", component: EntryForm },
  { path: "/entries/edit/:id", component: EntryForm },
  { path: "/charts", component: ChartsPage },
  { path: "/accounts", component: Accounts },
  { path: "/accounts/create", component: AccountForm },
  { path: "/accounts/edit/:id", component: AccountForm },
  { path: "/categories", component: Categories },
  { path: "/categories/create", component: CategoriesForm },
  { path: "/categories/edit/:id", component: CategoriesForm },
  { path: "/external-data", component: ExternalData },
  { path: "/external-data/import/csv", component: CsvImportForm },
  { path: "/external-data/import/ynab", component: YnabImportForm },
  { path: "/settings", component: Settings },
  { path: "/login", component: Login, meta: { noAuth: true } },
  { path: "/register", component: Register, meta: { noAuth: true } },
  { path: "/reminder", component: ForgotPassword, meta: { noAuth: true } },
  { path: "/reminder/changePassword", component: ForgotPasswordConfirm, meta: { noAuth: true } },
  { path: "/confirm", component: CompleteVerification, meta: { noAuth: true } },
  {
    path: "/logout",
    component: () => {
      store.dispatch("logout").then(() => {
        router.push("/login");
      });
    },
  },
];

const router = new VueRouter({
  mode: "history",
  routes,
  linkExactActiveClass: "active",
});

router.beforeEach((to, from, next) => {
  const needsAuth = !to.matched.some((record) => record.meta.noAuth);
  if (needsAuth && !store.getters["auth/getAccessToken"]) {
    next({ path: "/login" });
  } else {
    if (store.getters["auth/getAccessToken"]) {
      store.dispatch("entries/getBalance").catch((e) => {
        console.log(e);
      });
    }
    next();
  }
});

export default router;
