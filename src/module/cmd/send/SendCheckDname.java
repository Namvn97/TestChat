package module.cmd.send;

import com.basesfs.data.BaseMsg;
import com.forward.entities.CMD;

public class SendCheckDname extends BaseMsg {
    public SendCheckDname() {
        super(CMD.CMD_CHECK_DISPLAY_NAME);
    }

    public SendCheckDname(short errorCode) {
        super(CMD.CMD_CHECK_DISPLAY_NAME, errorCode);
    }

    //Put du lieu vao de gui cho client
    @Override
    public void packData() {
        super.packData();
        if (isError()) {
            return;
        }
    }
}
