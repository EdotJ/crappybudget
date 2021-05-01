<template>
  <div class="home-container">
    <div class="window" v-if="!isLoading">
      <EntryListHeader class="header" :selected="selected" @selected="handleSelected" />
      <div class="list">
        <EntryList />
      </div>
      <Goals class="goals" />
    </div>
    <div v-else class="loader-container">
      <Spinner />
    </div>
  </div>
</template>

<script>
import { mapGetters, mapActions, mapState } from "vuex";
import Spinner from "@/components/Spinner";
import Goals from "@/components/Goals";
import EntryList from "@/components/EntryList";
import EntryListHeader from "@/components/EntryListHeader";

export default {
  name: "Home",
  components: {
    EntryListHeader,
    EntryList,
    Goals,
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
    }),
  },
  data() {
    return {
      error: "",
      page: 0,
      selected: null,
    };
  },
  methods: {
    ...mapActions({
      getAccounts: "accounts/getAll",
      getEntries: "entries/getAll",
      getMonthlyStats: "accounts/getMonthly",
      getCategories: "categories/getAll",
      getGoals: "goals/getAll",
    }),
    handleSelected(selected) {
      this.selected = selected;
    },
  },
  mounted() {
    this.getCategories();
    this.getGoals();
    this.getAccounts().then((id) => {
      if (id) {
        this.selected = this.getAccountById(id);
        this.getEntries({
          accountId: this.selected.id,
          from: null,
          to: null,
          page: 0,
        });
        this.getMonthlyStats(id);
      }
    });
  },
};
</script>

<style scoped>
.home-container {
  height: 100%;
}

.window {
  height: 100vh;
  display: grid;
  grid-template-columns: 1.6fr 0.4fr;
  grid-template-rows: 0.3fr 1.7fr;
  gap: 0px 0px;
  grid-template-areas:
    "header goals"
    "list goals";
}

.header {
  grid-area: header;
  padding: 1.5rem 3rem 0 3rem;
}

.list {
  grid-area: list;
  overflow-y: auto;
  padding: 0 3rem;
}

.goals {
  grid-area: goals;
}

.loader-container {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

@media only screen and (max-width: 960px) {
  .window {
    display: flex;
    flex-direction: column;
  }

  .summary span {
    padding: 0 8px;
  }

  .header {
    order: 0;
  }

  .goals {
    order: 1;
  }

  .list {
    order: 2;
    padding: 0.5rem 1.5rem 1.5rem 1.5rem;
  }

  .list {
    overflow-y: unset;
  }
}
</style>
