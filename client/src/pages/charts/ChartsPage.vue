<template>
  <div class="charts">
    <div class="top">
      <div class="category-breakdown-container" v-if="loadedCategory">
        <div class="chart-title">Monthly Expense Breakdown By Category</div>
        <YearSelector @input="handleCategoryBreakdownYearChange" v-model="categoryBreakdownYear" />
        <select @change="handleCategoryBreakdownMonthChange" :value="categoryBreakdownMonth">
          <option v-for="month in [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]" :key="month" :value="month">
            {{ month }}
          </option>
        </select>
        <CategoryBreakdownChart
          class="category-breakdown"
          :chartData="categoryBreakdownData"
          v-if="categoryBreakdownData.labels.length > 0 && !categoryBreakdownError"
        />
        <div class="category-breakdown" v-else-if="!categoryBreakdownError">Did not find any data :(</div>
        <div class="error" v-else>{{ categoryBreakdownError }}</div>
      </div>
      <div class="loader-container" v-else>
        <Spinner class="spinner" />
      </div>
      <div class="top-expenses" v-if="loadedTopExpenses">
        <div class="chart-title">Top Monthly Expenses</div>
        <div class="selectors">
          <YearSelector @input="handleTopExpensesYearChange" v-model="topExpensesYear" />
          <select @change="handleTopExpensesMonthChange" :value="topExpensesMonth">
            <option v-for="month in [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]" :key="month" :value="month">
              {{ month }}
            </option>
          </select>
        </div>
        <div class="table-container" v-if="!topExpensesError">
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
        <div class="error" v-if="topExpensesError">{{ topExpensesError }}</div>
      </div>
      <div class="loader-container" v-else>
        <Spinner class="spinner" />
      </div>
    </div>
    <div class="bottom">
      <div class="yearly-breakdown" v-if="loadedYearly">
        <div class="chart-title">Yearly Activity Breakdown</div>
        <YearSelector @input="handleYearlyBreakdownSelect" v-model="yearlyBreakdownYear" />
        <BreakdownChart :incomeData="yearlyIncomeData" :expenseData="yearlyExpenseData" v-if="!yearlyBreakdownError" />
        <div class="error" v-else>{{ yearlyBreakdownError }}</div>
      </div>
      <div class="loader-container" v-else>
        <Spinner class="spinner" />
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

export default {
  name: "ChartsPage",
  components: { Spinner, YearSelector, CategoryBreakdownChart, BreakdownChart },
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
    handleCategoryBreakdownMonthChange(e) {
      this.categoryBreakdownMonth = e.target.value;
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
    handleTopExpensesMonthChange(e) {
      this.topExpensesMonth = e.target.value;
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
.charts {
  display: flex;
  flex-direction: column;
  max-height: 100vh;
}

.bottom {
  padding-top: 2rem;
}

.top {
  display: flex;
  flex-direction: column;
  padding: 0;
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

.selectors {
  margin-bottom: 1rem;
}

thead {
  background-color: var(--accent-main);
  color: var(--foreground-accent);
}

.top-expenses {
  height: 100%;
  margin-top: 1rem;
  display: flex;
  flex-direction: column;
  align-items: center;
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
}

.expense-value {
  color: var(--expense-color);
}

.category-breakdown {
  display: flex;
  align-items: center;
  justify-content: center;
}

.error {
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  color: var(--foreground-error);
}

/* Desktop Styles */
@media only screen and (min-width: 961px) and (min-height: 800px) {
  .charts {
    padding: 2rem;
    height: 100%;
  }

  .top-expenses {
    display: flex;
    flex-direction: column;
    height: 100%;
  }

  .top {
    flex-direction: row;
    align-items: center;
    justify-content: space-between;
    padding: 0 4rem;
    height: 100%;
  }

  .bottom {
    padding-top: 0;
    height: 100%;
  }

  .category-breakdown-container {
    height: 100%;
  }

  .loader-container {
    padding: 0;
  }
}
</style>
