<template>
  <div class="container">
    <form @submit.prevent="$route.params.id ? update() : create()">
      <div>
        <label for="name">Name</label>
        <input id="name" type="text" v-model="newGoal.name" />
      </div>

      <div>
        <label for="description">Description</label>
        <input id="description" type="text" v-model="newGoal.description" />
      </div>

      <div>
        <label for="value">Value</label>
        <input id="value" type="number" step="0.01" v-model="newGoal.value" />
      </div>

      <div>
        <label for="date">Date</label>
        <input id="date" type="date" v-model="newGoal.date" />
      </div>

      <div>
        <label for="account">Account</label>
        <input id="account" type="text" v-model="newGoal.accountId" />
      </div>
      <input type="submit" value="submit" />
    </form>
    <button @click="remove">Delete goal</button>
  </div>
</template>

<script>
export default {
  name: "SingleGoal",
  data() {
    return {
      newGoal: {},
    };
  },
  mounted() {
    if (this.$route.params.id) {
      this.$store
        .dispatch("goals/getSingle", this.$route.params.id)
        .then((response) => {
          this.newGoal = response;
        })
        .catch(() => {
          this.$router.push("/goals");
        });
    }
  },
  methods: {
    update() {
      if (this.newGoal) {
        this.$store.dispatch("goals/update", {
          id: this.$route.params.id,
          ...this.newGoal,
        });
      }
    },
    create() {
      if (this.newGoal) {
        this.$store.dispatch("goals/create", this.newGoal);
      }
    },
    remove() {
      this.$store.dispatch("goals/delete", this.$route.params.id);
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
