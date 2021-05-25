<template>
  <form v-on:submit.prevent="entry.id ? submitUpdate() : submitCreate()">
    <div class="input-group checkbox-input">
      <toggle-button
        class="toggle-button"
        v-model="entry.isExpense"
        :color="{ checked: '#f28d85', unchecked: '#7bc97f' }"
        :labels="{ checked: 'Expense', unchecked: 'Income' }"
        :width="100"
        :height="30"
        :font-size="14"
      />
    </div>
    <div class="input-group">
      <StyledInput
        id="entry-name"
        type="text"
        required="true"
        placeholder="Name"
        :vertical="true"
        :autocomplete="false"
        v-model="entry.name"
      />
    </div>
    <div class="input-group">
      <StyledInput
        id="description"
        type="text"
        placeholder="Description"
        :vertical="true"
        v-model="entry.description"
        :textarea="true"
      />
    </div>
    <div class="input-group">
      <StyledInput
        id="value"
        type="number"
        :vertical="true"
        :required="true"
        :autocomplete="false"
        placeholder="Value"
        :step="0.01"
        v-model="entry.value"
      />
    </div>
    <div class="input-group">
      <StyledInput id="date" type="date" required="true" placeholder="Date" vertical="true" v-model="entry.date" />
    </div>
    <div class="input-group">
      <label for="account">Account<span class="required-star">*</span></label>
      <AccountSelector id="account" v-model="entry.accountId" :clearable="false" />
    </div>
    <div class="input-group">
      <label for="category">Category<span class="required-star">*</span></label>
      <CategorySelector id="category" v-model="entry.categoryId" />
    </div>
    <div class="submit-container">
      <AccentedSubmitButton />
    </div>
  </form>
</template>

<script>
import AccentedSubmitButton from "@/components/AccentedSubmitButton";
import StyledInput from "@/components/StyledInput";
import { mapActions, mapGetters, mapState } from "vuex";
import CategorySelector from "@/components/CategorySelector";
import AccountSelector from "@/components/AccountSelector";

export default {
  name: "TraditionalEntryForm",
  data() {
    return {
      entry: {
        isExpense: true,
      },
    };
  },
  components: { AccountSelector, CategorySelector, AccentedSubmitButton, StyledInput },
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
    submitCreate() {
      this.$emit("throw-error", "");
      this.createEntry({
        ...this.entry,
      })
        .then(() => {
          this.$router.push("/");
        })
        .catch((e) => {
          console.log(e);
          this.$emit(
            "throw-error",
            e && e.response && e.response.data && e.response.data.message
              ? e.response.data.message
              : "Something went wrong"
          );
        });
    },
    submitUpdate() {
      this.updateEntry({
        ...this.entry,
      })
        .then(() => {
          this.refreshBalances();
          this.$router.push("/");
        })
        .catch((e) => {
          console.log(e);
          this.$emit(
            "throw-error",
            e && e.response && e.response.data && e.response.data.message
              ? e.response.data.message
              : "Something went wrong"
          );
        });
    },
  },
};
</script>

<style scoped>
form {
  width: 100%;
}

.input-group {
  display: flex;
  flex-direction: column;
}

.checkbox-input {
  flex-direction: row;
  justify-content: flex-end;
}

.submit-container {
  display: flex;
  justify-content: flex-end;
}

.submit-container button {
  width: 120px;
}

.selector {
  background: var(--input-bg-color);
  border-radius: 8px;
}

.child-category {
  margin-left: 1rem;
}

.required-star {
  color: var(--red-replacement);
}

@media only screen and (min-width: 415px) and (max-width: 960px) {
  form {
    width: 100%;
    justify-content: center;
  }
}

@media only screen and (min-width: 961px) {
  form {
    width: 100%;
    justify-content: center;
  }
}
</style>
