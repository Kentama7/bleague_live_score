package com.github.kentama7.bleague_live_score.crawler.b2;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.StringJoiner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.github.kentama7.bleague_live_score.mastodon.Mastodon;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class BigBullsCrawler extends WebCrawler {
    public static final String BIGBULLS_URL = "http://sportsnavi.ht.kyodo-d.jp/basketball/stats/bleague/teams/schedule/709/";
    public static String CACHE_SCORE = "";

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        // TOPのみクロール対象とする
        String href = url.getURL();
        return href.equals(BIGBULLS_URL);
    }

    @Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL();
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("M/d"));
        // BigBullsページのみ処理する
        if (url.equals(BIGBULLS_URL)) {
            HtmlParseData data = HtmlParseData.class.cast(page.getParseData());
            // ページのHTMLをJsoupでパース
            Document doc = Jsoup.parse(data.getHtml());
            Element scheduleTable = doc.select("#modBleagueSchedule03 div.partsTable").get(0);
            Elements rows = scheduleTable.getElementsByTag("tr");
            rows.forEach(row -> {
                String date = getElementTextByClass(row, "date");
                String score = getElementTextByClass(row, "score");
                if (!isGameDay(date, today) || isCached(score)) {
                    return;
                }

                String victoryOrDefeat = victoryOrDefeat(row);
                CACHE_SCORE = score;
                String section = getElementTextByClass(row, "cat");
                String place = getElementTextByClass(row, "place");
                String opposition = getElementTextByClass(row, "team").replace("岩手ビッグブルズ", "").trim();

                StringJoiner joiner = new StringJoiner("\n");
                joiner.add(score + victoryOrDefeat);
                joiner.add("vs." + opposition);
                if (place.equals("試合前")) {
                    joiner.add(section + "@" + place);
                }
                joiner.add("#岩手ビッグブルズ #Bリーグ");
                try {
                    Mastodon.toot(joiner.toString());
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println(score + victoryOrDefeat);
                System.out.println("vs." + opposition);
                System.out.println(section + "@" + place);
                System.out.println("------------------");
            });
        }
    }

    private static String getElementTextByClass(Element element, String className) {
        return element.getElementsByClass(className).text().trim();
    }

    private boolean isGameDay(String date, String today) {
        return date.startsWith(today);
    }

    private boolean isCached(String score) {
        return score.equals(CACHE_SCORE);
    }

    private static String victoryOrDefeat(Element element) {
        String victoryOrDefeat = getElementTextByClass(element, "win");
        switch (victoryOrDefeat) {
        case "○":
            victoryOrDefeat = " 勝利💪🏼";
            break;
        case "●":
            victoryOrDefeat = " 敗北😭";
            break;
        default:
            break;
        }
        return victoryOrDefeat;
    }
}
