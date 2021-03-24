<template>
  <div class="home-container">
    <div class="window" v-if="!isLoading">
      <div class="entry-section">
        <div class="entries-top">
          <div class="select-wrapper">
            <select class="account-select" @change="handleSelect($event)">
              <option v-if="accounts.length === 0">No accounts</option>
              <option v-for="account in accounts" :key="account.id" :value="account.id">{{ account.name }}</option>
            </select>
          </div>
          <div class="summary-container">
            <div class="summary" v-if="income != null && expenses != null && net != null">
              <span>{{ "Monthly income: " + income + "€" }}</span>
              <span>{{ "Monthly expenses: " + expenses + "€" }}</span>
              <span :class="net >= 0 ? 'positive' : 'negative'">{{ "Net: " + net + "€" }}</span>
            </div>
            <div class="add-entry-button" @click="toggleEntryModal">+</div>
          </div>
        </div>
        <Goals class="mobile-goals" />
        <div class="entry-list">
          <Entry
            v-for="(entry, i) in entries"
            :key="entry.id"
            v-bind:entry="entry"
            :index="i"
            :page="page"
            v-on:click.native.self="toggleEdit(entry)"
          />
        </div>
        <div class="page-selector" v-if="totalPages > 1">
          <IconBase class="page-selector-icon" icon-name="left" v-on:click.native="handlePageChange(page - 1)">
            <LeftArrowIcon />
          </IconBase>
          {{ page + 1 }} {{ totalPages ? "... " + totalPages : "" }}
          <IconBase class="page-selector-icon" icon-name="right" v-on:click.native="handlePageChange(page + 1)">
            <RightArrowIcon />
          </IconBase>
        </div>
      </div>
      <Goals class="desktop-goals" />
    </div>
    <div v-if="isLoading">
      <Spinner class="spinner" />
    </div>
    <EntryModal :entry="currentEntry" v-on:close-modal="closeModal" :show="showEntryModal" />
  </div>
</template>

<script>
import { mapGetters, mapActions, mapState } from "vuex";
import Spinner from "@/components/Spinner";
import Entry from "@/components/Entry";
import Goals from "@/components/Goals";
import IconBase from "@/components/IconBase";
import LeftArrowIcon from "@/components/icons/LeftArrowIcon";
import RightArrowIcon from "@/components/icons/RightArrowIcon";
import EntryModal from "@/components/EntryModal";

export default {
  name: "Home",
  components: {
    EntryModal,
    RightArrowIcon,
    LeftArrowIcon,
    IconBase,
    Goals,
    Entry,
    Spinner,
  },
  computed: {
    ...mapGetters({
      isLoggedIn: "auth/isLoggedIn",
      getAccountById: "accounts/accountById",
    }),
    ...mapState({
      accounts: (state) => state.accounts.accounts,
      entries: (state) => state.entries.entries,
      isLoading: (state) => state.accounts.isLoading,
      income: (state) => state.accounts.currentIncome,
      expenses: (state) => state.accounts.currentExpenses,
      net: (state) => state.accounts.currentNet,
      currentAccount: (state) => state.accounts.currentAccount,
      totalPages: (state) => state.entries.totalPages,
    }),
  },
  methods: {
    ...mapActions({
      getAccounts: "accounts/getAll",
      getEntries: "entries/getAll",
      getMonthlyStats: "accounts/getMonthly",
      getCategories: "categories/getAll",
      createEntry: "entries/create",
      refreshBalances: "refreshBalances",
    }),
    handleSelect(e) {
      this.getEntries({ accountId: e.target.value, page: 0 });
      this.getMonthlyStats(e.target.value);
    },
    handlePageChange(page) {
      if (page > -1) {
        this.getEntries({ accountId: this.currentAccount, page });
        this.page = page;
      }
    },
    toggleEntryModal() {
      this.showEntryModal = !this.showEntryModal;
    },
    closeModal() {
      this.showEntryModal = false;
      this.currentEntry = {};
    },
    toggleEdit(entry) {
      this.currentEntry = { ...entry };
      this.toggleEntryModal();
    },
  },
  mounted() {
    this.getCategories();
    this.getAccounts().then((id) => {
      if (id) {
        this.getEntries({ accountId: id, page: 0 });
        this.getMonthlyStats(id);
      }
    });
  },
  data() {
    return {
      showEntryModal: false,
      error: "",
      page: 0,
      currentEntry: {
        name: "",
        description: "",
        value: "",
        date: "",
        isExpense: true,
        accountId: "",
        categoryId: "",
      },
    };
  },
};
</script>

