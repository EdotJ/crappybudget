import request from "@/api/request";

export default {
  getAll() {
    return request.get("/categories");
  },
  getSingle(id) {
    return request.get(`/categories/${id}`);
  },
  create(category) {
    console.log(category);
    const body = {
      name: category.name,
      parentId: category.parentId ? category.parentId : null,
    };
    return request.post("/categories", body);
  },
  update(category) {
    const body = {
      name: category.name,
      parentId: category.parentId ? category.parentId : null,
    };
    return request.put(`/categories/${category.id}`, body);
  },
  delete(id) {
    return request.delete(`/categories/${id}`);
  },
};
