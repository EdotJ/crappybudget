<template>
  <v-select class="selector"
            :clearable="false"
            @input="handleChange"
            :value="value"
            :options="getYears()">
  </v-select>
</template>

<script>
import vSelect from "vue-select";

export default {
  name: "YearSelector",
  props: {
    value: [Number, String],
  },
  components: {vSelect},
  methods: {
    getYears() {
      return Array.from(Array(new Date().getFullYear() + 1 - 1980).keys(), (n) => n + 1980).reverse();
    },
    handleChange(value) {
      this.$emit("input", value);
    },
  },
};
</script>

<style scoped>
.selector {
  background: var(--default-selector-bg-color);
}
</style>
