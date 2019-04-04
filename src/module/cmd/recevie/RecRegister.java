package module.cmd.recevie;

import com.basesfs.data.BaseCmd;
import base.Authenticator;
import com.forward.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecRegister extends BaseCmd {

    public String userName = "";
    public String pass = "";
    public int type = Authenticator.AUTH_FW;
    public String dID;
    public String os; // platform
    public String osV;
    public String utmSource = "";
    public String utmCampaign = "";
    public String src = "";

    public RecRegister(ISFSObject data) {
        super(data);
    }

    @Override
    public void unpackData() {
        this.userName = this.data.getText(Params.USER_NAME);
        this.pass = this.data.getText(Params.USER_PASSWORD);
        this.type = this.data.getInt(Params.USER_REGISTER_TYPE);
        this.dID = this.data.getText(Params.DEVICE_ID);
        this.os = this.data.getText(Params.OS);
        this.osV = this.data.getText(Params.OS_VERSION);
        this.utmSource = this.data.getText(Params.UTM_SOURCE);
        this.utmCampaign = this.data.getText(Params.UTM_CAMPAIGN);
        try {
            this.src = this.data.getText(Params.SOURCE);
            if (this.src == null) {
                this.src = "";
            }
        } catch (Exception e) {
            this.src = "";

        }
    }
}
