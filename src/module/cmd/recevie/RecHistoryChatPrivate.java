package module.cmd.recevie;

import com.basesfs.data.BaseCmd;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecHistoryChatPrivate extends BaseCmd {
    public String message ;
    public String nameRoom;
    public RecHistoryChatPrivate(ISFSObject data) {
        super(data);
    }

    @Override
    public void unpackData() {
        this.message = data.getText("gId");
        this.nameRoom = data.getText("nameRoom");
    }
}
