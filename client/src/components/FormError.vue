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
  name: "FormError",
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
  padding: 0 8px;
  min-height: 40px;
  max-height: 200px;
  overflow-y: hidden;
  text-overflow: ellipsis;
}

.error-text {
  font-size: 1rem;
  color: var(--foreground-error);
}

.cross-icon {
  color: red;
  margin-right: 8px;
  height: 1rem;
  width: 1rem;
  flex-shrink: 0;
}
</style>
