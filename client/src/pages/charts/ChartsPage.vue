<template>
  <div class="grid-container">
    <div class="category-breakdown-grid-item">
      <div class="category-breakdown-with-loader">
        <div class="category-breakdown-container" v-if="loadedCategory">
          <div class="chart-title">Monthly Expense Breakdown By Category</div>
          <div class="selectors">
            <div>
              <YearSelector @input="handleCategoryBreakdownYearChange" v-model="categoryBreakdownYear" />
            </div>
            <div>
              <v-select
                class="selector"
                :clearable="false"
                @input="handleCategoryBreakdownMonthChange"
                :value="categoryBreakdownMonth"
                :options="[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]"
              />
            </div>
          </div>
          <CategoryBreakdownChart
            class="category-breakdown"
            :chartData="categoryBreakdownData"
            v-if="categoryBreakdownData.labels.length > 0 && !categoryBreakdownError"
          />
          <div class="category-breakdown" v-else-if="!categoryBreakdownError">
            <span class="no-text-data">Did not find any data :(</span>
          </div>
          <div class="error" v-else>{{ categoryBreakdownError }}</div>
        </div>
        <div class="loader-container" v-else>
          <Spinner class="spinner" />
        </div>
      </div>
    </div>
    <div class="top-expenses-grid-item">
      <div class="top-expenses-with-loader">
        <div class="top-expenses" v-if="loadedTopExpenses">
          <div class="chart-title">Top Monthly Expenses</div>
          <div class="selectors">
            <YearSelector @input="handleTopExpensesYearChange" v-model="topExpensesYear" />
            <v-select
              :clearable="false"
              class="selector"
              @input="handleTopExpensesMonthChange"
              :value="topExpensesMonth"
              :options="[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]"
            >
            </v-select>
          </div>
          <div class="table-container" v-if="!topExpensesError && topExpenses.length > 0">
            <table>
              <thead>
                <tr>
                  <td>Account</td>
                  <td>Name</td>
                  <td>Value</td>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(expense, index) in topExpenses" :key="index">
                  <td>{{ expense.account }}</td>
                  <td>{{ expense.name }}</td>
                  <td class="expense-value">{{ expense.value }}</td>
                </tr>
              </tbody>
            </table>
          </div>
          <div class="table-container" v-if="!topExpensesError && topExpenses.length === 0">
            <span class="no-text-data">Did not find any data :(</span>
          </div>
          <div class="error" v-if="topExpensesError">{{ topExpensesError }}</div>
        </div>
        <div class="loader-container" v-else>
          <Spinner class="spinner" />
        </div>
      </div>
    </div>
    <div class="yearly-breakdown-grid-item">
      <div class="yearly-breakdown-with-loader">
        <div class="yearly-breakdown" v-if="loadedYearly">
          <div class="chart-title">Yearly Activity Breakdown</div>
          <div class="yearly-selector">
            <YearSelector @input="handleYearlyBreakdownSelect" v-model="yearlyBreakdownYear" />
          </div>
          <BreakdownChart
            class="yearly-breakdown-chart"
            :incomeData="yearlyIncomeData"
            :expenseData="yearlyExpenseData"
            v-if="!yearlyBreakdownError"
          />
          <div class="error" v-else>{{ yearlyBreakdownError }}</div>
        </div>
        <div class="loader-container" v-else>
          <Spinner class="spinner" />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import BreakdownChart from "@/components/YearlyBreakdownChart";
import CategoryBreakdownChart from "@/components/CategoryBreakdownChart";
import { mapActions, mapState } from "vuex";
import colors from "./colors";
import YearSelector from "@/components/YearSelector";
import Spinner from "@/components/Spinner";
import vSelect from "vue-select";

export default {
  name: "ChartsPage",
  // eslint-disable-next-line
  components: { Spinner, YearSelector, CategoryBreakdownChart, BreakdownChart, vSelect },
  data() {
    return {
      loadedYearly: false,
      loadedCategory: false,
      loadedTopExpenses: false,
      parentCategoriesOnly: false,
      yearlyIncomeData: [],
      yearlyExpenseData: [],
      yearlyBreakdownYear: new Date().getFullYear(),
      yearlyBreakdownError: "",
      categoryBreakdownYear: new Date().getFullYear(),
      categoryBreakdownMonth: new Date().getMonth() + 1,
      categoryBreakdownError: "",
      topExpensesYear: new Date().getFullYear(),
      topExpensesMonth: new Date().getMonth() + 1,
      topExpenses: [],
      topExpensesError: "",
      categoryBreakdownData: {
        labels: [],
        datasets: [
          {
            label: "Breakdown",
            data: [],
            backgroundColor: colors,
          },
        ],
      },
    };
  },
  methods: {
    ...mapActions({
      getCategories: "categories/getAll",
    }),
    handleYearlyBreakdownSelect(val) {
      this.loadedYearly = false;
      this.yearlyIncomeData = [];
      this.yearlyExpenseData = [];
      this.$api.charts.getYearlyBreakdown(val).then((response) => this.handleYearlyResponse(response));
    },
    handleYearlyResponse(response) {
      if (!response || !response.data || !response.data.breakdown) {
        this.yearlyBreakdownError = "Failed fetching yearly activity breakdown";
      }
      const breakdown = response.data.breakdown;
      for (let i = 1; i <= 12; i++) {
        const entry = breakdown.find((e) => e.month === i);
        this.yearlyIncomeData.push(entry ? entry.income : 0);
        this.yearlyExpenseData.push(entry ? entry.expenses : 0);
      }
      this.loadedYearly = true;
    },
    handleCategoryBreakdownResponse(response) {
      if (!response || !response.data || !response.data.breakdown) {
        this.categoryBreakdownError = "Failed fetching category breakdown data";
      }
      const breakdown = response.data.breakdown;
      breakdown.map((entry) => {
        if ((this.parentCategoriesOnly && entry.parentId) || !entry.value) {
          return;
        }
        this.categoryBreakdownData.labels.push(this.categories.get(entry.categoryId).name);
        this.categoryBreakdownData.datasets[0].data.push(entry.value);
      });
      this.loadedCategory = true;
    },
    handleCategoryBreakdownYearChange(val) {
      this.categoryBreakdownYear = val;
      this.fetchNewCategoryBreakdown();
    },
    handleCategoryBreakdownMonthChange(value) {
      this.categoryBreakdownMonth = value;
      this.fetchNewCategoryBreakdown();
    },
    fetchNewCategoryBreakdown() {
      this.loadedCategory = false;
      this.categoryBreakdownData.labels = [];
      this.categoryBreakdownData.datasets[0].data = [];
      this.$api.charts
        .getCategoryBreakdown(this.categoryBreakdownYear, this.categoryBreakdownMonth)
        .then((response) => this.handleCategoryBreakdownResponse(response));
    },
    handleTopExpensesYearChange(val) {
      this.topExpensesYear = val;
      this.fetchNewTopExpenses();
    },
    handleTopExpensesMonthChange(value) {
      this.topExpensesMonth = value;
      this.fetchNewTopExpenses();
    },
    fetchNewTopExpenses() {
      this.loadedTopExpenses = false;
      this.$api.charts
        .getTopExpenses(this.topExpensesYear, this.topExpensesMonth)
        .then((response) => this.handleTopExpensesResponse(response));
    },
    handleTopExpensesResponse(response) {
      if (!response || !response.data || !response.data.expenses) {
        this.topExpensesError = "Failed fetching top expenses data";
      }
      this.topExpenses = response.data.expenses;
      this.loadedTopExpenses = true;
    },
  },
  computed: {
    ...mapState({
      categories: (state) => state.categories.categories,
    }),
  },
  mounted() {
    this.$api.charts.getYearlyBreakdown().then((response) => this.handleYearlyResponse(response));
    this.getCategories().then(() => {
      this.$api.charts.getCategoryBreakdown().then((response) => this.handleCategoryBreakdownResponse(response));
    });
    this.$api.charts.getTopExpenses().then((response) => this.handleTopExpensesResponse(response));
  },
};
</script>

<style scoped>
.grid-container {
  display: grid;
  grid-template-columns: repeat(1, 1fr);
  grid-template-rows: repeat(3, 1fr);
  gap: 0 0;
  grid-template-areas:
    "category-breakdown"
    "top-expenses"
    "yearly-breakdown";
  width: 100%;
  height: 100%;
}

.category-breakdown-grid-item {
  grid-area: category-breakdown;
  width: 100%;
  margin-bottom: 1rem;
}

.category-breakdown-with-loader {
  width: 100%;
  height: 100%;
}

.category-breakdown-container {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  max-height: calc(50vh - 16px);
}

.top-expenses-grid-item {
  grid-area: top-expenses;
  width: 100%;
  margin-bottom: 1rem;
}

.top-expenses-with-loader {
  width: 100%;
  height: 100%;
}

.top-expenses {
  width: 100%;
}

.yearly-breakdown-grid-item {
  grid-area: yearly-breakdown;
  width: 100%;
  max-height: calc(50vh - 16px);
  margin-bottom: 1rem;
}

.yearly-breakdown-with-loader {
  height: 100%;
  width: 100%;
}

.category-breakdown {
  display: flex;
  align-items: center;
  justify-content: center;
  max-width: 75%;
}

.yearly-breakdown-chart {
  max-height: 70%;
  max-width: 90%;
  margin-bottom: 60px;
}

table {
  border-collapse: collapse;
  border-radius: 4px;
  border-style: hidden;
  box-shadow: 0 0 0 1px black;
}

td {
  border: 1px solid black;
  padding: 4px;
}

thead {
  background-color: var(--accent-main);
  color: var(--foreground-accent);
}

.loader-container {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 4rem 0;
}

.table-container {
  height: 100%;
  display: flex;
  align-items: center;
  margin-top: 1rem;
  width: 100%;
}

.table-container table {
  width: 100%;
}

.expense-value {
  color: var(--expense-color);
}

.error {
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  color: var(--foreground-error);
}

.selectors {
  display: flex;
  width: 100%;
  justify-content: center;
  margin-bottom: 1rem;
}

.selectors div {
  width: 100%;
}

.selectors div:nth-child(2n) {
  margin-left: 2rem;
}

.yearly-selector {
  width: 100%;
  display: flex;
  justify-content: center;
}

.yearly-selector .selector {
  width: 50%;
}

.selector {
  background: var(--default-selector-bg-color);
}

.chart-title {
  font-family: "Quicksand", sans-serif;
  margin: 0.5rem 0;
}

.no-text-data {
  width: 100%;
  height: 100%;
}

@media only screen and (min-width: 750px) and (max-width: 1150px) {
  .category-breakdown-container {
    width: 100%;
    max-width: unset;
  }

  .category-breakdown {
    max-width: 40%;
  }

  .yearly-breakdown-chart {
    max-height: 60%;
    max-width: unset;
    margin-bottom: 0;
  }

  .loader-container {
    padding: 0;
  }

  .grid-container {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    grid-template-rows: repeat(2, 1fr);
    gap: 0 0;
    grid-template-areas:
      "category-breakdown top-expenses"
      "yearly-breakdown yearly-breakdown";
    padding: 1rem;
  }

  .category-breakdown-grid-item {
    margin-bottom: 0;
  }

  .top-expenses-grid-item {
    margin-bottom: 0;
  }

  .yearly-breakdown-grid-item {
    margin-bottom: 0;
  }
}

/* Desktop Styles */
@media only screen and (min-width: 1151px) and (min-height: 800px) {
  .top-expenses {
    display: flex;
    flex-direction: column;
    height: 100%;
    padding: 8px;
  }

  .category-breakdown-container {
    padding: 8px;
    width: 100%;
    max-width: unset;
  }

  .category-breakdown {
    max-width: 60%;
  }

  .yearly-breakdown-chart {
    max-height: 60%;
    max-width: unset;
    margin-bottom: 0;
  }

  .yearly-breakdown {
    max-width: unset;
  }

  .loader-container {
    padding: 0;
  }

  .grid-container {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    grid-template-rows: repeat(2, 1fr);
    gap: 0 0;
    grid-template-areas:
      "category-breakdown top-expenses"
      "yearly-breakdown yearly-breakdown";
    padding: 1rem;
  }

  .category-breakdown-grid-item {
    margin-bottom: 0;
  }

  .top-expenses-grid-item {
    margin-bottom: 0;
  }

  .yearly-breakdown-grid-item {
    margin-bottom: 0;
  }
}
</style>
