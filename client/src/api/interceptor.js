import request from "./request";

const getRequestInterceptor = (store) => {
  request.interceptors.request.use((config) => {
    if (store.getters["auth/getAccessToken"]) {
      config.headers.common["Authorization"] = `Bearer ${store.getters["auth/getAccessToken"]}`;
    }
    return config;
  }, null);
};

const getResponseInterceptor = () => {
  request.interceptors.response.use(null, async (error) => {
    const { status, data } = error.response;
    if (status !== 401 || (data && data.code === "AUTH_ERROR")) {
      return Promise.reject(error);
    }
    window.location = "/login";
    // TODO: add refresh token flow
  });
};

const plugin = (store) => {
  getRequestInterceptor(store);
  getResponseInterceptor(store);
};
export default plugin;
