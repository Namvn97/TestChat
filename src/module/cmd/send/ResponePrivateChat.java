package module.cmd.send;

import com.basesfs.data.BaseMsg;

public class ResponePrivateChat extends BaseMsg {
    public String dataChat;
    public String nameUser1;
    public String nameRoom;

    public ResponePrivateChat() {
        super(2030);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError()) {
            return;
        }
        data.putUtfString("message", dataChat);
        data.putText("nameRoom", nameRoom);
    }

    public void messageRepone(String msg) {
        String message = msg + " : " + dataChat;
        this.dataChat = message;
    }

}
