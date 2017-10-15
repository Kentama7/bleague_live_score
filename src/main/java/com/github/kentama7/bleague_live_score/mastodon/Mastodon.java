package com.github.kentama7.bleague_live_score.mastodon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Mastodon {
    
    public static void toot(String toot) throws IOException, InterruptedException {
        List<String> curl = new ArrayList<>();
        curl.add("curl");
        curl.add("-X");
        curl.add("POST");
        curl.add("--header");
        curl.add("Authorization: Bearer " + Info.ACCESS_TOKEN);
        curl.add(Info.HOST +  "/api/v1/statuses");
        curl.add("--data-binary");
        curl.add("status=" + toot);

        ProcessBuilder pb = new ProcessBuilder(curl);
        Process process = pb.start();
        process.waitFor();
    }
}
