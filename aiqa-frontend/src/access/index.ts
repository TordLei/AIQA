import router from "@/router";
import { useLoginUserStore } from "@/store/userStore";
import AccessEnum from "@/access/accessEnum";
import checkAccess from "@/access/checkAccess";

router.beforeEach(async (to, from, next) => {
  //获取当前登录的用户权限
  const loginUserStore = useLoginUserStore();
  let loginUser = loginUserStore.loginUser;

  if (!loginUser || !loginUser.userRole) {
    await loginUserStore.fetchLoginUser();
    loginUser = loginUserStore.loginUser;
  }

  //当前页面需要的权限
  const needAccess = (to.meta?.access as string) ?? AccessEnum.NOT_LOGIN;

  //如果界面需要权限验证
  if (needAccess !== AccessEnum.NOT_LOGIN) {
    //如果未登录，跳转到登录界面
    if (
      !loginUser ||
      !loginUser.userRole ||
      loginUser.userRole === AccessEnum.NOT_LOGIN
    ) {
      next(`/user/login?redirect=${to.fullPath}`);
    }
    //如果登录了但是权限不够，跳转到无权限界面
    if (!checkAccess(loginUser, needAccess)) {
      next("/noAuth");
      return;
    }
  }
  next();
});
