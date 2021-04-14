<template>
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
            :value="selected"
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
</template>

<script>
import Button from "@/components/Button";
import RangePicker from "@/components/icons/RangePicker";
import vSelect from "vue-select";
import { mapActions, mapState } from "vuex";

export default {
  name: "EntryListHeader",
  props: { selected: { type: Object } },
  components: {
    Button,
    RangePicker,
    vSelect,
  },
  data() {
    return {
      filterDates: {
        startDate: null,
        endDate: null,
      },
    };
  },
  computed: {
    ...mapState({
      income: (state) => state.accounts.currentIncome,
      expenses: (state) => state.accounts.currentExpenses,
      net: (state) => state.accounts.currentNet,
      accounts: (state) => state.accounts.accounts,
    }),
  },
  methods: {
    ...mapActions({
      getEntries: "entries/getAll",
      getMonthlyStats: "accounts/getMonthly",
    }),
    handleSelect(value) {
      this.getEntries({ accountId: value.id, page: 0 });
      this.getMonthlyStats(value.id);
      this.$emit("selected", value);
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
};
</script>

<style scoped>
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

.normal-desktop-picker {
  width: 100%;
  margin: 8px 0 0 0;
}

.small-scale-picker {
  display: none;
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
  .summary {
    font-size: 0.75rem;
    margin-left: 1rem;
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
