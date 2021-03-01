<template>
  <div>
    <h1 v-if="isLoggedIn">Logged in!</h1>
    <h2>Hello, World from Home!!</h2>
    <router-link to="/login" v-if="!isLoggedIn">Login</router-link>
    <router-link to="/register" v-if="!isLoggedIn">Register</router-link>
  </div>
</template>

<script>
import { mapGetters } from "vuex";

export default {
  name: "Home.vue",
  computed: {
    ...mapGetters({
      isLoggedIn: "auth/isLoggedIn",
    }),
  },
  created() {},
};
</script>

<style scoped>
div {
  display: flex;
  flex-direction: column;
}
</style>
