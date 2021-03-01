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
  getUserInfo(userId) {
    return request.get(`users/${userId}`);
  },
};
