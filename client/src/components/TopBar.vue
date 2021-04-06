<template>
  <div class="top-bar">
    <IconBase icon-name="hamburger" v-on:click.native="toggleBurger()" view-box="0 0 48 48">
      <HamburgerIcon />
    </IconBase>
    <transition name="slide">
      <nav class="hamburger-menu" v-if="show">
        <router-link to="/" class="hamburger-menu-url" v-on:click.native="toggleBurger()">Budget</router-link>
        <router-link to="/charts" class="hamburger-menu-url" v-on:click.native="toggleBurger()" v-if="isLoggedIn">
          Charts
        </router-link>
        <router-link to="/accounts" class="hamburger-menu-url" v-on:click.native="toggleBurger()" v-if="isLoggedIn">
          Accounts
        </router-link>
        <router-link to="/import" class="hamburger-menu-url" v-on:click.native="toggleBurger()" v-if="isLoggedIn">
          Import
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
import { mapGetters } from "vuex";

export default {
  name: "TopBar",
  components: { HamburgerIcon, IconBase },
  computed: {
    ...mapGetters({
      isLoggedIn: "auth/isLoggedIn",
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
  margin: 0 8px;
  width: 48px;
  height: 48px;
}

.hamburger-menu {
  opacity: 0.98;
  background-color: #111111dd;
  position: absolute;
  left: 0;
  top: 60px;
  height: 300px;
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

/* Desktop Styles */
@media only screen and (min-width: 961px) {
  .top-bar {
    display: none;
  }
}
</style>
