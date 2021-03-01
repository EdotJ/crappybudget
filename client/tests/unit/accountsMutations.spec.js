import { mutations } from "@/store/modules/accounts";

const { SET_ACCOUNTS, SET_IS_LOADING, ADD_ACCOUNT, UPDATE_ACCOUNT, DELETE_ACCOUNT } = mutations;

describe("accounts mutations", () => {
  it("set accounts", () => {
    const state = { accounts: [] };
    expect(state.accounts.length).toBe(0);
    const accounts = [{ name: "First Test Account" }, { name: "Second Test Account" }];
    SET_ACCOUNTS(state, accounts);
    expect(state.accounts.length).toBe(2);
    expect(state.accounts[0].name).toBe("First Test Account");
  });

  it("set loading", () => {
    const state = { isLoading: false };
    SET_IS_LOADING(state, true);
    expect(state.isLoading).toBe(true);
  });

  it("add account", () => {
    const state = { accounts: [{ name: "First test account" }] };
    ADD_ACCOUNT(state, { name: "Second test account" });
    expect(state.accounts.length).toBe(2);
    expect(state.accounts[1].name).toBe("Second test account");
  });

  it("update account", () => {
    const state = {
      accounts: [
        { id: 1, name: "First test account" },
        { id: 2, name: "Second test account" },
        { id: 56, name: "56th test account" },
      ],
    };
    UPDATE_ACCOUNT(state, {
      id: 2,
      name: "Updated second test account",
    });
    expect(state.accounts.length).toBe(3);
    expect(state.accounts[1].name).toBe("Updated second test account");
    expect(state.accounts[0].name).toBe("First test account");
  });

  it("delete account", () => {
    const state = {
      accounts: [
        { id: 1, name: "Good account" },
        { id: 123, name: "Bad account" },
      ],
    };
    expect(state.accounts.length).toBe(2);
    DELETE_ACCOUNT(state, 123);
    expect(state.accounts.length).toBe(1);
  });
});
