<template>
  <div class="picker-container">
    <DateRangePicker
      ref="picker"
      opens="right"
      :locale-data="{ firstDay: 1, format: 'yyyy-mm-dd' }"
      :dateRange="value"
      :timePicker="false"
      :showDropdowns="false"
      :ranges="false"
      :closeOnEsc="true"
      :autoApply="true"
      class="desktop-picker"
      v-on:update="emitToParent"
    >
      <template v-slot:input="picker" class="input">
        <span class="dates" v-if="picker.startDate">{{ picker.startDate | date }} - {{ picker.endDate | date }}</span>
        <span class="dates" v-else>Filter by Date</span>
        <IconBase class="icon" view-box="0 0 24 24">
          <Calendar />
        </IconBase>
      </template>
      <div slot="footer" slot-scope="data" class="footer slot">
        <div class="range-text">{{ data.rangeText }}</div>
        <div class="footer-buttons">
          <Button class="button" @click.native="data.clickCancel">Cancel</Button>
          <Button class="button" @click.native="data.clickApply" v-if="!data.in_selection">Apply</Button>
        </div>
      </div>
    </DateRangePicker>
    <DateRangePicker
      ref="picker"
      opens="center"
      :locale-data="{ firstDay: 1, format: 'yyyy-mm-dd' }"
      :dateRange="value"
      :timePicker="false"
      :showDropdowns="false"
      :ranges="false"
      :closeOnEsc="true"
      :autoApply="true"
      class="mobile-picker"
      v-on:update="emitToParent"
    >
      <template v-slot:input="picker" class="input">
        <span class="dates" v-if="picker.startDate">{{ picker.startDate | date }} - {{ picker.endDate | date }}</span>
        <span class="dates" v-else>Filter by Date</span>
        <IconBase class="icon" view-box="0 0 24 24">
          <Calendar />
        </IconBase>
      </template>
      <div slot="footer" slot-scope="data" class="slot">
        <div>{{ data.rangeText }}</div>
        <div class="footer-buttons">
          <Button class="button" @click.native="data.clickCancel">Cancel</Button>
          <Button class="button" @click.native="data.clickApply" v-if="!data.in_selection">Apply</Button>
        </div>
      </div>
    </DateRangePicker>
  </div>
</template>

<script>
import DateRangePicker from "vue2-daterange-picker";
import IconBase from "@/components/IconBase";
import Calendar from "@/components/icons/Calendar";
import Button from "@/components/Button";

export default {
  name: "RangePicker",
  components: { Button, Calendar, IconBase, DateRangePicker },
  props: { value: { type: Object } },
  data() {
    return {
      dateRange: this.value,
    };
  },
  filters: {
    date(val) {
      return val ? val.toLocaleDateString("lt-LT") : "";
    },
  },
  methods: {
    emitToParent(value) {
      this.$emit("applied", value);
    },
  },
};
</script>

<style>
.picker-container {
  width: 80%;
  margin: 0px 0;
}

.daterangepicker {
  --calendar-in-range: #3e3e6d99;
  --calendar-in-range-fg: #ffffff;
}

.daterangepicker .in-range.start-date,
.daterangepicker .in-range.end-date {
  background-color: var(--accent-main) !important;
}

.daterangepicker .in-range.start-date {
  border-radius: 16px 0 0 16px;
}

.daterangepicker .in-range.end-date {
  border-radius: 0 16px 16px 0;
}

.daterangepicker .in-range {
  background-color: var(--calendar-in-range) !important;
  color: var(--calendar-in-range-fg) !important;
}

.vue-daterange-picker .form-control.reportrange-text {
  border-radius: 16px !important;
  background-color: var(--default-selector-bg-color) !important;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 1rem;
}

.dates {
  flex-grow: 1;
}

.desktop-picker {
  display: none !important;
}

.mobile-picker {
  width: 100%;
}

.icon {
  width: 16px;
  height: 16px;
}

.footer-buttons {
  width: 100%;
  margin: 8px 0;
  padding: 0 2rem;
  display: flex;
  justify-content: space-between;
}

/* Desktop Styles */
@media only screen and (min-width: 961px) {
  .desktop-picker {
    display: initial !important;
  }

  .mobile-picker {
    display: none !important;
  }

  .footer {
    display: flex;
    justify-content: flex-end;
    align-items: center;
  }

  .footer-buttons {
    width: unset;
    justify-content: flex-end;
    padding: 0 1rem;
  }

  .button {
    margin: 0 8px;
  }

  .range-text {
    flex-grow: 1;
    display: flex;
    align-items: center;
    justify-content: flex-end;
  }

  .picker-container {
    width: 100%;
  }
}
</style>
