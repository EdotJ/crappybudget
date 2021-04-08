import request from "@/api/request";

export default {
  importCsv(formData, file) {
    let form = new FormData();
    form.append("nameHeader", formData.nameHeader);
    form.append("dateHeader", formData.dateHeader);
    form.append("valueHeader", formData.valueHeader);
    form.append("accountId", formData.accountId);
    if (formData.categoryHeader) {
      form.append("categoryHeader", formData.categoryHeader);
    }
    if (formData.descriptionHeader) {
      form.append("descriptionHeader", formData.categoryHeader);
    }
    if (file) {
      form.append("file", file);
    }
    return request.post("/imports/csv", form);
  },
  importYnab(token) {
    return request.post("/imports/ynab", { personalToken: token });
  },
  exportData() {
    return request.get("/export");
  },
};
