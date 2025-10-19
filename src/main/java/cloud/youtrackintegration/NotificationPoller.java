package cloud.youtrackintegration;

import java.util.List;

public class NotificationPoller {
    private final YouTrackClient youtrack;
    private final MessengerClient messenger;
    private final int intervalSec;

    public NotificationPoller(YouTrackClient youtrack, MessengerClient messenger, int intervalSec) {
        this.youtrack = youtrack;
        this.messenger = messenger;
        this.intervalSec = intervalSec;
    }

    public void start() throws Exception {
        System.out.println("Polling YouTrack for new issues every " + intervalSec + "s...");
        while (true) {
            List<String> newIssue = youtrack.getNewIssues();
            for (String msg : newIssue) {
                messenger.sendMarkdown(msg);
            }
            Thread.sleep(intervalSec * 1000L);
        }
    }
}
