package module.cmd.recevie;

import com.basesfs.data.BaseCmd;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecLeaveRoom extends BaseCmd {
    public String idUser;
    public String nameRoomLeave;
    public String gId;
    public RecLeaveRoom(ISFSObject data) {
        super(data);
    }

    @Override
    public void unpackData() {
        this.idUser = data.getText("idName");
        this.nameRoomLeave = data.getText("nameRoom");
        this.gId = data.getText("gId");
    }
}
