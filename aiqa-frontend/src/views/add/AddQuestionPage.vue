<template>
  <div id="addQuestionPage">
    <h2 style="margin-bottom: 32px">设置题目</h2>
    <a-form
      :model="questionContent"
      :style="{ width: '480px' }"
      label-align="left"
      auto-label-width
      @submit="handleSubmit"
    >
      <a-form-item label="应用ID">
        {{ appId }}
      </a-form-item>
      <a-form-item label="题目列表" :content-flex="false" :merge-props="false">
        <a-button @click="addQuestion(questionContent.length)">
          底部添加题目
        </a-button>
        <div v-for="(item, index) in questionContent" :key="index">
          <!-- 题目 -->
          <a-space>
            <h3>题目{{ index + 1 }}</h3>
            <a-button size="small" @click="addQuestion(index + 1)">
              添加题目
            </a-button>
            <a-button
              size="small"
              status="danger"
              @click="deleteQuestion(index)"
            >
              删除题目
            </a-button>
          </a-space>
          <a-form-item field="posts.post1" :label="`题目${index + 1}标题`">
            <a-input v-model="item.title" placeholder="请输入标题" />
          </a-form-item>
          <!-- 题目选项 -->
          <a-space>
            <h4>题目{{ index + 1 }} 选项列表</h4>
            <a-button
              size="small"
              @click="addQuestionOption(item, item.options.length)"
            >
              底部添加选项
            </a-button>
          </a-space>
          <a-form-item
            v-for="(option, optionIndex) in item.options"
            :key="optionIndex"
            :label="`选项${optionIndex + 1}`"
            :content-flex="false"
            :merge-props="false"
          >
            <a-form-item field="posts.post1" label="选项key">
              <a-input
                v-model="option.key"
                placeholder="请输入选项key"
              ></a-input>
            </a-form-item>
            <a-form-item field="posts.post1" label="选项值">
              <a-input
                v-model="option.value"
                placeholder="请输入选项值"
              ></a-input>
            </a-form-item>
            <a-form-item label="选项结果">
              <a-input v-model="option.result" placeholder="请输入选项结果" />
            </a-form-item>
            <a-form-item label="选项得分">
              <a-input-number
                v-model="option.score"
                placeholder="请输入选项得分"
              />
            </a-form-item>
            <a-space>
              <a-button
                size="mini"
                @click="addQuestionOption(item, optionIndex + 1)"
              >
                添加选项
              </a-button>
              <a-button
                size="mini"
                status="danger"
                @click="deleteQuestionOption(item, optionIndex)"
              >
                删除选项
              </a-button>
            </a-space>
          </a-form-item>
        </div>

        <!--        <a-form-item field="posts.post2" label="Post2">-->
        <!--          <a-input-->
        <!--            v-model="form.posts.post2"-->
        <!--            placeholder="please enter your post..."-->
        <!--          />-->
        <!--        </a-form-item>-->
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 120px">
          提交
        </a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import API from "@/api";
import { useLoginUserStore } from "@/store/userStore";
import message from "@arco-design/web-vue/es/message";
import { useRouter } from "vue-router";
import {
  addAppUsingPost,
  editAppUsingPost,
  getAppVoByIdUsingGet,
} from "@/api/appController";
import { APP_SCORING_STRATEGY_MAP, APP_TYPE_MAP } from "@/constant/app";
import { reactive, withDefaults, defineProps, ref, watchEffect } from "vue";

import PictureUploader from "@/App.vue";
import {
  addQuestionUsingPost,
  editQuestionUsingPost,
  getQuestionVoByIdUsingGet,
  listQuestionVoByPageUsingPost,
} from "@/api/questionController";

const loginUserStore = useLoginUserStore();

const router = useRouter();

const oldQuestion = ref<API.QuestionVO>();

//定义提交上传的题目列表,并定义增加和删除元素的方法
const questionContent = ref<API.QuestionContentDTO[]>([]);
/**
 * 增加题目
 * @param index
 */
const addQuestion = (index: number) => {
  questionContent.value.splice(index, 0, {
    title: "",
    options: [],
  });
};
/**
 * 删除题目
 * @param index
 */
const deleteQuestion = (index) => {
  questionContent.value.splice(index, 1);
};
/**
 * 增加题目选项
 * @param index
 */
const addQuestionOption = (question: API.QuestionContentDTO, index: number) => {
  if (!question.options) {
    question.options = [];
  }
  question.options.splice(index, 0, {
    key: "",
    value: "",
  });
};
/**
 * 删除题目选项
 * @param index
 */
const deleteQuestionOption = (
  question: API.QuestionContentDTO,
  index: number
) => {
  if (!question.options) {
    question.options = [];
  }
  question.options.splice(index, 1);
};

//获取传来的参数
interface Props {
  appId: string;
}

const props = withDefaults(defineProps<Props>(), {
  appId: () => "",
});

//获取原来的数据
const loadData = async () => {
  if (!props.appId) {
    return;
  }
  const res = await listQuestionVoByPageUsingPost({
    appId: props.appId,
    current: 1,
    pageSize: 1,
    sortField: "createTime",
    sortOrder: "descend",
  });
  if (res.data.code === 0 && res.data.data?.records) {
    oldQuestion.value = res.data.data?.records[0];
    if (oldQuestion.value) {
      questionContent.value = oldQuestion.value.questionContent ?? [];
    }
  } else {
    message.error("获取数据失败" + res.data.message);
  }
};

watchEffect(() => {
  loadData();
});

//判断后修改应用或新增应用
const handleSubmit = async () => {
  if (!props.appId || !questionContent.value) {
    return;
  }
  let res: any;
  if (oldQuestion.value?.id) {
    res = await editQuestionUsingPost({
      id: oldQuestion.value.id,
      questionContent: questionContent.value,
    });
  } else {
    res = await addQuestionUsingPost({
      appId: props.appId as any,
      questionContent: questionContent.value,
    });
  }
  if (res.data.code === 0) {
    message.success("操作成功，即将跳转到应用详情页");
    setTimeout(() => {
      router.push({
        path: `/app/detail/${props.appId}`,
        replace: true,
      });
    }, 3000);
  } else {
    message.error("操作失败，" + res.data.message);
  }
};
</script>
