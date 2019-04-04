package module.cmd.recevie;

import com.basesfs.data.BaseCmd;
import com.forward.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecLogin extends BaseCmd {
    public String uName;
    public String pass;
    public int loginType;
    public String dId;
    public String os;
    public String osV;

    public RecLogin(ISFSObject data) {
        super(data);
    }

    @Override
    public void unpackData() {
        uName = data.getText(Params.USER_NAME);
        pass = data.getText(Params.USER_PASSWORD);
        loginType = data.getInt(Params.USER_LOGIN_TYPE);
        dId = data.getText(Params.DEVICE_ID);
        os = data.getText(Params.OS);
        osV = data.getText(Params.OS_VERSION);
    }

    @Override
    public String toString() {
        return "user: " + uName + " | login type: " + loginType +" | " + "deviceId : " + dId;
    }
}
