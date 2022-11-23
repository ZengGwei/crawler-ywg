package com.microants;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;
import us.codecraft.xsoup.XElements;
import us.codecraft.xsoup.Xsoup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * @author fuyou
 * @version 1.0.0
 * @ClassName YIwugo
 * @Description TODO
 * @CreateTime 2022年11月2022/11/22日 13:50:00
 */
public class YIwugo implements PageProcessor {
    private Site site = Site.me()
            .setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36 Edg/106.0.1370.52")
            .setDomain("yiwugo.com")
            .setCharset("UTF-8")
            .addHeader("Referer","https://www.yiwugo.com")
            .setSleepTime(1000);

    private Map<String,String> shopNameHyuan = new HashMap<>(20000);

    private int shopCount = 1 ;
//    private int page =30;
    private String  shoplistUri = "https://www.yiwugo.com/shop_list/i_{page}.html?spm=d3d3Lnlpd3Vnby5jb20v";
    private boolean flist = true;

    public void process(Page page) {
        Html html = page.getHtml();
        Selectable xpath = html.xpath("//div[@class='shop_left_part shanpuxx']"); //商铺信息标签
        String s = xpath.toString();
        if (s != null){ //进入店铺页面 解析数据
            System.out.println("进入店铺:" +shopCount++);
            Document document = Jsoup.parse(s);
            HashMap<String, String> shopInfoMap = new HashMap<>();
            //input标签 取属性value的值
            String sid = html.xpath("//input[@class='shopid']").get();
            String shopId = Xsoup.compile("//input/@value").evaluate(Jsoup.parse(sid)).get();

            shopInfoMap.put("sid",shopId);
            shopInfoMap.put("huiyuan",shopNameHyuan.get(shopId));
            shopNameHyuan.remove(shopId);


            List<String> shopInfo = Xsoup.compile("//ul/allText()").evaluate(document).list();
            StringBuilder stringBuilder = new StringBuilder();
            shopInfo.stream().forEach(si->stringBuilder.append(si).append(" "));
            shopInfoMap.put("shopInfo",stringBuilder.toString());
            page.putField("shopInfo",shopInfoMap);
        }else {
            List<String> shopUrl = page.getHtml()
                    .xpath("//div[@class='pro_list_company_img']").$("p")
                    .links().all();
            page.addTargetRequests(shopUrl);  // 所有商铺链接


            //解析商铺会员商铺名称与
            Selectable li = page.getHtml().xpath("//div[@class='pro_list_company_img']");
            for (Selectable  node    :li.nodes()){
                Document nodeDocument = Jsoup.parse(node.toString());

                XElements shopId = Xsoup.compile("//div/@shopid").evaluate(nodeDocument);
                XElements yh = Xsoup.compile("//ul/li//em/@title").evaluate(nodeDocument);
                shopNameHyuan.put(shopId.get(),yh.get());
            }
        }

        //下一页商铺
        if (flist){
            for (int pages: IntStream.rangeClosed(90,100).toArray()){
                page.addTargetRequest("/shop_list/i_"+pages+".html?spm=d3d3Lnlpd3Vnby5jb20v");
            }
        }
        flist =false;
    }

    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        int page =89 ;
//        for (int page: IntStream.rangeClosed(30,100).toArray()){

            Spider.create(new YIwugo()).addUrl("https://www.yiwugo.com/shop_list/i_"+page+".html?spm=d3d3Lnlpd3Vnby5jb20v")
                    .clearPipeline()
                    .addPipeline(new DataProcess())
                    .thread(1)
                    .run();
//        }

    }
}
