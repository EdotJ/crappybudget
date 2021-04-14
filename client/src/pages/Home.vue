<template>
  <div class="home-container">
    <div class="window" v-if="!isLoading">
      <div class="entry-section">
        <div class="entries-top">
          <div class="side-container">
            <div class="leftside-container">
              <div class="select-wrapper">
                <v-select
                  class="account-select selector"
                  @input="handleSelect"
                  :options="accounts"
                  label="name"
                  :clearable="false"
                  :searchable="false"
                  v-model="selected"
                >
                  <template #no-options>
                    <router-link to="/accounts/create" class="no-options"> Create a new account?</router-link>
                  </template>
                </v-select>
              </div>
              <div class="date-picker-group normal-desktop-picker">
                <div class="date-picker-container">
                  <RangePicker class="date-picker" @applied="handleDateChange" v-model="filterDates" />
                </div>
                <Button class="clear-button" @click.native="clearDates">Clear</Button>
              </div>
            </div>
            <div class="rightside-container">
              <div class="summary" v-if="income != null && expenses != null && net != null">
                <span>{{ "Monthly income: " + income + "€" }}</span>
                <span>{{ "Monthly expenses: " + expenses + "€" }}</span>
                <span :class="net >= 0 ? 'positive' : 'negative'">{{ "Net: " + net + "€" }}</span>
              </div>
              <div class="add-entry-button" @click="$router.push('entries/create')" v-if="accounts.length > 0">+</div>
            </div>
          </div>
          <div class="date-picker-group small-scale-picker">
            <div class="date-picker-container">
              <RangePicker class="date-picker" @applied="handleDateChange" v-model="filterDates" />
            </div>
            <Button class="clear-button" @click.native="clearDates">Clear</Button>
          </div>
        </div>
        <!--          <Goals class="mobile-goals" />-->
        <div class="entry-list" v-if="!isLoadingEntries">
          <div class="no-entries-warning" v-if="entries.length === 0">
            {{
              accounts.length > 0 ? "No entries :(" : "Create your account in the Accounts tab and add some entries!"
            }}
          </div>
          <Entry
            v-for="(entry, i) in entries"
            :key="entry.id"
            v-bind:entry="entry"
            :index="i"
            :page="page"
            v-on:clicked-entry="toggleEdit(entry)"
            :from="filterDates.startDate"
            :to="filterDates.endDate"
          />
        </div>
        <div class="page-selector" v-if="totalPages > 1 && !isLoadingEntries">
          <div class="page-selector-icon-container">
            <IconBase class="page-selector-icon" icon-name="left" v-on:click.native="handlePageChange(page - 1)">
              <LeftArrowIcon />
            </IconBase>
          </div>
          <div class="pages-container">
            <span class="page-jump" v-on:click="handlePageChange(0)"> 1 </span>
            <span v-if="page !== 0 && page !== totalPages - 1">
              {{ page + 1 }}
            </span>
            <span class="page-jump" v-on:click="handlePageChange(totalPages - 1)"
              >{{ totalPages ? "... " + totalPages : "" }}
            </span>
          </div>
          <div class="page-selector-icon-container">
            <IconBase class="page-selector-icon" icon-name="right" v-on:click.native="handlePageChange(page + 1)">
              <RightArrowIcon />
            </IconBase>
          </div>
        </div>
        <div class="loader-container entry-loader" v-if="isLoadingEntries">
          <Spinner />
        </div>
      </div>
      <Goals class="desktop-goals" />
    </div>
    <div v-if="isLoading" class="loader-container">
      <Spinner />
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
import vSelect from "vue-select";
import RangePicker from "@/components/icons/RangePicker";
import Button from "@/components/Button";

