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

const entryCount = 9;

export default {
  getAll(from, to, page) {
    return request.get(
      `/entries?size=${entryCount}${page || page === 0 ? "&page=" + page : ""}${from ? "&from=" + from : ""}${
        to ? "&to=" + to : ""
      }`
    );
  },
  async getAllForAccount(accountId, from, to, page) {
    return request.get(
      `/entries?accountId=${accountId}&size=${entryCount}${page || page === 0 ? "&page=" + page : ""}${
        from ? "&from=" + from : ""
      }${to ? "&to=" + to : ""}`
    );
  },
  getSingle(id) {
    return request.get(`/entries/${id}`);
  },
  create(entry) {
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
  scanReceipts(file) {
    let formData = new FormData();
    if (file) {
      formData.append("file", file);
    }
    return request.post(`/receipts?isBasic=false`, formData);
  },
  submitReceiptEntries(receipt) {
    const body = {
      accountId: receipt.accountId,
      categoryId: receipt.categoryId,
      date: receipt.date,
      entries: receipt.entries.map((e) => ({
        name: e.label,
        price: e.price,
      })),
    };
    return request.post(`/entries/receipt`, body);
  },
};
