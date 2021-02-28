<template>
  <div>
    <div v-if="isLoading">Loading...</div>
    <ul v-else>
      <li v-for="category in this.categories" :key="category.id">
        <div>{{ category.name }}</div>
        <router-link :to="`/categories/edit/${category.id}`">Edit</router-link>
      </li>
    </ul>
  </div>
</template>

<script>
import { mapState } from "vuex";

export default {
  name: "Categories",
  mounted() {
    this.$store.dispatch("categories/getAll");
  },
  computed: {
    ...mapState({
      categories: (state) => state.categories.categories,
      isLoading: (state) => state.categories.isLoading,
    }),
  },
};
</script>

<style scoped></style>
