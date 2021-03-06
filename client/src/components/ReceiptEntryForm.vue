<template>
  <form class="entry-form" @submit.prevent="submitEntries">
    <Modal :show="showModal" v-on:close-modal="handleModal" class="image-modal">
      <template v-slot:header> Receipt</template>
      <template v-slot:content>
        <img class="receipt-image" :style="`transform: rotate(${rotation}deg);`" :src="file" alt="file" v-if="file" />
        <div class="controls">
          <Button @click.native="rotate(-90)" type="button">Rotate Left</Button>
          <Button @click.native="rotate(90)" type="button">Rotate Right</Button>
        </div>
      </template>
    </Modal>
    <div class="picture-container">
      <IconBase @click.native="handleModal()">
        <Picture />
      </IconBase>
    </div>
    <div class="starting-form" v-if="receipt && !submittedIds">
      <h3>Choose account and category</h3>
      <div class="input-group">
        <label for="account">Account<span class="required-star">*</span></label>
        <AccountSelector id="account" :clearable="false" v-model="receipt.accountId" />
      </div>
      <div class="input-group">
        <label for="category">Category<span class="required-star">*</span></label>
        <CategorySelector id="category" :clearable="false" v-model="receipt.categoryId" />
      </div>
      <div class="submit-container">
        <Button type="button" @click.native="handleNext()">Next</Button>
      </div>
    </div>
    <div class="end-form" v-if="submittedIds">
      <div class="form-top">
        <StyledInput type="date" required="true" :value="receipt.date" v-model="receipt.date" placeholder="Date" />
        <div class="totals">
          <div class="total">Parsed total: {{ receipt.total ? receipt.total : "N/A :(" }}</div>
          <div class="total">
            Current total:
            {{
              this.receipt && this.receipt.entries
                ? this.receipt.entries
                    .map((e) => (e && e.price ? e.price : 0))
                    .reduce((total, curr) => total + curr, 0)
                    .toFixed(2)
                : 0
            }}
          </div>
        </div>
      </div>
      <div class="entries-list">
        <div :class="['entry', entry.discount ? '' : '']" v-for="(entry, index) in receipt.entries" :key="index">
          <StyledInput
            class="entry-input"
            type="text"
            required="true"
            v-model="entry.label"
            :value="entry.label"
            :label="false"
            placeholder="Name"
          />
          <StyledInput
            :class="['entry-input', 'price-input', entry.discount ? 'discount' : '']"
            type="number"
            required="true"
            v-model.number="entry.price"
            :value="entry.price"
            :step="0.01"
            :label="false"
            placeholder="Price"
            :min="-500"
          />
          <div class="actions">
            <DeleteButton class="delete-button" @click.native="handleEntryDelete(index)" />
          </div>
        </div>
        <div class="form-bottom">
          <div class="add-entry-button" @click="handleEntryAdd">+</div>
        </div>
        <div class="submit-container">
          <Button>Submit</Button>
        </div>
      </div>
    </div>
  </form>
</template>

<script>
import Button from "@/components/Button";
import DeleteButton from "@/components/DeleteButton";
import StyledInput from "@/components/StyledInput";
import { mapActions, mapGetters, mapState } from "vuex";
import Modal from "@/components/Modal";
import IconBase from "@/components/IconBase";
import Picture from "@/components/icons/Picture";
import CategorySelector from "@/components/CategorySelector";
import AccountSelector from "@/components/AccountSelector";

export default {
  name: "ReceiptEntryForm",
  props: { receipt: Object, file: String },
  components: {
    AccountSelector,
    CategorySelector,
    IconBase,
    Button,
    DeleteButton,
    StyledInput,
    Picture,
    Modal,
  },
  data() {
    return {
      submittedIds: false,
      showModal: false,
      rotation: 0,
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
      submitReceiptEntries: "entries/submitReceiptEntries",
    }),
    submitEntries() {
      this.submitReceiptEntries(this.receipt)
        .then(() => this.$router.push("/"))
        .catch((e) =>
          this.$emit(
            "throw-error",
            e && e.response && e.response.data && e.response.data.message
              ? e.response.data.message
              : "Something went wrong!"
          )
        );
    },
    handleEntryDelete(index) {
      if (this.receipt && this.receipt.entries && this.receipt.entries[index]) {
        this.receipt.entries.splice(index, 1);
      }
    },
    handleEntryAdd() {
      if (this.receipt && this.receipt.entries) {
        this.receipt.entries.push({ name: "", price: 0, isDiscount: false });
      } else {
        this.receipt.entries = [{ name: "", price: 0, isDiscount: false }];
      }
    },
    handleNext() {
      if (!this.receipt.accountId) {
        this.$emit("throw-error", "Please select an account");
        return;
      }
      if (!this.receipt.categoryId) {
        this.$emit("throw-error", "Please select a category");
        return;
      }
      this.submittedIds = true;
      if (this.receipt.accountId && this.receipt.categoryId) {
        this.$emit("throw-error", "");
      }
    },
    handleModal() {
      this.showModal = !this.showModal;
    },
    rotate(degrees) {
      this.rotation += degrees;
    },
  },
};
</script>

<style scoped>
form {
  display: flex;
  flex-direction: column;
  margin: 2rem 0;
}

.submit-container {
  width: 100%;
  display: flex;
  justify-content: flex-end;
}

.submit-container button {
  width: 120px;
  margin-top: 16px;
}

.entries-list {
  width: 100%;
}

.form-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.entry {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.entry-input {
  margin: 0 8px;
  flex-grow: 1;
}

.price-input {
  max-width: 20%;
}

.delete-button {
  margin-left: 0;
}

.form-bottom {
  width: 100%;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  padding-top: 8px;
}

.add-entry-button {
  height: 2rem;
  width: 2rem;
  color: var(--accent-main);
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 2rem;
  border: 2px solid var(--accent-main);
  border-radius: 2rem;
  cursor: pointer;
}

.add-entry-button:hover {
  background: var(--accent-main-lighter);
  color: var(--foreground-accent);
  border: 2px solid var(--accent-main-lighter);
}

.total {
  font-size: 0.75rem;
}

.input-group {
  display: flex;
  width: 80%;
  align-items: center;
  justify-content: space-between;
  margin: 4px 0;
}

.input-group label {
  margin: 0 8px;
  width: 30%;
  display: flex;
  justify-content: flex-start;
}

.selector {
  flex-grow: 1;
  background: var(--input-bg-color);
}

.receipt-image {
  max-width: 60%;
  max-height: 60%;
  margin-bottom: 1rem;
}

.picture-container {
  width: 100%;
  display: flex;
  justify-content: flex-end;
}

.controls {
  display: flex;
}

.discount >>> input {
  background: #13c20022;
  border-radius: 8px;
}

.required-star {
  color: var(--red-replacement);
}

@media only screen and (min-width: 961px) {
  .total {
    font-size: 1rem;
  }
}
</style>
