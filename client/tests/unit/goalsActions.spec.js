import api from "@/api";
import { actions } from "@/store/modules/goals";
import { testAction } from "../helpers";

jest.mock("@/api");

const getAllResponse = {
  data: {
    goals: [
      { id: 3, name: "Test Goal" },
      { id: 5, name: "Test Goal 2" },
    ],
  },
};

const getSingleResponse = {
  data: {
    id: 5,
    name: "Test Goal",
  },
};

const updateResponse = { data: { id: 1, name: "Updated Goal" }, status: 200 };

api.goals.getAll.mockResolvedValue(getAllResponse);
api.goals.getSingle.mockResolvedValue(getSingleResponse);
api.goals.create.mockResolvedValue({ data: { id: 1, name: "New Goal" }, status: 201 });
api.goals.update.mockResolvedValue(updateResponse);
api.goals.delete.mockResolvedValue({ status: 204 });

describe("goals actions", () => {
  it("gets all goals", (done) => {
    testAction(
      actions.getAll,
      null,
      {},
      [
        { type: "SET_IS_LOADING", payload: true },
        { type: "SET_GOALS", payload: getAllResponse.data.goals },
        { type: "SET_IS_LOADING", payload: false },
      ],
      done
    );
  });
  it("gets single goal", (done) => {
    const response = testAction(
      actions.getSingle,
      5,
      {},
      [
        { type: "SET_IS_LOADING", payload: true },
        { type: "SET_IS_LOADING", payload: false },
      ],
      done
    );
    expect(response).resolves.toBe(getSingleResponse.data);
  });
  it("creates a goal", (done) => {
    const payload = {
      name: "New Goal",
    };
    testAction(actions.create, payload, {}, [{ type: "ADD_GOAL", payload: { id: 1, name: "New Goal" } }], done);
  });

  it("updates a goal", (done) => {
    const payload = updateResponse.data;
    let state = {
      goals: [
        { id: 1, name: "Old Goal" },
        { id: 2, name: "Newer Goal" },
      ],
    };
    testAction(actions.update, payload, state, [{ type: "UPDATE_GOAL", payload: updateResponse.data }], done);
  });

  it("deletes a goal", (done) => {
    let state = {
      goals: [
        { id: 1, name: "Old Goal" },
        { id: 2, name: "Newer Goal" },
      ],
    };
    testAction(actions.delete, 1, state, [{ type: "DELETE_GOAL", payload: 1 }], done);
  });
});
