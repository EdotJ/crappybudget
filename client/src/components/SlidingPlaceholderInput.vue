<template>
  <div>
    <div :class="['container', { active: focused || value }]">
      <label :for="id"> {{ placeholder }} </label>
      <input
        :id="id"
        :type="type"
        @focus="focused = true"
        @blur="focused = false"
        :required="required"
        :value="value"
        v-on:input="$emit('input', $event.target.value)"
      />
    </div>
  </div>
</template>

<script>
export default {
  name: "SlidingPlaceholderInput",
  props: ["id", "type", "placeholder", "required", "value"],
  data() {
    return {
      focused: false,
    };
  },
};
</script>

<style scoped>
.container {
  margin-bottom: 8px;
  position: relative;
  border-bottom: 1px solid gray;
  width: 100%;
  -webkit-box-sizing: border-box;
  -moz-box-sizing: border-box;
  box-sizing: border-box;
  color: var(--main-fg-color);
}

.container input {
  border: none;
  font-size: 16px;
  padding: 16px 0 10px;
  outline: 0;
  width: 100%;
  background: var(--main-bg-color);
  box-shadow: none;
}

.container label {
  font-size: 16px;
  position: absolute;
  transform-origin: top left;
  transform: translate(0, 16px) scale(1);
  -ms-transform: translate(0, 16px) scale(1);
  transition: all 0.1s ease-in-out;
}

.container.active label {
  transform: translate(0, 4px) scale(0.75);
  -ms-transform: translate(0, 4px) scale(0.75);
}

label {
  display: flex;
  flex-direction: row;
  align-items: flex-end;
}

label span {
  display: inline;
  flex-direction: row;
  justify-content: space-between;
}
</style>
