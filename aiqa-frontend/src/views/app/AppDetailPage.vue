<template>
  <div class="appDetailPage">
    <a-card>
      <a-row class="grid-demo" style="margin-bottom: 16px">
        <a-col flex="auto" class="content-wrapper">
          <h2>{{ data.appName }}</h2>
          <p>{{ data.appDesc }}</p>
          <p>应用类型：{{ APP_TYPE_MAP[data.appType] }}</p>
          <p>评分策略：{{ APP_SCORING_STRATEGY_MAP[data.scoringStrategy] }}</p>
          <p>
            <a-space>
              作者：
              <div
                :style="{
                  display: 'flex',
                }"
              >
                <a-avatar
                  :size="24"
                  :image-url="data.user?.userAvatar"
                  :style="{ marginRight: '8px' }"
                />
                <a-typography-text
                  >{{ data.user?.userName ?? "未命名用户" }}
                </a-typography-text>
              </div>
            </a-space>
          </p>

          <p>
            创建时间：{{ dayjs(data.createTime).format("YYYY-MM-DD HH:mm:ss") }}
          </p>
          <a-space size="medium">
            <a-button type="primary" :href="`/answer/do/${id}`">
              开始答题
            </a-button>
            <a-button>分享应用</a-button>
            <a-button v-if="isSelf" :href="`/add/question/${data.id}`">
              设置题目
            </a-button>
            <a-button v-if="isSelf" :href="`/add/scoring_result/${data.id}`">
              设置评分
            </a-button>
            <a-button v-if="isSelf" :href="`/add/app/${data.id}`">
              修改应用
            </a-button>
          </a-space>
        </a-col>
        <a-col flex="320px">
          <a-image width="100%" :src="data.appIcon" />
        </a-col>
      </a-row>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watchEffect } from "vue";
import API from "@/api";
import message from "@arco-design/web-vue/es/message";
import { getAppVoByIdUsingGet } from "@/api/appController";
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
const data = ref<API.AppVO>({});

//加载数据
const loadData = async () => {
  if (!props.id) {
    return;
  }
  const res = await getAppVoByIdUsingGet({
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
#appDetailPage .content-wrapper > * {
  margin-bottom: 24px;
}
</style>
