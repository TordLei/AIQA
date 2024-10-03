<template>
  <div id="adminUserPage">
    <a-form
      :model="newSearchParams"
      :style="{ margin: '10px auto' }"
      layout="inline"
      @submit="doSearch"
    >
      <a-form-item field="userName" label="用户名">
        <a-input
          allow-clear
          v-model="newSearchParams.userName"
          placeholder="请输入用户名"
        />
      </a-form-item>
      <a-form-item field="userProfile" label="用户简介">
        <a-input
          allow-clear
          v-model="newSearchParams.userProfile"
          placeholder="请输入用户简介"
        />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit">搜索</a-button>
      </a-form-item>
    </a-form>
    <a-table
      :columns="columns"
      :data="dataList"
      :pagination="{
        showTotal: true,
        pageSize: searchParams.pageSize,
        current: searchParams.current,
        total,
      }"
      @page-change="onPageChange"
    >
      <template #userAvatar="{ record }">
        <a-avatar :imageUrl="record.userAvatar"></a-avatar>
      </template>
      <template #createTime="{ record }">
        {{ dayjs(record.createTime).format("YYYY-MM-DD HH:mm:ss") }}
      </template>
      <template #updateTime="{ record }">
        {{ dayjs(record.updateTime).format("YYYY-MM-DD HH:mm:ss") }}
      </template>
      <template #optional="{ record }">
        <a-space>
          <a-button status="danger" @click="doDelete(record)">删除</a-button>
        </a-space>
      </template>
    </a-table>
  </div>
</template>

<script setup lang="ts">
import { ref, watchEffect } from "vue";
import {
  deleteUserUsingPost,
  listUserByPageUsingPost,
} from "@/api/userController";
import API from "@/api";
import message from "@arco-design/web-vue/es/message";
import { dayjs } from "@arco-design/web-vue/es/_utils/date";

//初始分页搜索参数
const initSearchParams = {
  current: 1,
  pageSize: 10,
};

//分页搜索参数
const searchParams = ref<API.UserQueryRequest>({
  ...initSearchParams,
});

//新的分页搜索参数
const newSearchParams = ref<API.UserQueryRequest>({});

//用户列表数据
const dataList = ref<API.User[]>([]);
const total = ref<number>(0);

//加载数据
const loadData = async () => {
  const res = await listUserByPageUsingPost(searchParams.value);
  if (res.data.code === 0) {
    dataList.value = res.data.data?.records || [];
    total.value = res.data.data?.total || 0;
  } else {
    message.error("获取数据失败" + res.data.message);
  }
};

/**
 * 当分页变化时，改变搜索条件，触发数据加载
 * @param page
 */
const onPageChange = (page: number) => {
  searchParams.value = {
    ...searchParams.value,
    current: page,
  };
};

/**
 * 删除用户
 * @param record
 */
const doDelete = async (record: API.User) => {
  const res = await deleteUserUsingPost({ id: record.id });
  if (res.data.code === 0) {
    loadData();
    message.success("删除成功");
  } else {
    message.error("删除失败" + res.data.message);
  }
};

//更新分页搜索参数
const doSearch = () => {
  searchParams.value = {
    ...initSearchParams,
    ...newSearchParams.value,
  };
};

watchEffect(() => {
  loadData();
});

const columns = [
  {
    title: "ID",
    dataIndex: "id",
  },
  {
    title: "账号",
    dataIndex: "userAccount",
  },
  {
    title: "用户名",
    dataIndex: "userName",
  },
  {
    title: "用户头像",
    dataIndex: "userAvatar",
    slotName: "userAvatar",
  },
  {
    title: "用户简介",
    dataIndex: "userProfile",
  },
  {
    title: "权限",
    dataIndex: "userRole",
  },
  {
    title: "创建日期",
    dataIndex: "updateTime",
    slotName: "updateTime",
  },
  {
    title: "更新日期",
    dataIndex: "createTime",
    slotName: "createTime",
  },
  {
    title: "操作",
    slotName: "optional",
  },
];
</script>
