import api from "@/api";
import { actions } from "@/store/modules/categories";
import { testAction } from "../helpers";

jest.mock("@/api");

const getAllResponse = {
  data: {
    categories: [
      { id: 3, name: "Test Category" },
      { id: 5, name: "Test Category 2" },
    ],
  },
};

const getSingleResponse = {
  data: {
    id: 5,
    name: "Test Category",
  },
};

const updateResponse = { data: { id: 1, name: "Updated Category" }, status: 200 };

api.categories.getAll.mockResolvedValue(getAllResponse);
api.categories.getSingle.mockResolvedValue(getSingleResponse);
api.categories.create.mockResolvedValue({ data: { id: 1, name: "New Category" }, status: 201 });
api.categories.update.mockResolvedValue(updateResponse);
api.categories.delete.mockResolvedValue({ status: 204 });

describe("categories actions", () => {
  it("gets all categories", (done) => {
    testAction(
      actions.getAll,
      null,
      {},
      [
        { type: "SET_IS_LOADING", payload: true },
        { type: "SET_CATEGORIES", payload: getAllResponse.data.categories },
        { type: "SET_IS_LOADING", payload: false },
      ],
      done
    );
  });
  it("gets single category", (done) => {
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
  it("creates a category", (done) => {
    const payload = {
      name: "New Category",
    };
    testAction(actions.create, payload, {}, [{ type: "ADD_CATEGORY", payload: { id: 1, name: "New Category" } }], done);
  });

  it("updates a category", (done) => {
    const payload = updateResponse.data;
    let state = {
      categories: [
        { id: 1, name: "Old Category" },
        { id: 2, name: "Newer Category" },
      ],
    };
    testAction(actions.update, payload, state, [{ type: "UPDATE_CATEGORY", payload: updateResponse.data }], done);
  });

  it("deletes a category", (done) => {
    let state = {
      categories: [
        { id: 1, name: "Old Category" },
        { id: 2, name: "Newer Category" },
      ],
    };
    testAction(actions.delete, 1, state, [{ type: "DELETE_CATEGORY", payload: 1 }], done);
  });
});
