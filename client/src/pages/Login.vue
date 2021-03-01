<template>
  <div>
    <div>{{ error }}</div>
    <form @submit.prevent="login">
      <label>Username</label>
      <input required v-model="username" type="text" placeholder="Username" />

      <label>Password</label>
      <input required v-model="password" type="password" placeholder="Password" />

      <input type="submit" text value="Submit" />
    </form>
  </div>
</template>

<script>
import { mapGetters } from "vuex";

export default {
  name: "Login",
  data() {
    return {
      username: "",
      password: "",
      error: "",
    };
  },
  computed: {
    ...mapGetters(["auth/getAccessToken"]),
  },
  methods: {
    login() {
      this.$store.dispatch("auth/login", { username: this.username, password: this.password }).catch((e) => {
        if (e && e.response && e.response.data) {
          this.error = e.response.data.message;
        }
      });
    },
  },
};
</script>

<style scoped></style>
