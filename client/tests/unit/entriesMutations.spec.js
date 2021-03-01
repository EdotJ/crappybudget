import { mutations } from "@/store/modules/entries";

const { SET_ENTRIES, SET_IS_LOADING, ADD_ENTRY, UPDATE_ENTRY, DELETE_ENTRY } = mutations;

describe("entries mutations", () => {
  it("set entries", () => {
    const state = { entries: [] };
    expect(state.entries.length).toBe(0);
    const entries = [{ name: "First Test Entry" }, { name: "Second Test Entry" }];
    SET_ENTRIES(state, entries);
    expect(state.entries.length).toBe(2);
    expect(state.entries[0].name).toBe("First Test Entry");
  });

  it("set loading", () => {
    const state = { isLoading: false };
    SET_IS_LOADING(state, true);
    expect(state.isLoading).toBe(true);
  });

  it("add entry", () => {
    const state = { entries: [{ name: "First test entry" }] };
    ADD_ENTRY(state, { name: "Second test entry" });
    expect(state.entries.length).toBe(2);
    expect(state.entries[1].name).toBe("Second test entry");
  });

  it("update entry", () => {
    const state = {
      entries: [
        { id: 1, name: "First test entry" },
        { id: 2, name: "Second test entry" },
        { id: 99, name: "99th test entry" },
      ],
    };
    UPDATE_ENTRY(state, {
      id: 99,
      name: "Updated 99th test entry",
    });
    expect(state.entries.length).toBe(3);
    expect(state.entries[2].name).toBe("Updated 99th test entry");
    expect(state.entries[0].name).toBe("First test entry");
  });

  it("delete entry", () => {
    const state = {
      entries: [
        { id: 123, name: "Good entry" },
        { id: 516, name: "Bad entry" },
      ],
    };
    expect(state.entries.length).toBe(2);
    DELETE_ENTRY(state, 123);
    expect(state.entries.length).toBe(1);
  });
});
