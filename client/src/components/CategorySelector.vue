<template>
  <v-select
    id="category"
    :class="['selector', { 'width-80': width80 }]"
    :options="
      parentsOnly
        ? sortedCategories.filter(
            (c) => !c.parentId && (currentCategoryOverride ? currentCategoryOverride.id !== c.id : value.id !== c.id)
          )
        : sortedCategories
    "
    :value="value"
    :reduce="(category) => category.id"
    label="name"
    :searchable="true"
    :clearable="clearable"
    :required="required"
    @input="emitValue"
  >
    <template v-slot:option="option">
      <span :class="{ 'child-category': option.parentId }">
        {{ option.name }}
      </span>
    </template>
  </v-select>
</template>

<script>
import vSelect from "vue-select";
import { mapGetters } from "vuex";

export default {
  name: "CategorySelector",
  components: { vSelect },
  props: ["value", "required", "parentsOnly", "clearable", "width80", "currentCategoryOverride"],
  computed: {
    ...mapGetters({
      sortedCategories: "categories/getSortedCategories",
    }),
  },
  methods: {
    emitValue(value) {
      this.$emit("input", value);
    },
  },
};
</script>

<style scoped>
.selector {
  background: var(--input-bg-color);
  border-radius: 8px;
  font-size: 1rem;
}

.width-80 {
  width: 80%;
}

.child-category {
  margin-left: 1rem;
}
</style>
