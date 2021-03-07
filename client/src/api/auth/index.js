import request from "@/api/request";

export default {
  login(username, password) {
    const body = {
      username,
      password,
    };
    return request.post("login", body);
  },
  register(username, password, email) {
    const body = {
      username,
      password,
      email,
    };
    return request.post("users", body);
  },
  refreshToken(token) {
    const body = {
      grant_type: "refresh_token",
      refresh_token: token,
    };
    return request.post("refresh_token", body, {
      headers: {
        "Content-Type": "application/json",
      },
    });
  },
};
