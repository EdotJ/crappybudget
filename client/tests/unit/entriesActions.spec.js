import api from "@/api";
import { actions } from "@/store/modules/entries";
import { testAction } from "../helpers";

jest.mock("@/api");

const getAllResponse = {
  data: {
    entries: [
      { id: 3, name: "Test Entry" },
      { id: 5, name: "Test Entry 2" },
    ],
    totalPages: 1,
  },
};

const getSingleResponse = {
  data: {
    id: 5,
    name: "Test Entry",
  },
};

const updateResponse = { data: { id: 1, name: "Updated Entry" }, status: 200 };

api.entries.getAll.mockResolvedValue(getAllResponse);
api.entries.getSingle.mockResolvedValue(getSingleResponse);
api.entries.create.mockResolvedValue({ data: { id: 1, name: "New Entry" }, status: 201 });
api.entries.update.mockResolvedValue(updateResponse);
api.entries.delete.mockResolvedValue({ status: 204 });

describe("entries actions", () => {
  it("gets all entries", (done) => {
    testAction(
      actions.getAll,
      {},
      {},
      [
        { type: "SET_IS_LOADING", payload: true },
        { type: "SET_ENTRIES", payload: getAllResponse.data.entries },
        { type: "SET_TOTAL_PAGES", payload: 1 },
        { type: "SET_IS_LOADING", payload: false },
      ],
      done
    );
  });
  it("gets single entry", (done) => {
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
  it("creates an entry", (done) => {
    const payload = {
      name: "New Entry",
    };
    testAction(actions.create, payload, {}, [{ type: "ADD_ENTRY", payload: { id: 1, name: "New Entry" } }], done);
  });

  it("updates an entry", (done) => {
    const payload = updateResponse.data;
    let state = {
      entries: [
        { id: 1, name: "Old Entry" },
        { id: 2, name: "Newer Entry" },
      ],
    };
    testAction(actions.update, payload, state, [{ type: "UPDATE_ENTRY", payload: updateResponse.data }], done);
  });

  it("deletes an entry", (done) => {
    let state = {
      entries: [
        { id: 1, name: "Old Entry" },
        { id: 2, name: "Newer Entry" },
      ],
    };
    testAction(actions.delete, 1, state, [{ type: "DELETE_ENTRY", payload: 1 }], done);
  });
});
