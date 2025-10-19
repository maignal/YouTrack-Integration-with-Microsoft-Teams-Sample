package cloud.youtrackintegration;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Usage: java -jar integration.jar [poll|create <summary>]");
            return;
        }

        Config cfg = new Config("config.properties");
        String token = System.getenv("YOUTRACK_TOKEN");
        YouTrackClient yt = new YouTrackClient(cfg.get("youtrack.url"), token);
        MessengerClient ms = new MessengerClient();

        switch (args[0]) {
            case "poll" -> {
                int interval = Integer.parseInt(cfg.get("poll.interval"));
                NotificationPoller poller = new NotificationPoller(yt, ms, interval);
                poller.start();
            }
            case "create" -> {
                if (args.length < 2) {
                    System.out.println("Please provide a summary for the issue.");
                    return;
                }
                String summary = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
                yt.createIssue(cfg.get("project.id"), summary);
            }
            default -> System.out.println("Unknown command: " + args[0]);
        }
    }
}

