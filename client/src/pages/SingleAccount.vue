<template>
  <div>
    <form @submit.prevent="$route.params.id ? update() : create()">
      <label for="name">Name</label>
      <input id="name" type="text" v-model="newAccount.name" />

      <input type="submit" value="submit" />
    </form>
    <button @click="remove">Delete account</button>
  </div>
</template>

<script>
export default {
  name: "SingleAccount",
  computed: {},
  data() {
    return {
      newAccount: {},
    };
  },
  mounted() {
    if (this.$route.params.id) {
      this.$store
        .dispatch("accounts/getSingle", this.$route.params.id)
        .then((response) => {
          this.newAccount = response;
        })
        .catch(() => {
          this.$router.push("/accounts");
        });
    }
  },
  methods: {
    update() {
      if (this.newAccount) {
        this.$store.dispatch("accounts/update", {
          id: this.$route.params.id,
          name: "Bankomat",
        });
      }
    },
    create() {
      if (this.newAccount) {
        this.$store.dispatch("accounts/create", {
          name: this.newAccount.name,
        });
      }
    },
    remove() {
      this.$store.dispatch("accounts/delete", this.$route.params.id);
    },
  },
};
</script>

<style scoped></style>
