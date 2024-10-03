<template>
  <div id="answerResultPage">
    <a-card hoverable :style="{ marginBottom: '20px' }">
      <a-row>
        <a-col flex="auto" class="contentWrapper">
          <h2>{{ data.resultName }}</h2>
          <p>结果描述：{{ data.resultDesc }}</p>
          <p>结果 id：{{ data.resultId }}</p>
          <p>结果得分：{{ data.resultScore }}</p>
          <p>我的答案：{{ data.choices }}</p>
          <p>应用 id：{{ data.appId }}</p>
          <p>应用类型：{{ APP_TYPE_MAP[data.userAnswerType] }}</p>
          <p>评分策略：{{ APP_SCORING_STRATEGY_MAP[data.scoringStrategy] }}</p>
          <div :style="{ display: 'flex', alignItems: 'center' }">
            <span>答题人：</span>
            <a-avatar
              :size="24"
              :style="{ marginRight: '8px' }"
              :image-url="data.user?.userAvatar"
            />
            <a-typography-text>{{ data.user?.userName }}</a-typography-text>
          </div>
          <p>
            答题时间：{{ dayjs(data.createTime).format("YYYY-MM-DD HH:mm:ss") }}
          </p>
          <a-space size="medium">
            <a-button type="primary" href="/"> 返回首页 </a-button>
          </a-space>
        </a-col>
        <a-col flex="320px">
          <a-image width="100%" :src="data?.resultPicture" />
        </a-col>
      </a-row>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watchEffect } from "vue";
import API from "@/api";
import message from "@arco-design/web-vue/es/message";
import { getUserAnswerVoByIdUsingGet } from "@/api/userAnswerController";
import { withDefaults, defineProps } from "vue";
import { APP_SCORING_STRATEGY_MAP, APP_TYPE_MAP } from "../../constant/app";
import { dayjs } from "@arco-design/web-vue/es/_utils/date";
import { useLoginUserStore } from "@/store/userStore";

//获取传参的id
interface Props {
  id: number;
}

const props = withDefaults(defineProps<Props>(), {
  id: () => {
    return 0;
  },
});

//判断是否为本人
const loginUserStore = useLoginUserStore();
const loginUserId = loginUserStore.loginUser.id;

const isSelf = computed(() => {
  return loginUserId && loginUserId === data.value.userId;
});

//应用数据
const data = ref<API.UserAnswerVO>({});

//加载数据
const loadData = async () => {
  if (!props.id) {
    return;
  }
  const res = await getUserAnswerVoByIdUsingGet({
    id: props.id,
  });
  if (res.data.code === 0) {
    data.value = res.data.data || {};
  } else {
    message.error("获取数据失败" + res.data.message);
  }
};

watchEffect(() => {
  loadData();
});
</script>

<style scoped>
#answerResultPage .content-wrapper > * {
  margin-bottom: 24px;
}
</style>
