package module.cmd.send;

import com.basesfs.data.BaseMsg;

public class ResNewMessagePrivate extends BaseMsg {
    public String nameUser;

    public ResNewMessagePrivate() {
        super(2070);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError()) {
            return;
        }
        data.putText("nameUser", nameUser);
    }
}
