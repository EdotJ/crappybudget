<template>
  <v-select
    id="account"
    class="selector"
    :options="accounts"
    label="name"
    :reduce="(account) => account.id"
    :clearable="clearable"
    :value="value"
    @input="emitValue"
  />
</template>

<script>
import vSelect from "vue-select";
import { mapState } from "vuex";

export default {
  name: "AccountSelector",
  props: ["clearable", "value"],
  components: { vSelect },
  methods: {
    emitValue(value) {
      this.$emit("input", value);
    },
  },
  computed: {
    ...mapState({
      accounts: (state) => state.accounts.accounts,
    }),
  },
};
</script>

<style scoped>
.selector {
  background: var(--input-bg-color);
  border-radius: 8px;
  font-size: 1rem;
}
</style>
