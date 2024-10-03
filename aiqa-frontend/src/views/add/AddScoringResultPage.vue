<template>
  <div id="addScoringResultPage">
    <h2 style="margin-bottom: 32px">设置评分</h2>
    <a-form :model="form" :style="{ width: '480px' }" @submit="handleSubmit">
      <a-form-item label="应用ID">
        {{ appId }}
      </a-form-item>
      <a-form-item v-if="updateId" label="修改评分ID">
        {{ updateId }}
      </a-form-item>
      <a-form-item field="resultName" label="结果名称">
        <a-input v-model="form.resultName" placeholder="请输入结果名称" />
      </a-form-item>
      <a-form-item field="resultDesc" label="结果描述">
        <a-input v-model="form.resultDesc" placeholder="请输入结果描述" />
      </a-form-item>

      <a-form-item field="resultPicture" label="结果图标">
        <a-input
          v-model="form.resultPicture"
          placeholder="请输入结果图标地址"
        />
      </a-form-item>
      <a-form-item field="resultProp" label="结果集">
        <a-input-tag
          v-model="form.resultProp"
          :style="{ width: '320px' }"
          placeholder="请输入结果集，按回车确认"
          allow-clear
        />
      </a-form-item>
      <a-form-item field="resultScoreRange" label="结果得分范围">
        <a-input-number
          v-model="form.resultScoreRange"
          :style="{ width: '320px' }"
          placeholder="请输入评分结果范围"
        />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 120px">
          提交
        </a-button>
      </a-form-item>
    </a-form>
  </div>
  <h2 style="margin-bottom: 32px">评分管理</h2>
  <ScoringResultTable :appId="appId" :doUpdate="doUpdate" ref="tableRef" />
</template>

<script setup lang="ts">
import API from "@/api";
import { useLoginUserStore } from "@/store/userStore";
import message from "@arco-design/web-vue/es/message";
import { useRouter } from "vue-router";
import {
  addScoringResultUsingPost,
  editScoringResultUsingPost,
  getScoringResultVoByIdUsingGet,
} from "@/api/scoringResultController";
import { APP_SCORING_STRATEGY_MAP, APP_TYPE_MAP } from "@/constant/app";
import { reactive, withDefaults, defineProps, ref, watchEffect } from "vue";

import PictureUploader from "@/App.vue";
import ScoringResultTable from "@/views/add/components/ScoringResultTable.vue";

const loginUserStore = useLoginUserStore();

const router = useRouter();

const updateId = ref<any>();

const tableRef = ref();

//定义提交上传的表单
const initForm = ref({
  resultDesc: "",
  resultPicture: "",
  resultName: "",
} as API.ScoringResultAddRequest);

const form = ref({
  resultDesc: "",
  resultPicture: "",
  resultName: "",
} as API.ScoringResultAddRequest);

//获取传来的参数
interface Props {
  appId: string;
}

const props = withDefaults(defineProps<Props>(), {
  appId: () => "",
});

const doUpdate = (ScoringResult: API.ScoringResultVO) => {
  updateId.value = ScoringResult.id;
  form.value = ScoringResult;
};

// //判断后修改应用或新增应用
const handleSubmit = async () => {
  if (!props.appId) {
    return;
  }
  let res: any;
  if (updateId.value) {
    res = await editScoringResultUsingPost({
      id: updateId as any,
      ...form.value,
    });
  } else {
    res = await addScoringResultUsingPost({
      appId: props.appId as any,
      ...form.value,
    });
  }
  if (res.data.code === 0) {
    message.success("操作成功");
  } else {
    message.error("操作失败，" + res.data.message);
  }
  if (tableRef.value) {
    tableRef.value.loadData();
    updateId.value = undefined;
    form.value = {
      ...initForm.value,
    };
  }
};
</script>
