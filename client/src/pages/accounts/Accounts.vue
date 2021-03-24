<template>
  <div class="root">
    <div class="top">
      <h1>Accounts</h1>
      <div class="add-account-button" @click="$router.push('accounts/create')">+</div>
    </div>
    <table>
      <thead>
        <tr>
          <td>Name</td>
          <td>Balance</td>
          <td>Actions</td>
        </tr>
      </thead>
      <tbody>
        <tr v-for="account in accounts" :key="account.id">
          <td>{{ account.name }}</td>
          <td>{{ account.balance && account.balance.toFixed(2) }}</td>
          <td>
            <div class="actions">
              <IconBase class="action delete-action" view-box="0 0 24 24" @click.native="deleteAccount(account)">
                <DeleteIcon />
              </IconBase>
              <IconBase
                class="action edit-action"
                view-box="0 0 24 24"
                @click.native="$router.push(`/accounts/edit/${account.id}`)"
              >
                <EditIcon />
              </IconBase>
            </div>
          </td>
        </tr>
      </tbody>
    </table>
    <ConfirmationModal
      :show="showConfirmationModal"
      v-on:close-modal="toggleConfirmModal"
      v-on:confirmed="submitDelete()"
      entity-name="account"
      :name="deletingAccount.name"
    />
  </div>
</template>

<script>
import { mapActions, mapState } from "vuex";
import IconBase from "@/components/IconBase";
import DeleteIcon from "@/components/icons/DeleteIcon";
import EditIcon from "@/components/icons/EditIcon";
import ConfirmationModal from "@/components/ConfirmationModal";

export default {
  name: "Accounts",
  components: { ConfirmationModal, EditIcon, DeleteIcon, IconBase },
  computed: {
    ...mapState({
      accounts: (state) => state.accounts.accounts,
    }),
  },
  data() {
    return {
      showConfirmationModal: false,
      deletingAccount: {
        id: "",
        name: "",
      },
    };
  },
  methods: {
    ...mapActions({
      getAccounts: "accounts/getAll",
      removeAccount: "accounts/delete",
    }),
    toggleConfirmModal() {
      this.showConfirmationModal = !this.showConfirmationModal;
      if (!this.showConfirmationModal) {
        this.deletingAccount = {
          id: "",
          name: "",
        };
      }
    },
    deleteAccount(account) {
      this.deletingAccount = { id: account.id, name: account.name };
      this.toggleConfirmModal();
    },
    submitDelete() {
      this.removeAccount(this.deletingAccount.id);
      this.deletingAccount = {
        id: "",
        name: "",
      };
      this.toggleConfirmModal();
    },
  },
  mounted() {
    this.getAccounts(true);
  },
};
</script>

<style scoped>
.root {
  padding: 8px;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  flex-direction: column;
}

.top {
  display: flex;
  width: 100%;
  align-items: center;
  justify-content: space-between;
}

table {
  width: 100%;
  border-collapse: collapse;
}

thead {
  background: #cccccc;
}

td {
  text-align: center;
  vertical-align: middle;
}

.actions {
  display: flex;
  width: 100%;
  height: 100%;
  justify-content: center;
  align-items: center;
}

.action {
  width: 40px;
  height: 40px;
  padding: 8px;
  margin: 0 4px;
  border-radius: 8px;
}

.action:hover {
  background: var(--accent-main);
  color: var(--foreground-accent);
}

.add-account-button {
  height: 3rem;
  width: 3rem;
  color: var(--accent-main);
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 3rem;
  border: 2px solid var(--accent-main);
  border-radius: 3rem;
  cursor: pointer;
}

.add-account-button:hover {
  background: var(--accent-main-lighter);
  color: var(--foreground-accent);
  border: 2px solid var(--accent-main-lighter);
}

/* Tablet Styles */
@media only screen and (min-width: 450px) and (max-width: 960px) {
  .root {
    padding: 1rem;
    width: 80%;
  }
}

/* Desktop Styles */
@media only screen and (min-width: 961px) {
  .root {
    padding: 3rem;
    width: 80%;
  }
  .action {
    width: 48px;
    height: 48px;
    margin: 0 16px;
  }
}
</style>
