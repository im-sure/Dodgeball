package cloudchen.dodgeball;

import cn.bmob.v3.BmobObject;

public class WorldRecord extends BmobObject {

    private String deviceName;
    private long worldRecord;

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public long getWorldRecord() {
        return worldRecord;
    }

    public void setWorldRecord(long worldRecord) {
        this.worldRecord = worldRecord;
    }
}
