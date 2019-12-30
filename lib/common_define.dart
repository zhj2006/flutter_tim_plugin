// 会话类型
class TIMConversationType {


  static const int Invalid = 0;
  static const int C2C = 1;
  static const int Group = 2;
  static const int System = 3;
}

//消息类型
class MessageType{
  static const int Invalid = 0;
  static const int Text = 1;

  static const int Image = 2;
  static const int Sound = 3;
  static const int Custom =4;
  static const int File = 5;
  static const int GroupTips = 6;
  static const int Face = 7;
  static const int Location = 8;
  static const int GroupSystem = 9;
  static const int SNSTips = 10;
  static const int ProfileTips = 11;
  static const int Video = 12;

}

//图片类型
class TIMImageType{
  static const int Original = 0;
  static const int Thumb = 1;
  static const int Large = 1;
}

//消息发送状态
class TIMSentStatus {
  static const int Sending = 10;//发送中
  static const int Failed = 20;//发送失败
  static const int Sent = 30;//发送成功
}

//消息方向
class TIMMessageDirection {
  static const int Send = 1;
  static const int Receive = 2;
}

//消息接收状态
class TIMReceivedStatus {
  static const int Unread = 0;//未读
  static const int Read = 1;//已读
  static const int Listened = 2;//已听，语音消息
  static const int Downloaded = 4;//已下载
  static const int Retrieved = 8;//已经被其他登录的多端收取过
  static const int MultipleReceive = 16;//被多端同时收取
}

//回调状态
class TIMOperationStatus {
  static const int Success = 0;
  static const int Failed = 1;
}

//消息免打扰状态
class TIMConversationNotificationStatus {
  static const int DoNotDisturb = 0;//免打扰
  static const int Notify = 1;//新消息通知
}

//聊天室成员顺序
class TIMChatRoomMemberOrder {
  static const int Asc = 1;//升序，最早加入
  static const int Desc = 2;//降序，最晚加入
}

//用户黑名单状态
class TIMBlackListStatus {
  static const int In = 0;//在黑名单中
  static const int NotIn = 1;//不在黑名单中
}

class TIMConnectionStatus {
  static const int Connected = 0;//连接成功
  static const int Connecting = 1;//连接中
  static const int KickedByOtheTIMlient = 2;//该账号在其他设备登录，导致当前设备掉线
  static const int NetworkUnavailable = 3;//网络不可用
  static const int TokenIncorrect = 4;//token 非法，此时无法连接 im，需重新获取 token
  static const int UserBlocked = 5;//用户被封禁
}

class TIMUserProfile{
   static final String TIM_PROFILE_TYPE_KEY_NICK = "Tag_Profile_IM_Nick";
   static final String TIM_PROFILE_TYPE_KEY_FACEURL = "Tag_Profile_IM_Image";
   static final String TIM_PROFILE_TYPE_KEY_ALLOWTYPE = "Tag_Profile_IM_AllowType";
   static final String TIM_PROFILE_TYPE_KEY_GENDER = "Tag_Profile_IM_Gender";
   static final String TIM_PROFILE_TYPE_KEY_BIRTHDAY = "Tag_Profile_IM_BirthDay";
   static final String TIM_PROFILE_TYPE_KEY_LOCATION = "Tag_Profile_IM_Location";
   static final String TIM_PROFILE_TYPE_KEY_LANGUAGE = "Tag_Profile_IM_Language";
   static final String TIM_PROFILE_TYPE_KEY_LEVEL = "Tag_Profile_IM_Level";
   static final String TIM_PROFILE_TYPE_KEY_ROLE = "Tag_Profile_IM_Role";
   static final String TIM_PROFILE_TYPE_KEY_SELFSIGNATURE = "Tag_Profile_IM_SelfSignature";
   static final String TIM_PROFILE_TYPE_KEY_CUSTOM_PREFIX = "Tag_Profile_Custom_";
}

class TIMFriendGenderType{
   static final int GENDER_UNKNOWN = 0;
   static final int GENDER_MALE = 1;
   static final int GENDER_FEMALE = 2;
}

///错误码
///
///iOS 参考 [TIMStatusDefine.h] 的枚举 [TIMConnectErroTIMode] 和 [TIMErroTIMode]
///Android 参考 [RongIMClient.java] 的枚举 [ErroTIMode]
class TIMErroTIMode {
  ///成功
  static const int Success = 0;

  ///已被对方加入黑名单
  static const int RejectedByBlackList = 405;

  ///发送消息频率过高，1秒最多允许发送5条消息
  static const int SendMsgOverfrequency = 20604;

  ///不在该群组中
  static const int NotInGroup = 22406;

  ///在群组中被禁言
  static const int ForbiddenInGroup = 22408;

  ///不在该聊天室中
  static const int NotInChatRoom = 23406;

  ///在聊天室中被禁言
  static const int ForbiddenInChatRoom = 23408;

  ///AppKey 错误
  static const int AppKeyError = 31002;

  ///token 无效，需要获取新的 token 连接 IM
  ///一般有已下两种原因
  ///一是token错误，请您检查客户端初始化使用的AppKey和您服务器获取token使用的AppKey是否一致；
  ///二是token过期，是因为您在开发者后台设置了token过期时间，您需要请求您的服务器重新获取token并再次用新的token建立连接。
  static const int TokenIncorrect = 31004;

  /// AppKey 与 Token 不匹配，需要获取新的 token 连接 IM
  /// 原因同 [TokenIncorrect]
  static const int NotAuthrorized = 31005;

  /// AppKey 被封禁或者已删除，请检查 AppKey 是否正确
  static const int AppBlockedOrDeleted = 31008;

  /// 用户被封禁，请检查 Token 是否正确以及对应的 UserId 是否被封禁
  static const int UserBlocked = 31009;

  /// 被其他端踢掉线
  static const int KickByOtheTIMlient = 31010;

  /// SDK 没有初始化，使用任何 SDK 接口前必须先调用 init 接口
  static const int ClientNotInit = 33001;

  /// 非法参数，请检查调用接口传入的参数
  static const int InvalidParameter = 33003;

  /// 历史消息云存储功能未开通
  static const int RoamingServiceUnAvailable = 33007;

  /// 小视频消息时长超限，最长 10s
  static const int SightMessageDurationLimitExceed = 34002;
}