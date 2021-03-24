<template>
  <div class="container">
    <h1>Welcome aboard!</h1>
    <FormError :value="error" />
    <form @submit.prevent="register">
      <SlidingPlaceholderInput
        class="input"
        id="registration-email"
        required="true"
        v-model="email"
        type="email"
        placeholder="Email Address"
      />

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
        <div>
          <span>Already have an account? </span>
          <router-link to="/login">Sign in here</router-link>
        </div>
      </div>
      <AccentedSubmitButton :is-loading="isLoading" />
    </form>
  </div>
</template>

<script>
import SlidingPlaceholderInput from "@/components/SlidingPlaceholderInput";
import AccentedSubmitButton from "@/components/AccentedSubmitButton";
import { mapState } from "vuex";
import FormError from "@/components/FormError";

export default {
  name: "Register",
  components: { SlidingPlaceholderInput, AccentedSubmitButton, FormError },
  data() {
    return {
      username: "",
      password: "",
      email: "",
      error: "",
    };
  },
  computed: mapState({ isLoading: (state) => state.auth.isLoading }),
  methods: {
    register() {
      this.$store
        .dispatch("auth/register", { username: this.username, password: this.password, email: this.email })
        .then(() => {
          this.$router.push("/login");
        })
        .catch((e) => {
          if (e && e.response && e.response.data) {
            this.error = e.response.data.message;
          }
        });
    },
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

.submit-button {
  background: var(--accent-main);
  color: var(--foreground-accent);
  border: 0;
  height: 32px;
  border-radius: 16px;
  margin-top: 32px;
  box-shadow: 0 4px 2px -2px gray;
  font-size: 1rem;
  font-family: "Quicksand", sans-serif;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.submit-button:hover {
  background: var(--accent-main-darker);
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
    width: 33%;
  }
}
</style>
