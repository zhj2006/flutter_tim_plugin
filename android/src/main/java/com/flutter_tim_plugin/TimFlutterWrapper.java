package com.flutter_tim_plugin;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMCustomElem;
import com.tencent.imsdk.TIMElem;
import com.tencent.imsdk.TIMFaceElem;
import com.tencent.imsdk.TIMFileElem;
import com.tencent.imsdk.TIMFriendGenderType;
import com.tencent.imsdk.TIMFriendshipManager;
import com.tencent.imsdk.TIMGroupManager;
import com.tencent.imsdk.TIMImageElem;
import com.tencent.imsdk.TIMLocationElem;
import com.tencent.imsdk.TIMLogLevel;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageListener;
import com.tencent.imsdk.TIMSdkConfig;
import com.tencent.imsdk.TIMSnapshot;
import com.tencent.imsdk.TIMSoundElem;
import com.tencent.imsdk.TIMTextElem;
import com.tencent.imsdk.TIMUserConfig;
import com.tencent.imsdk.TIMUserProfile;
import com.tencent.imsdk.TIMUserStatusListener;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.TIMVideo;
import com.tencent.imsdk.TIMVideoElem;
import com.tencent.imsdk.ext.group.TIMGroupMemberResult;
import com.tencent.imsdk.ext.message.TIMMessageLocator;
import com.tencent.imsdk.session.SessionWrapper;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.flutter.plugin.common.JSONUtil;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class TimFlutterWrapper {


    private static Context mContext = null;
    private static MethodChannel mChannel = null;
    private Handler mMainHandler = null;

    private TimFlutterWrapper() {
        mMainHandler = new Handler(Looper.getMainLooper());
    }

    private static class SingletonInstance {
        private static final TimFlutterWrapper INSTANCE = new TimFlutterWrapper();
    }

    public static TimFlutterWrapper getInstance() {
        return SingletonInstance.INSTANCE;
    }


    public void onFlutterMethodCall(MethodCall call, MethodChannel.Result result) {


        if (TimMethodList.MethodKeyInit.equalsIgnoreCase(call.method)) {
            initIM(call.arguments, result);

        } else if (TimMethodList.MethodKeyLogin.equalsIgnoreCase(call.method)) {
            login(call, result);
        } else if (TimMethodList.MethodKeyLogout.equalsIgnoreCase(call.method)) {
            logout(result);
        } else if (TimMethodList.MethodKeySendMessage.equalsIgnoreCase(call.method)) {
            sendMessage(call, result);
        } else if (TimMethodList.MethodKeyDownloadFile.equalsIgnoreCase(call.method)) {

            downloadFile((Map) call.arguments, result, false);


        } else if (TimMethodList.MethodKeyDownloadVideo.equalsIgnoreCase(call.method)) {

            downloadFile((Map) call.arguments, result, true);

        } else if (TimMethodList.MethodKeyGetConversationList.equalsIgnoreCase(call.method)) {


            getConversationList(result);

        } else if (TimMethodList.MethodKeyGetLocalMessage.equalsIgnoreCase(call.method)) {


            getLocalMessage((Map) call.arguments, result);

        } else if (TimMethodList.MethodKeyGetMessage.equalsIgnoreCase(call.method)) {


            getMessage((Map) call.arguments, result);

        } else if (TimMethodList.MethodKeyDeleteConversation.equalsIgnoreCase(call.method)) {


            deleteConversation((Map) call.arguments, result);

        } else if (TimMethodList.MethodKeyCreateGroup.equalsIgnoreCase(call.method)) {


            createGroup((Map) call.arguments, result);

        } else if (TimMethodList.MethodKeyInviteGroupMember.equalsIgnoreCase(call.method)) {


            inviteGroupMember((Map) call.arguments, result);

        } else if (TimMethodList.MethodKeySetReadMessage.equalsIgnoreCase(call.method)) {


            setReadMessage((Map) call.arguments, result);

        } else if (TimMethodList.MethodKeyGetSelfProfile.equalsIgnoreCase(call.method)) {
            getSelfProfile(result);
        } else if (TimMethodList.MethodKeyModifySelfProfile.equalsIgnoreCase(call.method)) {
            modifySelfProfile((HashMap<String, Object>) call.arguments, result);
        } else if (TimMethodList.MethodKeyApplyJoinGroup.equalsIgnoreCase(call.method)) {
            applyJoinGroup((HashMap<String, Object>) call.arguments, result);
        } else if (TimMethodList.MethodKeyRevokeMessage.equalsIgnoreCase(call.method)) {
            revokeMessage((HashMap<String, Object>) call.arguments, result);
        }else if (TimMethodList.MethodKeyGetUserSig.equalsIgnoreCase(call.method)) {
            getUserSig((HashMap<String, Object>) call.arguments, result);
        }


    }

    private void getUserSig(HashMap<String, Object> arguments, MethodChannel.Result result) {
        int appid = (int) arguments.get("appid");
        String userId =  arguments.get("userId").toString();
        int time = (int) arguments.get("time");
        String key =  arguments.get("key").toString();
        String sig = GenerateTestUserSig.genTestUserSig(userId,appid,time,key);
        result.success(sig);
    }

    private void revokeMessage(HashMap<String, Object> arguments, final MethodChannel.Result result) {
        final TIMConversation conversation = getTIMConversationByID(arguments);

        List<TIMMessageLocator> locators = new ArrayList<>();
        TIMMessageLocator locator = new TIMMessageLocator();
        locator.setRand(Long.parseLong(arguments.get("rand").toString()));
        locator.setSelf((boolean) arguments.get("self"));
        locator.setSeq(Long.parseLong(arguments.get("seq").toString()));

        locator.setTimestamp(Long.parseLong(arguments.get("timestamp").toString()));
        setMsgField(locator, "stype", TIMConversationType.values()[(int) arguments.get("conversationType")]);
        setMsgField(locator, "sid", arguments.get("id"));
        locators.add(locator);

        conversation.findMessages(locators, new TIMValueCallBack<List<TIMMessage>>() {
            @Override
            public void onError(int i, String s) {

                result.success(buildResponseMap(i, s));

            }

            @Override
            public void onSuccess(List<TIMMessage> timMessages) {


                if (timMessages != null && timMessages.size() > 0) {


                    conversation.revokeMessage(timMessages.get(0), new TIMCallBack() {
                        @Override
                        public void onError(int i, String s) {
                            result.success(buildResponseMap(i, s));
                        }

                        @Override
                        public void onSuccess() {
                            result.success(buildResponseMap(0, "revokeMessage ok"));
                        }
                    });


                } else {
                    result.success(buildResponseMap(-1, "msg    error"));
                }

            }
        });


    }

    private void applyJoinGroup(HashMap<String, Object> arguments, final MethodChannel.Result result) {
        String groupId = (String) arguments.get("groupId");

        String reason = (String) arguments.get("reason");
        TIMGroupManager.getInstance().applyJoinGroup(groupId, reason, new TIMCallBack() {
            @Override
            public void onError(int code, String desc) {
                result.success(buildResponseMap(code, desc));
            }

            @Override
            public void onSuccess() {
                result.success(buildResponseMap(0, "applyJoinGroup ok"));
            }
        });
    }

    private void modifySelfProfile(HashMap<String, Object> arguments, final MethodChannel.Result result) {




        TIMFriendshipManager.getInstance().modifySelfProfile(arguments, new TIMCallBack() {
            @Override
            public void onError(int code, String desc) {
                result.success(buildResponseMap(code, desc));

            }

            @Override
            public void onSuccess() {
                result.success(buildResponseMap(0, "modifySelfProfile ok"));

            }
        });
    }

    private void getSelfProfile(final MethodChannel.Result result) {
        TIMFriendshipManager.getInstance().getSelfProfile(new TIMValueCallBack<TIMUserProfile>() {
            @Override
            public void onError(int i, String s) {
                result.success(buildResponseMap(i, s));
            }

            @Override
            public void onSuccess(TIMUserProfile timUserProfile) {


                Map map = new HashMap();
                map.put("allowType", timUserProfile.getAllowType());
                map.put("birthday", timUserProfile.getBirthday());
                map.put("faceUrl", timUserProfile.getFaceUrl());
                map.put("gender", timUserProfile.getGender());
                map.put("identifier", timUserProfile.getIdentifier());
                map.put("nickName", timUserProfile.getNickName());
                map.put("location", timUserProfile.getLocation());

                result.success(buildResponseMap(0, map));

            }
        });

    }

    private void setReadMessage(Map arguments, final MethodChannel.Result result) {
        final TIMConversation conversation = getTIMConversationByID(arguments);

        conversation.setReadMessage(null, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                result.success(buildResponseMap(i, s));
            }

            @Override
            public void onSuccess() {

                result.success(buildResponseMap(0, "setReadMessage ok"));
            }
        });
    }

    private void inviteGroupMember(Map arguments, final MethodChannel.Result result) {

        String groupId = (String) arguments.get("groupId");
        List<String> memList = (List<String>) arguments.get("memList");



        //将 list 中的用户加入群组
        TIMGroupManager.getInstance().inviteGroupMember(groupId, memList, new TIMValueCallBack<List<TIMGroupMemberResult>>() {
            @Override
            public void onError(int i, String s) {
                result.success(buildResponseMap(i, s));
            }

            @Override
            public void onSuccess(List<TIMGroupMemberResult> results) {
                List<Map> list = new ArrayList<>();

                for (TIMGroupMemberResult result : results) {
                    Map map = new HashMap();
                    map.put("result", result.getResult());
                    map.put("user", result.getUser());
                    list.add(map);
                }
                result.success(buildResponseMap(0, list));
            }
        });
    }

    private void createGroup(Map arguments, final MethodChannel.Result result) {
        String type = (String) arguments.get("type");
        String name = (String) arguments.get("name");
        //创建公开群，且不自定义群 ID
        TIMGroupManager.CreateGroupParam param = new TIMGroupManager.CreateGroupParam(type, name);
        TIMGroupManager.getInstance().createGroup(param, new TIMValueCallBack<String>() {
            @Override
            public void onError(int i, String s) {
                result.success(buildResponseMap(i, s));
            }

            @Override
            public void onSuccess(String s) {
                result.success(buildResponseMap(0, s));
            }
        });
    }

    private void deleteConversation(Map arguments, MethodChannel.Result result) {
        boolean delMsg = (boolean) arguments.get("delLocalMsg");
        int id = (int) arguments.get("id");
        boolean resultFlag;

        if (delMsg) {
            resultFlag = TIMManager.getInstance().deleteConversationAndLocalMsgs(TIMConversationType.values()[(int) arguments.get("conversationType")], id + "");

        } else {
            resultFlag = TIMManager.getInstance().deleteConversation(TIMConversationType.values()[(int) arguments.get("conversationType")], id + "");
        }

        result.success(buildResponseMap(0, resultFlag));
    }

    private void getMessage(Map map, final MethodChannel.Result result) {

        int count = (int) map.get("count");



        TIMConversation conversation = getTIMConversationByID(map);




        conversation.getMessage(count, null, new TIMValueCallBack<List<TIMMessage>>() {
            @Override
            public void onError(int i, String s) {

                result.success(buildResponseMap(i, s));
            }

            @Override
            public void onSuccess(List<TIMMessage> msgs) {


                //遍历取得的消息
                for (TIMMessage msg : msgs) {

                    //可以通过 timestamp()获得消息的时间戳, isSelf()是否为自己发送的消息
                    Log.e("getMessage", msg.toString());


                }

                result.success(buildResponseMap(0, MessageFactory.getInstance().message2List(msgs)));
            }
        });
    }

    private void getLocalMessage(final Map map, final MethodChannel.Result result) {

        int count = (int) map.get("count");



        TIMConversation conversation = getTIMConversationByID(map);

        conversation.getLocalMessage(count, null, new TIMValueCallBack<List<TIMMessage>>() {
            @Override
            public void onError(int i, String s) {

                result.success(buildResponseMap(i, s));
            }

            @Override
            public void onSuccess(List<TIMMessage> msgs) {


                result.success(buildResponseMap(0, MessageFactory.getInstance().message2List(msgs)));
            }
        });
    }

    private void getConversationList(MethodChannel.Result result) {


        List<TIMConversation> conversationList = TIMManager.getInstance().getConversationList();



        List<Map> list = new ArrayList<>();
        for (TIMConversation timConversation : conversationList) {

            Map map = new HashMap();
            map.put("peer", timConversation.getPeer());
            map.put("conversationType", timConversation.getType().value());
            map.put("unreadMessageNum", timConversation.getUnreadMessageNum());
            list.add(map);

        }
        result.success(buildResponseMap(0, list));
    }

    private void downloadFile(final Map map, final MethodChannel.Result result, final boolean isVideo) {
        TIMConversation conversation = getTIMConversationByID(map);

        List<TIMMessageLocator> locators = new ArrayList<>();

        TIMMessageLocator locator = new TIMMessageLocator();
        locator.setRand(Long.parseLong(map.get("rand").toString()));
        locator.setSelf((boolean) map.get("self"));
        locator.setSeq(Long.parseLong(map.get("seq").toString()));

        locator.setTimestamp(Long.parseLong(map.get("timestamp").toString()));
        setMsgField(locator, "stype", TIMConversationType.values()[(int) map.get("conversationType")]);
        setMsgField(locator, "sid", map.get("id"));
        locators.add(locator);

        conversation.findMessages(locators, new TIMValueCallBack<List<TIMMessage>>() {
            @Override
            public void onError(int i, String s) {

                result.success(buildResponseMap(i, s));
            }

            @Override
            public void onSuccess(List<TIMMessage> timMessages) {


                if (timMessages != null && timMessages.size() > 0) {
                    TIMElem element = timMessages.get(0).getElement(0);
                    if (isVideo) {
                        downloadVideo((TIMVideoElem) element, map, result);

                    } else {
                        downloadFileByType(element, map.get("path").toString(), result);
                    }


                }

            }
        });
    }

    private TIMConversation getTIMConversationByID(Map map) {
        return TIMManager.getInstance().getConversation(
                TIMConversationType.values()[(int) map.get("conversationType")],    //会话类型
                map.get("id").toString());
    }

    private void downloadFileByType(TIMElem elem, String path, final MethodChannel.Result result) {


        TIMCallBack timCallBack = new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                result.success(buildResponseMap(i, s));

            }

            @Override
            public void onSuccess() {
                result.success(buildResponseMap(0, "download ok"));

            }
        };
        if (elem instanceof TIMFileElem) {
            TIMFileElem fileElem = (TIMFileElem) elem;
            fileElem.getToFile(path, timCallBack);
        } else if (elem instanceof TIMSoundElem) {
            TIMSoundElem soundElem = (TIMSoundElem) elem;
            soundElem.getSoundToFile(path, timCallBack);
        } else if (elem instanceof TIMImageElem) {
            TIMImageElem imageElem = (TIMImageElem) elem;
            imageElem.getImageList().get(2).getImage(path, timCallBack);
        }
    }

    private void downloadVideo(final TIMVideoElem elem, final Map map, final MethodChannel.Result result) {
        elem.getVideoInfo().getVideo(map.get("videoPath").toString(), new TIMCallBack() {
            @Override
            public void onError(int i, String s) {

                result.success(buildResponseMap(i, s));
            }

            @Override
            public void onSuccess() {
                elem.getSnapshotInfo().getImage(map.get("snapshotPath").toString(), new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {

                        result.success(buildResponseMap(i, s));
                    }

                    @Override
                    public void onSuccess() {

                        result.success(buildResponseMap(0, "download ok"));
                    }
                });
            }
        });
    }

    private void setMsgField(TIMMessageLocator locator, String key, Object value) {


        try {
            Field field = locator.getClass().getDeclaredField(key);
            field.setAccessible(true);
            // 给变量赋值
            field.set(locator, value);



        } catch (Exception e) {


            e.printStackTrace();
        }
    }

    private void sendMessage(MethodCall call, MethodChannel.Result result) {



        if (call.arguments instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) call.arguments;

            int type = (int) map.get("messageType");


            switch (type) {
                case MessageType.Text:
                    sendTextMessage(map, result);
                    break;
                case MessageType.Image:
                    sendImageMessage(map, result);
                    break;
                case MessageType.Sound:
                    sendSoundMessage(map, result);
                    break;
                case MessageType.Face:
                    sendEmojiMessage(map, result);
                    break;
                case MessageType.Location:
                    sendLocationMessage(map, result);
                    break;
                case MessageType.File:
                    sendFileMessage(map, result);
                    break;
                case MessageType.Custom:
                    sendCustomMessage(map, result);
                    break;
                case MessageType.Video:
                    sendVideoMessage(map, result);
                    break;
            }


        }
    }

    private void sendVideoMessage(Map<String, Object> map, MethodChannel.Result result) {

        Map content = (Map) map.get("content");


        //构造一条消息
        TIMMessage msg = new TIMMessage();

        //构造一个短视频对象
        TIMVideoElem ele = new TIMVideoElem();

        TIMVideo video = new TIMVideo();
        video.setDuaration((int) content.get("duration")); //设置视频时长
        // video.setType("mp4"); // 设置视频文件类型

        TIMSnapshot snapshot = new TIMSnapshot();
        snapshot.setWidth((int) content.get("width")); // 设置视频快照图宽度
        snapshot.setHeight((int) content.get("height")); // 设置视频快照图高度

        ele.setSnapshot(snapshot);
        ele.setVideo(video);
        ele.setSnapshotPath(content.get("imgPath").toString());
        ele.setVideoPath(content.get("videoPath").toString());


        //将 elem 添加到消息
        if (msg.addElement(ele) != 0) {

            return;
        }

        //发送消息

        sendMessageCallBack(MessageType.Location, map, msg, result);
    }

    private void sendCustomMessage(Map<String, Object> map, MethodChannel.Result result) {
        Map content = (Map) map.get("content");
        //构造一条消息
        TIMMessage msg = new TIMMessage();


        //向 TIMMessage 中添加自定义内容
        TIMCustomElem elem = new TIMCustomElem();
        elem.setData((byte[]) content.get("data"));      //自定义 byte[]


        //将 elem 添加到消息
        if (msg.addElement(elem) != 0) {

            return;
        }

        //发送消息

        sendMessageCallBack(MessageType.Custom, map, msg, result);
    }

    private void sendFileMessage(Map<String, Object> map, MethodChannel.Result result) {
        Map content = (Map) map.get("content");
        //构造一条消息
        TIMMessage msg = new TIMMessage();
        //添加文件内容
        TIMFileElem elem = new TIMFileElem();
        elem.setPath(content.get("filePath").toString()); //设置文件路径
        elem.setFileName(content.get("fileName").toString()); //设置消息展示用的文件名称


        //将 elem 添加到消息
        if (msg.addElement(elem) != 0) {

            return;
        }
        sendMessageCallBack(MessageType.Location, map, msg, result);
    }

    private void sendLocationMessage(Map<String, Object> map, MethodChannel.Result result) {

        Map content = (Map) map.get("content");
        //构造一条消息
        TIMMessage msg = new TIMMessage();

        //添加位置信息
        TIMLocationElem elem = new TIMLocationElem();
        elem.setLatitude((double) content.get("latitude"));   //设置纬度
        elem.setLongitude((Double) content.get("longitude"));   //设置经度
        elem.setDesc(content.get("desc").toString());

        //将elem添加到消息
        if (msg.addElement(elem) != 0) {
            return;
        }
        //发送消息

        sendMessageCallBack(MessageType.Location, map, msg, result);
    }

    private void sendEmojiMessage(Map<String, Object> map, MethodChannel.Result result) {

        Map content = (Map) map.get("content");

        //构造一条消息
        TIMMessage msg = new TIMMessage();

        //添加表情
        TIMFaceElem elem = new TIMFaceElem();
        elem.setData((byte[]) content.get("emoji")); //自定义 byte[]
        elem.setIndex((int) content.get("index"));   //自定义表情索引

        //将 elem 添加到消息
        if (msg.addElement(elem) != 0) {

            return;
        }


        //发送消息

        sendMessageCallBack(MessageType.Face, map, msg, result);
    }

    private void sendSoundMessage(Map<String, Object> map, MethodChannel.Result result) {
        Map content = (Map) map.get("content");

        //构造一条消息
        TIMMessage msg = new TIMMessage();

        //添加语音
        TIMSoundElem elem = new TIMSoundElem();
        elem.setPath(content.get("localPath").toString()); //填写语音文件路径
        elem.setDuration(Long.parseLong(content.get("duration").toString()));  //填写语音时长

        //将 elem 添加到消息
        if (msg.addElement(elem) != 0) {

            return;
        }
        //发送消息

        sendMessageCallBack(MessageType.Sound, map, msg, result);
    }

    private void sendImageMessage(Map<String, Object> map, final MethodChannel.Result result) {


        Map content = (Map) map.get("content");

        //构造一条消息
        TIMMessage msg = new TIMMessage();

        //添加图片
        final TIMImageElem elem = new TIMImageElem();
        elem.setPath(content.get("localPath").toString());

        //将 elem 添加到消息
        if (msg.addElement(elem) != 0) {

            return;
        }


        //发送消息

        sendMessageCallBack(MessageType.Image, map, msg, result);

    }

    private void sendTextMessage(Map<String, Object> map, final MethodChannel.Result result) {


        Map content = (Map) map.get("content");

        TIMMessage timMessage = new TIMMessage();
        TIMTextElem elem = new TIMTextElem();
        elem.setText(content.get("text").toString());
        //将elem添加到消息
        if (timMessage.addElement(elem) != 0) {

            return;
        }
        //发送消息
        sendMessageCallBack(MessageType.Text, map, timMessage, result);


    }


    /**
     * 发送消息完成后的回调
     *
     * @param type
     * @param map
     * @param timMessage
     * @param result
     */
    private void sendMessageCallBack(final int type, final Map map, TIMMessage timMessage, final MethodChannel.Result result) {


        TIMConversation conversation = getTIMConversationByID(map);
        conversation.sendMessage(timMessage, new TIMValueCallBack<TIMMessage>() {//发送消息回调
            @Override
            public void onError(int code, String desc) {//发送消息失败
                //错误码 code 和错误描述 desc，可用于定位请求失败原因
                //错误码 code 含义请参见错误码表
                result.success(buildResponseMap(code, desc));

            }

            @Override
            public void onSuccess(TIMMessage msg) {//发送消息成功



                switch (type) {
                    case MessageType.Text:
                    case MessageType.Face:
                    case MessageType.Location:

                        result.success(buildResponseMap(0, MessageFactory.getInstance().basicMessage2String(msg)));
                        break;
                    case MessageType.Image:

                        result.success(buildResponseMap(0, MessageFactory.getInstance().imageMessage2String(msg)));
                        break;
                    case MessageType.Sound:
                        result.success(buildResponseMap(0, MessageFactory.getInstance().soundMessage2String(msg)));

                        break;
                    case MessageType.File:
                        result.success(buildResponseMap(0, MessageFactory.getInstance().fileMessage2String(msg)));

                        break;
                    case MessageType.Video:
                        result.success(buildResponseMap(0, MessageFactory.getInstance().videoMessage2String(msg)));

                        break;
                    case MessageType.Custom:
                        result.success(buildResponseMap(0, MessageFactory.getInstance().customMessage2String(msg)));

                        break;
                }


            }
        });
    }


    private void logout(final MethodChannel.Result result) {

        TIMManager.getInstance().logout(new TIMCallBack() {
            @Override
            public void onError(int code, String desc) {
                result.success(buildResponseMap(code, desc));
            }

            @Override
            public void onSuccess() {
                result.success(buildResponseMap(0, "logout Success"));
            }
        });
    }

    private void login(MethodCall call, final MethodChannel.Result result) {

        final Map<String, Object> map = (Map<String, Object>) call.arguments;


        String userID = map.get("userID").toString();

        String userSig = map.get("userSig").toString();



        // identifier 为用户名，userSig 为用户登录凭证
        TIMManager.getInstance().login(userID, userSig, new TIMCallBack() {
            @Override
            public void onError(int code, String desc) {
                //错误码 code 和错误描述 desc，可用于定位请求失败原因
                //错误码 code 列表请参见错误码表
                result.success(buildResponseMap(code, desc));


            }

            @Override
            public void onSuccess() {


                result.success(buildResponseMap(0, "login Success"));

            }
        });


    }


    private Map<String, Object> buildResponseMap(int codeVal, Object descVal) {
        Map<String, Object> successMap = new HashMap<>();
        successMap.put("code", codeVal);
        successMap.put("data", descVal);
        return successMap;
    }

    /**
     * 初始化
     *
     * @param arguments
     * @param result
     */
    private void initIM(Object arguments, MethodChannel.Result result) {




        //初始化 IM SDK 基本配置
        //判断是否是在主线程
        if (SessionWrapper.isMainProcess(mContext)) {
            TIMSdkConfig config = new TIMSdkConfig((int) arguments)
                    .enableLogPrint(false)
                    .setLogLevel(TIMLogLevel.DEBUG);
            TIMManager.getInstance().addMessageListener(new TIMMessageListener() {
                @Override
                public boolean onNewMessages(List<TIMMessage> list) {
                    TIMMessage timMessage = list.get(0);


                    mChannel.invokeMethod(TimMethodList.MethodCallBackKeyNewMessages, MessageFactory.getInstance().message2List(list));

                    return false;
                }
            });


            TIMManager.getInstance().setUserConfig(buildTIMUserConfig());


            //初始化 SDK
            boolean init = TIMManager.getInstance().init(mContext, config);

            result.success(buildResponseMap(init ? 0 : 1, init));

        }
    }

    private TIMUserConfig buildTIMUserConfig() {
        TIMUserConfig userConfig = new TIMUserConfig();


        userConfig.setUserStatusListener(new TIMUserStatusListener() {
            @Override
            public void onForceOffline() {

                mChannel.invokeMethod(TimMethodList.MethodCallBackKeyUserStatus, 1);
            }

            @Override
            public void onUserSigExpired() {
                mChannel.invokeMethod(TimMethodList.MethodCallBackKeyUserStatus, 2);
            }
        });

        return userConfig;
    }

    public void saveContext(Context context) {
        mContext = context;
    }

    public void saveChannel(MethodChannel channel) {
        mChannel = channel;
    }
}