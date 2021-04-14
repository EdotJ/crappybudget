<template>
  <div class="entry-list-container">
    <div class="entry-list" v-if="!isLoadingEntries">
      <div class="no-entries-warning" v-if="entries.length === 0">
        {{ accounts.length > 0 ? "No entries :(" : "Create your account in the Accounts tab and add some entries!" }}
      </div>
      <Entry
        v-for="(entry, i) in entries"
        :key="entry.id"
        v-bind:entry="entry"
        :index="i"
        :page="page"
        v-on:clicked-entry="toggleEdit(entry)"
      />
    </div>
    <div class="page-selector" v-if="totalPages > 1 && !isLoadingEntries">
      <div class="page-selector-icon-container" v-on:click="handlePageChange(page - 1)">
        <IconBase class="page-selector-icon" icon-name="left">
          <LeftArrowIcon />
        </IconBase>
      </div>
      <div class="pages-container">
        <span :class="['page', 'page-jump', { active: page === 0 }]" v-on:click="handlePageChange(0)"> 1 </span>
        <span class="page active" v-if="page !== 0 && page !== totalPages - 1">
          {{ page + 1 }}
        </span>
        <span class="page-dots" v-if="totalPages && totalPages > 3">...</span>
        <span
          :class="['page-jump', 'page', { active: page === totalPages - 1 }]"
          v-on:click="handlePageChange(totalPages - 1)"
        >
          {{ totalPages ? totalPages : "" }}
        </span>
      </div>
      <div class="page-selector-icon-container" v-on:click="handlePageChange(page + 1)">
        <IconBase class="page-selector-icon" icon-name="right">
          <RightArrowIcon />
        </IconBase>
      </div>
    </div>
    <div class="loader-container entry-loader" v-if="isLoadingEntries">
      <Spinner />
    </div>
    <EntryModal :entry="currentEntry" v-on:close-modal="closeModal" :show="showEntryModal" />
  </div>
</template>

<script>
import RightArrowIcon from "@/components/icons/RightArrowIcon";
import LeftArrowIcon from "@/components/icons/LeftArrowIcon";
import IconBase from "@/components/IconBase";
import Entry from "@/components/Entry";
import Spinner from "@/components/Spinner";
import EntryModal from "@/components/EntryModal";
import { mapActions, mapGetters, mapState } from "vuex";

export default {
  name: "EntryList",
  components: { RightArrowIcon, LeftArrowIcon, IconBase, Entry, Spinner, EntryModal },
  computed: {
    ...mapGetters({}),
    ...mapState({
      accounts: (state) => state.accounts.accounts,
      entries: (state) => state.entries.entries,
      currentAccount: (state) => state.accounts.currentAccount,
      totalPages: (state) => state.entries.totalPages,
      isLoadingEntries: (state) => state.entries.isLoading,
    }),
  },
  data() {
    return {
      showEntryModal: false,
      page: 0,
      currentEntry: {},
    };
  },
  methods: {
    ...mapActions({
      getEntries: "entries/getAll",
    }),
    handlePageChange(page) {
      if (page > -1 && page < this.totalPages && page !== this.page) {
        this.getEntries({ accountId: this.currentAccount, page });
        this.page = page;
      }
    },
    toggleEdit(entry) {
      this.currentEntry = { ...entry };
      this.toggleEntryModal();
    },
    closeModal() {
      this.showEntryModal = false;
      this.currentEntry = {};
    },
    toggleEntryModal() {
      this.showEntryModal = !this.showEntryModal;
    },
  },
};
</script>

<style scoped>
.entry-loader {
  height: 40%;
}

.no-entries-warning {
  display: flex;
  justify-content: flex-start;
  font-size: 1.5rem;
}

.page-selector {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 2rem;
}

.page-selector-icon-container {
  height: 100%;
  display: flex;
  align-items: center;
}

.page-selector-icon {
  width: 16px;
  height: 1.25rem;
  margin: 0 8px;
}

.page-selector-icon,
.page-jump {
  cursor: pointer;
}

.page-selector-icon-container:hover,
.page-jump:not(.active):hover {
  background: #cccccc;
}

.pages-container {
  height: 100%;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
}

.active {
  background-color: var(--accent-main);
  color: var(--foreground-accent);
}

.page {
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 4px;
  padding: 6px 4px 4px 4px;
  border-radius: 2px;
  width: 2rem;
  height: 100%;
}

.page-dots {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 2rem;
  height: 100%;
  margin: 0;
  padding: 0;
}

@media only screen and (max-width: 960px) {
  .entry-list {
    margin: 8px 0;
  }
}
</style>
