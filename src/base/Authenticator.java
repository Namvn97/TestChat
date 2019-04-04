package base;

import base.custome.FW_SocialController;
import com.basesfs.utils.socialcontroller.bean.UserInfo;
import com.basesfs.utils.socialcontroller.business.ISocialController;
import com.basesfs.utils.socialcontroller.exceptions.SocialControllerException;
import com.forward.entities.ServerConstant;

import java.util.HashMap;
import java.util.Map;

public class Authenticator {
    public static final int AUTH_FW = 0;
    public static final int AUTH_FB = 1;
    public static final int AUTH_QUICK = 2;
    public static Map<Integer, String> prefixList = new HashMap<Integer, String>();

    static {
        prefixList.put(AUTH_FW, ServerConstant.AUTH_FW);
        prefixList.put(AUTH_FB, ServerConstant.AUTH_FB);
        prefixList.put(AUTH_QUICK, ServerConstant.AUTH_QUICK);
    }

    public static UserInfo doLogin(String session, String pass, int loginType) throws Exception {
        UserInfo info = null;
        ISocialController controller = getSocialController(loginType);
        if (controller != null) {
            info = controller.getUserInfo(session, pass);
        }
        return info;
    }

    public synchronized static UserInfo doRegister(String session, String pass, int loginType) throws SocialControllerException {
        ISocialController controller = getSocialController(loginType);
        if (controller != null) {
            return controller.register(session, pass);
        }
        return null;
    }

    public static Integer getLoginType(String prefix) {
        Integer ret = -1;
        for (Map.Entry<Integer, String> entry : prefixList.entrySet()) {
            if (entry.getValue().equals(prefix)) {
                ret = entry.getKey();
                break;
            }
        }
        return ret;
    }

    public static ISocialController getSocialController(int auth_type) {
        ISocialController socialController = null;
        switch (auth_type) {
            case AUTH_FW:
                socialController = new FW_SocialController();
                break;
            case AUTH_FB:
//                socialController = new FB_SocialController();
                break;
            case AUTH_QUICK:
//                socialController = new QP_SocialController();
                break;
        }
        return socialController;
    }
}
