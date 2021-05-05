<template>
  <div v-if="show" class="modal-root">
    <div class="modal-overlay" @click="handleClose" v-on:keyup.esc="handleClose"></div>
    <div class="modal">
      <div class="header">
        <span class="title"> <slot name="header"></slot> </span>
        <span class="exit-button" @click="handleClose"> ✕ </span>
      </div>
      <div class="content">
        <slot name="content"></slot>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "Modal",
  props: {
    show: Boolean,
  },
  watch: {
    show() {
      if (this.show === false) {
        window.removeEventListener("keyup", this.onEscapeKeyUp);
      } else {
        window.addEventListener("keyup", this.onEscapeKeyUp);
      }
    },
  },
  methods: {
    handleClose() {
      this.$emit("close-modal");
    },
    onEscapeKeyUp(event) {
      if (event.which === 27) {
        this.handleClose();
      }
    },
  },
};
</script>

<style scoped>
.modal-overlay {
  position: absolute;
  height: 100%;
  width: 100%;
  top: 0;
  left: 0;
  opacity: 0.6;
  background: var(--accent-main-darker);
  z-index: 10;
}

.modal {
  background: var(--main-bg-color);
  position: absolute;
  z-index: 11;
  top: 30%;
  left: 50%;
  transform: translate(-50%, -30%);
  border-radius: 8px;
  min-width: 320px;
}

.header {
  width: 100%;
  background: var(--accent-main);
  color: var(--foreground-accent);
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px 8px 0 0;
}

.title {
  flex-grow: 1;
  padding-left: 32px;
}

.exit-button {
  background: var(--accent-main-lighter);
  height: 100%;
  width: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 0 8px 0 0;
}

.exit-button:hover {
  background: indianred;
}

.content {
  box-sizing: border-box;
  display: flex;
  align-items: center;
  flex-direction: column;
  padding: 1rem;
}

.input-group {
  padding: 0.5rem;
  display: flex;
  align-items: center;
}

.input-group > label {
  flex: 1 0 120px;
  max-width: 220px;
}

.checkbox-input > label {
  flex: 1 0 0;
  max-width: 120px;
}

label {
  padding-right: 0.5em;
  display: flex;
  justify-content: flex-start;
  font-size: 1rem;
  letter-spacing: 0.05em;
}

input:not([type="checkbox"]),
select {
  flex: 1 1 auto;
  height: 2rem;
  width: 80%;
}

@media only screen and (min-width: 490px) and (max-width: 1280px) {
  .modal {
    top: 30%;
    left: 50%;
    transform: translate(-50%, -30%);
    min-width: 470px;
  }
}

@media only screen and (min-width: 1281px) {
  .modal {
    top: 30%;
    left: 50%;
    transform: translate(-50%, -30%);
    min-width: 470px;
  }
}
</style>
