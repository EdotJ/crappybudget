<template>
  <div class="entry">
    <div class="left">
      <div class="title">
        {{ entry.name }}
      </div>
      <div class="date">
        {{ entry.date }}
      </div>
    </div>
    <div class="center"></div>
    <div class="right">
      <div class="entry-data">
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
        <IconBase
          width="24"
          height="24"
          view-box="0 0 24 24"
          class="delete-button"
          v-on:click.native.stop="toggleConfirmationModal"
        >
          <DeleteIcon />
        </IconBase>
      </div>
      <ConfirmationModal
        :show="showConfirmation"
        v-on:close-modal="toggleConfirmationModal"
        v-on:confirmed="deleteEntryAfterConfirmation"
      />
    </div>
  </div>
</template>

<script>
import { mapState, mapActions } from "vuex";
import IconBase from "@/components/IconBase";
import DeleteIcon from "@/components/icons/DeleteIcon";
import ConfirmationModal from "@/components/ConfirmationModal";

export default {
  name: "Entry",
  components: { ConfirmationModal, DeleteIcon, IconBase },
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
      if (!this.entry.category || this.categories.get() === undefined) {
        return "";
      }
      const category = this.categories.get(this.entry.categoryId);
      let parent;
      if (category.parentId) {
        parent = this.categories.get(category.parentId);
      }
      return parent ? parent.name + " > " + category.name : category.name;
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
  },
};
</script>

<style scoped>
.entry {
  display: flex;
  padding: 20px;
  margin: 0 8px 8px 8px;
  justify-content: space-between;
  align-items: center;
  background: #eeeeee;
  border-radius: 8px;
  box-shadow: 0 1px 1px -1px gray;
}

.entry:hover {
  background: #cccccc;
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
}

.value {
  display: flex;
  justify-content: flex-end;
}

.category-full,
.category-mobile {
  font-size: 0.75rem;
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

.delete-button {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-left: 16px;
  border-radius: 0.5rem;
  color: var(--accent-main);
  border: 0;
  font-size: 1.5rem;
  padding: 8px;
  width: 2.5rem;
  height: 2.5rem;
}

.delete-button:hover {
  background: var(--accent-main);
  color: var(--foreground-accent);
}

h1 {
  font-size: 1.5rem;
}

.buttons {
  width: 100%;
  display: flex;
  justify-content: space-between;
  padding: 1rem 1rem;
}

/* Tablet Styles */
@media only screen and (min-width: 600px) and (max-width: 960px) {
  .buttons {
    width: 100%;
    display: flex;
    justify-content: space-between;
    padding: 1rem 3rem;
  }
}

/* Smaller Desktop Styles */
@media only screen and (min-width: 961px) and (max-width: 1100px) {
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

  .buttons {
    width: 100%;
    display: flex;
    justify-content: space-between;
    padding: 1rem 3rem;
  }
}
</style>