<style scoped>
.home-container {
  height: 100%;
}

.window {
  display: flex;
  flex-direction: row;
  height: 100%;
}

.entry-section {
  flex-grow: 1;
  height: 100%;
}

.account-select {
  display: flex;
  align-items: flex-start;
  background: var(--main-bg-color);
  font-size: 1.5rem;
  color: var(--main-fg-color);
  -webkit-appearance: none;
  -moz-appearance: none;
  appearance: none;
  border: 1px solid var(--accent-main);
  border-radius: 16px;
  position: relative;
  width: 20rem;
  height: 4rem;
  z-index: 1;
}

.select-wrapper {
  position: relative;
}

.select-wrapper:after {
  content: "▼";
  font: 32px "Consolas", monospace;
  color: var(--accent-main);
  padding: 0 0 2px;
  top: 16px;
  left: 18rem;
  position: absolute;
  pointer-events: none;
  z-index: 5;
}

.entries-top {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  margin: 1rem 8px;
}

.summary {
  height: 100%;
  border: 1px solid var(--accent-main);
  padding: 0.75rem;
  font-size: 1rem;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  align-items: flex-start;
  margin: 0 3rem 0 3rem;
}

.summary-container {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
}

.mobile-goals {
  display: none;
}

.headers {
  display: grid;
  grid-template-columns: repeat(auto-fill, 25%);
  border-bottom: 1px solid var(--accent-main-darker);
  padding-bottom: 0.5rem;
  margin-bottom: 0.5rem;
}

.headers p {
  margin: 0;
}

.entry-section {
  padding: 3rem;
  flex-grow: 1;
}

.positive {
  color: var(--income-color);
  font-weight: bold;
}

.negative {
  color: var(--expense-color);
  font-weight: bold;
}

.add-entry-button {
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

.add-entry-button:hover {
  background: var(--accent-main-lighter);
  color: var(--foreground-accent);
  border: 2px solid var(--accent-main-lighter);
}

form {
  width: 100%;
}

.page-selector {
  display: flex;
  justify-content: center;
  align-items: center;
}

.page-selector-icon {
  width: 16px;
  height: 1.25rem;
  margin: 0 8px;
}

.page-selector-icon {
  cursor: pointer;
}

@media only screen and (min-width: 961px) and (max-width: 1280px) {
  .account-select {
    width: 15rem;
  }

  .select-wrapper:after {
    left: 13rem;
  }

  .summary {
    font-size: 0.75rem;
    margin-left: 0.5rem;
    padding: 0.25rem;
  }

  .add-entry-button {
    height: 2rem;
    width: 2rem;
    font-size: 2rem;
  }

  .entry-section {
    padding: 0.75rem;
  }
}

@media only screen and (max-width: 960px) {
  .window {
    display: flex;
    flex-direction: column;
  }

  .entry-list {
    margin: 8px 0;
  }

  .summary {
    font-size: 0.75rem;
    margin-left: 1rem;
  }

  .mobile-goals {
    display: initial;
  }

  .desktop-goals {
    display: none;
  }

  .even {
    background: black;
  }

  .entries-top {
    flex-direction: column-reverse;
  }

  .select-wrapper {
    margin-top: 1rem;
  }

  .select-wrapper {
    width: 100%;
  }

  .account-select {
    width: 100%;
  }

  .select-wrapper:after {
    left: 90%;
  }

  .summary-container {
    width: 100%;
  }

  .summary {
    margin: 0 36px 0 0;
    padding: 8px 0;
    flex-grow: 1;
    border-radius: 16px;
    border: 1px solid var(--accent-main-lighter);
    background: var(--accent-main-lighter);
    color: var(--foreground-accent);
    font-size: 1rem;
  }

  .summary span {
    padding: 0 8px;
  }

  .entry-section {
    padding: 8px;
  }
}
</style>
