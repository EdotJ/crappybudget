<template>
  <button class="button" :class="{ 'button-with-icon': iconName }" :type="type">
    <div class="button-text" :class="{ 'text-with-icon': iconName }">
      <slot />
    </div>
    <IconBase
      class="icon"
      v-if="iconName"
      :width="iconWidth"
      :height="iconWidth"
      :view-box="viewBox"
      icon-color="#FBFAF8"
    >
      <component v-bind:is="iconName" />
    </IconBase>
  </button>
</template>

<script>
import IconBase from "./IconBase";

export default {
  name: "Button",
  components: { IconBase },
  props: {
    type: { type: String, default: "submit" },
    buttonType: { type: String, default: "normal" },
    iconName: { type: String, default: "" },
    iconHeight: { type: String, default: "32" },
    iconWidth: { type: String, default: "34" },
    viewBox: { type: String, default: "0 0 32 34" },
  },
  data() {
    return {
      iconToComponent: {},
    };
  },
};
</script>

<style scoped>
.button {
  background: var(--accent-main);
  color: var(--foreground-accent);
  display: inline-block;
  border: 2px solid var(--accent-main-lighter);
  padding: 0.25em 0.5em;
  border-radius: 3px;
  font-family: "Quicksand", sans-serif;
  font-size: 1em;
  font-weight: 700;
  letter-spacing: 0.02em;
  line-height: 1;
  text-decoration: none;
  text-transform: uppercase;
  cursor: pointer;
  transition: 0.3s;
}
.button:hover {
  background-color: var(--accent-main-lighter);
  border: 2px solid var(--accent-main-lighter);
  color: var(--foreground-accent);
}
.button:active {
  background-color: var(--accent-main-lighter);
  border: 2px solid var(--accent-main-lighter);
  color: var(--foreground-accent);
}
.text-with-icon {
  margin-right: 0.5em;
}
.button-with-icon {
  padding: 0 0 0 0.25em;
  display: flex;
  align-items: center;
}
.icon {
  background-color: tomato;
  transition: 0.3s;
  height: 20px;
  padding: 5px 0;
}
.button:active .icon {
  background-color: var(--accent-main-lighter);
}
/* Desktop Styles */
@media only screen and (min-width: 961px) {
  .icon {
    height: 26px;
  }
}
</style>
