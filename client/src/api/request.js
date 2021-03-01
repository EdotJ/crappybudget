import axios from "axios";

export default axios.create({
  baseURL: process.env.VUE_APP_API_HOST + process.env.VUE_APP_API_BASE_PATH,
  headers: {
    "Content-Type": "application/json",
  },
});
