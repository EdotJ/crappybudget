<template>
  <div class="container">
    <div class="form-container">
      <div class="receipt-link">
        <Button @click.native="toggleFormType">{{ isReceiptForm ? "Add single entry" : "Scan a receipt" }}</Button>
      </div>
      <FormError :value="error" />
      <TraditionalEntryForm :entry="entry" v-if="!isReceiptForm" v-on:throw-error="handleError" />
      <ReceiptScanForm v-if="isReceiptForm" v-on:throw-error="handleError" />
    </div>
  </div>
</template>

<script>
import { mapActions, mapGetters, mapState } from "vuex";

import FormError from "@/components/FormError";
import Button from "@/components/Button";
import TraditionalEntryForm from "@/components/TraditionalEntryForm";
import ReceiptScanForm from "@/components/ReceiptScanForm";

export default {
  name: "EntryForm",
  components: { ReceiptScanForm, TraditionalEntryForm, FormError, Button },
  data() {
    return {
      error: "",
      entry: {
        isExpense: true,
      },
      isReceiptForm: false,
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
      getAccounts: "accounts/getAll",
      getCategories: "categories/getAll",
    }),
    toggleFormType() {
      this.isReceiptForm = !this.isReceiptForm;
      this.error = "";
    },
    handleError(error) {
      this.error = error;
    },
  },
  mounted() {
    this.getAccounts();
    this.getCategories();
  },
};
</script>

<style scoped>
.container {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.receipt-link {
  width: 100%;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  padding: 8px 0;
}

.form-container {
  width: 100%;
  padding: 1rem;
  margin-top: 10%;
  background-color: #eeeeee;
  border-radius: 8px;
  box-shadow: 0 5px 10px rgba(154, 160, 185, 0.1), 0 15px 40px rgba(166, 173, 201, 0.2);
}

@media only screen and (min-width: 415px) and (max-width: 960px) {
  .form-container {
    width: 66%;
  }
}

@media only screen and (min-width: 961px) {
  .form-container {
    width: 60%;
  }
}
</style>
