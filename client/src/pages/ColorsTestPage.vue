<template>
  <div>
    <div class="block accent-main-darker"></div>
    <div class="block block-embed accent-main-lighter"></div>
    <div class="block block-mid accent-main"></div>
    <div class="block block-right accent-main-lighter"></div>
  </div>
</template>

<script>
export default {
  name: "ColorsTestPage.vue",
};
</script>

<style scoped>
.block {
  height: 100px;
  width: 100px;
  border-radius: 100px;
  position: absolute;
  top: 0;
  margin-top: 100px;
  margin-left: 100px;
}

.accent-main-darker {
  background: var(--accent-main-darker);
}

.accent-main-lighter {
  background: var(--accent-main-lighter);
}

.accent-main {
  background: var(--accent-main);
}

.block-embed {
  margin-left: 125px;
  margin-top: 125px;
  height: 50px;
  width: 50px;
}

.block-mid {
  margin-left: 250px;
}

.block-right {
  margin-left: 400px;
}
</style>
