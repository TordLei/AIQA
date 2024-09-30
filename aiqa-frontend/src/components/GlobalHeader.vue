<template>
  <a-row class="globalHeader" align="center" :wrap="false">
    <a-col flex="auto">
      <a-menu
        mode="horizontal"
        :selected-keys="selectedKeys"
        @menu-item-click="doMenuClick"
      >
        <a-menu-item
          key="0"
          :style="{ padding: 0, marginRight: '38px' }"
          disabled
        >
          <div class="titleBar">
            <img class="logo" src="../assets/logo.png" />
            <div class="title">智能答题平台</div>
          </div>
        </a-menu-item>
        <a-menu-item v-for="item in visibleRoutes" :key="item.path">
          {{ item.name }}
        </a-menu-item>
      </a-menu>
    </a-col>
    <a-col flex="100px">
      <div v-if="loginUserStore.loginUser.id">
        {{ loginUserStore.loginUser.userName ?? "未命名用户" }}
      </div>
      <div v-else>
        <a-button type="primary" href="/user/login">登录</a-button>
      </div>
    </a-col>
  </a-row>
</template>

<script setup lang="ts">
import { routes } from "@/router/router";
import { useRouter } from "vue-router";
import { computed, ref } from "vue";
import { useLoginUserStore } from "@/store/userStore";
import checkAccess from "@/access/checkAccess";

const router = useRouter();

//获取当前登录用户
const loginUserStore = useLoginUserStore();

//当前选中的菜单项
const selectedKeys = ref(["/"]);

//路由跳转时，自动更新菜单项
router.afterEach((to, from, failure) => {
  selectedKeys.value = [to.path];
});

//展示在菜单栏的路由数组
const visibleRoutes = computed(() => {
  return routes.filter((item) => {
    //隐藏菜单不显示
    if (item.meta?.hideInMenu) {
      return false;
    }
    //没权限访问的菜单不显示
    if (!checkAccess(loginUserStore.loginUser, item.meta?.access as string)) {
      return false;
    }
    return true;
  });
});

//点击菜单跳转到指定页面
const doMenuClick = (key: string) => {
  router.push({ path: key });
};
</script>

<style scoped>
.titleBar {
  display: flex;
  align-items: center;
}

.title {
  margin-left: 16px;
  color: black;
}

.logo {
  width: 48px;
}
</style>
