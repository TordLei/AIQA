<template>
  <div id="userLoginPage">
    <h2 style="margin-bottom: 20px">用户登录</h2>
    <a-form
      :model="form"
      :style="{ width: '480px', margin: '0 auto' }"
      @submit="handleSubmit"
    >
      <a-form-item field="userAccount" label="账号">
        <a-input v-model="form.userAccount" placeholder="请输入账号" />
      </a-form-item>
      <a-form-item field="userPassword" label="密码" tooltip="密码不小于八位">
        <a-input-password
          v-model="form.userPassword"
          placeholder="请输入密码"
        />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 120px">
          登录
        </a-button>
        <a-link href="/user/register" style="margin-left: 80px">
          新用户注册
        </a-link>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { reactive } from "vue";
import API from "@/api";
import { userLoginUsingPost } from "@/api/userController";
import { useLoginUserStore } from "@/store/userStore";
import message from "@arco-design/web-vue/es/message";
import { useRouter } from "vue-router";

const loginUserStore = useLoginUserStore();
const router = useRouter();

const form = reactive({
  userAccount: "",
  userPassword: "",
} as API.UserLoginRequest);

const handleSubmit = async () => {
  const res = await userLoginUsingPost(form);
  if (res.data.code === 0) {
    await loginUserStore.fetchLoginUser();
    message.success("登录成功");
    router.push({
      path: "/",
      replace: true,
    });
  } else {
    message.error("登录失败，" + res.data.message);
  }
};
</script>
