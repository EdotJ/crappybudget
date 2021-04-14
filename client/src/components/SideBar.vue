<template>
  <div class="side-bar" v-if="isLoggedIn">
    <div class="balance-container">
      <div class="balance">
        <span class="balance-total" v-if="!isLoading">{{ totalBalance ? totalBalance.toFixed(2) : 0 }} €</span>
        <GradientLoader v-else />
        <span class="balance-subtext">Current Balance</span>
      </div>
    </div>
    <nav>
      <div class="link-container">
        <router-link to="/" class="sidebar-url">
          <span class="link-icon">
            <IconBase class="link-icon-actual" icon-name="budget" view-box="0 0 24 24">
              <BudgetIcon />
            </IconBase>
          </span>
          <span class="link-title">Budget</span>
        </router-link>
      </div>
      <div class="link-container">
        <router-link to="/charts" class="sidebar-url">
          <span class="link-icon">
            <IconBase class="link-icon-actual" icon-name="charts" view-box="0 0 24 24">
              <ChartsIcon />
            </IconBase>
          </span>
          <span class="link-title">Charts</span>
        </router-link>
      </div>
      <div class="link-container">
        <router-link to="/accounts" class="sidebar-url">
          <span class="link-icon">
            <IconBase class="link-icon-actual" icon-name="charts" view-box="0 0 24 24">
              <AccountsIcon />
            </IconBase>
          </span>
          <span class="link-title">Accounts</span>
        </router-link>
      </div>
      <div class="link-container">
        <router-link to="/categories" class="sidebar-url">
          <span class="link-icon">
            <IconBase class="link-icon-actual" icon-name="categories" view-box="0 0 24 24">
              <CategoriesIcon />
            </IconBase>
          </span>
          <span class="link-title">Categories</span>
        </router-link>
      </div>
      <div class="link-container">
        <router-link to="/external-data" class="sidebar-url">
          <span class="link-icon">
            <IconBase class="link-icon-actual" icon-name="import" view-box="0 0 24 24">
              <ImportIcon />
            </IconBase>
          </span>
          <span class="link-title">External data</span>
        </router-link>
      </div>
      <div class="link-container">
        <router-link to="/settings" class="sidebar-url">
          <span class="link-icon">
            <IconBase class="link-icon-actual" icon-name="settings" view-box="0 0 24 24">
              <SettingsIcon />
            </IconBase>
          </span>
          <span class="link-title">Settings</span>
        </router-link>
      </div>
      <div class="link-container">
        <router-link to="/logout" class="sidebar-url">
          <span class="link-icon">
            <IconBase class="link-icon-actual" icon-name="logout" view-box="0 0 24 24">
              <LogoutIcon />
            </IconBase>
          </span>
          <span class="link-title">Log Out</span>
        </router-link>
      </div>
    </nav>
  </div>
</template>

<script>
import { mapGetters, mapState } from "vuex";
import BudgetIcon from "./icons/Budget";
import ChartsIcon from "./icons/Charts";
import AccountsIcon from "./icons/Accounts";
import ImportIcon from "./icons/Import";
import SettingsIcon from "./icons/Settings";
import LogoutIcon from "./icons/LogOut";
import IconBase from "@/components/IconBase";
import CategoriesIcon from "./icons/CategoriesIcon";
import GradientLoader from "@/components/GradientLoader";

export default {
  name: "SideBar",
  components: {
    GradientLoader,
    IconBase,
    BudgetIcon,
    ChartsIcon,
    AccountsIcon,
    ImportIcon,
    SettingsIcon,
    LogoutIcon,
    CategoriesIcon,
  },
  computed: {
    ...mapGetters({
      isLoggedIn: "auth/isLoggedIn",
    }),
    ...mapState({
      totalBalance: (state) => state.entries.balance,
      isLoading: (state) => state.entries.isLoadingBalance,
    }),
  },
  mounted() {},
};
</script>

<style scoped>
.side-bar {
  height: 100vh;
  width: 250px;
  background: var(--accent-main);
  display: flex;
  flex-direction: column;
}

.side-bar .balance span {
  display: block;
}

.balance-container {
  padding: 30px;
}

.balance {
  background: var(--accent-main-lighter);
  color: var(--foreground-accent);
  padding: 1rem;
  border-radius: 12px;
}

.balance-total {
  font-weight: bold;
}

.balance-subtext {
  font-weight: lighter;
  font-size: 1rem;
  padding: 0.75rem 0;
}

.side-bar nav {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.link-container {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.sidebar-url {
  width: 85%;
  padding: 8px 0;
  text-decoration: none;
  color: var(--foreground-accent);
  display: flex;
  align-items: center;
  border-radius: 8px;
  height: 2em;
  margin: 4px 0;
}

.sidebar-url:hover {
  background-color: var(--accent-main-lighter);
}

.link-icon {
  height: 100%;
  padding: 0 8px 0 30px;
  display: flex;
  align-items: center;
}

.link-title {
  height: 100%;
  display: flex;
  align-items: center;
  padding-top: 2px;
}

.active {
  background-color: var(--accent-main-darker);
}

/* Don't display on mobile and tablet */
@media only screen and (max-width: 960px) {
  .side-bar {
    display: none;
  }
}
</style>
