package module.cmd.send;

import com.basesfs.data.BaseMsg;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;
import model.ChatPrivate;
import model.ChatTestModel;

import java.util.Iterator;

public class ResponeHistoryChatPrivate extends BaseMsg {
    public ChatTestModel chatTestModel;
    public String nameRoom;

    public ResponeHistoryChatPrivate() {
        super(2060);
    }

    public void packData() {
        super.packData();
        if (!this.isError()) {
            ISFSArray array = new SFSArray();
            if (this.chatTestModel.chats.size() != 0) {
                Iterator var3 = this.chatTestModel.chats.iterator();
                while (var3.hasNext()) {
                    ChatPrivate item = (ChatPrivate) var3.next();
                    ISFSObject obj = new SFSObject();
                    obj.putUtfString("dName", item.dName);
                    obj.putUtfString("mess", item.mess);
                    array.addSFSObject(obj);
                }
                this.data.putSFSArray("list", array);
            } else {
                this.data.putSFSArray("list", array);
            }
            this.data.putText("nameRoom", nameRoom);
        }
    }
}
