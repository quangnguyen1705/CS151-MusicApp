package sjsu.edu.musicapp;

/**
 * Created by fredericknguyen on 4/22/24.
 */

public class Utility {
    public static String createHMFormatfromM(long _value) {

        String _res, _strMinute, _strSecond = null;
        long _tempsecond, _second,_minute = 0;

        _tempsecond = _value/MILISECOND;
        _second = _tempsecond%MINUTE;
        _minute = _tempsecond/MINUTE;

        if(_minute < 10) {
            _strMinute = "0" + String.valueOf(_minute);
        } else {
            _strMinute = String.valueOf(_minute);
        }

        if(_second < 10) {
            _strSecond = "0" + String.valueOf(_second);
        } else {
            _strSecond = String.valueOf(_second);
        }

        _res = _strMinute + ":" + _strSecond;

        return _res;
    }

    public static final int MILISECOND = 1000;
    public static final int MINUTE = 60;
}
