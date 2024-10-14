<template>
  <a-card class="appCard" hoverable @click="doCardClick">
    <template #actions>
      <!--      <span class="icon-hover"> <IconThumbUp /> </span>-->
      <span class="icon-hover" @click="doShare"> <IconShareInternal /> </span>
    </template>
    <template #cover>
      <div
        :style="{
          height: '204px',
          overflow: 'hidden',
        }"
      >
        <img
          :style="{
            width: '90%',
            transform: 'translateY(-20px)',
            margin: '10% 5%',
          }"
          :alt="app.appName"
          :src="app.appIcon"
        />
      </div>
    </template>
    <a-card-meta :title="app.appName" :description="app.appDesc">
      <template #avatar>
        <div
          :style="{ display: 'flex', alignItems: 'center', color: '#1D2129' }"
        >
          <a-avatar
            :size="24"
            :image-url="app.user?.userAvatar"
            :style="{ marginRight: '8px' }"
          />
          <a-typography-text
            >{{ app.user?.userName ?? "未命名用户" }}
          </a-typography-text>
        </div>
      </template>
    </a-card-meta>
  </a-card>
  <ShareModel :link="shareLink" title="应用分享" ref="shareModelRef" />
</template>

<script setup lang="ts">
import { IconShareInternal } from "@arco-design/web-vue/es/icon";
import API from "@/api";
import { withDefaults, defineProps, ref } from "vue";
import { useRouter } from "vue-router";
import ShareModel from "@/components/ShareModel.vue";

const router = useRouter();

interface Props {
  app: API.AppVO;
}

const props = withDefaults(defineProps<Props>(), {
  app: () => {
    return {};
  },
});

//接收弹窗组件暴露出来的函数
const shareModelRef = ref();

const shareLink = `${window.location.protocol}://${window.location.host}/app/detail/${props.app.id}`;

const doShare = (e: Event) => {
  if (shareModelRef.value) {
    shareModelRef.value.openModal();
  }
  // 阻止冒泡
  e.stopPropagation();
};

const doCardClick = () => {
  router.push({
    path: `/app/detail/${props.app.id}`,
  });
};
</script>
<style scoped>
.appCard {
  cursor: pointer;
}

.icon-hover {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  transition: all 0.1s;
}

.icon-hover:hover {
  background-color: rgb(var(--gray-2));
}
</style>
