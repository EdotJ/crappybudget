<template>
  <div class="container">
    <h1>Welcome</h1>
    <FormError :value="error" />
    <form @submit.prevent="login">
      <SlidingPlaceholderInput
        class="input"
        id="username"
        required="true"
        v-model="username"
        type="text"
        placeholder="Username"
      />
      <SlidingPlaceholderInput
        class="input"
        id="password"
        required="true"
        v-model="password"
        type="password"
        placeholder="Password"
      />
      <div class="links">
        <router-link to="/reminder">Forgot password?</router-link>
        <div>
          <span class="link-text">Don't have an account yet? </span>
          <router-link to="/register">Sign up</router-link>
        </div>
      </div>
      <AccentedSubmitButton :is-loading="isLoading" />
    </form>
  </div>
</template>

<script>
import { mapGetters, mapState } from "vuex";
import SlidingPlaceholderInput from "@/components/SlidingPlaceholderInput";
import AccentedSubmitButton from "@/components/AccentedSubmitButton";
import FormError from "@/components/FormError";

export default {
  name: "Login",
  components: { FormError, AccentedSubmitButton, SlidingPlaceholderInput },
  data() {
    return {
      username: "",
      password: "",
      error: "",
    };
  },
  computed: {
    ...mapGetters(["auth/getAccessToken"]),
    ...mapState({
      isLoading: (state) => state.auth.isLoading,
      isVerified: (state) => state.auth.isVerified,
    }),
  },
  methods: {
    login() {
      this.$store
        .dispatch("auth/login", { username: this.username, password: this.password })
        .then(() => {
          if (!this.isVerified) {
            this.error = "You need to verify your account first before using the system";
            return;
          }
          this.$router.push("/");
          this.error = "";
        })
        .catch((e) => {
          if (e && e.response && e.response.data) {
            this.error = e.response.data.message;
          }
        });
    },
  },
  mounted() {
    localStorage.clear();
  },
};
</script>

<style scoped>
h1 {
  font-family: "Quicksand", sans-serif;
}

.container {
  width: 100%;
  height: 80%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

form {
  display: flex;
  flex-direction: column;
  align-content: center;
  width: 90%;
}

.input {
  margin-top: 16px;
}

.links {
  color: var(--main-fg-color);
  font-size: 0.75rem;
}

a {
  text-decoration: none;
  color: var(--accent-main-darker);
  font-weight: 600;
}

/* Tablet Styles */
@media only screen and (min-width: 415px) and (max-width: 960px) {
  form {
    width: 66%;
  }
}

/* Desktop Styles */
@media only screen and (min-width: 961px) {
  form {
    width: 40%;
  }
}
</style>
