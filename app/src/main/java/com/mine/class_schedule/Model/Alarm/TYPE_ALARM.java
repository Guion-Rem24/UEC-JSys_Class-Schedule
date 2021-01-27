package com.mine.class_schedule.Model.Alarm;

public class TYPE_ALARM {
    public static final byte ALARM_1 = (byte) 0x0100;
    public static final byte ALARM_2 = (byte) 0x0200;
    public static final byte ALARM_3 = (byte) 0x0300;
    public static final byte ALARM_MASK = (byte) 0x0F00;

    public static byte getAlarm(int num){
        switch(num){
            case 0:
                return ALARM_1;
            case 1:
                return ALARM_2;
            case 2:
                return ALARM_3;
            default:
                throw new IllegalArgumentException();
        }
    }
}
