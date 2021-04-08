<template>
  <select @change="handleChange" :value="value">
    <option v-for="year in getYears()" :key="year" :value="year">
      {{ year }}
    </option>
  </select>
</template>

<script>
export default {
  name: "YearSelector",
  props: {
    value: [Number, String],
  },
  methods: {
    getYears() {
      return Array.from(Array(new Date().getFullYear() + 1 - 1980).keys(), (n) => n + 1980).reverse();
    },
    handleChange(e) {
      this.$emit("input", e.target.value);
    },
  },
};
</script>

<style scoped></style>
