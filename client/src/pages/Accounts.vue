<template>
  <div>
    <div v-if="isLoading">Loading...</div>
    <ul v-else>
      <li v-for="account in this.accounts" :key="account.id">
        <div>{{ account.name }}</div>
        <router-link :to="`/accounts/edit/${account.id}`">Edit</router-link>
      </li>
    </ul>
  </div>
</template>

<script>
import { mapState } from "vuex";

export default {
  name: "Accounts",
  mounted() {
    this.$store.dispatch("accounts/getAll");
  },
  computed: {
    ...mapState({
      accounts: (state) => state.accounts.accounts,
      isLoading: (state) => state.accounts.isLoading,
    }),
  },
};
</script>

<style scoped></style>
