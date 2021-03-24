<template>
  <div class="root">
    <IconBase class="icon-left" @click.native="$router.go(-1)">
      <LeftArrowIcon />
    </IconBase>
    <FormError :value="error" />
    <form v-on:submit.prevent="$route.params.id ? submitUpdate() : submitCreate()">
      <div class="input-group">
        <label for="name">Name</label>
        <input id="name" type="text" required placeholder="Name" v-model="currentCategory.name" />
      </div>
      <div class="input-group">
        <label for="parent">Parent</label>
        <select id="parent" v-model="currentCategory.parentId">
          <option value="" :selected="!currentCategory.parentId ? 'selected' : ''">None</option>
          <option
            v-for="category in sortedCategories.filter((c) => c.id !== currentCategory.id)"
            :key="category.id"
            :value="category.id"
          >
            {{ category.name }}
          </option>
        </select>
      </div>
      <div class="submit-container">
        <AccentedSubmitButton />
      </div>
    </form>
  </div>
</template>

<script>
import FormError from "@/components/FormError";
import AccentedSubmitButton from "@/components/AccentedSubmitButton";
import { mapActions, mapGetters } from "vuex";
import IconBase from "@/components/IconBase";
import LeftArrowIcon from "@/components/icons/LeftArrowIcon";

export default {
  name: "CategoriesForm",
  components: { LeftArrowIcon, IconBase, FormError, AccentedSubmitButton },
  data() {
    return {
      error: "",
      currentCategory: {
        name: "",
        parentId: "",
      },
    };
  },
  computed: {
    ...mapGetters({
      sortedCategories: "categories/getSortedCategories",
    }),
  },
  methods: {
    ...mapActions({
      createCategory: "categories/create",
      updateCategory: "categories/update",
      getSingle: "categories/getSingle",
      getAll: "categories/getAll",
    }),
    submitUpdate() {
      console.log(this.currentCategory);
      this.updateCategory(this.currentCategory)
        .then(() => this.$router.push("/categories"))
        .catch((e) => {
          this.error = e.response.data.message;
        });
    },
    submitCreate() {
      console.log(this.currentCategory);
      this.createCategory(this.currentCategory)
        .then(() => this.$router.push("/categories"))
        .catch((e) => {
          this.error = e.response.data.message;
        });
    },
  },
  mounted() {
    if (this.$route.params.id) {
      this.getSingle(this.$route.params.id).then((resp) => {
        this.currentCategory = { ...resp };
        if (this.currentCategory.parentId == null) {
          this.currentCategory.parentId = "";
        }
      });
    }
    this.getAll();
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
