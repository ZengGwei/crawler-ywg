package main.java.com.microants;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.processor.example.GithubRepoPageProcessor;

public class Main implements PageProcessor {

    private Site site = Site.me().setRetryTimes(3).setSleepTime(100);

    public void process(Page page) {
        System.out.println(
                page.getHtml().toString()
        );
      /*  page.addTargetRequests(page.getHtml().links().regex("(https://github\\.com/\\w+/\\w+)").all());
        page.putField("author", page.getUrl().regex("https://github\\.com/(\\w+)/.*").toString());
        page.putField("name", page.getHtml().xpath("//h1[@class='entry-title public']/strong/a/text()").toString());
        if (page.getResultItems().get("name")==null){
            //skip this page
            page.setSkip(true);
        }
        page.putField("readme", page.getHtml().xpath("//div[@id='readme']/tidyText()"));*/
    }

    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        Spider.create(new Main()).addUrl("https://github.com/code4craft").thread(5).run();
    }

}
