import request from "./request";
import axios from "axios";

const getRequestInterceptor = (store) => {
  request.interceptors.request.use((config) => {
    if (config.url !== "refresh_token" && store.getters["auth/getAccessToken"]) {
      config.headers.common["Authorization"] = `Bearer ${store.getters["auth/getAccessToken"]}`;
    }
    return config;
  }, null);
};

const getResponseInterceptor = (store) => {
  request.interceptors.response.use(null, async (error) => {
    const { status, data } = error.response;
    if (status !== 401 || (data && data.code === "AUTH_ERROR")) {
      if (data.code === "INVALID_TOKEN") {
        store.dispatch("auth/deleteTokens");
      }
      return Promise.reject(error);
    }
    try {
      return store.dispatch("auth/refreshToken").then(async () => {
        error.config.headers["Authorization"] = `Bearer ${store.getters["auth/getAccessToken"]}`;
        return await axios.request(error.config);
      });
    } catch (e) {
      store.dispatch("auth/deleteTokens");
      return Promise.reject(error);
    }
  });
};

const plugin = (store) => {
  getRequestInterceptor(store);
  getResponseInterceptor(store);
};
export default plugin;
