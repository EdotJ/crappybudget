<script>
import { Bar } from "vue-chartjs";

export default {
  extends: Bar,
  name: "YearlyBreakdownChart",
  props: {
    incomeData: Array,
    expenseData: Array,
  },
  data() {
    return {
      chartData: {
        labels: ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"],
        datasets: [
          { label: "Income", backgroundColor: "#13c200", data: this.incomeData },
          { label: "Expenses", backgroundColor: "#d90000", data: this.expenseData },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        scales: {
          y: {
            beginAtZero: true,
          },
        },
        legend: { labels: { fontColor: "#201b22" } },
      },
    };
  },
  mounted() {
    this.renderChart(this.chartData, this.options);
  },
};
</script>

<style scoped></style>
