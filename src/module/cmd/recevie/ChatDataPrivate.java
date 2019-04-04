package module.cmd.recevie;

import com.basesfs.data.BaseCmd;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class ChatDataPrivate extends BaseCmd {
    public String dataChat;
    public String nameRoom;
    public String gId;

    public ChatDataPrivate(ISFSObject data) {
        super(data);
    }

    @Override
    public void unpackData() {
        this.dataChat = data.getUtfString("message");
        this.nameRoom = data.getText("nameRoom");
        this.gId = data.getText("gId");
    }
}
