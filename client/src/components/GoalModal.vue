<template>
  <Modal :show="show" v-on:close-modal="closeModal">
    <template v-slot:header>{{ goal && goal.id ? "Edit Goal" : "Create New Goal" }}</template>
    <template v-slot:content>
      <FormError :value="error" />
      <form v-on:submit.prevent="goal.id ? submitUpdate() : submitCreate()">
        <div class="current-value" v-if="goal.id">
          <span>Current value:</span>
          <span class="">{{ goal.currentValue }}</span>
        </div>
        <div class="input-group">
          <label for="name">Name</label>
          <input id="name" type="text" required placeholder="Name" v-model="goal.name" />
        </div>
        <div class="input-group">
          <label for="description">Description</label>
          <input id="description" type="text" placeholder="Description" v-model="goal.description" />
        </div>
        <div class="input-group">
          <label for="date">Date</label>
          <input id="date" type="date" required placeholder="Date" v-model="goal.date" />
        </div>
        <div class="input-group">
          <label for="value">Goal Value</label>
          <input id="value" type="number" step="0.01" min="0" required placeholder="Value" v-model="goal.goalValue" />
        </div>
        <div class="input-group">
          <label for="category">Category</label>
          <select id="category" required v-model="goal.categoryId">
            <option v-for="category in sortedCategories" :key="category.id" :value="category.id">
              {{ category.parentId ? `- ${category.name}` : category.name }}
            </option>
          </select>
        </div>
        <div class="input-group">
          <label for="goalType">Goal Type</label>
          <select id="goalType" required v-model="goal.goalType">
            <option
              v-for="goalType in goalTypes"
              :key="goalType.id"
              :value="goalType.id"
              :selected="goalType.id === goal.goalType"
            >
              {{ goalType.name }}
            </option>
          </select>
        </div>
        <div class="submit-container">
          <AccentedSubmitButton type="button" @click.native="submitDelete" text="DELETE" />
          <AccentedSubmitButton />
        </div>
      </form>
    </template>
  </Modal>
</template>

<script>
import FormError from "@/components/FormError";
import AccentedSubmitButton from "@/components/AccentedSubmitButton";
import Modal from "@/components/Modal";
import { mapActions, mapGetters, mapState } from "vuex";

export default {
  name: "GoalModal",
  components: { FormError, AccentedSubmitButton, Modal },
  props: { goal: Object, show: Boolean },
  data() {
    return {
      error: "",
    };
  },
  computed: {
    ...mapGetters({
      sortedCategories: "categories/getSortedCategories",
    }),
    ...mapState({
      goalTypes: (state) => state.goals.goalTypes,
    }),
  },
  methods: {
    ...mapActions({
      createGoal: "goals/create",
      updateGoal: "goals/update",
      deleteGoal: "goals/delete",
      refreshBalances: "goals/getAll",
    }),
    closeModal() {
      this.$emit("close-modal");
    },
    submitCreate() {
      this.createGoal({
        ...this.goal,
      })
        .then(() => {
          this.closeModal();
          this.refreshBalances();
        })
        .catch((e) => (this.error = e.data.message));
    },
    submitUpdate() {
      this.updateGoal({
        ...this.goal,
      })
        .then(() => {
          this.closeModal();
          this.refreshBalances();
        })
        .catch((e) => {
          console.log(e);
          this.error = e.data.message;
        });
    },
    submitDelete() {
      this.deleteGoal(this.goal.id);
      this.closeModal();
    },
  },
};
</script>

<style scoped>
.current-value {
  font-size: 1rem;
  display: flex;
  justify-content: space-between;
  padding: 0 8px;
}

.submit-container {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
