<template>
  <div class="container">
    <form @submit.prevent="$route.params.id ? update() : create()">
      <div>
        <label for="name">Name</label>
        <input id="name" type="text" v-model="newEntry.name" />
      </div>

      <div>
        <label for="value">Value</label>
        <input id="value" type="number" step="0.01" v-model="newEntry.value" />
      </div>

      <div>
        <label for="date">Date</label>
        <input id="date" type="date" v-model="newEntry.date" />
      </div>

      <div>
        <label for="account">Account</label>
        <input id="account" type="text" v-model="newEntry.accountId" />
      </div>

      <div>
        <label for="category">Category</label>
        <input id="category" type="text" v-model="newEntry.categoryId" />
      </div>

      <div>
        <label for="isExpense">Expense?</label>
        <input id="isExpense" type="checkbox" v-model="newEntry.isExpense" />
      </div>
      <input type="submit" value="submit" />
    </form>
    <button @click="remove">Delete entry</button>
  </div>
</template>

<script>
export default {
  name: "SingleEntry",
  data() {
    return {
      newEntry: {},
    };
  },
  mounted() {
    if (this.$route.params.id) {
      this.$store
        .dispatch("entries/getSingle", this.$route.params.id)
        .then((response) => {
          this.newEntry = response;
        })
        .catch(() => {
          this.$router.push("/entries");
        });
    }
  },
  methods: {
    update() {
      if (this.newEntry) {
        this.$store.dispatch("entries/update", {
          id: this.$route.params.id,
          ...this.newEntry,
        });
      }
    },
    create() {
      if (this.newEntry) {
        this.$store.dispatch("entries/create", this.new);
      }
    },
    remove() {
      this.$store.dispatch("entries/delete", this.$route.params.id);
    },
  },
};
</script>

<style scoped>
.container {
  display: flex;
  flex-direction: column;
}
</style>
