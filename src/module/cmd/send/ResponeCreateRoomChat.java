package module.cmd.send;

import com.basesfs.data.BaseMsg;

public class ResponeCreateRoomChat extends BaseMsg {
    public String nameUser1;
    public String nameUser2;
    public String nameRoom;
    public String resposeMessage;
    public String gid;
    public Boolean isOldRoom;


    public ResponeCreateRoomChat() {
        super(2010);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError()) {
            return;
        }
//        this.resposeMessage = nameUser1 + " chat private with " + nameUser2 + " in " + nameRoom;
//        data.putUtfString("message", resposeMessage);
        data.putText("nameRoom", nameRoom);
        data.putBool("isOldRoom", isOldRoom);
        data.putText("gID", gid);
    }
}
