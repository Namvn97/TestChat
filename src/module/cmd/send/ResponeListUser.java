package module.cmd.send;

import com.basesfs.data.BaseMsg;
import com.forward.manager.UserManager;
import com.forward.model.UserModel;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.util.List;

public class ResponeListUser extends BaseMsg {
    public List<User> users;

    public ResponeListUser() {
        super(2019);
    }

    @Override
    public void packData() {
        super.packData();
        if (isError()) {
            return;
        }
        data.putSFSArray("list_user_in_zone", getListUserForUser());
    }

    public String getDisplayName(User user) {
        UserModel um = UserManager.getInstance().getUserModel(user.getName());
        return um.displayName;
    }

    public long getId(User user) {
        UserModel um = UserManager.getInstance().getUserModel(user.getName());
        return um.id;
    }

    public ISFSArray getListUserForUser() {
        ISFSArray listUser = new SFSArray();
        for (User user : users) {
            ISFSObject object = new SFSObject();
            object.putText("name", getDisplayName(user));
            object.putLong("id", getId(user));
            object.putUtfString("nameID", user.getName());
            listUser.addSFSObject(object);
        }
        return listUser;
    }
}
