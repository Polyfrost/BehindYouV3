package org.polyfrost.behindyou.client.compat;

//? if = 1.8.9 {
/*public enum CameraType {
    FIRST_PERSON,
    THIRD_PERSON_BACK,
    THIRD_PERSON_FRONT;

    public int getId() {
        return ordinal();
    }

    public static CameraType fromId(int id) {
        CameraType[] values = values();
        return values[Math.floorMod(id, values.length)];
    }
}
*///?}
