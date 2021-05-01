<template>
  <div class="wrapper">
    <div class="external-data-selection" v-if="!selected">
      <h2>Choose action</h2>
      <Button @click.native="showImportOptions">Import</Button>
      <Button @click.native="exportData">Export</Button>
    </div>
    <div class="import-source-selection" v-if="importing">
      <h2>Choose your data source</h2>
      <div class="buttons">
        <router-link to="/external-data/import/csv">
          <Button>CSV</Button>
        </router-link>
        <router-link to="/external-data/import/ynab">
          <Button>YNAB</Button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script>
import Button from "@/components/Button";

export default {
  name: "ExternalData",
  components: { Button },
  data() {
    return {
      importing: false,
      selected: false,
    };
  },
  methods: {
    exportData() {
      this.$api.externalData.exportData().then((res) => {
        const blob = new Blob([res.data], { type: "text/csv" });
        const link = document.createElement("a");
        link.href = window.URL.createObjectURL(blob);
        link.download = "budget-export.csv";
        link.click();
      });
    },
    showImportOptions() {
      this.importing = true;
      this.selected = true;
    },
  },
};
</script>

<style scoped>
.wrapper {
  height: 100%;
}

.import-source-selection {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 60%;
  flex-direction: column;
}

.external-data-selection {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 60%;
  flex-direction: column;
}

.external-data-selection button {
  margin: 2rem 0;
}

.buttons {
  display: flex;
  justify-content: center;
  width: 100%;
}

.buttons button {
  margin: 0 2rem;
  height: 3rem;
  width: auto;
}

/* Tablet Styles */
@media only screen and (min-width: 415px) and (max-width: 960px) {
}

/* Desktop Styles */
@media only screen and (min-width: 961px) {
  .buttons button {
    margin: 0 8rem;
    height: 6rem;
    width: 10rem;
    font-size: 2rem;
  }
}
</style>
