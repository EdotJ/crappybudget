<template>
  <div class="wrapper">
    <Paper class="paper">
      <FormError :value="error" />
      <span class="disclaimer"> (header value count should match filled header names) </span>
      <form @submit.prevent="submit">
        <StyledInput
          type="text"
          required="true"
          :value="formData.nameHeader"
          v-model="formData.nameHeader"
          :vertical="true"
          placeholder="Name Header"
        />
        <StyledInput
          type="text"
          required="true"
          :value="formData.dateHeader"
          v-model="formData.dateHeader"
          :vertical="true"
          placeholder="Date Header"
        />
        <StyledInput
          type="text"
          required="true"
          :value="formData.valueHeader"
          v-model="formData.valueHeader"
          :vertical="true"
          placeholder="Value Header"
        />
        <StyledInput
          type="text"
          :value="formData.categoryHeader"
          v-model="formData.categoryHeader"
          :vertical="true"
          placeholder="Category Header"
        />
        <StyledInput
          type="text"
          :value="formData.descriptionHeader"
          v-model="formData.descriptionHeader"
          :vertical="true"
          placeholder="Description Header"
        />
        <div class="input-group">
          <label for="account">Account <span>*</span></label>
          <AccountSelector id="account" :clearable="false" v-model="formData.accountId" />
        </div>
        <div class="drop-container" @drop="handleFileDrop">
          <div class="file-wrapper">
            <div class="file-upload-title">
              <IconBase>
                <Folder />
              </IconBase>
              <span>Click or drag to insert</span>
            </div>
            <input type="file" id="file" name="file-input" @change="handleFileInput" accept=".csv" />
          </div>
        </div>
        <div class="file-title" v-if="file.name">Uploaded file: {{ file.name }}</div>
        <div class="submit-container">
          <AccentedSubmitButton :isLoading="isLoading" />
        </div>
      </form>
    </Paper>
  </div>
</template>

<script>
import Paper from "@/components/Paper";
import FormError from "@/components/FormError";
import StyledInput from "@/components/StyledInput";
import AccentedSubmitButton from "@/components/AccentedSubmitButton";
import IconBase from "@/components/IconBase";
import Folder from "@/components/icons/Folder";
import { mapActions, mapState } from "vuex";
import AccountSelector from "@/components/AccountSelector";

export default {
  name: "CsvImportForm",
  components: { AccountSelector, Paper, FormError, StyledInput, AccentedSubmitButton, IconBase, Folder },
  data() {
    return {
      file: {},
      error: "",
      formData: {},
      isLoading: false,
    };
  },
  computed: {
    ...mapState({
      accounts: (state) => state.accounts.accounts,
    }),
  },
  methods: {
    ...mapActions({
      getAccounts: "accounts/getAll",
    }),
    submit() {
      if (!this.file.name) {
        this.error = "Please add a file";
      }
      this.isLoading = true;
      this.$api.externalData
        .importCsv(this.formData, this.file)
        .then(() => {
          this.$router.push("/");
        })
        .catch((e) => {
          if (e && e.response && e.response.data && e.response.data.message) {
            this.error = e.response.data.message;
          }
        })
        .finally(() => (this.isLoading = false));
    },
    handleFileDrop(e) {
      if (e.dataTransfer.files[0]) {
        this.file = e.dataTransfer.files[0];
      }
    },
    handleFileInput(e) {
      if (e.target.files[0]) {
        this.file = e.target.files[0];
      }
    },
  },
  mounted() {
    this.getAccounts();
  },
};
</script>

<style scoped>
.wrapper {
  width: 100%;
  display: flex;
  justify-content: center;
}

.paper {
  width: 100%;
}

form {
  width: 100%;
  margin-top: 1rem;
}

.input-group {
  display: flex;
  width: 100%;
  flex-direction: column;
}

.submit-container {
  width: 100%;
  display: flex;
  justify-content: flex-end;
}

.submit-container button {
  width: 150px;
}

.drop-container {
  margin-top: 1rem;
  height: 200px;
  width: 100%;
}

.file-wrapper {
  text-align: center;
  overflow: hidden;
  border: 5px dashed var(--accent-main);
  border-radius: 16px;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  font-family: "Quicksand", sans-serif;
  font-weight: 700;
  color: var(--accent-main);
}

.file-wrapper input {
  position: absolute;
  top: 0;
  right: 0;
  cursor: pointer;
  opacity: 0;
  filter: alpha(opacity=0);
  font-size: 300px;
  height: 200px;
}

.file-upload-title {
  display: flex;
  flex-direction: column;
  align-items: center;
}

svg {
  height: 64px;
  width: 64px;
}

.file-title {
  margin: 16px 0;
  font-family: "Quicksand", sans-serif;
  font-weight: 400;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 100%;
}

.selector {
  background: var(--input-bg-color);
  border-radius: 8px;
}

label span {
  color: red;
}

.disclaimer {
  font-size: 1rem;
  padding: 1rem 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* Desktop Styles */
@media only screen and (min-width: 961px) {
  .paper {
    width: 50%;
    margin-top: 2rem;
  }
}
</style>
