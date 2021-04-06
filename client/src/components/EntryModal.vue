<template>
  <Modal v-on:close-modal="closeModal" :show="show" class="modal-root">
    <template v-slot:header>{{ entry.id ? "Update Entry" : "Create new Entry" }}</template>
    <template v-slot:content>
      <FormError :value="error" />
      <form v-on:submit.prevent="entry.id ? submitUpdate() : submitCreate()">
        <div class="input-group">
          <label for="name">Name</label>
          <input id="name" type="text" required placeholder="Name" v-model="entry.name" />
        </div>
        <div class="input-group">
          <label for="description">Description</label>
          <input id="description" type="text" placeholder="Description" v-model="entry.description" />
        </div>
        <div class="input-group">
          <label for="value">Value</label>
          <input id="value" type="number" step="0.01" min="0" required placeholder="Value" v-model="entry.value" />
        </div>
        <div class="input-group">
          <label for="date">Date</label>
          <input id="date" type="date" required placeholder="Date" v-model="entry.date" />
        </div>
        <div class="input-group checkbox-input">
          <label for="is-expense">Expense?</label>
          <input id="is-expense" type="checkbox" v-model="entry.isExpense" />
        </div>
        <div class="input-group">
          <label for="account">Account</label>
          <select id="account" required v-model="entry.accountId">
            <option v-for="account in accounts" :key="account.id" :value="account.id">{{ account.name }}</option>
          </select>
        </div>
        <div class="input-group">
          <label for="category">Category</label>
          <select id="category" v-model="entry.categoryId">
            <option v-for="category in sortedCategories" :key="category.id" :value="category.id">
              {{ category.parentId ? `- ${category.name}` : category.name }}
            </option>
          </select>
        </div>
        <div class="submit-container">
          <AccentedSubmitButton />
        </div>
      </form>
    </template>
  </Modal>
</template>

<script>
import FormError from "@/components/FormError";
import AccentedSubmitButton from "@/components/AccentedSubmitButton";
import Modal from "@/components/Modal";
import { mapActions, mapGetters, mapState } from "vuex";

export default {
  name: "EntryModal",
  components: { FormError, AccentedSubmitButton, Modal },
  props: { entry: Object, show: Boolean },
  data() {
    return {
      error: "",
    };
  },
  computed: {
    ...mapState({
      accounts: (state) => state.accounts.accounts,
    }),
    ...mapGetters({
      sortedCategories: "categories/getSortedCategories",
    }),
  },
  methods: {
    ...mapActions({
      createEntry: "entries/create",
      updateEntry: "entries/update",
      refreshBalances: "refreshBalances",
    }),
    closeModal() {
      this.$emit("close-modal");
    },
    submitCreate() {
      this.createEntry({
        ...this.entry,
      })
        .then(() => {
          this.closeModal();
          this.refreshBalances();
        })
        .catch((e) => (this.error = e.data.message));
    },
    submitUpdate() {
      this.updateEntry({
        ...this.entry,
      })
        .then(() => {
          this.closeModal();
          this.refreshBalances();
        })
        .catch((e) => (this.error = e.data.message));
    },
  },
};
</script>

<style scoped></style>
