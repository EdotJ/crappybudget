<template>
  <div id="app" @dragover.prevent @drop.prevent>
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
  --accent-main: #303054;
  --accent-main-darker: #22223b;
  --accent-main-darker-transparent: #22223baa;
  --accent-main-lighter: #3e3e6d;
  --accent-main-lighter-transparent: #3e3e6dff;
  --accent-main-lighter-even-lighter: #8785bd;
  --main-fg-color: #201b22;
  --foreground-accent: #fefefe;
  --error-color: #ffbaba;
  --foreground-error: #b00900;
  --expense-color: #d90000;
  --income-color: #13c200;
  --main-fg-color-transparent: #201b22aa;
  --input-bg-color: #22223b33;
  --default-selector-bg-color: #fcfcfc;
  --entry-bg: #fcfcfc;
  --entry-hover: #cccccc;
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

.selector .vs__search::placeholder,
.selector .vs__dropdown-toggle,
.selector .vs__dropdown-menu {
  border: none;
  color: #ffffff;
}

.selector .vs__dropdown-toggle {
  width: 100%;
}

.selector .vs__clear,
.selector .vs__open-indicator {
  fill: var(--accent-main-darker);
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
