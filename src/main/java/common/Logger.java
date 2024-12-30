package common;

import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    private static final Logger LOG = new Logger();
    private static final OutputStream INFO_STREAM = System.out;
    private static final OutputStream ERROR_STREAM = System.err;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private Logger() {}

    public static Logger getLogger() {
        return LOG;
    }

    public void info(String message) {
        log(INFO_STREAM, "INFO", message);
    }

    public void error(String message) {
        log(ERROR_STREAM, "ERROR", message);
    }

    private void log(OutputStream stream, String level, String message) {
        PrintStream printStream = new PrintStream(stream);
        String timestamp = DATE_FORMAT.format(new Date());
        printStream.printf("%s [%s] %s%n", timestamp, level, message);
    }
}
