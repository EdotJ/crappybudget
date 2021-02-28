<template>
  <div>
    <form @submit.prevent="$route.params.id ? update() : create()">
      <label for="name">Name</label>
      <input id="name" type="text" v-model="newCategory.name" />

      <label for="parent">Parent</label>
      <input id="parent" type="text" v-model="newCategory.parentId" />

      <input type="submit" value="submit" />
    </form>
    <button @click="remove">Delete category</button>
  </div>
</template>

<script>
export default {
  name: "SingleCategory",
  data() {
    return {
      newCategory: {},
    };
  },
  mounted() {
    if (this.$route.params.id) {
      this.$store
        .dispatch("categories/getSingle", this.$route.params.id)
        .then((response) => {
          this.newCategory = response;
        })
        .catch(() => {
          this.$router.push("/categories");
        });
    }
  },
  methods: {
    update() {
      if (this.newCategory) {
        this.$store.dispatch("categories/update", {
          id: this.$route.params.id,
          name: this.newCategory.name,
          parentId: this.newCategory.parentId ? this.newCategory.parentId : null,
        });
      }
    },
    create() {
      if (this.newCategory) {
        this.$store.dispatch("categories/create", {
          name: this.newCategory.name,
          parentId: this.newCategory.parentId ? this.newCategory.parentId : null,
        });
      }
    },
    remove() {
      this.$store.dispatch("categories/delete", this.$route.params.id);
    },
  },
};
</script>

<style scoped></style>
