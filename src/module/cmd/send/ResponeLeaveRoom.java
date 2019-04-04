package module.cmd.send;

import com.basesfs.data.BaseMsg;

public class ResponeLeaveRoom extends BaseMsg {
    public String nameUser;
    public String nameRoom;

    public ResponeLeaveRoom() {
        super(2020);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError()) {
            return;
        }
        String resposeMessage = nameUser + " leave  " +"" + nameRoom;
        data.putUtfString("message", resposeMessage);
        data.putText("nameRoom", nameRoom);
    }
}
