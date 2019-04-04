package module.cmd.send;

import com.basesfs.data.BaseMsg;
import com.forward.manager.UserManager;
import com.forward.model.UserModel;
import com.smartfoxserver.v2.entities.User;

public class ResponeListUserAll extends BaseMsg {
    public String name ;
    public Long id;
    public String nameID;

    public ResponeListUserAll() {
        super(2040);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError()) {
            return;
        }
        data.putText("name",this.name);
        data.putLong("id",this.id);
        data.putUtfString("nameID",this.nameID);
    }

    public String getDisplayName(User user) {
        UserModel um = UserManager.getInstance().getUserModel(user.getName());
        return this.name = um.displayName;
    }

    public long getId(User user) {
        UserModel um = UserManager.getInstance().getUserModel(user.getName());
        return this.id = um.id;
    }

}
