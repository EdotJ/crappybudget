<template>
  <div class="gradient"></div>
</template>

<script>
export default {
  name: "GradientLoader",
};
</script>

<style scoped>
.gradient {
  animation-duration: 1.8s;
  animation-fill-mode: forwards;
  animation-iteration-count: infinite;
  animation-name: slide;
  animation-timing-function: linear;
  background: linear-gradient(
    to right,
    var(--accent-main-lighter-transparent) 8%,
    var(--accent-main) 38%,
    var(--accent-main-darker-transparent) 75%
  );
  background-size: 1000px 640px;
  position: relative;
  height: 24px;
  line-height: 1;
  border-radius: 8px;
}

@keyframes slide {
  0% {
    background-position: -468px 0;
  }
  100% {
    background-position: 468px 0;
  }
}
</style>
