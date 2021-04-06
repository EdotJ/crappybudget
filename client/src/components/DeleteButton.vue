<template>
  <IconBase width="24" height="24" view-box="0 0 24 24" class="delete-button">
    <DeleteIcon />
  </IconBase>
</template>

<script>
import IconBase from "@/components/IconBase";
import DeleteIcon from "@/components/icons/DeleteIcon";

export default {
  name: "DeleteButton",
  components: { IconBase, DeleteIcon },
};
</script>

<style scoped>
.delete-button {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-left: 16px;
  border-radius: 0.5rem;
  color: var(--accent-main);
  border: 0;
  font-size: 1.5rem;
  padding: 8px;
  width: 2.5rem;
  height: 2.5rem;
}

.delete-button:hover {
  background: var(--accent-main);
  color: var(--foreground-accent);
}
</style>
