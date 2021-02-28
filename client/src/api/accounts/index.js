import request from "@/api/request";

export default {
  getAll() {
    return request.get("/accounts");
  },
  getSingle(id) {
    return request.get(`/accounts/${id}`);
  },
  create(account) {
    const body = {
      name: account.name,
    };
    return request.post("/accounts", body);
  },
  update(account) {
    const body = {
      name: account.name,
    };
    return request.put(`/accounts/${account.id}`, body);
  },
  delete(id) {
    return request.delete(`/accounts/${id}`);
  },
};
