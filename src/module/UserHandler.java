package module;

import base.Authenticator;
import com.basesfs.BaseExtension;
import com.basesfs.ExtensionBaseClientRequestHandler;
import com.basesfs.entities.Session;
import com.basesfs.model.SessionModel;
import com.basesfs.utils.extension.ExtensionUtility;
import com.basesfs.utils.socialcontroller.bean.UserInfo;
import com.basesfs.utils.socialcontroller.exceptions.SocialControllerException;
import com.fawkes.entities.opt.OtpFactory;
import com.forward.config.DynamicConfigServer;
import com.forward.entities.CMD;
import com.forward.entities.CustomErrorCode;
import com.forward.entities.Params;
import com.forward.entities.ServerConstant;
import com.forward.factory.SmsFactory;
import com.forward.manager.UserManager;
import com.forward.model.DisplayNameModel;
import com.forward.model.GoogleAuthenticatorKeyModel;
import com.forward.model.UserModel;
import com.forward.mysqldb.dbo.RegisterLogDBO;
import com.forward.mysqldb.dbo.SessionLogDBO;
import com.forward.utils.CMDUtils;
import com.forward.utils.UserUtils;
import com.smartfoxserver.bitswarm.sessions.ISession;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSConstants;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.exceptions.SFSErrorData;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.exceptions.SFSLoginException;
import com.smartfoxserver.v2.extensions.ExtensionLogLevel;
import manager.ChatTestManager;
import manager.CreateRoomChatManager;
import manager.LeaveRoomManager;
import model.ChatPrivate;
import module.cmd.recevie.*;
import module.cmd.send.*;
import org.apache.log4j.Logger;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class UserHandler extends ExtensionBaseClientRequestHandler {
    public Logger logger = Logger.getLogger(UserHandler.class);

    public UserHandler(BaseExtension extension) {
        super(extension);
        this.MODULE_ID = Params.Module.MODULE_USER;
        System.err.println(" ============ new UserHandler are created! ============");
    }

    @Override
    protected void doClientRequest(int cmdId, User user, ISFSObject data) {
        switch (cmdId) {
            case CMD.CMD_CHANGE_PASS:
                handleChangePass(user, data);
                break;
            case CMD.CMD_CHECK_DISPLAY_NAME:
                handleCheckDname(user, data);
                break;
            case CMD.CMD_TRANSFER:
//                handleTransfer(user, data);
                break;
            case CMD.CMD_UPDATE_PHONE:
                handleUpdatePhoneNumber(user, data);
                break;
            case CMD.CMD_VERIFY_PHONE:
                handleVerifyPhoneNumber(user, data);
                break;
            case 33000:
                handleRequestChat(user, data);
                break;
            case 44000:
                handelChatListUser(user, data);
                break;
            case 55000:
                handleCreateRoomChat(user, data);
                break;
            case 66000:
                handleMessagePrivate(user, data);
                break;
            case 77000:
                handlerLeaveRoomChat(user, data);
                break;
            case 88000:
                handlerGetHistoryChatPrivate(user, data);
                break;
        }
    }

    private void handlerGetHistoryChatPrivate(User user, ISFSObject data) {
        RecHistoryChatPrivate recHistoryChatPrivate = new RecHistoryChatPrivate(data);
        recHistoryChatPrivate.unpackData();
        ResponeHistoryChatPrivate responeHistoryChatPrivate = new ResponeHistoryChatPrivate();
        responeHistoryChatPrivate.chatTestModel = ChatTestManager.getInstance().getChatTestManager(recHistoryChatPrivate.message).getChatTestModelInManager();
        responeHistoryChatPrivate.nameRoom = recHistoryChatPrivate.nameRoom;
        send(responeHistoryChatPrivate, user);
    }

    private void handlerLeaveRoomChat(User user, ISFSObject data) {
        RecLeaveRoom recLeaveRoom = new RecLeaveRoom(data);
        recLeaveRoom.unpackData();
        ResponeLeaveRoom responeLeaveRoom = new ResponeLeaveRoom();
        responeLeaveRoom.nameUser = recLeaveRoom.idUser;
        responeLeaveRoom.nameRoom = recLeaveRoom.nameRoomLeave;

        LeaveRoomManager leaveRoomManager = new LeaveRoomManager(user, recLeaveRoom.nameRoomLeave, this);
        leaveRoomManager.userLeaveRoom();
        leaveRoomManager.getUser2();

        UserModel um1 = UserManager.getInstance().getUserModel(user.getName());
        UserModel um2 = UserManager.getInstance().getUserModel(leaveRoomManager.getUser2().getName());

        ChatTestManager.getInstance().getChatTestManager(recLeaveRoom.gId).idUserLeaveRoom = String.valueOf(um1.id);
        ChatTestManager.getInstance().getChatTestManager(recLeaveRoom.gId).nameUserLeaveRoom = um1.displayName;
        ChatTestManager.getInstance().getChatTestManager(recLeaveRoom.gId).nameUserInRoom = um2.displayName;

        int checkUser = extension.getParentZone().getRoomByName(responeLeaveRoom.nameRoom).getUserList().size();
        if (checkUser == 1) {
            send(responeLeaveRoom, leaveRoomManager.user2);
        }
    }

    private void handleMessagePrivate(User user, ISFSObject data) {
        ChatDataPrivate chatDataPrivate = new ChatDataPrivate(data);
        chatDataPrivate.unpackData();

        UserModel um = UserManager.getInstance().getUserModel(user.getName());

        ResponePrivateChat responePrivateChat = new ResponePrivateChat();
        responePrivateChat.nameUser1 = um.displayName;
        responePrivateChat.nameRoom = chatDataPrivate.nameRoom;
        responePrivateChat.dataChat = chatDataPrivate.dataChat;
        responePrivateChat.messageRepone(responePrivateChat.nameUser1);

        ChatTestManager.getInstance().getChatTestManager(chatDataPrivate.gId).chatTestModel.push(new ChatPrivate(um.displayName, chatDataPrivate.dataChat));

        List<User> userList = extension.getParentZone().getRoomByName(chatDataPrivate.nameRoom).getUserList();
        send(responePrivateChat, userList);

        if (extension.getParentZone().getRoomByName(chatDataPrivate.nameRoom).getUserList().size() == 1) {
            User userLeaveRoom = ChatTestManager.getInstance().getChatTestManager(chatDataPrivate.gId).getUserLeaveRoom();
            if (userLeaveRoom != null) {
                ResNewMessagePrivate resNewMessagePrivate = new ResNewMessagePrivate();
                resNewMessagePrivate.nameUser = ChatTestManager.getInstance().getChatTestManager(chatDataPrivate.gId).nameUserInRoom;
                send(resNewMessagePrivate, userLeaveRoom);
            }
        }
    }

    private void handleCreateRoomChat(User user, ISFSObject data) {
        RecCreateRoom recCreateRoom = new RecCreateRoom(data);
        recCreateRoom.unpackData();
        recCreateRoom.getUser1ForModel(user.getName());
        User user2 = extension.getParentZone().getUserByName(String.valueOf(recCreateRoom.idUser2));

        ResponeCreateRoomChat responeChat = new ResponeCreateRoomChat();
        responeChat.nameUser1 = recCreateRoom.nameUser1;
        responeChat.nameUser2 = recCreateRoom.nameUser2;

        ChatTestManager.getInstance().setChatTestManager(recCreateRoom.idUser1, recCreateRoom.idUser2, this);
        ChatTestManager.getInstance().setgId();
//        ChatTestManager.getInstance().deleleModelChat();

        CreateRoomChatManager createRoomChatManager = new CreateRoomChatManager(user, user2, recCreateRoom.idUser1, recCreateRoom.idUser2, this);
        createRoomChatManager.setNameRoom();

        responeChat.nameRoom = createRoomChatManager.nameRoom;
        responeChat.isOldRoom = createRoomChatManager.isOldRoom;
        responeChat.gid = ChatTestManager.getInstance().gid;

        List<User> userList = new ArrayList<>();
        userList.add(user);
        userList.add(user2);
        send(responeChat, userList);

        System.out.println("******************");
        System.out.println("List user in " + responeChat.nameRoom + ""
                + extension.getParentZone().getRoomByName(responeChat.nameRoom).getUserList() + "\n");
    }

    private void handelChatListUser(User user, ISFSObject data) {
        ResponeListUser responeListUser = new ResponeListUser();
        responeListUser.users = (List<User>) extension.getParentZone().getUserList();
        responeListUser.users.remove(user);
        send(responeListUser, user);

        ResponeListUserAll responeListUserAll = new ResponeListUserAll();
        responeListUserAll.getDisplayName(user);
        responeListUserAll.getId(user);
        responeListUserAll.nameID = user.getName();
        List<User> listUser;
        listUser = (List<User>) extension.getParentZone().getUserList();
        listUser.remove(user);
        send(responeListUserAll, listUser);
    }

    private void handleRequestChat(User user, ISFSObject data) {
        RecChatDataPublic recChatDataPublic = new RecChatDataPublic(data);
        recChatDataPublic.unpackData();
        ResponeChat responeChat = new ResponeChat();
        responeChat.dataChat = recChatDataPublic.dataChat;
        responeChat.getDisplayNameUser(user);
        responeChat.messageRepone(responeChat.nameUser);
        List<User> listUser;
        listUser = (List<User>) extension.getParentZone().getUserList();
        send(responeChat, listUser);

        System.out.println("***************");
        System.out.println("LIST ROOM : " + extension.getParentZone().getRoomList() + "\n");
    }

    private void handleCheckDname(User user, ISFSObject data) {
        RecCheckDname recCheckDname = new RecCheckDname(data);
        recCheckDname.unpackData();
        SendCheckDname send = null;

        DisplayNameModel model = DisplayNameModel.copyFromDBtoObject(recCheckDname.name);
        if (model != null) {
            send = new SendCheckDname();
        } else {
            send = new SendCheckDname(ServerConstant.ErrorCode.ERR_DISPLAYNAME_INVALID);
        }
        send(send, user);
    }

    private void handleVerifyPhoneNumber(User user, ISFSObject data) {
    }

    private void handleUpdatePhoneNumber(User user, ISFSObject data) {

    }

    private void handleChangePass(User user, ISFSObject data) {

    }

    @Override
    protected void doServerEvent(SFSEventType type, ISFSEvent event) throws SFSException {
        switch (type) {
            case USER_LOGIN:
                handleLogin(event);
                break;
            case USER_LOGOUT:
                handleLogout(event);
                break;
            case USER_DISCONNECT:
                handleDisconnect(event);
                break;
        }
    }

    private void handleDisconnect(ISFSEvent event) {
        try {
            User user = (User) event.getParameter(SFSEventParam.USER);
            this.logger.info("=====================================" +
                    "\nuser disconnect: " + user.getName());
            ISession session = user.getSession();
            SessionLogDBO sessionLogDBO = new SessionLogDBO();
            if (session.getProperty(Params.LOGIN_TIME) != null) {
                sessionLogDBO.logintime = (LocalDateTime) session.getProperty(Params.LOGIN_TIME);
            }
//            sessionLogDBO.logouttime = ServerConstant.ConvertToZoneLocalDateTime(LocalDateTime.now());
            sessionLogDBO.logouttime = LocalDateTime.now(ServerConstant.ZONE_ID);

            sessionLogDBO.uid = Integer.valueOf(user.getName());
            sessionLogDBO.uname = UserManager.getInstance().getUserModel(user.getName()).displayName;
            if (session.getProperty(Params.DEVICE_ID) != null) {
                sessionLogDBO.deviceid = (String) session.getProperty(Params.DEVICE_ID);
            }
            if (session.getProperty(Params.PLATFORM) != null) {
                sessionLogDBO.platform = (String) session.getProperty(Params.PLATFORM);
            }
            sessionLogDBO.ip = session.getFullIpAddress();
            sessionLogDBO.ip = UserUtils.fullIpToShortIp(sessionLogDBO.ip);
//            SessionLogTask.getInstance().add(sessionLogDBO);

            ResponeLeaveZone responeLeaveZone = new ResponeLeaveZone();
            responeLeaveZone.users = (List<User>) extension.getParentZone().getUserList();
            responeLeaveZone.users.remove(user);
            List<User> listUser;
            listUser = (List<User>) extension.getParentZone().getUserList();
            listUser.remove(user);
            send(responeLeaveZone, listUser);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleLogout(ISFSEvent event) {
        try {
            User u = (User) event.getParameter(SFSEventParam.USER);
            if (u == null) {
                return;
            }
            UserModel um = UserManager.getInstance().getUserModel(u.getName());
            if (um == null) {
                return;
            }

            trace("user logout!");
            Session s = SessionModel.getSession(um.sessionKey);
            ExtensionUtility.getInstance().disconnectUser(u);
            if (s != null) {
                s.delete();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void handleLogin(ISFSEvent event) throws SFSException {
        SFSObject data = (SFSObject) event.getParameter(SFSEventParam.LOGIN_IN_DATA);
        int cmdId = data.getInt(Params.CMD_ID);
        ISession session = (ISession) event.getParameter(SFSEventParam.SESSION);
        UserModel um = null;
        switch (cmdId) {
            case CMD.CMD_LOGIN:
                um = handleLogin(data);
                break;
            case CMD.CMD_REGISTER:
                um = handleRegister(data);
                break;
        }

        ISFSObject outData = (ISFSObject) event.getParameter(SFSEventParam.LOGIN_OUT_DATA);
        if (um != null) {
            // check special login
            if (!DynamicConfigServer.instance().IS_LOGIN) {
//                if (!DynamicConfigServer.instance().ACCEPT_IDS.contains(String.valueOf(um.id)) && um.userType != ServerConstant.USER_TYPE_SEEDING) {
                if (um.userType != ServerConstant.USER_TYPE_SEEDING) {
                    SFSErrorData errorData = new SFSErrorData(CustomErrorCode.ERROR_SERVER_MAINTENANCE);
                    throw new SFSLoginException("Server maintenance!", errorData);
                }
            }

            User user = ExtensionUtility.getInstance().getUserById(um.id);
            if (user != null) {
                ExtensionUtility.getInstance().kickUser(user, null, "1", 0);
            }
            outData.putUtfString(SFSConstants.NEW_LOGIN_NAME, String.valueOf(um.id));
            CMDUtils.putUserModel(outData, um);
//            session.setProperty(ServerConstant.USER_MODEL, um);
            if (UserUtils.verifyPlatform(data.getText(Params.OS))) {
                session.setProperty(Params.PLATFORM, data.getText(Params.OS));
            }
            String deviceId = data.getText(Params.DEVICE_ID);
            try {
                ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode(deviceId);
                byte ptext[] = byteBuffer.array();
                deviceId = new String(ptext, StandardCharsets.UTF_8);
            } catch (Exception e) {
                e.printStackTrace();
                deviceId = "NaN-deviceid-login";
            }
            session.setProperty(Params.DEVICE_ID, deviceId);
            session.setProperty(Params.LOGIN_TIME, LocalDateTime.now(ServerConstant.ZONE_ID));
            if (um.isNew) {
                um.isNew = false;
                RegisterLogDBO dbo = new RegisterLogDBO();
                dbo.uid = (int) um.id;
                dbo.registertime = LocalDateTime.now(ServerConstant.ZONE_ID);
                dbo.uname = um.socialId;
                dbo.deviceid = deviceId;
                dbo.ip = session.getFullIpAddress();
                dbo.ip = UserUtils.fullIpToShortIp(dbo.ip);
                dbo.platform = data.getText(Params.OS);
                dbo.logintype = um.authId;
                dbo.utmsource = um.utmSource;
                dbo.utmcampaign = um.utmCampaign;
                dbo.src = um.src;
//                RegisterLogTask.getInstance().add(dbo);
            }

            // check first login in day
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(ServerConstant.DATE_FORMAT);
                LocalDate lastLogin = LocalDate.parse(um.lastLogin, formatter);

                LocalDate now = LocalDate.now(ServerConstant.ZONE_ID);

                um.isFirstLoginDay = now.isAfter(lastLogin);
                if (um.isFirstLoginDay) {
//                    checkLuckySpin(um);
                    um.isFirstRechargeInDay = false;
                }

                um.lastLogin = now.format(formatter);
                um.saveToDB();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void initHandlerClientRequest() {
        this.extension.addRequestHandler(Params.Module.MODULE_USER, this);
    }

    public boolean handlerAddUserBalance(long uid, long balance) {
        if (ServerConstant.IS_CHEAT) {
            return UserManager.getInstance().getUserModel(uid).changeBalance(balance, UserUtils.TransactionType.TRANS_TYPE_TEST_ADD_BALANCE, -1, -1);
        }
        return false;
    }

    @Override
    protected void initHandlerServerEvent() {
        this.extension.addEventHandler(SFSEventType.USER_LOGIN, this);
        this.extension.addEventHandler(SFSEventType.USER_LOGOUT, this);
        this.extension.addEventHandler(SFSEventType.USER_DISCONNECT, this);
        this.extension.addServerHandler(Params.Module.MODULE_USER, this);
    }

    private UserModel handleLogin(ISFSObject data) throws SFSException {
        RecLogin cmd = new RecLogin(data);
        cmd.unpackData();
        UserInfo userInfo;
        SFSErrorData errorData = new SFSErrorData(CustomErrorCode.ERROR_SYSTEM);
        try {
            if (ServerConstant.PRE_MAINTENANCE) {
                throw new SocialControllerException(ServerConstant.ErrorCode.ERR_SERVER_PRE_MAINTENANCE, "Hệ thống đang bảo trì");
            }

            userInfo = Authenticator.doLogin(cmd.uName, cmd.pass, cmd.loginType);
            if (userInfo != null && userInfo.getProperty(ServerConstant.USER_MODEL) != null) {
                UserModel userModel = (UserModel) userInfo.getProperty(ServerConstant.USER_MODEL);

                //check ban
                if (userModel.status == ServerConstant.UserStatus.BANNED) {
                    throw new SocialControllerException(ServerConstant.ErrorCode.ERR_USER_IS_BANNED, userModel.statusDetail);
                }

                // login success
                User sfsUser = ExtensionUtility.getInstance().getUserById(userModel.id);
                if (sfsUser != null) {
                    ExtensionUtility.getInstance().kickUser(sfsUser, null, "2", 0);
                }
                userModel.saveToDB();
                return userModel;
            } else {
                trace(ExtensionLogLevel.ERROR, "error: " + errorData.getCode());
                throw new SFSLoginException("error system!", errorData);
            }
        } catch (SocialControllerException e) {
            switch (e.error) {
                case ServerConstant.ErrorCode.ERR_USER_NOT_FOUND:
                    errorData.setCode(CustomErrorCode.ERR_USER_NOT_FOUND);
                    break;
                case ServerConstant.ErrorCode.ERR_WRONG_PASS:
                    errorData.setCode(CustomErrorCode.ERR_WRONG_PASS);
                    break;
                case ServerConstant.ErrorCode.ERR_SESSION_NOT_LIVE:
                    errorData.setCode(CustomErrorCode.ERR_SESSION_NOT_LIVE);
                    break;
                case ServerConstant.ErrorCode.ERR_USER_IS_BANNED:
                    errorData = new SFSErrorData(CustomErrorCode.ERR_USER_IS_BANNED);
                    break;
                case ServerConstant.ErrorCode.ERR_SERVER_PRE_MAINTENANCE:
                    errorData.setCode(CustomErrorCode.ERROR_SERVER_MAINTENANCE);
                    break;
            }
            trace(ExtensionLogLevel.ERROR, "error: " + errorData.getCode());
            throw new SFSLoginException("error!", errorData);
        } catch (Exception e) {
            e.printStackTrace();
            trace(ExtensionLogLevel.ERROR, "error: " + errorData.getCode());
            throw new SFSLoginException("error system!", errorData);
        }
    }

    private UserModel handleRegister(ISFSObject data) throws SFSException {
        RecRegister rec = new RecRegister(data);
        rec.unpackData();
        SFSErrorData errorData = new SFSErrorData(CustomErrorCode.ERROR_SYSTEM);
        try {
            if (ServerConstant.PRE_MAINTENANCE) {
                throw new SocialControllerException(ServerConstant.ErrorCode.ERR_SERVER_PRE_MAINTENANCE, "Hệ thống đang bảo trì");
            }
            // custom register
            UserInfo userInfo = Authenticator.doRegister(rec.userName, rec.pass, rec.type);
            if (userInfo == null) {
                throw new SFSLoginException("error system!", errorData);
            }
            UserModel userModel = UserManager.getInstance().getUserModel(userInfo.getId());
            userModel.saveToDB();
            userModel.utmSource = rec.utmSource;
            userModel.utmCampaign = rec.utmCampaign;
            userModel.src = rec.src;
            return userModel;

        } catch (SocialControllerException e) {
            e.printStackTrace();
            switch (e.error) {
                case ServerConstant.ErrorCode.ERR_USER_EXIST:
                    errorData.setCode(CustomErrorCode.ERR_USER_EXIST);
                    break;
                case ServerConstant.ErrorCode.ERR_WRONG_PASS:
                    errorData.setCode(CustomErrorCode.ERR_WRONG_PASS);
                    break;
                case ServerConstant.ErrorCode.ERR_INVALID_USERNAME_SPECIAL_CHAR:
                    errorData.setCode(CustomErrorCode.ERR_UNAME_SPECIAL_CHAR);
                    break;
                case ServerConstant.ErrorCode.ERR_PASS_SPECIAL_CHAR:
                    errorData.setCode(CustomErrorCode.ERR_PASS_SPECIAL_CHAR);
                    break;
                case ServerConstant.ErrorCode.ERR_UNAME_LONG:
                    errorData.setCode(CustomErrorCode.ERR_UNAME_LONG);
                    break;
                case ServerConstant.ErrorCode.ERR_PASS_LONG:
                    errorData.setCode(CustomErrorCode.ERR_PASS_LONG);
                    break;
                case ServerConstant.ErrorCode.ERR_SQL:
                    errorData.setCode(CustomErrorCode.ERROR_SYSTEM);
                    break;
                case ServerConstant.ErrorCode.ERR_TO_MANY_ACC_PER_DEVICE:
                    errorData.setCode(CustomErrorCode.ERR_TO_MANY_ACC_PER_DEVICE);
                    break;
                case ServerConstant.ErrorCode.ERR_SERVER_PRE_MAINTENANCE:
                    errorData.setCode(CustomErrorCode.ERROR_SERVER_MAINTENANCE);
                    break;
                case ServerConstant.ErrorCode.ERR_INVALID_USERNAME_TO_SHORT:
                    errorData.setCode(CustomErrorCode.ERR_INVALID_USERNAME_TO_SHORT);
                    break;
                case ServerConstant.ErrorCode.ERR_INVALID_USERNAME_FIRST_CHAR:
                    errorData.setCode(CustomErrorCode.ERR_INVALID_USERNAME_FIRST_CHAR);
                    break;
                case ServerConstant.ErrorCode.ERR_INVALID_PA_TO_SHORT:
                    errorData.setCode(CustomErrorCode.ERR_INVALID_PA_TO_SHORT);
                    break;
                case ServerConstant.ErrorCode.ERR_INVALID_PA_SPECIAL_CHAR:
                    errorData.setCode(CustomErrorCode.ERR_INVALID_PA_SPECIAL_CHAR);
                    break;
                case ServerConstant.ErrorCode.ERR_INVALID_PASSWORD:
                    errorData.setCode(CustomErrorCode.ERR_INVALID_PASSWORD);
                    break;
            }
            logger.info(String.format("Register error | %s | %s", e.error, e.message));
            throw new SFSLoginException("error", errorData);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SFSLoginException("error system!", errorData);
        }
    }
}