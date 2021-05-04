<template>
  <Modal :show="show" v-on:close-modal="closeModal">
    <template v-slot:header>{{ goal && goal.id ? "Edit Goal" : "Create New Goal" }}</template>
    <template v-slot:content>
      <FormError :value="error" />
      <form v-on:submit.prevent="goal.id ? submitUpdate() : submitCreate()">
        <div class="current-value" v-if="goal && goal.id">
          <span>Current value:</span>
          <div class="current-value-container">
            <span class="">{{ goal.currentValue }}</span>
          </div>
        </div>
        <StyledInput
          id="goal-name"
          type="text"
          :required="true"
          :autocomplete="false"
          placeholder="Name"
          v-model="goal.name"
        />
        <StyledInput
          id="goal-description"
          type="text"
          :textarea="true"
          :autocomplete="false"
          placeholder="Description"
          v-model="goal.description"
        />
        <StyledInput id="goal-date" type="date" :required="true" placeholder="Date" v-model="goal.date" />
        <StyledInput
          id="goal-value"
          type="number"
          step="0.01"
          min="0"
          :required="true"
          placeholder="Value"
          v-model="goal.goalValue"
        />
        <div class="input-group">
          <label for="category">Category<span>*</span></label>
          <CategorySelector id="category" v-model="goal.categoryId" :required="true" :clearable="false" />
        </div>
        <div class="input-group">
          <label for="goalType">Goal Type<span>*</span></label>
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
        <div class="submit-container" :class="{ left: !(goal && goal.id) }">
          <AccentedSubmitButton v-if="goal && goal.id" type="button" @click.native="submitDelete" text="DELETE" />
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
import StyledInput from "@/components/StyledInput";

export default {
  name: "GoalModal",
  components: { StyledInput, CategorySelector, FormError, AccentedSubmitButton, Modal, vSelect },
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
}

.submit-container {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.left {
  justify-content: flex-end;
}

.selector {
  width: 100%;
  background: var(--input-bg-color);
  border-radius: 8px;
  font-size: 1rem;
}

.current-value span {
  display: flex;
  justify-content: flex-start;
  width: 29%;
}

.current-value-container {
  flex: 1;
  margin: 0 1.25rem;
  display: flex;
  justify-content: flex-start;
}

.input-group > label {
  width: 33%;
  max-width: unset;
  flex: none;
}

.input-group {
  padding: 0.5rem 0;
}

label span {
  color: red;
}
</style>
