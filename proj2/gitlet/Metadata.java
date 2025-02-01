package gitlet;

import java.io.Serializable;

public class Metadata implements Serializable {
    String timestamp;
    String logMessage;

    public Metadata(String time, String log) {
        this.timestamp = time;
        this.logMessage = log;
    }
}
