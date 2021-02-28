<template>
  <div>
    <form @submit.prevent="register">
      <label>Username</label>
      <input required v-model="username" type="text" placeholder="Username" />

      <label>Email</label>
      <input required v-model="email" type="email" placeholder="Email" />

      <label>Password</label>
      <input required v-model="password" type="password" placeholder="Password" />

      <input type="submit" value="Submit" />
    </form>
  </div>
</template>

<script>
export default {
  name: "Register",
  data() {
    return {
      username: "",
      password: "",
      email: "",
    };
  },
  methods: {
    register() {
      this.$store.dispatch("auth/register", { username: this.username, password: this.password, email: this.email });
    },
  },
};
</script>

<style scoped>
div {
  width: 100%;
  display: flex;
  justify-content: center;
}
form {
  display: flex;
  flex-direction: column;
  width: 33%;
}
</style>
