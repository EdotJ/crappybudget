<template>
  <div class="group">
    <label v-if="vertical && label" :for="id"> {{ placeholder }} <span v-if="required">*</span> </label>
    <div :class="['container', { active: focused || value }, { 'no-padding': noPadding }]">
      <label v-if="!vertical && label" :for="id"> {{ placeholder }}<span v-if="required">*</span> </label>
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
        :autocomplete="autocomplete ? 'on' : 'off'"
        v-on:input="$emit('input', $event.target.value)"
        v-if="!textarea"
        class="stylable"
        :maxlength="maxlength"
      />
      <textarea
        :id="id"
        :type="type"
        @focus="focused = true"
        @blur="focused = false"
        :required="required"
        :value="value"
        :step="step"
        :min="min"
        :placeholder="placeholder"
        :autocomplete="autocomplete ? 'on' : 'off'"
        v-on:input="$emit('input', $event.target.value)"
        class="stylable"
        v-else
        rows="3"
        :maxlength="maxlength"
      >
      </textarea>
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
    noPadding: { type: Boolean, default: false },
    autocomplete: { type: Boolean, default: true },
    textarea: { type: Boolean, default: false },
    maxlength: { type: Number, default: 60 },
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
  width: 20%;
  display: flex;
  justify-content: flex-start;
  margin: 0;
}

.modal .container label {
  font-size: 1rem;
  width: 40%;
}

label span {
  color: red;
}

.stylable {
  max-width: 100%;
  flex-grow: 1;
  height: 32px;
  margin: 8px 0;
  background-color: var(--input-bg-color);
  box-shadow: none;
  border: none;
  border-radius: 8px;
  padding: 0 1rem;
  width: 80%;
  font-size: 14px;
}

textarea.stylable {
  height: auto;
  padding: 8px;
}

.stylable:focus {
  outline: none;
  box-shadow: none;
  border: #0366d6 1px solid;
}

.stylable::-webkit-outer-spin-button,
.stylable::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

.stylable[type="number"] {
  -moz-appearance: textfield;
}

.group {
  width: 100%;
}

.no-padding input {
  padding: 0;
}
</style>
