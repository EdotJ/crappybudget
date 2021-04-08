<template>
  <div class="side-bar" v-if="isLoggedIn">
    <div class="balance-container">
      <div class="balance">
        <span class="balance-total">{{ totalBalance ? totalBalance.toFixed(2) : 0 }} €</span>
        <span class="balance-subtext">Current Balance</span>
      </div>
    </div>
    <nav>
      <div class="link-container">
        <router-link to="/" class="sidebar-url">
          <span class="link-icon">
            <IconBase icon-name="budget" view-box="0 0 24 24" width="24" height="24">
              <BudgetIcon />
            </IconBase>
          </span>
          <span class="link-title">Budget</span>
        </router-link>
      </div>
      <div class="link-container">
        <router-link to="/charts" class="sidebar-url">
          <span class="link-icon">
            <IconBase icon-name="charts" view-box="0 0 24 24" width="24" height="24">
              <ChartsIcon />
            </IconBase>
          </span>
          <span class="link-title">Charts</span>
        </router-link>
      </div>
      <div class="link-container">
        <router-link to="/accounts" class="sidebar-url">
          <span class="link-icon">
            <IconBase icon-name="charts" view-box="0 0 24 24" width="24" height="24">
              <AccountsIcon />
            </IconBase>
          </span>
          <span class="link-title">Accounts</span>
        </router-link>
      </div>
      <div class="link-container">
        <router-link to="/categories" class="sidebar-url">
          <span class="link-icon">
            <IconBase icon-name="categories" view-box="0 0 24 24" width="24" height="24">
              <CategoriesIcon />
            </IconBase>
          </span>
          <span class="link-title">Categories</span>
        </router-link>
      </div>
      <div class="link-container">
        <router-link to="/external-data" class="sidebar-url">
          <span class="link-icon">
            <IconBase icon-name="import" view-box="0 0 24 24" width="24" height="24">
              <ImportIcon />
            </IconBase>
          </span>
          <span class="link-title">External data</span>
        </router-link>
      </div>
      <div class="link-container">
        <router-link to="/settings" class="sidebar-url">
          <span class="link-icon">
            <IconBase icon-name="settings" view-box="0 0 24 24" width="24" height="24">
              <SettingsIcon />
            </IconBase>
          </span>
          <span class="link-title">Settings</span>
        </router-link>
      </div>
      <div class="link-container">
        <router-link to="/logout" class="sidebar-url">
          <span class="link-icon">
            <IconBase icon-name="logout" view-box="0 0 24 24" width="24" height="24">
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

export default {
  name: "SideBar",
  components: { IconBase, BudgetIcon, ChartsIcon, AccountsIcon, ImportIcon, SettingsIcon, LogoutIcon, CategoriesIcon },
  computed: {
    ...mapGetters({
      isLoggedIn: "auth/isLoggedIn",
    }),
    ...mapState({
      totalBalance: (state) => state.entries.balance,
    }),
  },
  mounted() {},
};
</script>

<style scoped>
.side-bar {
  height: 100%;
  width: 250px;
  background: var(--accent-main);
  display: flex;
  flex-direction: column;
}

.side-bar span {
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
}

.sidebar-url:hover {
  background-color: var(--accent-main-lighter);
}

.link-icon {
  padding: 0 8px 0 30px;
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
