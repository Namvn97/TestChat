package module.cmd.recevie;

import com.basesfs.data.BaseCmd;
import com.forward.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecCheckDname extends BaseCmd {
    public String name;
    public RecCheckDname(ISFSObject data) {
        super(data);
    }

    //Lay phan tich du lieu tu client gui len
    @Override
    public void unpackData() {
        name = data.getText(Params.USER_DISPLAY_NAME).toLowerCase();
    }
}
