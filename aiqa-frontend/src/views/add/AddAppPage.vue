<template>
  <div id="addAppPage">
    <h2 style="margin-bottom: 32px">创建应用</h2>
    <a-form :model="form" :style="{ width: '480px' }" @submit="handleSubmit">
      <a-form-item field="appName" label="应用名称">
        <a-input v-model="form.appName" placeholder="请输入应用名称" />
      </a-form-item>
      <a-form-item field="appDesc" label="应用描述">
        <a-input v-model="form.appDesc" placeholder="请输入应用描述" />
      </a-form-item>
      <!--      <PictureUploader-->
      <!--        :value="form.appIcon"-->
      <!--        :onChange="(value) => form.appIcon == value"-->
      <!--      />-->
      <a-form-item field="appIcon" label="应用图标">
        <a-input v-model="form.appIcon" placeholder="请输入应用图标地址" />
      </a-form-item>
      <a-form-item field="appType" label="应用类型">
        <a-select
          v-model="form.appType"
          :style="{ width: '320px' }"
          placeholder="请选择应用类型"
        >
          <a-option
            v-for="(value, key) of APP_TYPE_MAP"
            :value="Number(key)"
            :label="value"
          />
        </a-select>
      </a-form-item>
      <a-form-item field="scoringStrategy" label="评分策略">
        <a-select
          v-model="form.scoringStrategy"
          :style="{ width: '320px' }"
          placeholder="请选择评分策略"
        >
          <a-option
            v-for="(value, key) of APP_SCORING_STRATEGY_MAP"
            :value="Number(key)"
            :label="value"
          />
        </a-select>
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

const loginUserStore = useLoginUserStore();

const router = useRouter();

const oldApp = ref<API.AppVO>();

//定义提交上传的表单
const form = ref({
  appDesc: "",
  appIcon: "",
  appName: "",
  appType: 0,
  scoringStrategy: 0,
} as API.AppAddRequest);

//获取传来的参数
interface Props {
  id: string;
}

const props = withDefaults(defineProps<Props>(), {
  id: () => "",
});

//获取原来的数据
const loadData = async () => {
  if (!props.id) {
    return;
  }
  const res = await getAppVoByIdUsingGet({
    id: props.id,
  });
  if (res.data.code === 0 && res.data.data) {
    oldApp.value = res.data.data || {};
    form.value = res.data.data;
  } else {
    message.error("获取数据失败" + res.data.message);
  }
};

watchEffect(() => {
  loadData();
});

//判断后修改应用或新增应用
const handleSubmit = async () => {
  let res: any;
  if (props.id) {
    res = await editAppUsingPost({
      id: props.id,
      ...form.value,
    });
  } else {
    res = await addAppUsingPost(form.value);
  }
  if (res.data.code === 0) {
    message.success("操作成功，即将跳转到应用详情页");
    setTimeout(() => {
      router.push({
        path: `/app/detail/${props.id ?? res.data.data}`,
        replace: true,
      });
    }, 3000);
  } else {
    message.error("操作失败，" + res.data.message);
  }
};
</script>
