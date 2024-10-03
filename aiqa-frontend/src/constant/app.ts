export const REVIEW_STATUS_ENUM = {
  //审核中
  REVIEWING: 0,
  //通过
  PASS: 1,
  //拒绝
  REJECT: 2,
};

export const REVIEW_STATUS_MAP = {
  0: "审核中",
  1: "通过",
  2: "拒绝",
};

export const APP_TYPE_ENUM = {
  // 得分类
  SCORE: 0,
  // 测评类
  TEST: 1,
};

export const APP_TYPE_MAP = {
  0: "得分类",
  1: "测评类",
};

export const APP_SCORING_STRATEGY_ENUM = {
  // 自定义
  CUSTOM: 0,
  // AI
  AI: 1,
};

export const APP_SCORING_STRATEGY_MAP = {
  0: "自定义",
  1: "AI",
};
