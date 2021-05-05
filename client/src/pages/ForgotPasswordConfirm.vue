<template>
  <div class="container">
    <h1 class="desktop-title">Change your password</h1>
    <h1 class="mobile-title">What will it be this time?</h1>
    <div class="help-title" v-if="!error">If your email was correct, you should receive a token to input here.</div>
    <form @submit.prevent="execute">
      <FormError :value="error" />
      <SlidingPlaceholderInput
        class="input"
        id="token-input"
        required="true"
        v-model="token"
        type="text"
        placeholder="Reset Token"
      />

      <SlidingPlaceholderInput
        class="input"
        id="password-input"
        required="true"
        v-model="password"
        type="password"
        placeholder="New Password"
      />

      <SlidingPlaceholderInput
        class="input"
        id="confirm-password-input"
        required="true"
        v-model="confirmPassword"
        type="password"
        placeholder="Confirm New Password"
      />

      <AccentedSubmitButton :is-loading="isLoading" />
    </form>
  </div>
</template>

<script>
import SlidingPlaceholderInput from "@/components/SlidingPlaceholderInput";
import AccentedSubmitButton from "@/components/AccentedSubmitButton";
import FormError from "@/components/FormError";

export default {
  name: "ForgotPasswordConfirm",
  components: { FormError, AccentedSubmitButton, SlidingPlaceholderInput },
  data() {
    return {
      password: "",
      confirmPassword: "",
      error: "",
      token: "",
      isLoading: false,
    };
  },
  methods: {
    execute() {
      if (this.password !== this.confirmPassword) {
        this.error = "Passwords do not match! Please check that the passwords match";
        return;
      }
      if (!this.token) {
        this.error = "No token was provided";
        return;
      }
      this.$api.auth
        .passwordResetConfirm(this.token, this.password)
        .then(() => this.$router.push("/login"))
        .catch(
          (e) =>
            (this.error =
              e && e.response && e.response.data && e.response.data.message
                ? e.response.data.message
                : "Something went wrong")
        );
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

.input {
  margin-top: 16px;
}

a {
  text-decoration: none;
  color: var(--accent-main-darker);
  font-weight: 600;
}

.desktop-title {
  display: none;
}

h1 {
  margin: 0;
}

h2 {
  margin: 0;
}

.help-title {
  display: flex;
  align-items: center;
  background: #ade396;
  border: 1px solid #ade396;
  border-radius: 8px;
  padding: 0 8px;
  min-height: 40px;
  max-height: 200px;
  overflow-y: hidden;
  text-overflow: ellipsis;
  margin: 1rem 0;
  color: #2b2b2a;
}

/* Tablet Styles */
@media only screen and (min-width: 415px) and (max-width: 960px) {
  form {
    width: 66%;
  }

  .desktop-title {
    display: initial;
  }

  .mobile-title {
    display: none;
  }

  h1 {
    margin: initial;
  }
}

/* Desktop Styles */
@media only screen and (min-width: 961px) {
  form {
    width: 33%;
  }

  .desktop-title {
    display: initial;
  }

  .mobile-title {
    display: none;
  }

  h1 {
    margin: initial;
  }

  .form-error {
    margin: 1rem 0;
  }
}
</style>
