<template>
  <div class="container">
    <h1 class="desktop-title">No worries... We've got you covered!</h1>
    <h1 class="mobile-title">What was it again?</h1>
    <h2 class="mobile-title">Remind password</h2>
    <div class="error">{{ error }}</div>
    <form @submit.prevent="execute">
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
import SlidingPlaceholderInput from "@/components/SlidingPlaceholderInput";
import { mapState } from "vuex";
import AccentedSubmitButton from "@/components/AccentedSubmitButton";

export default {
  name: "ForgotPassword",
  components: { AccentedSubmitButton, SlidingPlaceholderInput },
  computed: {
    ...mapState({ isLoading: (state) => state.auth.isLoading }),
  },
  data() {
    return {
      email: "",
      error: "",
    };
  },
  methods: {
    execute() {},
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
}
</style>
