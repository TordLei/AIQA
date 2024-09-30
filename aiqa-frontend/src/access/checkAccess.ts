import accessEnum from "@/access/accessEnum";
import AccessEnum from "@/access/accessEnum";

/**
 * 检查权限
 * @param loginUser 当前用户权限
 * @param needAccess  需要有的权限
 * @return boolean 有无权限
 */
const checkAccess = (
  loginUser: API.LoginUserVO,
  needAccess = accessEnum.NOT_LOGIN
) => {
  //获取当前用户具有的权限，（如果没有loginUser，说明是未登录）
  const loginUserAccess = loginUser?.userRole ?? AccessEnum.NOT_LOGIN;
  //如果不需要登录，直接返回true
  if (needAccess === AccessEnum.NOT_LOGIN) {
    return true;
  }
  //如果需要普通用户权限，但是是未登录状态，返回false
  if (needAccess === AccessEnum.USER) {
    if (loginUserAccess === AccessEnum.NOT_LOGIN) {
      return false;
    }
  }
  //如果需要管理员权限，但是是普通用户或者未登录状态，返回false
  if (needAccess == AccessEnum.ADMIN) {
    if (loginUserAccess !== AccessEnum.ADMIN) {
      return false;
    }
  }
  return true;
};
export default checkAccess;
