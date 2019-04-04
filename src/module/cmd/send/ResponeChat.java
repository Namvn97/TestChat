package module.cmd.send;

import com.basesfs.data.BaseMsg;
import com.forward.manager.UserManager;
import com.forward.model.UserModel;
import com.smartfoxserver.v2.entities.User;

public class ResponeChat extends BaseMsg {
    public String dataChat ;
    public String nameUser;
    public ResponeChat() {
        super(2048);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError()) {
            return;
        }
        data.putUtfString("dataChat", dataChat);
    }

    public void messageRepone(String msg){
        String message = msg + " : " + dataChat;
        this.dataChat = message;
    }

    public String getDisplayNameUser(User user) {
        UserModel um = UserManager.getInstance().getUserModel(user.getName());
        return this.nameUser = um.displayName;
    }
}
