<template>
  <div>
    <div v-if="isLoading">Loading...</div>
    <ul v-else>
      <li v-for="goal in this.goals" :key="goal.id">
        <div>{{ goal.name }}</div>
        <router-link :to="`/goals/edit/${goal.id}`">Edit</router-link>
      </li>
    </ul>
  </div>
</template>

<script>
import { mapState } from "vuex";

export default {
  name: "Goals",
  mounted() {
    this.$store.dispatch("goals/getAll");
  },
  computed: {
    ...mapState({
      goals: (state) => state.goals.goals,
      isLoading: (state) => state.goals.isLoading,
    }),
  },
};
</script>

<style scoped></style>
