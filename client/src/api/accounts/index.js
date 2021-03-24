import request from "@/api/request";

export default {
  getAll(withBalance) {
    return request.get(`/accounts${withBalance ? "?balances=true" : ""}`);
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
  getMonthly(id) {
    return request.get(`/entries/currentMonth?accountId=${id}`);
  },
};
