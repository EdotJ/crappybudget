<template>
  <div class="container">
    <div class="title">Settings</div>
    <Button @click.native="clearRefreshTokens">Clear all refresh tokens</Button>
  </div>
</template>

<script>
import Button from "@/components/Button";
import { mapMutations } from "vuex";

export default {
  name: "Settings",
  components: { Button },
  methods: {
    ...mapMutations({
      setRefreshToken: "auth/SET_REFRESH_TOKEN",
    }),
    clearRefreshTokens() {
      this.$api.auth.clearRefreshTokens().then(() => this.setRefreshToken(""));
    },
  },
};
</script>

<style scoped>
.title {
  font-size: 2rem;
  margin: 1rem 0;
}

.container {
  width: 100%;
  height: 60%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}
</style>