export default {
  name: "Home",
  components: {
    Button,
    RangePicker,
    EntryModal,
    RightArrowIcon,
    LeftArrowIcon,
    IconBase,
    Goals,
    Entry,
    Spinner,
    vSelect,
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
      isLoadingEntries: (state) => state.entries.isLoading,
    }),
  },
  data() {
    return {
      showEntryModal: false,
      error: "",
      page: 0,
      currentEntry: {},
      selected: null,
      filterDates: {
        startDate: null,
        endDate: null,
      },
    };
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
    handleSelect(value) {
      this.selected = value;
      this.getEntries({ accountId: this.selected.id, page: 0 });
      this.getMonthlyStats(this.selected.id);
    },
    handlePageChange(page) {
      if (page > -1 && page < this.totalPages) {
        this.getEntries({ accountId: this.currentAccount, page });
        this.page = page;
      }
    },
    handleDateChange(dates) {
      this.filterDates.startDate = dates.startDate;
      this.filterDates.endDate = dates.endDate;
      this.getEntries({
        accountId: this.currentAccount,
        page: 0,
        from: this.filterDates.startDate,
        to: this.filterDates.endDate,
      });
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
    clearDates() {
      this.filterDates.startDate = null;
      this.filterDates.endDate = null;
      this.getEntries({
        accountId: this.currentAccount,
        page: 0,
        from: this.filterDates.startDate,
        to: this.filterDates.endDate,
      });
    },
  },
  mounted() {
    this.getCategories();
    this.getAccounts().then((id) => {
      if (id) {
        this.selected = this.getAccountById(id);
        this.getEntries({
          accountId: this.selected.id,
          from: this.filterDates.startDate,
          to: this.filterDates.endDate,
          page: 0,
        });
        this.getMonthlyStats(id);
      }
    });
  },
};
</script>

<style scoped>
.bigboi {
  height: 100%;
}

.home-container {
  height: 100%;
}

.window {
  display: flex;
  flex-direction: row;
  height: 100vh;
}

.entry-section {
  flex-grow: 1;
  padding: 1.5rem 3rem 3rem 3rem;
  overflow-y: auto;
}

.select-wrapper {
  display: flex;
  align-items: center;
  width: 100%;
}

.leftside-container {
  width: 50%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
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
  border-radius: 8px;
  width: 100%;
  position: relative;
  background: var(--default-selector-bg-color);
}

.no-options {
  color: var(--main-fg-color);
  font-size: 1rem;
  text-decoration: underline;
  height: 100%;
  width: 100%;
}

.entries-top {
  margin: 8px 0;
}

.side-container {
  display: flex;
  flex-direction: row;
  align-items: flex-start;
  justify-content: space-between;
}

.summary {
  height: 100%;
  padding: 0.75rem;
  font-size: 1rem;
  display: flex;
  flex-grow: 1;
  flex-direction: column;
  justify-content: flex-start;
  align-items: flex-start;
  margin: 0 3rem 0 3rem;
  border-radius: 8px;
  background-color: #fcfcfc;
  box-shadow: 0 1px 1px -2px black;
}

.rightside-container {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  width: 50%;
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
  height: 2rem;
}

.page-selector-icon-container {
  height: 100%;
}

.page-selector-icon {
  width: 16px;
  height: 1.25rem;
  margin: 0 8px;
  cursor: pointer;
}

.page-jump {
  cursor: pointer;
}

.pages-container {
  height: 100%;
}

.date-picker-container {
  flex-grow: 1;
  width: 100%;
  display: flex;
  align-items: center;
  height: 100%;
}

.date-picker-group {
  display: flex;
  width: 50%;
  align-items: center;
  margin-bottom: 8px;
}

.clear-button {
  padding: 0 0.25rem;
  font-size: 1rem;
  line-height: 0.5;
  height: 1.5rem;
}

.no-entries-warning {
  display: flex;
  justify-content: flex-start;
  font-size: 1.5rem;
}

.normal-desktop-picker {
  width: 100%;
  margin: 8px 0 0 0;
}

.small-scale-picker {
  display: none;
}

.loader-container {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.entry-loader {
  height: 40%;
}

@media only screen and (min-width: 961px) and (max-width: 1280px) {
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
    overflow-y: auto;
  }

  .date-picker {
    margin: 8px 0;
  }

  .normal-desktop-picker {
    display: none;
  }

  .small-scale-picker {
    display: flex;
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

  .side-container {
    flex-direction: column-reverse;
  }

  .rightside-container {
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
    overflow-y: unset;
  }

  .select-wrapper {
    margin: 8px 0;
    width: 100%;
  }

  .leftside-container {
    width: 100%;
  }

  .date-picker-group {
    width: 100%;
  }

  .date-picker-container {
    margin-right: 1rem;
  }

  .date-picker {
    display: flex;
    margin: 0;
    justify-content: center;
    width: 100%;
  }

  .normal-desktop-picker {
    display: none;
  }

  .small-scale-picker {
    display: flex;
  }
}
</style>
