<template>
  <div>
    <label v-if="vertical && label" :for="id"> {{ placeholder }} </label>
    <div :class="['container', { active: focused || value }]">
      <label v-if="!vertical && label" :for="id"> {{ placeholder }} </label>
      <input
        :id="id"
        :type="type"
        @focus="focused = true"
        @blur="focused = false"
        :required="required"
        :value="value"
        :step="step"
        :min="min"
        :placeholder="placeholder"
        v-on:input="$emit('input', $event.target.value)"
      />
    </div>
  </div>
</template>

<script>
export default {
  name: "StyledInput",
  props: {
    id: String,
    type: String,
    placeholder: { type: String, default: "" },
    required: { type: [String, Boolean], default: false },
    value: [Number, String],
    vertical: { type: [String, Boolean], default: false },
    step: [Number, String],
    min: [Number, String],
    label: { type: [String, Boolean], default: true },
  },
  data() {
    return {
      focused: false,
    };
  },
};
</script>

<style scoped>
.container {
  width: 100%;
  display: flex;
  align-items: center;
}

.container label {
  margin: 0 8px;
}

input {
  width: 100%;
  height: 32px;
  margin: 8px 0;
  background-color: #22223b33;
  box-shadow: none;
  border: none;
  border-radius: 8px;
  padding: 0 1rem;
}

input:focus {
  outline: none;
  box-shadow: none;
  border: #0366d6 1px solid;
}

input::-webkit-outer-spin-button,
input::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

input[type="number"] {
  -moz-appearance: textfield;
}
</style>
