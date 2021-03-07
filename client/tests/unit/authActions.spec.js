import api from "@/api";
import { actions } from "@/store/modules/auth";
import { testAction } from "../helpers";

jest.mock("@/api");

api.auth.login.mockResolvedValue({ data: { access_token: "AccessToken", refresh_token: "RefreshToken" } });
api.auth.register.mockResolvedValue({ data: { id: 5, username: "TestUser", email: "TestEmail@Gmail.com" } });

describe("auth actions", () => {
  it("adds access token to store", (done) => {
    testAction(
      actions.login,
      { username: "TestUser", password: "UnsecurePassword" },
      {},
      [
        { type: "SET_ACCESS_TOKEN", payload: "AccessToken" },
        { type: "SET_REFRESH_TOKEN", payload: "RefreshToken" },
      ],
      done
    );
  });
  it("registers user", (done) => {
    testAction(
      actions.register,
      { username: "TestUser", password: "UnsecurePassword", email: "TestEmail@Gmail.com" },
      {},
      [],
      done
    );
  });
});
