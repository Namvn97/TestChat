package module.cmd.recevie;

import com.basesfs.data.BaseCmd;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public class RecChatDataPublic extends BaseCmd {
    public String dataChat;

    public RecChatDataPublic(ISFSObject data) {
        super(data);
    }

    @Override
    public void unpackData() {
        dataChat = data.getUtfString("chatContent");
    }
}
