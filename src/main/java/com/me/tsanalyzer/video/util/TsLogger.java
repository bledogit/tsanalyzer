/*
 * Transport Stream Analyzer 
 * Copyright 2011 Jose Mortensen
 * 
 * This file is part of tsAnalyzer.
 * 
 * tsAnalyzer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * tsAnalyzer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package com.me.tsanalyzer.video.util;

import java.util.logging.Level;

/**
 * This class is used as interface to log informational and
 * debugging messages.
 * @author Jose Mortensen <josemortensen@gmail.com>
 */
public class TsLogger {

    private static Level logLevel = Level.INFO;
    private static MessageProcessor processor = null;

    public static MessageProcessor getProcessor() {
        return processor;
    }

    public static void setProcessor(MessageProcessor processor) {
        TsLogger.processor = processor;
    }

    public static Level getLogLevel() {
        return logLevel;
    }

    public static void setLogLevel(Level logLevel) {
        TsLogger.logLevel = logLevel;

    }

    public static void log(String msg) {
        log(Level.FINE, msg);
    }

    public static void log(Level level, String msg) {
        if (level.intValue() >= logLevel.intValue()) {
            if (processor == null) {
                System.out.println(level + " : " + msg);
            } else {
                processor.println(level, msg);
            }
        }
    }

    public static void log(Exception e) {
        log(Level.SEVERE, e.getMessage());
        e.printStackTrace();
    }
}
