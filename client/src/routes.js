import Home from "@/pages/Home";
import Login from "@/pages/Login";
import Register from "@/pages/Register";
import Accounts from "@/pages/Accounts";
import SingleAccount from "@/pages/SingleAccount";
import Categories from "@/pages/Categories";
import SingleCategory from "@/pages/SingleCategory";
import Entries from "@/pages/Entries";
import SingleEntry from "@/pages/SingleEntry";
import Goals from "@/pages/Goals";
import SingleGoal from "@/pages/SingleGoal";

export default [
  { path: "/", component: Home },
  { path: "/login", component: Login },
  { path: "/register", component: Register },
  { path: "/accounts", component: Accounts },
  { path: "/accounts/edit/:id", component: SingleAccount },
  { path: "/accounts/create", component: SingleAccount },
  { path: "/categories", component: Categories },
  { path: "/categories/edit/:id", component: SingleCategory },
  { path: "/categories/create", component: SingleCategory },
  { path: "/entries", component: Entries },
  { path: "/entries/edit/:id", component: SingleEntry },
  { path: "/entries/create", component: SingleEntry },
  { path: "/goals", component: Goals },
  { path: "/goals/edit/:id", component: SingleGoal },
  { path: "/goals/create", component: SingleGoal },
];
