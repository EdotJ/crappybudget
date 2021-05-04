<template>
  <Modal v-on:close-modal="closeModal" :show="show" class="modal-root">
    <template v-slot:header>{{ entry.id ? "Update Entry" : "Create new Entry" }}</template>
    <template v-slot:content>
      <FormError :value="error" />
      <form v-on:submit.prevent="entry.id ? submitUpdate() : submitCreate()">
        <div class="input-group">
          <StyledInput type="text" :required="true" :value="entry.name" v-model="entry.name" placeholder="Name" />
        </div>
        <div class="input-group">
          <StyledInput
            type="text"
            :required="false"
            :value="entry.description"
            v-model="entry.description"
            placeholder="Description"
            :textarea="true"
          />
        </div>
        <div class="input-group">
          <StyledInput
            type="number"
            :required="true"
            :value="entry.value"
            v-model="entry.value"
            placeholder="Value"
            :step="0.01"
            :min="0"
          />
        </div>
        <div class="input-group">
          <StyledInput type="date" :required="true" :value="entry.date" v-model="entry.date" placeholder="Date" />
        </div>
        <div class="input-group checkbox-input">
          <label for="is-expense">Expense?</label>
          <input id="is-expense" type="checkbox" v-model="entry.isExpense" />
        </div>
        <div class="input-group">
          <label for="account">Account</label>
          <AccountSelector id="account" v-model="entry.accountId" :clearable="false" />
        </div>
        <div class="input-group">
          <label for="category">Category</label>
          <CategorySelector id="category" :clearable="false" v-model="entry.categoryId" />
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
import StyledInput from "@/components/StyledInput";
import { mapActions, mapGetters, mapState } from "vuex";
import AccountSelector from "@/components/AccountSelector";
import CategorySelector from "@/components/CategorySelector";

export default {
  name: "EntryModal",
  components: { CategorySelector, AccountSelector, FormError, AccentedSubmitButton, Modal, StyledInput },
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

<style scoped>
.input-group > label {
  width: 32%;
  max-width: unset;
  flex: none;
}

.selector {
  background: var(--input-bg-color);
  border-radius: 8px;
  width: 70%;
}

form {
  width: 80%;
  margin-bottom: 1rem;
}

.submit-container {
  display: flex;
  justify-content: flex-end;
}
</style>
