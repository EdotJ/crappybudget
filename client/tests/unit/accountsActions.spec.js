import api from "@/api";
import { actions } from "@/store/modules/accounts";
import { testAction } from "../helpers";

jest.mock("@/api");

const getAllResponse = {
  data: {
    accounts: [
      { id: 3, name: "Test Account" },
      { id: 5, name: "Test Account 2" },
    ],
  },
};

const getSingleResponse = {
  data: {
    id: 5,
    name: "Test Account",
  },
};

const updateResponse = { data: { id: 1, name: "Updated Account" }, status: 200 };

api.accounts.getAll.mockResolvedValue(getAllResponse);
api.accounts.getSingle.mockResolvedValue(getSingleResponse);
api.accounts.create.mockResolvedValue({ data: { id: 1, name: "New Account" }, status: 201 });
api.accounts.update.mockResolvedValue(updateResponse);
api.accounts.delete.mockResolvedValue({ status: 204 });

describe("accounts actions", () => {
  it("gets all accounts", (done) => {
    testAction(
      actions.getAll,
      null,
      {},
      [
        { type: "SET_IS_LOADING", payload: true },
        { type: "SET_ACCOUNTS", payload: getAllResponse.data.accounts },
        { type: "SET_IS_LOADING", payload: false },
      ],
      done
    );
  });
  it("gets single account", (done) => {
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
  it("creates an account", (done) => {
    const payload = {
      name: "New Account",
    };
    testAction(actions.create, payload, {}, [{ type: "ADD_ACCOUNT", payload: { id: 1, name: "New Account" } }], done);
  });

  it("updates an account", (done) => {
    const payload = updateResponse.data;
    let state = {
      accounts: [
        { id: 1, name: "Old Account" },
        { id: 2, name: "Newer Account" },
      ],
    };
    testAction(actions.update, payload, state, [{ type: "UPDATE_ACCOUNT", payload: updateResponse.data }], done);
  });

  it("deletes an account", (done) => {
    let state = {
      accounts: [
        { id: 1, name: "Old Account" },
        { id: 2, name: "Newer Account" },
      ],
    };
    testAction(actions.delete, 1, state, [{ type: "DELETE_ACCOUNT", payload: 1 }], done);
  });
});
