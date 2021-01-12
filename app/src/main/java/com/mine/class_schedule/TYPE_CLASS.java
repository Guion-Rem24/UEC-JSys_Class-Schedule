package com.mine.class_schedule;

public class TYPE_CLASS {
    static final byte MONDAY = 0x00;
    static final byte TUESDAY = 0x01;
    static final byte WEDNESDAY = 0x02;
    static final byte THURSDAY = 0x03;
    static final byte FRIDAY = 0x04;
    static final byte SATURDAY = 0x05;
    static final byte PERIOD_1 = (byte) 0x10; //(byte)((byte) 0xF0 & (byte)0xFF);
    static final byte PERIOD_2 = (byte) 0x20;
    static final byte PERIOD_3 = (byte) 0x30;
    static final byte PERIOD_4 = (byte) 0x40;
    static final byte PERIOD_5 = (byte) 0x50;
    static final byte PERIOD_6 = (byte) 0x60;
    static String castToString(byte b){
        return String.format("%8s", Integer.toBinaryString(b)).replace(' ', '0');
    }
    static byte getPeriod(int p){
        switch(p){
            case 0:
                return PERIOD_1;
            case 1:
                return PERIOD_2;
            case 2:
                return PERIOD_3;
            case 3:
                return PERIOD_4;
            case 4:
                return PERIOD_5;
            case 5:
                return PERIOD_6;
            default:
                throw new IllegalArgumentException();
        }
    }
    static byte getDay(int d){
        switch(d){
            case 0:
                return MONDAY;
            case 1:
                return TUESDAY;
            case 2:
                return WEDNESDAY;
            case 3:
                return THURSDAY;
            case 4:
                return FRIDAY;
            case 5:
                return SATURDAY;
            default:
                throw new IllegalArgumentException();
        }
    }

    static final byte DAY_CLEAR_MASK = (byte) 0xF0;
    static final byte PERIOD_CLEAR_MASK = (byte) 0x0F;

}
