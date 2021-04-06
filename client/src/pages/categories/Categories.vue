<template>
  <div class="root">
    <div class="top">
      <h1>Categories</h1>
      <div class="add-category-button" @click="$router.push('categories/create')">+</div>
    </div>
    <table>
      <thead>
        <tr>
          <td>Name</td>
          <td>Actions</td>
        </tr>
      </thead>
      <tbody>
        <tr v-for="category in sortedCategories" :key="category.id">
          <td>{{ category.name }}</td>
          <td>
            <div class="actions">
              <DeleteButton class="action" view-box="0 0 24 24" @click.native="deleteCategory(category)" />
              <IconBase
                class="action"
                view-box="0 0 24 24"
                @click.native="$router.push(`/categories/edit/${category.id}`)"
              >
                <EditIcon />
              </IconBase>
            </div>
          </td>
        </tr>
      </tbody>
    </table>
    <ConfirmationModal
      :show="showConfirmationModal"
      v-on:close-modal="toggleConfirmModal"
      v-on:confirmed="submitDelete()"
      entity-name="category"
      :name="deletingCategory.name"
      warning="This will delete all entries for this category!"
    />
  </div>
</template>

<script>
import IconBase from "@/components/IconBase";
import EditIcon from "@/components/icons/EditIcon";
import ConfirmationModal from "@/components/ConfirmationModal";
import { mapActions, mapGetters } from "vuex";
import DeleteButton from "@/components/DeleteButton";

export default {
  name: "Categories",
  components: { DeleteButton, IconBase, EditIcon, ConfirmationModal },
  data() {
    return {
      deletingCategory: {},
      showConfirmationModal: false,
    };
  },
  computed: {
    ...mapGetters({
      sortedCategories: "categories/getSortedCategories",
    }),
  },
  methods: {
    toggleConfirmModal() {
      this.showConfirmationModal = !this.showConfirmationModal;
      if (!this.showConfirmationModal) {
        this.deletingCategory = {};
      }
    },
    deleteCategory(category) {
      this.deletingCategory = { ...category };
      this.toggleConfirmModal();
    },
    submitDelete() {
      this.removeCategory(this.deletingCategory.id).then(() => {
        this.getCategories().then(() => this.toggleConfirmModal());
      });
    },
    ...mapActions({
      getCategories: "categories/getAll",
      removeCategory: "categories/delete",
    }),
  },
  mounted() {
    this.getCategories();
  },
};
</script>

<style scoped>
.root {
  padding: 8px;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  flex-direction: column;
}

.top {
  display: flex;
  width: 100%;
  align-items: center;
  justify-content: space-between;
}

table {
  width: 100%;
  border-collapse: collapse;
}

thead {
  background: #cccccc;
}

td {
  text-align: center;
  vertical-align: middle;
}

.actions {
  display: flex;
  width: 100%;
  height: 100%;
  justify-content: center;
  align-items: center;
}

.action {
  width: 40px;
  height: 40px;
  padding: 8px;
  margin: 0 4px;
  border-radius: 8px;
  color: var(--accent-main-darker);
}

.action:hover {
  background: var(--accent-main);
  color: var(--foreground-accent);
}

.add-category-button {
  height: 3rem;
  width: 3rem;
  color: var(--accent-main);
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 3rem;
  border: 2px solid var(--accent-main);
  border-radius: 3rem;
  cursor: pointer;
}

.add-category-button:hover {
  background: var(--accent-main-lighter);
  color: var(--foreground-accent);
  border: 2px solid var(--accent-main-lighter);
}

/* Tablet Styles */
@media only screen and (min-width: 450px) and (max-width: 960px) {
  .root {
    padding: 1rem;
    width: 80%;
  }
}

/* Desktop Styles */
@media only screen and (min-width: 961px) {
  .root {
    padding: 3rem;
    width: 80%;
  }
  .action {
    width: 48px;
    height: 48px;
    margin: 0 16px;
  }
}
</style>
