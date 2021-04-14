<template>
  <div class="root">
    <Paper class="paper">
      <div class="top">
        <h1>Accounts</h1>
        <div class="add-account-button" @click="$router.push('accounts/create')">+</div>
      </div>
      <table v-if="!isLoading">
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
            <td>{{ account.balance || account.balance === 0 ? account.balance.toFixed(2) : "" }}</td>
            <td>
              <div class="actions">
                <DeleteButton class="action" @click.native="deleteAccount(account)" />
                <IconBase
                  class="action"
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
      <div class="loader-container" v-else>
        <Spinner />
      </div>
    </Paper>
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
import EditIcon from "@/components/icons/EditIcon";
import ConfirmationModal from "@/components/ConfirmationModal";
import DeleteButton from "@/components/DeleteButton";
import Paper from "@/components/Paper";
import Spinner from "@/components/Spinner";

export default {
  name: "Accounts",
  components: { Spinner, Paper, DeleteButton, ConfirmationModal, EditIcon, IconBase },
  computed: {
    ...mapState({
      accounts: (state) => state.accounts.accounts,
      isLoading: (state) => state.accounts.isLoading,
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
      getBalance: "entries/getBalance",
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
      this.removeAccount(this.deletingAccount.id).then(() => {
        this.deletingAccount = {
          id: "",
          name: "",
        };
        this.getBalance().then(() => this.toggleConfirmModal());
      });
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

.paper {
  width: 100%;
}

.top {
  display: flex;
  width: 100%;
  align-items: center;
  justify-content: space-between;
}

.loader-container {
  width: 100%;
  height: 50%;
  display: flex;
  justify-content: center;
}

table {
  width: 100%;
  border-collapse: collapse;
  border: 1px solid black;
}

tr:nth-child(2n) {
  background: #dddddd;
}

thead {
  background: var(--accent-main);
  color: var(--foreground-accent);
}

thead td {
  padding: 0.5rem 0;
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
  color: var(--accent-main-darker);
  cursor: pointer;
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
    width: 100%;
  }

  .action {
    width: 48px;
    height: 48px;
    margin: 0 16px;
  }
}
</style>
