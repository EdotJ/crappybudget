<template>
  <div class="progress">
    <div class="shell">
      <div class="bar" :style="{ width: progress <= 0 ? 0 : progress + '%' }">
        <span>{{ progress > 19 ? progress.toFixed(2) + "%" : "" }}</span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "ProgressBar",
  props: {
    progress: Number,
  },
};
</script>

<style scoped>
.progress {
  border: 1px solid;
  height: 24px;
  margin: 0 16px;
}

.shell {
  height: 100%;
}

.bar {
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(to right, var(--accent-main-lighter), var(--accent-main-darker));
  color: var(--foreground-accent);
}

.bar span {
  font-size: 0.75rem;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
}
</style>
