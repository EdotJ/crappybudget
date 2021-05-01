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
          <label for="goal-name">Name</label>
          <input id="goal-name" type="text" required placeholder="Name" autocomplete="false" v-model="goal.name" />
        </div>
        <div class="input-group">
          <label for="goal-description">Description</label>
          <input
            id="goal-description"
            type="text"
            placeholder="Description"
            autocomplete="false"
            v-model="goal.description"
          />
        </div>
        <div class="input-group">
          <label for="date">Goal Date</label>
          <input id="date" type="date" required placeholder="Date" v-model="goal.date" />
        </div>
        <div class="input-group">
          <label for="value">Goal Value</label>
          <input id="value" type="number" step="0.01" min="0" required placeholder="Value" v-model="goal.goalValue" />
        </div>
        <div class="input-group">
          <label for="category">Category</label>
          <CategorySelector id="category" v-model="goal.categoryId" :required="true" :clearable="false" />
        </div>
        <div class="input-group">
          <label for="goalType">Goal Type</label>
          <v-select
            class="selector"
            id="goalType"
            required
            v-model="goal.goalType"
            :options="goalTypes"
            :reduce="(goalType) => goalType.id"
            label="name"
            :clearable="false"
          />
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
import vSelect from "vue-select";
import { mapActions, mapGetters, mapState } from "vuex";
import CategorySelector from "@/components/CategorySelector";

export default {
  name: "GoalModal",
  components: { CategorySelector, FormError, AccentedSubmitButton, Modal, vSelect },
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
      getGoals: "goals/getAll",
    }),
    closeModal() {
      this.$emit("close-modal");
      this.error = "";
    },
    submitCreate() {
      this.createGoal({
        ...this.goal,
      })
        .then(() => {
          this.closeModal();
        })
        .catch((e) => (this.error = e.data.message));
    },
    submitUpdate() {
      this.updateGoal({
        ...this.goal,
      })
        .then(() => {
          this.closeModal();
        })
        .catch((e) => {
          console.log(e);
          this.error = e.data.message;
        });
    },
    submitDelete() {
      this.deleteGoal(this.goal.id).then(() => {
        this.closeModal();
      });
    },
  },
};
</script>

<style scoped>
form {
  width: 100%;
}

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

.selector {
  width: 100%;
  background: var(--input-bg-color);
  border-radius: 8px;
  font-size: 1rem;
}
</style>
