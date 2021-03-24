<template>
  <div id="app">
    <Layout>
      <router-view></router-view>
    </Layout>
  </div>
</template>

<script>
import Layout from "@/components/Layout";

export default {
  name: "App",
  components: { Layout },
};
</script>

<style>
:root {
  --main-bg-color: #dedede;
  --accent-main: #22223b;
  --accent-main-darker: #1b1b2f;
  --accent-main-darker-transparent: #1b1b2faa;
  --accent-main-lighter: #38384f;
  --accent-main-lighter-transparent: #38384fff;
  --main-fg-color: #201b22;
  --foreground-accent: #e9e9eb;
  --error-color: #ffbaba;
  --foreground-error: #b00900;
  --expense-color: #d90000;
  --income-color: #13c200;
  --main-fg-color-transparent: #201b22aa;
}

html {
  box-sizing: border-box;
}

*,
*:before,
*:after {
  box-sizing: inherit;
}

html,
body {
  margin: 0;
  padding: 0;
  background-color: var(--main-bg-color);
  overflow: hidden;
}

#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  width: 100vw;
  height: 100vh;
  font-size: 1rem;
}

/* Tablet Styles */
@media only screen and (min-width: 415px) and (max-width: 960px) {
}
/* Desktop Styles */
@media only screen and (min-width: 961px) {
  #app {
    font-size: 1.25rem;
  }
}
</style>
