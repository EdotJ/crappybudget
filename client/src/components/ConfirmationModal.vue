<template>
  <Modal :show="show" v-on:close-modal="toggleConfirmationModal" class="confirmation-modal">
    <template v-slot:header>Confirm Deletion</template>
    <template v-slot:content>
      <div class="title">Are you sure you want to delete {{ entityName }} '{{ name }}'?</div>
      <div class="warning" v-if="warning">{{ warning }}</div>
      <div class="buttons">
        <button type="button" class="button delete-cancel" @click="toggleConfirmationModal">Cancel</button>
        <button type="button" class="button delete-confirm" @click="handleConfirm">Proceed</button>
      </div>
    </template>
  </Modal>
</template>

<script>
import Modal from "@/components/Modal";

export default {
  name: "ConfirmationModal",
  components: { Modal },
  props: { show: Boolean, entityName: String, name: String, warning: String },
  methods: {
    toggleConfirmationModal() {
      this.$emit("close-modal");
    },
    handleConfirm() {
      this.$emit("confirmed");
    },
  },
};
</script>

<style scoped>
.delete-cancel,
.delete-confirm {
  border: 2px solid var(--accent-main);
  border-radius: 8px;
  font-size: 1.25rem;
  height: 2rem;
}

.delete-cancel {
  background: var(--accent-main);
  color: var(--foreground-accent);
}

.delete-cancel:hover {
  background: var(--main-bg-color);
  color: var(--main-fg-color);
}

.delete-confirm {
  background: var(--main-bg-color);
}

.delete-confirm:hover {
  background: var(--accent-main);
  color: var(--foreground-accent);
}

.buttons {
  width: 100%;
  display: flex;
  justify-content: space-between;
  padding: 1rem 1rem;
}

.warning {
  color: red;
  font-weight: bold;
  padding: 1rem 0;
}

/* Tablet Styles */
@media only screen and (min-width: 600px) and (max-width: 960px) {
  .buttons {
    width: 100%;
    display: flex;
    justify-content: space-between;
    padding: 1rem 3rem;
  }
}

/* Desktop Styles */
@media only screen and (min-width: 1101px) {
  .buttons {
    width: 100%;
    display: flex;
    justify-content: space-between;
    padding: 1rem 3rem;
  }
}
</style>
