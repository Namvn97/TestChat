package base.custome;

import com.basesfs.entities.Session;
import com.basesfs.model.SessionModel;
import com.basesfs.utils.Utils;
import com.basesfs.utils.socialcontroller.bean.UserInfo;
import com.basesfs.utils.socialcontroller.business.ISocialController;
import com.basesfs.utils.socialcontroller.exceptions.SocialControllerException;
import base.Authenticator;
import com.forward.entities.Params;
import com.forward.entities.ServerConstant;
import com.forward.manager.UserManager;
import com.forward.model.DisplayNameModel;
import com.forward.model.UserBase;
import com.forward.model.UserModel;
import com.forward.utils.ValidateUtils;

import java.util.List;

public class FW_SocialController implements ISocialController {
    public static final String PREFIX = "FW";

    @Override
    public long getLoggedInUser(String sessionKey)
            throws SocialControllerException {
        return 0;
    }

    @Override
    public UserInfo getUserInfo(String access_token, String filter) throws SocialControllerException {
        access_token = access_token.toLowerCase();
        UserBase userBase = UserBase.copyFromDBtoObject(access_token);
        if (userBase == null) {
            throw new SocialControllerException(ServerConstant.ErrorCode.ERR_USER_NOT_FOUND, "user not found!");
        }

//        UserModel userModel = UserModel.copyFromDBtoObject(userBase.userId);
        UserModel userModel = UserManager.getInstance().getUserModel(userBase.userId);
        if (userModel == null) {
            return null;
        }
//        cheat
//        if ((userModel.id < 10000 && ServerConstant.IS_CHEAT) || userModel.comparePassword(filter)) {
        if (userModel.comparePassword(filter)) {
            // xoa session cu
            Session ss = Session.copyFromDBtoObject(userModel.sessionKey);
            if (ss != null) {
                ss.delete();
            }
            // tao ss moi'
            ss = SessionModel.createSession(String.valueOf(userModel.id));
            userModel.sessionKey = ss.sessionKey;
            userModel.saveToDB();
            UserInfo info = new UserInfo();
            info.setId(userModel.id);
            info.setProperty(ServerConstant.USER_MODEL, userModel);
            return info;
        } else {
            UserManager.getInstance().invalidate(userModel);
            throw new SocialControllerException(ServerConstant.ErrorCode.ERR_WRONG_PASS, "wrong pass!");
        }
    }

    @Override
    public UserInfo getUserInfo(Long userId, String filter) throws SocialControllerException {
        return null;
    }

    @Override
    public List<UserInfo> getUserInfo(List<Long> uids, String filter)
            throws SocialControllerException {
        return null;
    }

    @Override
    public List<Long> getFriendList(String sessionId)
            throws SocialControllerException {
        return null;
    }

    @Override
    public boolean feedOpenApi2(String session_key, int template_bundle_id,
                                String message, String name, String href, String caption,
                                String description, String media_type, String media_src,
                                String media_href) {
        return false;
    }

    @Override
    public UserInfo register(String access_token, String filter) throws SocialControllerException {
        validateRegInfo(access_token, filter);
        String tmpDName = access_token;
        access_token = access_token.toLowerCase();
        UserBase userBase = UserBase.copyFromDBtoObject(access_token);
        DisplayNameModel displayNameModel = DisplayNameModel.copyFromDBtoObject(access_token);
        if (userBase != null || displayNameModel != null) {
            throw new SocialControllerException(ServerConstant.ErrorCode.ERR_USER_EXIST, "");
        }
        UserModel userModel = UserModel.createUserModel(Authenticator.AUTH_FW, access_token, filter);

        UserManager.getInstance().cache(String.valueOf(userModel.id),userModel);
        userModel.isNew= true;
        userModel.displayName = tmpDName;

        DisplayNameModel.create(userModel);

        UserInfo info = new UserInfo();
        info.setId(userModel.id);
        info.setUserId(access_token);
        info.setProperty(ServerConstant.USER_MODEL, userModel);
//        info.setProperty(Params.IS_SAVE_REGISTER, 1);
        return info;
    }

    public void validateRegInfo(String uname, String pass) throws SocialControllerException {
        ValidateUtils.isUsername(uname);
        ValidateUtils.isPassword(pass);
    }
}
