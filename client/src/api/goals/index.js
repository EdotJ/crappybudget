import request from "@/api/request";

const getGoalBody = (goal) => {
  return {
    name: goal.name,
    description: goal.description ? goal.description : "",
    date: goal.date,
    value: goal.value,
  };
};

export default {
  getAll() {
    return request.get("/goals");
  },
  getSingle(id) {
    return request.get(`/goals/${id}`);
  },
  create(goal) {
    const body = getGoalBody(goal);
    return request.post("/goals", body);
  },
  update(goal) {
    const body = getGoalBody(goal);
    return request.put(`/goals/${goal.id}`, body);
  },
  delete(id) {
    return request.delete(`/goals/${id}`);
  },
};
