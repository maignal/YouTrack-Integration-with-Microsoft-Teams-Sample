package cloud.youtrackintegration;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class MessengerClient {
    public void sendMarkdown(String markdown) {
        System.out.println("[Mock Messenger] " + markdown);
        System.out.println();
        return;
    }
}
