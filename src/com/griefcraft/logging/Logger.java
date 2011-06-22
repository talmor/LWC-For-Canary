package com.griefcraft.logging;

import java.io.PrintStream;

public class Logger {
    private String name;

    public static Logger getLogger(String name) {
        return new Logger(name);
    }

    private Logger(String name) {
        this.name = name;
    }

    public void info(String str) {
        log(str);
    }

    public void log(String str) {
        System.out.println(format(str));
    }

    private String format(String msg) {
        return String.format("%s\t[%s]\t%s", new Object[] { this.name, com.griefcraft.LWCInfo.VERSION, msg });
    }
}
