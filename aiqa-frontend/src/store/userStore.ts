import { defineStore } from "pinia";
import { computed, ref } from "vue";
import { getLoginUserUsingGet } from "@/api/userController";
import AccessEnum from "@/access/accessEnum";

export const useLoginUserStore = defineStore("loginUser", () => {
  const loginUser = ref<API.LoginUserVO>({
    userName: "未登录",
  });

  function setLoginUser(newLoginUser: API.LoginUserVO) {
    loginUser.value = newLoginUser;
  }

  async function fetchLoginUser() {
    const res = await getLoginUserUsingGet();
    if (res.data.code == 0 && res.data.data) {
      loginUser.value = res.data.data;
    } else {
      loginUser.value.userRole = AccessEnum.NOT_LOGIN;
    }
  }

  return { loginUser, setLoginUser, fetchLoginUser };
});
