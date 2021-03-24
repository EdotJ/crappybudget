import request from "@/api/request";

const getEntryBody = (entry) => {
  return {
    name: entry.name,
    description: entry.description ? entry.description : null,
    value: entry.value,
    date: entry.date,
    accountId: entry.accountId,
    categoryId: entry.categoryId,
    isExpense: entry.isExpense,
  };
};

export default {
  getAll(page) {
    return request.get(`/entries?size=8${page ? "&page=" + page : ""}`);
  },
  async getAllForAccount(accountId, page) {
    return request.get(`/entries?accountId=${accountId}&size=8${page ? "&page=" + page : ""}`);
  },
  getSingle(id) {
    return request.get(`/entries/${id}`);
  },
  create(entry) {
    console.log(entry);
    const body = getEntryBody(entry);
    return request.post("/entries", body);
  },
  update(entry) {
    const body = getEntryBody(entry);
    return request.put(`/entries/${entry.id}`, body);
  },
  delete(id) {
    return request.delete(`/entries/${id}`);
  },
  getBalance() {
    return request.get(`/entries/balance`);
  },
};
