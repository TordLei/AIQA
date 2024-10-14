<template>
  <div class="AppStatisticPage">
    <h2>热门应用统计</h2>
    <v-charts
      :option="appAnswerCountOption"
      style="width: 100%; height: 500px"
    />
    <h2>应用结果统计</h2>
    <div class="search-bar">
      <a-input-search
        :style="{ width: '320px' }"
        placeholder="输入 appId"
        button-text="搜索"
        search-button
        size="large"
        @search="(value) => loadAppResultCountData(value)"
      />
    </div>
    <v-charts
      :option="appResultCountOption"
      style="width: 100%; height: 500px"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watchEffect } from "vue";
import API from "@/api";
import message from "@arco-design/web-vue/es/message";
import {
  getAppAnswerCountUsingGet,
  getAppResultCountUsingGet,
} from "@/api/appStatisticController";
import VCharts from "vue-echarts";
import "echarts";

//应用使用情况数据
const appAnswerCountList = ref<API.AppAnswerCountDTO[]>([]);
//应用结果数据
const appAnswerResultCountList = ref<API.AppAnswerResultCountDTO[]>([]);

const appAnswerCountOption = computed(() => {
  return {
    xAxis: {
      type: "category",
      data: appAnswerCountList.value.map((item) => item.appId),
      name: "应用 id",
    },
    yAxis: {
      type: "value",
      name: "用户答案数",
    },
    series: [
      {
        data: appAnswerCountList.value.map((item) => item.answerCount),
        type: "bar",
      },
    ],
  };
});

const appResultCountOption = computed(() => {
  return {
    legend: {
      orient: "vertical",
      left: "left",
    },
    series: [
      {
        name: "应用答案结果分布",
        type: "pie",
        radius: "50%",
        data: appAnswerResultCountList.value.map((item) => {
          return { value: item.resultCount, name: item.resultName };
        }),
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: "rgba(0, 0, 0, 0.5)",
          },
        },
      },
    ],
  };
});

//加载数据
const loadAppAnswerCountData = async () => {
  const res = await getAppAnswerCountUsingGet();
  if (res.data.code === 0) {
    appAnswerCountList.value = res.data.data || [];
  } else {
    message.error("获取数据失败" + res.data.message);
  }
};

const loadAppResultCountData = async (appId: string) => {
  if (!appId) return;
  const res = await getAppResultCountUsingGet({ appId: appId as any });
  if (res.data.code === 0) {
    appAnswerResultCountList.value = res.data.data || [];
  } else {
    message.error("获取数据失败" + res.data.message);
  }
};

watchEffect(() => {
  loadAppAnswerCountData();
});
watchEffect(() => {
  loadAppResultCountData("");
});
</script>
