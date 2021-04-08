import request from "@/api/request";

export default {
  getCategoryBreakdown(year, month) {
    return request.get(
      `/statistics/category-breakdown${
        !year && !month ? "" : year ? "?year=" + year + "&month=" + month : "?month=" + month
      }`
    );
  },
  getTopExpenses(year, month) {
    return request.get(
      `/statistics/top-expenses${!year && !month ? "" : year ? "?year=" + year + "&month=" + month : "?month=" + month}`
    );
  },
  getYearlyBreakdown(year) {
    return request.get(`/statistics/yearly-breakdown${year ? "?year=" + year : ""}`);
  },
};
