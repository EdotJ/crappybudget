import { mutations } from "@/store/modules/categories";

const { SET_CATEGORIES, SET_IS_LOADING, ADD_CATEGORY, UPDATE_CATEGORY, DELETE_CATEGORY } = mutations;

describe("categories mutations", () => {
  it("set categories", () => {
    const state = { categories: [] };
    expect(state.categories.length).toBe(0);
    const categories = [{ name: "First Test Category" }, { name: "Second Test Category" }];
    SET_CATEGORIES(state, categories);
    expect(state.categories.length).toBe(2);
    expect(state.categories[0].name).toBe("First Test Category");
  });

  it("set loading", () => {
    const state = { isLoading: false };
    SET_IS_LOADING(state, true);
    expect(state.isLoading).toBe(true);
  });

  it("add category", () => {
    const state = { categories: [{ name: "First test category" }] };
    ADD_CATEGORY(state, { name: "Second test category" });
    expect(state.categories.length).toBe(2);
    expect(state.categories[1].name).toBe("Second test category");
  });

  it("update category", () => {
    const state = {
      categories: [
        { id: 1, name: "First test category" },
        { id: 2, name: "Second test category" },
        { id: 13, name: "13th test category" },
      ],
    };
    UPDATE_CATEGORY(state, {
      id: 13,
      name: "Updated 13th test category",
    });
    expect(state.categories.length).toBe(3);
    expect(state.categories[2].name).toBe("Updated 13th test category");
    expect(state.categories[0].name).toBe("First test category");
  });

  it("delete category", () => {
    const state = {
      categories: [
        { id: 123, name: "Good category" },
        { id: 516, name: "Bad category" },
      ],
    };
    expect(state.categories.length).toBe(2);
    DELETE_CATEGORY(state, 123);
    expect(state.categories.length).toBe(1);
  });
});
