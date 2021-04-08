<template>
  <button :type="type" class="submit-button">
    <Spinner v-if="isLoading" foreground="accent" />
    <span v-else>{{ text }}</span>
  </button>
</template>

<script>
import Spinner from "@/components/Spinner";

export default {
  name: "AccentedSubmitButton",
  components: { Spinner },
  props: { isLoading: Boolean, type: { type: String, default: "submit" }, text: { type: String, default: "SUBMIT" } },
};
</script>

<style scoped>
.submit-button {
  background: var(--accent-main);
  color: var(--foreground-accent);
  border: 0;
  height: 32px;
  border-radius: 16px;
  margin-top: 32px;
  box-shadow: 0 4px 2px -2px gray;
  font-size: 1em;
  font-weight: 700;
  font-family: "Quicksand", sans-serif;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0.25em 0.5em;
  letter-spacing: 0.02em;
  line-height: 1;
  text-decoration: none;
  text-transform: uppercase;
  transition: 0.3s;
}

.submit-button:hover {
  background: var(--accent-main-lighter);
}
</style>
