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
    return request.post(`users?host=${document.location.host}`, body);
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
  passwordResetInit(email) {
    return request.get(`passwordReset?email=${email}`);
  },
  passwordResetConfirm(token, newPassword) {
    const body = {
      token: token,
      password: newPassword,
    };
    return request.post(`passwordReset`, body, {
      headers: {
        "Content-Type": "application/json",
      },
    });
  },
  verifyEmail(token) {
    return request.get(`email?token=${token}`);
  },
  resendEmail(email) {
    return request.post(
      `email`,
      { email: email, clientHost: document.location.host },
      {
        headers: {
          "Content-Type": "application/json",
        },
      }
    );
  },
  clearRefreshTokens() {
    return request.post(``);
  },
};
