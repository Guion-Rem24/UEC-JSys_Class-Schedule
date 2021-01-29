package com.mine.class_schedule.ui.classview;

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
    public static String castToString(byte b){
        return String.format("%16s", Integer.toBinaryString(b)).replace(' ', '0');
    }
    public static byte getPeriod(int p){
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
    public static byte getDay(int d){
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

    public static int getDay(byte posId){
        switch(posId & DAY_MASK){
            case MONDAY:
                return 0;
            case TUESDAY:
                return 1;
            case WEDNESDAY:
                return 2;
            case THURSDAY:
                return 3;
            case FRIDAY:
                return 4;
            case SATURDAY:
                return 5;
            default:
                throw new IllegalArgumentException();
        }
    }
    public static String getDayString(byte posId){
        switch(posId & DAY_MASK){
            case MONDAY:
                return "月曜日";
            case TUESDAY:
                return "火曜日";
            case WEDNESDAY:
                return "水曜日";
            case THURSDAY:
                return "木曜日";
            case FRIDAY:
                return "金曜日";
            case SATURDAY:
                return "土曜日";
            default:
                throw new IllegalArgumentException();
        }
    }

    public static String getPeriodString(byte posId){
        switch(posId & PERIOD_MASK){
            case PERIOD_1:
                return "1限";
            case PERIOD_2:
                return "2限";
            case PERIOD_3:
                return "3限";
            case PERIOD_4:
                return "4限";
            case PERIOD_5:
                return "5限";
            case PERIOD_6:
                return "6限";
            default:
                throw new IllegalArgumentException();
        }
    }


    static final byte DAY_CLEAR_MASK = (byte) 0xF0;
    static final byte PERIOD_CLEAR_MASK = (byte) 0x0F;

    static final byte DAY_MASK = (byte) 0x0F;
    static final byte PERIOD_MASK = (byte) 0xF0;

    public static int[] getClassPos(byte pos_){
        int[] pos = new int[2];

        switch(pos_ & DAY_MASK){
            case MONDAY:
                pos[0] = 0;
                break;
            case TUESDAY:
                pos[0] = 1;
                break;
            case WEDNESDAY:
                pos[0] = 2;
                break;
            case THURSDAY:
                pos[0] = 3;
                break;
            case FRIDAY:
                pos[0] = 4;
                break;
            case SATURDAY:
                pos[0] = 5;
                break;
            default:
                throw new IllegalArgumentException();
        }
        switch(pos_ & PERIOD_MASK){
            case PERIOD_1:
                pos[1] = 0;
                break;
            case PERIOD_2:
                pos[1] = 1;
                break;
            case PERIOD_3:
                pos[1] = 2;
                break;
            case PERIOD_4:
                pos[1] = 3;
                break;
            case PERIOD_5:
                pos[1] = 4;
                break;
            case PERIOD_6:
                pos[1] = 5;
                break;
            default:
                throw new IllegalArgumentException();
        }
        return pos;
    }

    public static int getPeriodStartHour(byte posId){
        switch(posId & PERIOD_MASK){
            case PERIOD_1:
                return 9;
            case PERIOD_2:
                return 10;
            case PERIOD_3:
                return 13;
            case PERIOD_4:
                return 14;
            case PERIOD_5:
                return 22; //test
            case PERIOD_6:
                return 17;
            default:
                throw new IllegalArgumentException();
        }
    }
    public static int getPeriodStartMin(byte posId){
        switch(posId & PERIOD_MASK){
            case PERIOD_1:
            case PERIOD_3:
                return 0;
            case PERIOD_2:
            case PERIOD_4:
                return 40;
            case PERIOD_5:
                return 30; //test
            case PERIOD_6:
                return 45;
            default:
                throw new IllegalArgumentException();
        }
    }

}
