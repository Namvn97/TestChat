package model;

import com.basesfs.datacontroller.DataControllerException;
import com.basesfs.model.DataModel;
import com.basesfs.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class ChatTestModel extends DataModel {
    public String gId;
    public List<ChatPrivate> chats = new ArrayList();

    public ChatTestModel() {
    }

    public void saveToDB() {
        try {
            super.saveModel(this.gId);
        } catch (DataControllerException var2) {
            var2.printStackTrace();
        }
    }

    public boolean deleteToDB(String gId) {
        try {
            super.deleteModel(gId,ChatTestModel.class);
            return true;
        } catch (DataControllerException var1) {
            var1.printStackTrace();
            return false;
        }
    }

    public static ChatTestModel copyFromDBtoObject(String gid) {
        ChatTestModel model = null;

        try {
            String str = (String) DataModel.getModel(gid, ChatTestModel.class);
            if (str != null) {
                model = (ChatTestModel) Utils.fromJson(str, ChatTestModel.class);
                if (model != null) {
                    ;
                }
            } else {
                model = new ChatTestModel();
                model.gId = gid;
                model.saveToDB();
            }
        } catch (DataControllerException var3) {
            var3.printStackTrace();
        }
        return model;
    }

    public void push(ChatPrivate item) {
        if (this.chats.size() < 30) {
            this.chats.add(item);
        } else {
            this.chats.remove(0);
            this.chats.add(item);
        }

        this.saveToDB();
    }
}
