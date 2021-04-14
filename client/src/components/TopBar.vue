<template>
  <div class="top-bar">
    <div class="click-overlay" v-if="show" v-on:click.self="toggleBurger()"></div>
    <div class="bar-elements">
      <div class="balance-container">
        <div class="balance">
          <span class="balance-total" v-if="!isLoadingBalance">{{ totalBalance ? totalBalance.toFixed(2) : 0 }} €</span>
          <GradientLoader class="gradient" v-else />
          <span class="balance-subtext">Current Balance</span>
        </div>
      </div>
      <IconBase icon-name="hamburger" v-on:click.native="toggleBurger()" view-box="0 0 48 48">
        <HamburgerIcon />
      </IconBase>
    </div>
    <transition name="slide">
      <nav class="hamburger-menu" v-if="show">
        <router-link to="/" class="hamburger-menu-url" v-on:click.native="toggleBurger()">Home</router-link>
        <router-link to="/charts" class="hamburger-menu-url" v-on:click.native="toggleBurger()" v-if="isLoggedIn">
          Charts
        </router-link>
        <router-link to="/accounts" class="hamburger-menu-url" v-on:click.native="toggleBurger()" v-if="isLoggedIn">
          Accounts
        </router-link>
        <router-link to="/categories" class="hamburger-menu-url" v-on:click.native="toggleBurger()" v-if="isLoggedIn">
          Categories
        </router-link>
        <router-link
          to="/external-data"
          class="hamburger-menu-url"
          v-on:click.native="toggleBurger()"
          v-if="isLoggedIn"
        >
          External Data
        </router-link>
        <router-link to="/settings" class="hamburger-menu-url" v-on:click.native="toggleBurger()" v-if="isLoggedIn">
          Settings
        </router-link>
        <router-link to="/logout" class="hamburger-menu-url" v-if="isLoggedIn"> Log out</router-link>
      </nav>
    </transition>
  </div>
</template>

<script>
import HamburgerIcon from "@/components/icons/HamburgerIcon";
import IconBase from "@/components/IconBase";
import { mapGetters, mapState } from "vuex";
import GradientLoader from "@/components/GradientLoader";

export default {
  name: "TopBar",
  components: { GradientLoader, HamburgerIcon, IconBase },
  computed: {
    ...mapGetters({
      isLoggedIn: "auth/isLoggedIn",
    }),
    ...mapState({
      totalBalance: (state) => state.entries.balance,
      isLoadingBalance: (state) => state.entries.isLoadingBalance,
    }),
  },
  data() {
    return {
      show: false,
    };
  },
  methods: {
    toggleBurger() {
      this.show = !this.show;
    },
  },
};
</script>

<style scoped>
.top-bar {
  width: 100%;
  min-height: 60px;
  background: var(--accent-main);
  display: flex;
  justify-content: flex-end;
  align-items: center;
  color: var(--foreground-accent);
}

.top-bar svg {
  width: 48px;
  height: 48px;
}

.bar-elements {
  width: 100%;
  display: flex;
  justify-content: space-between;
  margin: 0 8px;
}

.hamburger-menu {
  opacity: 0.98;
  background-color: #111111dd;
  position: absolute;
  left: 0;
  top: 60px;
  height: 350px;
  width: 100vw;
  padding: 10px 0;
  display: flex;
  flex-direction: column;
  z-index: 20;
}

.hamburger-menu-url {
  width: 100%;
  padding: 10px 0;
  margin-top: 3px;
  background-color: var(--accent-main-lighter-transparent);
  text-decoration: none;
  color: var(--foreground-accent);
}

.hamburger-menu-url:hover {
  background-color: var(--accent-main-darker-transparent);
}

.click-overlay {
  position: absolute;
  height: 100%;
  width: 100%;
  top: 0;
  left: 0;
  opacity: 0;
  background: var(--accent-main-darker);
  z-index: 11;
}

.slide-enter-active {
  -moz-transition-duration: 0.2s;
  -webkit-transition-duration: 0.2s;
  -o-transition-duration: 0.2s;
  transition-duration: 0.2s;
}

.slide-leave-active {
  -moz-transition-duration: 0.2s;
  -webkit-transition-duration: 0.2s;
  -o-transition-duration: 0.2s;
  transition-duration: 0.2s;
}

.slide-enter-to,
.slide-leave {
  max-height: 100px;
  overflow: hidden;
}

.slide-enter,
.slide-leave-to {
  overflow: hidden;
  max-height: 0;
}

.balance-container {
  padding: 0;
  flex-grow: 1;
  margin-right: 3rem;
}

.balance {
  background: var(--accent-main-lighter);
  color: var(--foreground-accent);
  border-radius: 12px;
  display: flex;
  flex-direction: row-reverse;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem;
}

.balance-total {
  font-weight: bold;
}

.balance-subtext {
  font-weight: lighter;
  font-size: 1rem;
}

.gradient {
  width: 40%;
}

/* Tablet Styles */
@media only screen and (min-width: 415px) and (max-width: 960px) {
  .balance {
    justify-content: flex-end;
  }

  .balance-subtext {
    margin-right: 1rem;
  }
}
/* Desktop Styles */
@media only screen and (min-width: 961px) {
  .top-bar {
    display: none;
  }
}
</style>
