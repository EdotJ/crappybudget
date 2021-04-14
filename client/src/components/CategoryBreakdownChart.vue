<script>
import { Pie } from "vue-chartjs";

export default {
  name: "CategoryBreakdownChart",
  extends: Pie,
  props: {
    chartData: Object,
  },
  data() {
    return {
      options: {
        responsive: true,
        maintainAspectRatio: true,
        scales: {
          y: {
            beginAtZero: true,
          },
        },
        legend: {
          position: "right",
          labels: {
            fontColor: "#201b22",
          },
        },
      },
    };
  },
  mounted() {
    this.renderChart(this.chartData, this.options);
  },
};
</script>

<style scoped></style>
