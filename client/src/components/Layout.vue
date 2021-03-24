<template>
  <div class="top-level">
    <TopBar v-if="isLoggedIn" />
    <SideBar v-if="isLoggedIn" />
    <div class="content">
      <slot></slot>
    </div>
  </div>
</template>

<script>
import { mapGetters } from "vuex";
import TopBar from "@/components/TopBar";
import SideBar from "@/components/SideBar";

export default {
  name: "Layout",
  components: { SideBar, TopBar },
  computed: {
    ...mapGetters({
      isLoggedIn: "auth/isLoggedIn",
    }),
  },
  data() {
    return {
      show: false,
    };
  },
  methods: {
    toggleBurger() {
      this.show = !this.show;
    },
  },
};
</script>

<style scoped>
.top-level {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.content {
  flex-grow: 1;
  overflow-y: scroll;
}

/* Desktop Styles */
@media only screen and (min-width: 961px) {
  .top-level {
    flex-direction: row;
  }

  .content {
    padding: initial;
  }
}
</style>
