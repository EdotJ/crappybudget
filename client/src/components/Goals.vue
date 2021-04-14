<template>
  <div class="goals">
    <div class="goals-mobile">
      <div class="goal-trigger" @click="toggleShow()">
        <span></span>
        <button type="button">
          Goals
          <IconBase icon-name="arrow" height="12" width="12" view-box="0 0 24 24">
            <ArrowDown />
          </IconBase>
        </button>
      </div>
      <transition name="slide">
        <div class="goal-list" v-if="show">
          <div class="goal-button-container">
            <div class="add-goal-button" @click="toggleGoalModal">Add</div>
          </div>
          <Goal v-for="goal in goals" :goal="goal" :key="goal.id" @click.native="toggleEdit(goal)" />
        </div>
      </transition>
    </div>
    <div class="goals-desktop">
      <div class="goals-top">
        <h1>Goals</h1>
        <div class="add-goal-button" @click="toggleGoalModal">+</div>
      </div>
      <Goal v-for="goal in goals" :goal="goal" :key="goal.id" @click.native="toggleEdit(goal)" />
    </div>
    <GoalModal :show="showGoalModal" :goal="currentGoal" v-on:close-modal="closeModal" />
  </div>
</template>

<script>
import ArrowDown from "@/components/icons/ArrowDown";
import IconBase from "@/components/IconBase";
import Goal from "@/components/Goal";
import { mapActions, mapGetters, mapState } from "vuex";
import GoalModal from "@/components/GoalModal";

export default {
  name: "Goals",
  components: { GoalModal, Goal, ArrowDown, IconBase },
  data() {
    return {
      show: false,
      showGoalModal: false,
      error: "",
      currentGoal: {
        name: "",
        description: "",
        date: "",
        goalValue: 0,
        account: "",
        category: "",
        goalType: "",
      },
    };
  },
  methods: {
    toggleShow() {
      this.show = !this.show;
    },
    ...mapActions({
      getGoals: "goals/getAll",
      createGoal: "goals/create",
      getGoalTypes: "goals/getGoalTypes",
    }),
    toggleGoalModal() {
      this.showGoalModal = !this.showGoalModal;
      if (this.showGoalModal) {
        this.getGoalTypes();
      }
    },
    closeModal() {
      this.showGoalModal = false;
      this.currentGoal = {};
    },
    toggleEdit(goal) {
      this.currentGoal = { ...goal };
      this.toggleGoalModal();
    },
  },
  computed: {
    ...mapState({
      goals: (state) => state.goals.goals,
    }),
    ...mapGetters({
      sortedCategories: "categories/getSortedCategories",
    }),
  },
  mounted() {
    this.getGoals();
  },
};
</script>

<style scoped>
.goals {
  height: 100vh;
  width: 20%;
  padding-top: 3rem;
  border-left: 1px solid var(--accent-main);
  overflow-y: auto;
  -ms-overflow-style: none;
  scrollbar-width: none;
}

.goals::-webkit-scrollbar {
  display: none;
}

h1 {
  font-size: 2rem;
  font-weight: normal;
}

.goal-trigger {
  display: none;
}

.goals-mobile {
  display: none;
}

.goals-top {
  display: flex;
  justify-content: space-around;
  align-items: center;
}

.add-goal-button {
  height: 3rem;
  width: 3rem;
  color: var(--accent-main);
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 3rem;
  border: 2px solid var(--accent-main);
  border-radius: 3rem;
  cursor: pointer;
}

.add-goal-button:hover {
  background: var(--accent-main-lighter);
  color: var(--foreground-accent);
  border: 2px solid var(--accent-main-lighter);
}

@media only screen and (min-width: 961px) and (max-width: 1100px) {
  .goals {
    width: 25%;
    padding-top: 1rem;
  }

  .add-goal-button {
    height: 2rem;
    width: 2rem;
    font-size: 2rem;
  }
}

@media only screen and (max-width: 960px) {
  .goals {
    width: 100%;
    flex-grow: 1;
    border: none;
  }

  .goal-list {
    min-height: 10%;
    display: flex;
    flex-direction: column;
    border-bottom: 2px solid var(--accent-main-lighter-transparent);
  }

  .goal-trigger {
    height: 30px;
    display: flex;
    justify-content: center;
    align-items: center;
    margin: 4px 0;
    position: relative;
    z-index: 0;
  }

  .goal-trigger span {
    position: absolute;
    border-top: 2px solid var(--accent-main-lighter);
    background: black;
    width: 100%;
    transform: translateY(-50%);
    overflow: visible;
    z-index: 3;
  }

  .goal-trigger button {
    padding: 2px 16px;
    border: 1px solid var(--accent-main-lighter);
    border-radius: 16px;
    background: var(--accent-main-lighter);
    font-size: 1rem;
    color: var(--foreground-accent);
    z-index: 5;
  }

  .slide-enter-active {
    -moz-transition-duration: 0.1s;
    -webkit-transition-duration: 0.1s;
    -o-transition-duration: 0.1s;
    transition-duration: 0.1s;
  }

  .slide-leave-active {
    -moz-transition-duration: 0.1s;
    -webkit-transition-duration: 0.1s;
    -o-transition-duration: 0.1s;
    transition-duration: 0.1s;
  }

  .slide-enter-to,
  .slide-leave {
    max-height: 100px;
    overflow: hidden;
  }

  .slide-enter,
  .slide-leave-to {
    overflow: hidden;
    max-height: 0;
  }

  .goals-mobile {
    display: initial;
  }

  .goals-desktop {
    display: none;
  }

  .goal-button-container {
    width: 100%;
    display: flex;
    justify-content: flex-end;
  }

  .add-goal-button {
    font-size: 1em;
    border-radius: 8px;
    width: 4rem;
    font-family: "Quicksand", sans-serif;
    font-weight: 700;
    letter-spacing: 0.02em;
    line-height: 1;
    text-decoration: none;
    text-transform: uppercase;
    cursor: pointer;
    height: 2rem;
  }
}
</style>
