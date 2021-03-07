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

const getResponseInterceptor = async (store) => {
  request.interceptors.response.use(null, async (error) => {
    const { status, data } = error.response;
    if (status !== 401 || (data && data.code === "AUTH_ERROR")) {
      return Promise.reject(error);
    }
    try {
      store.dispatch("auth/refreshToken").then(async () => {
        error.config.headers["Authorization"] = `Bearer ${store.getters["auth/getAccessToken"]}`;
        await axios.request(error.config);
      });
    } catch (e) {
      console.error(e);
    }
    store.dispatch("auth/deleteTokens");
    return Promise.reject(error);
  });
};

const plugin = (store) => {
  getRequestInterceptor(store);
  getResponseInterceptor(store);
};
export default plugin;
