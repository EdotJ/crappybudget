<template>
  <div>
    <Spinner v-if="isLoading" />
    <form class="file-form" @submit.prevent="submitFile" v-if="!isLoading && !receipt">
      <div class="drop-container" @drop="handleFileDrop">
        <div class="file-wrapper">
          <div class="file-upload-title">
            <IconBase>
              <Folder />
            </IconBase>
            <span>Click or drag to insert</span>
          </div>
          <input type="file" id="file" name="file-input" @change="handleFileInput" accept="image/*" />
        </div>
      </div>
      <div class="file-title" v-if="file.name">Uploaded file: {{ file.name }}</div>
      <div class="submit-container">
        <AccentedSubmitButton />
      </div>
    </form>
    <ReceiptEntryForm v-on:throw-error="handleError" v-if="!isLoading && receipt" :receipt="receipt" />
  </div>
</template>

<script>
import AccentedSubmitButton from "@/components/AccentedSubmitButton";
import IconBase from "@/components/IconBase";
import Folder from "@/components/icons/Folder";
import Spinner from "@/components/Spinner";
import ReceiptEntryForm from "@/components/ReceiptEntryForm";

export default {
  name: "ReceiptScanForm",
  components: { AccentedSubmitButton, IconBase, Folder, ReceiptEntryForm, Spinner },
  data() {
    return {
      file: {},
      isLoading: false,
      receipt: null,
    };
  },
  methods: {
    submitFile() {
      if (!this.file.name && !this.file.size) {
        this.$emit("throw-error", "Please select an image of a receipt to scan");
      } else {
        this.isLoading = true;
        this.$api.entries
          .scanReceipts(this.file)
          .then((response) => {
            if (response && response.data) {
              this.receipt = response.data;
            }
          })
          .catch((e) => {
            this.$emit(
              "throw-error",
              e && e.response && e.response.data && e.response.data.message
                ? e.response.data.message
                : "Something went wrong!"
            );
          })
          .finally(() => (this.isLoading = false));
      }
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
    setReceipt(newReceipt) {
      this.receipt = newReceipt;
      this.isLoading = false;
    },
    handleError(error) {
      this.$emit("throw-error", error);
      this.isLoading = false;
    },
  },
};
</script>

<style scoped>
form {
  display: flex;
  flex-direction: column;
  margin: 2rem 0;
}

.file-form {
  align-items: flex-start;
}

.submit-container {
  width: 100%;
  display: flex;
  justify-content: flex-end;
}

.submit-container button {
  width: 120px;
  margin-top: 16px;
}

.drop-container {
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
</style>
