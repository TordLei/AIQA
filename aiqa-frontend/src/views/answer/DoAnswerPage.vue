<template>
  <div id="addQuestionPage">
    <a-card>
      <h1>{{ app.appName }}</h1>
      <p>{{ app.appDesc }}</p>
      <h2 style="margin: 30px 0 12px">
        {{ current }}、{{ currentQuestion?.title }}
      </h2>
      <a-radio-group
        direction="vertical"
        v-model="currentAnswer"
        :options="questionOptions"
        @change="doRadioChange"
      />
      <div style="margin-top: 24px">
        <a-space size="large">
          <a-button
            type="primary"
            circle
            v-if="current < questionContent.length"
            :disabled="!currentAnswer"
            @click="current += 1"
          >
            下一题
          </a-button>
          <a-button
            type="primary"
            v-if="current === questionContent.length"
            circle
            :disabled="!currentAnswer"
            @click="doSubmit"
          >
            查看结果
          </a-button>
          <a-button v-if="current > 1" circle @click="current -= 1">
            上一题
          </a-button>
        </a-space>
      </div>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import API from "@/api";
import { useLoginUserStore } from "@/store/userStore";
import message from "@arco-design/web-vue/es/message";
import { useRouter } from "vue-router";
import { getAppVoByIdUsingGet } from "@/api/appController";
import {
  withDefaults,
  defineProps,
  ref,
  watchEffect,
  computed,
  reactive,
} from "vue";

import {
  addQuestionUsingPost,
  editQuestionUsingPost,
  listQuestionVoByPageUsingPost,
} from "@/api/questionController";
import { addUserAnswerUsingPost } from "@/api/userAnswerController";

const router = useRouter();

//应用信息
const app = ref<API.AppVO>({});

//题目列表
const questionContent = ref<API.QuestionContentDTO[]>([]);

//当前题目序号
const current = ref<number>(1);

//当前题目
const currentQuestion = ref<API.QuestionContentDTO>({});

//当前题目的选项
const questionOptions = computed(() => {
  return currentQuestion.value?.options
    ? currentQuestion.value.options.map((option) => {
        return {
          label: `${option.key}. ${option.value}`,
          value: option.key,
        };
      })
    : [];
});

//当前回答
const currentAnswer = ref<string>();

//回答列表
const answerList = reactive<string[]>([]);

//获取传来的参数
interface Props {
  appId: string;
}

const props = withDefaults(defineProps<Props>(), {
  appId: () => "",
});

//获取数据
const loadData = async () => {
  if (!props.appId) {
    return;
  }
  //获取应用信息
  let res: any = await getAppVoByIdUsingGet({
    id: props.appId as any,
  });
  if (res.data.code === 0) {
    app.value = res.data.data;
  } else {
    message.error("获取应用数据失败" + res.data.message);
  }
  //获取题目
  res = await listQuestionVoByPageUsingPost({
    appId: props.appId as any,
    current: 1,
    pageSize: 1,
    sortField: "createTime",
    sortOrder: "descend",
  });
  if (res.data.code === 0 && res.data.data?.records) {
    questionContent.value = res.data.data?.records[0].questionContent;
  } else {
    message.error("获取题目数据失败" + res.data.message);
  }
};

watchEffect(() => {
  loadData();
});

watchEffect(() => {
  currentQuestion.value = questionContent.value[current.value - 1];
  currentAnswer.value = answerList[current.value - 1];
});

//选中选项后，保存选项记录
const doRadioChange = (value: string) => {
  answerList[current.value - 1] = value;
};

//判断后修改应用或新增应用
const doSubmit = async () => {
  if (!props.appId || !answerList) {
    return;
  }
  const res = await addUserAnswerUsingPost({
    appId: props.appId as any,
    choices: answerList,
  });
  if (res.data.code === 0) {
    router.push({
      path: `/answer/result/${res.data.data}`,
      replace: true,
    });
  } else {
    message.error("提交答案失败，" + res.data.message);
  }
};
</script>
