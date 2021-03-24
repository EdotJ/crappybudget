<template>
  <div class="goal">
    <div class="goal-name">{{ goal.name }}</div>
    <ProgressBar :progress="progressValue" />
  </div>
</template>

<script>
import ProgressBar from "@/components/ProgressBar";
export default {
  name: "Goal",
  components: { ProgressBar },
  props: {
    goal: Object,
  },
  computed: {
    progressValue: function () {
      if (this.goal.currentValue > this.goal.goalValue) {
        return 100;
      }
      return this.goal.currentValue >= 0 && this.goal.goalValue > 0
        ? (this.goal.currentValue / this.goal.goalValue) * 100
        : 0;
    },
  },
};
</script>

<style scoped>
.goal {
  padding: 8px 0;
}
.goal:hover {
  background: #cccccc;
}

.goal-name {
  font-size: 1rem;
}
</style>
