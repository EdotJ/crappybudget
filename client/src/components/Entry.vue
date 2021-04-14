<template>
  <div class="entry" @click="handleEntryClick">
    <div class="left">
      <div class="title">
        {{ entry.name }}
      </div>
      <div class="date">
        {{ entry.date }}
      </div>
    </div>
    <div class="center"></div>
    <div class="right" @click.stop>
      <div class="entry-data" @click="handleEntryClick">
        <div :class="['value', entry.isExpense ? 'expense' : 'income']">
          {{ entry.isExpense ? "-" : "+" }}{{ entry.value }}
        </div>
        <div class="category-full">
          {{ getCategoryWithParent() }}
        </div>
        <div class="category-mobile">
          {{ this.categories.get(entry.categoryId) !== undefined ? this.categories.get(entry.categoryId).name : "" }}
        </div>
      </div>
      <div class="actions">
        <DeleteButton class="delete-button" v-on:click.native="toggleConfirmationModal" />
      </div>
      <ConfirmationModal
        :show="showConfirmation"
        v-on:close-modal="toggleConfirmationModal"
        v-on:confirmed="deleteEntryAfterConfirmation"
        entityName="entry"
        :name="entry.name"
      />
    </div>
  </div>
</template>

<script>
import { mapState, mapActions } from "vuex";
import ConfirmationModal from "@/components/ConfirmationModal";
import DeleteButton from "@/components/DeleteButton";

export default {
  name: "Entry",
  components: { DeleteButton, ConfirmationModal },
  props: { entry: Object, index: Number, page: Number },
  computed: {
    ...mapState({
      categories: (state) => state.categories.categories,
      currentAccount: (state) => state.accounts.currentAccount,
    }),
  },
  data() {
    return {
      showConfirmation: false,
    };
  },
  methods: {
    ...mapActions({
      deleteEntry: "entries/delete",
      getEntries: "entries/getAll",
      refreshBalances: "refreshBalances",
    }),
    getCategoryWithParent() {
      if (!this.entry.categoryId || this.categories.get(this.entry.categoryId) === undefined) {
        return "";
      }
      const category = this.categories.get(this.entry.categoryId);
      let parent;
      if (category.parentId) {
        parent = this.categories.get(category.parentId);
      }
      return parent ? parent.name + " | " + category.name : category.name;
    },
    toggleConfirmationModal() {
      this.showConfirmation = !this.showConfirmation;
    },
    async deleteEntryAfterConfirmation() {
      await this.deleteEntry(this.entry.id);
      this.showConfirmation = false;
      this.refreshBalances();
      await this.getEntries({ accountId: this.currentAccount, page: this.page });
    },
    handleEntryClick() {
      this.$emit("clicked-entry");
    },
    handleEntryTextClick() {
      this.$emit("clicked-entry");
    },
  },
};
</script>

<style scoped>
.entry {
  display: flex;
  padding: 16px;
  margin-bottom: 8px;
  justify-content: space-between;
  align-items: center;
  background: var(--entry-bg);
  border-radius: 8px;
  box-shadow: 0 1px 1px -1px gray;
  cursor: pointer;
}

.entry:hover {
  background: var(--entry-hover);
}

.title,
.date {
  display: flex;
  justify-content: flex-start;
}

.date {
  font-size: 0.75rem;
}

.title {
  font-weight: bold;
  font-size: 0.75rem;
}

.value {
  display: flex;
  justify-content: flex-end;
  font-size: 0.75rem;
}

.category-full,
.category-mobile {
  font-size: 0.75rem;
  justify-content: flex-end;
  text-align: right;
  display: inline-block;
}

.category-full {
  display: none;
}

.date,
.category-full,
.category-mobile {
  color: var(--main-fg-color-transparent);
}

.expense {
  color: var(--expense-color);
}

.income {
  color: var(--income-color);
}

.right {
  display: flex;
  flex-direction: row;
}

.actions {
  display: flex;
  justify-content: center;
  align-items: center;
}

h1 {
  font-size: 1.5rem;
}

.delete-button {
  width: 32px;
  height: 32px;
  padding: 4px;
}

/* Smaller Desktop Styles */
@media only screen and (min-width: 961px) and (max-width: 1100px) {
  .entry {
    padding: 20px;
  }
}

/* Desktop Styles */
@media only screen and (min-width: 1101px) {
  .category-full {
    display: initial;
  }

  .category-mobile {
    display: none;
  }

  .entry-data {
    display: flex;
    flex-direction: column;
  }

  .title,
  .value {
    font-size: 1rem;
  }

  .entry {
    padding: 20px;
  }
}
</style>
