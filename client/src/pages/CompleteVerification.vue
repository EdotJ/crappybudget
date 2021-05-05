<template>
  <div class="container">
    <Spinner v-if="isLoading" />
    <form @submit.prevent="execute" v-if="!isLoading">
      <FormError class="form-error" :value="error" />
      <SlidingPlaceholderInput
        class="input"
        id="email-input"
        required="true"
        v-model="email"
        type="email"
        placeholder="Email Address"
      />

      <AccentedSubmitButton :is-loading="isLoading" />
    </form>
  </div>
</template>

<script>
import FormError from "@/components/FormError";
import AccentedSubmitButton from "@/components/AccentedSubmitButton";
import SlidingPlaceholderInput from "@/components/SlidingPlaceholderInput";
import Spinner from "@/components/Spinner";

export default {
  name: "CompleteVerification",
  components: { Spinner, FormError, AccentedSubmitButton, SlidingPlaceholderInput },
  data() {
    return {
      isLoading: false,
      email: "",
      error: "",
    };
  },
  methods: {
    execute() {
      if (!this.email) {
        this.error = "Please enter an email!";
      }
      this.$api.auth.resendEmail(this.email);
    },
  },
  mounted() {
    this.isLoading = true;
    if (!this.$route.query.token) {
      this.$router.push("/login");
    }
    this.$api.auth
      .verifyEmail(this.$route.query.token)
      .then(() => {
        this.$router.push("/login");
      })
      .catch(() => {
        this.isLoading = false;
        this.error = "Unable to verify your account. Enter your email if you'd like to try again";
      });
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
