<template>
  <div class="root">
    <IconBase class="icon-left" @click.native="$router.go(-1)"><LeftArrowIcon /> </IconBase>
    <FormError :value="error" />
    <form v-on:submit.prevent="$route.params.id ? submitUpdate() : submitCreate()">
      <StyledInput type="text" :required="true" placeholder="Name" v-model="currentAccount.name" />
      <div class="submit-container">
        <AccentedSubmitButton />
      </div>
    </form>
  </div>
</template>

<script>
import FormError from "@/components/FormError";
import AccentedSubmitButton from "@/components/AccentedSubmitButton";
import { mapActions } from "vuex";
import IconBase from "@/components/IconBase";
import LeftArrowIcon from "@/components/icons/LeftArrowIcon";
import StyledInput from "@/components/StyledInput";

export default {
  name: "AccountForm",
  components: { StyledInput, LeftArrowIcon, IconBase, FormError, AccentedSubmitButton },
  data() {
    return {
      error: "",
      currentAccount: {
        name: "",
      },
    };
  },
  methods: {
    ...mapActions({
      createAccount: "accounts/create",
      updateAccount: "accounts/update",
      getSingle: "accounts/getSingle",
    }),
    submitUpdate() {
      this.updateAccount(this.currentAccount)
        .then(() => this.$router.push("/accounts"))
        .catch((e) => (this.error = e.response.data));
    },
    submitCreate() {
      this.createAccount(this.currentAccount)
        .then(() => this.$router.push("/accounts"))
        .catch((e) => (this.error = e.response.data));
    },
  },
  mounted() {
    if (this.$route.params.id) {
      this.getSingle(this.$route.params.id).then((resp) => {
        this.currentAccount = resp;
      });
    }
  },
};
</script>

<style scoped>
.root {
  display: flex;
  align-items: center;
  flex-direction: column;
  padding: 3rem 8px 8px 8px;
}

.icon-left {
  align-self: flex-start;
  padding: 8px;
  width: 48px;
  height: 48px;
  border-radius: 32px;
  font-size: 2rem;
  background: var(--accent-main);
  color: var(--foreground-accent);
}

.icon-left:hover {
  background: var(--accent-main-darker);
  color: var(--foreground-accent);
}
form {
  width: 100%;
}

.input-group {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.input-group input {
  width: 80%;
}

.submit-container {
  display: flex;
  align-items: center;
  justify-content: flex-end;
}

/* Desktop Styles */
@media only screen and (min-width: 961px) {
  .root {
    padding: 3rem;
    width: 80%;
  }

  form {
    margin-top: 20%;
    width: 60%;
  }
}
</style>
