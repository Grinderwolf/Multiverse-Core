/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.utils;

import com.onarandombox.MultiverseCore.MultiverseCoreConfiguration;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * The Multiverse debug-logger.
 */
public class DebugLog extends Logger {

    private FileHandler fh;
    private Logger standardLog = null;

    /**
     * Creates a new debug logger.
     *
     * @param logger The name of the logger.
     * @param file   The file to log to.
     */
    public DebugLog(String logger, String file) {
        super(logger, null);
        try {
            this.fh = new FileHandler(file, true);
            this.setUseParentHandlers(false);
            List<Handler> toRemove = Arrays.asList(this.getHandlers());
            for (Handler handler : toRemove) {
                this.removeHandler(handler);
            }
            this.addHandler(this.fh);
            this.setLevel(Level.ALL);
            this.fh.setFormatter(new LogFormatter());
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Specifies the logger to use to send debug messages to as the debug logger itself only sends messages to a file.
     *
     * @param logger Logger to send debug messages to.
     */
    public void setStandardLogger(Logger logger) {
        this.standardLog = logger;
    }

    /**
     * Log a message at a certain level.
     *
     * @param level The log-{@link Level}.
     * @param msg the message.
     */
    public void log(Level level, String msg) {
        if (MultiverseCoreConfiguration.isSet() && MultiverseCoreConfiguration.getInstance().getGlobalDebug() > 0) {
            if (standardLog != null) {
                standardLog.log(level, "[MVCore-Debug] " + msg);
            }
            super.log(level, "[MVCore-Debug] " + msg);
        }
    }

    /**
     * Our log-{@link Formatter}.
     */
    private class LogFormatter extends Formatter {
        private final SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        @Override
        public String format(LogRecord record) {
            StringBuilder builder = new StringBuilder();
            Throwable ex = record.getThrown();

            builder.append(this.date.format(record.getMillis()));
            builder.append(" [");
            builder.append(record.getLevel().getLocalizedName().toUpperCase());
            builder.append("] ");
            builder.append(record.getMessage());
            builder.append('\n');

            if (ex != null) {
                StringWriter writer = new StringWriter();
                ex.printStackTrace(new PrintWriter(writer));
                builder.append(writer);
            }

            return builder.toString();
        }
    }

    /**
     * Closes this {@link DebugLog}.
     */
    public void close() {
        this.fh.close();
    }
}
