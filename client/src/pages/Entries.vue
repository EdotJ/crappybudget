<template>
  <div>
    <div v-if="isLoading">Loading...</div>
    <ul v-else>
      <li v-for="entry in this.entries" :key="entry.id">
        <div>{{ entry.name }}</div>
        <router-link :to="`/entries/edit/${entry.id}`">Edit</router-link>
      </li>
    </ul>
  </div>
</template>

<script>
import { mapState } from "vuex";

export default {
  name: "Entries",
  mounted() {
    this.$store.dispatch("entries/getAll");
  },
  computed: {
    ...mapState({
      entries: (state) => state.entries.entries,
      isLoading: (state) => state.entries.isLoading,
    }),
  },
};
</script>

<style scoped></style>
