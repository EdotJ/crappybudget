<template>
  <div v-if="value" class="error">
    <IconBase class="cross-icon" width="16" height="16" view-box="0 0 16 16">
      <Cross />
    </IconBase>
    <p class="error-text">
      {{ value }}
    </p>
  </div>
</template>
<script>
import IconBase from "./IconBase";
import Cross from "./icons/Cross";
export default {
  name: "ErrorMessage",
  components: { Cross, IconBase },
  props: {
    value: { type: String, default: "Something went wrong!" },
  },
};
</script>

<style scoped>
.error {
  display: flex;
  align-items: center;
  background: var(--error-color);
  border: 1px solid var(--error-color);
  border-radius: 4px;
  height: 40px;
  padding: 0 8px;
}
.error-text {
  font-size: 1rem;
  color: var(--foreground-error);
}
.cross-icon {
  color: red;
  padding: 0 8px 0 0;
}

/* Tablet Styles */
@media only screen and (min-width: 415px) and (max-width: 960px) {
}
/* Desktop Styles */
@media only screen and (min-width: 961px) {
}
</style>
