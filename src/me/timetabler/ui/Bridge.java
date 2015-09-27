package me.timetabler.ui;

import me.util.Log;

/**
 * Created by stuart on 18/09/15.
 */
public class Bridge {
    public void out(String msg) {
        Log.out(msg);
    }

    public void say(int i) {
        Log.out(i);
    }
}
