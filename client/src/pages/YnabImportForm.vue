<template>
  <div class="wrapper">
    <Paper class="paper">
      <FormError :value="error" />
      <form @submit.prevent="submit">
        <StyledInput
          type="text"
          required="true"
          :value="token"
          v-model="token"
          placeholder="Personal Token"
          :maxlength="128"
        />
        <div class="submit-container">
          <AccentedSubmitButton :isLoading="isLoading" />
        </div>
      </form>
    </Paper>
  </div>
</template>

<script>
import StyledInput from "@/components/StyledInput";
import Paper from "@/components/Paper";
import AccentedSubmitButton from "@/components/AccentedSubmitButton";
import FormError from "@/components/FormError";

export default {
  name: "YnabImportForm",
  components: { AccentedSubmitButton, Paper, StyledInput, FormError },
  data() {
    return {
      token: "",
      error: "",
      isLoading: false,
    };
  },
  methods: {
    submit() {
      if (!this.token) {
        this.error = "Please add a personal token from YNAB";
      } else {
        this.error = "";
        this.isLoading = true;
        this.$api.externalData
          .importYnab(this.token)
          .then(() => {
            this.$router.push("/");
          })
          .catch((e) => {
            if (e && e.response && e.response.data && e.response.data.message) {
              this.error = e.response.data.message;
            }
          })
          .finally(() => (this.isLoading = false));
      }
    },
  },
};
</script>

<style scoped>
.wrapper {
  width: 100%;
  display: flex;
  justify-content: center;
}

form {
  width: 100%;
  margin-top: 1rem;
}

.submit-container {
  width: 100%;
  display: flex;
  justify-content: flex-end;
}

.submit-container button {
  width: 150px;
}

.paper {
  margin-top: 10%;
}

/* Tablet Styles */
@media only screen and (min-width: 415px) and (max-width: 960px) {
}

/* Desktop Styles */
@media only screen and (min-width: 961px) {
  .paper {
    width: 50%;
  }
}
</style>
