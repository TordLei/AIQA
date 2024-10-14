<template>
  <a-modal v-model:visible="visible" :footer="false" @cancel="closeModal">
    <template #title>
      {{ title }}
    </template>
    <div>
      <h4 style="margin-top: 0">复制分享链接</h4>
      <a-typography-paragraph copyable>{{ link }}</a-typography-paragraph>
      <h4>手机扫描查看</h4>
      <img :src="codeImg" />
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import API from "@/api";
import { withDefaults, defineProps, ref, defineExpose } from "vue";
//@ts-ignore
import QRCode from "qrcode";

interface Props {
  title: string;
  link: string;
}

const props = withDefaults(defineProps<Props>(), {
  title: () => {
    return "分享";
  },
  link: () => {
    return "http://tord.top";
  },
});

const codeImg = ref<string>();

const visible = ref(false);

const openModal = () => {
  visible.value = true;
};
const closeModal = () => {
  visible.value = false;
};

defineExpose({
  openModal,
});

QRCode.toDataURL(props.link)
  .then((url) => {
    console.log(url);
    codeImg.value = url;
  })
  .catch((err) => {
    console.error(err);
  });
</script>
