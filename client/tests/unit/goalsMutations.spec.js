import { mutations } from "@/store/modules/goals";

const { SET_GOALS, SET_IS_LOADING, ADD_GOAL, UPDATE_GOAL, DELETE_GOAL } = mutations;

describe("goals mutations", () => {
  it("set goals", () => {
    const state = { goals: [] };
    expect(state.goals.length).toBe(0);
    const goals = [{ name: "First Test Goal" }, { name: "Second Test Goal" }];
    SET_GOALS(state, goals);
    expect(state.goals.length).toBe(2);
    expect(state.goals[0].name).toBe("First Test Goal");
  });

  it("set loading", () => {
    const state = { isLoading: false };
    SET_IS_LOADING(state, true);
    expect(state.isLoading).toBe(true);
  });

  it("add goal", () => {
    const state = { goals: [{ name: "First test goal" }] };
    ADD_GOAL(state, { name: "Second test goal" });
    expect(state.goals.length).toBe(2);
    expect(state.goals[1].name).toBe("Second test goal");
  });

  it("update goal", () => {
    const state = {
      goals: [
        { id: 1, name: "First test goal" },
        { id: 2, name: "Second test goal" },
        { id: 99, name: "99th test goal" },
      ],
    };
    UPDATE_GOAL(state, {
      id: 99,
      name: "Updated 99th test goal",
    });
    expect(state.goals.length).toBe(3);
    expect(state.goals[2].name).toBe("Updated 99th test goal");
    expect(state.goals[0].name).toBe("First test goal");
  });

  it("delete goal", () => {
    const state = {
      goals: [
        { id: 123, name: "Good goal" },
        { id: 516, name: "Bad goal" },
      ],
    };
    expect(state.goals.length).toBe(2);
    DELETE_GOAL(state, 123);
    expect(state.goals.length).toBe(1);
  });
});
