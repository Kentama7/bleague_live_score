package com.github.kentama7.bleague_live_score.crawler;

import com.github.kentama7.bleague_live_score.crawler.b2.BigBullsCrawler;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class Crawl {

    public static void main(String[] args) throws Exception {
        // 50回もあれば十分
        for (int i = 1; i < 50; i++) {
            System.out.println(i + "回目");
            crawl();
            Thread.sleep(60000L);
        }
    }

    public static void crawl() throws Exception {
        // クローラの同時実行数
        int numberOfCrawler = 1;
        CrawlConfig config = new CrawlConfig();
        // 開始URLから何ホップ先までリンクをたどるか
        config.setMaxDepthOfCrawling(0);
        // クローラのデータを保存するディレクトリ
        config.setCrawlStorageFolder("./data/crawl/root");
        config.setPolitenessDelay(10000);
        // 1秒以下だとまずい
        if (config.getPolitenessDelay() < 1000) {
            return;
        }

        // CrawlControllerを準備する
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

        // クロールを開始するURLを指定
        controller.addSeed(BigBullsCrawler.BIGBULLS_URL);
        // クロールを開始
        controller.start(BigBullsCrawler.class, numberOfCrawler);
    }
}
