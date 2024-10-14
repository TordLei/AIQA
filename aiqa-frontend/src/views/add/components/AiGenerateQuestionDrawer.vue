<template>
  <a-button type="outline" @click="handleClick">AI生成题目</a-button>
  <a-drawer
    :width="350"
    :visible="visible"
    @ok="handleOk"
    @cancel="handleCancel"
    unmountOnClose
  >
    <template #title>AI生成题目</template>
    <div>
      <a-form
        label-align="left"
        auto-label-width
        :model="form"
        @submit="handleSubmit"
      >
        <a-form-item label="应用ID">
          {{ appId }}
        </a-form-item>
        <a-form-item field="questionNumber" label="题目数量">
          <a-input-number
            :min="1"
            :max="30"
            v-model="form.questionNumber"
            placeholder="请输入题目数量"
          />
        </a-form-item>
        <a-form-item field="optionNumber" label="选项数量">
          <a-input-number
            :min="1"
            :max="6"
            v-model="form.optionNumber"
            placeholder="请输入每题的选项数量"
          />
        </a-form-item>
        <a-form-item>
          <a-space>
            <a-button
              :loading="submitLoading"
              type="primary"
              html-type="submit"
              style="width: 120px"
              @click="handleSubmit"
            >
              一键生成
            </a-button>
            <a-button
              :loading="sseSubmitLoading"
              style="width: 120px"
              @click="handleSseSubmit"
            >
              实时生成
            </a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </div>
  </a-drawer>
</template>

<script setup lang="ts">
import API from "@/api";
import { useRouter } from "vue-router";
import { withDefaults, defineProps, ref, reactive } from "vue";
import { aiGenerateQuestionUsingPost } from "@/api/questionController";
import message from "@arco-design/web-vue/es/message";
import { Message } from "@arco-design/web-vue";

const router = useRouter();

//获取传来的参数
interface Props {
  appId: string;

  onSuccess?: (result: API.QuestionContentDTO[]) => void;
  onSseSuccess?: (result: API.QuestionContentDTO) => void;
  onSseClose?: (event) => void;
  onSseStart?: (event) => void;
}

const props = withDefaults(defineProps<Props>(), {
  appId: () => "",
});

//定义提交上传的表单
const form = reactive({
  questionNumber: 10,
  optionNumber: 2,
} as API.AiGenerateQuestionRequest);

const visible = ref(false);

const submitLoading = ref(false);
const sseSubmitLoading = ref(false);

const handleClick = () => {
  visible.value = true;
};
const handleOk = () => {
  visible.value = false;
};
const handleCancel = () => {
  visible.value = false;
};

const handleSubmit = async () => {
  if (!props.appId) {
    return;
  }
  submitLoading.value = true;
  const res = await aiGenerateQuestionUsingPost({
    appId: props.appId as any,
    ...form,
  });
  console.log(res.data);
  if (res.data.code === 0 && res.data.data.length > 0) {
    if (props.onSuccess) {
      props.onSuccess(res.data.data);
    }
    message.success("生成成功");
  } else {
    message.error("操作失败，" + res.data.message);
  }
  submitLoading.value = false;
};
const handleSseSubmit = async () => {
  if (!props.appId) {
    return;
  }
  sseSubmitLoading.value = true;
  const eventSource = new EventSource(
    "http://localhost:8102/api/question/ai_generate/sse" +
      `?appId=${props.appId}&questionNumber=${form.questionNumber}&optionNumber=${form.optionNumber}`
  );
  let first = true;
  eventSource.onmessage = (event) => {
    if (first) {
      props.onSseStart?.(event);
      handleCancel();
      first = false;
    }
    props.onSseSuccess?.(JSON.parse(event.data));
    console.log(event.data);
  };
  eventSource.onerror = (event) => {
    if (event.eventPhase === EventSource.CLOSED) {
      console.log("关闭连接");
      eventSource.close();
      if (props.onSseClose) {
        props.onSseClose(event);
      }
    }
  };

  sseSubmitLoading.value = false;
};
</script>
