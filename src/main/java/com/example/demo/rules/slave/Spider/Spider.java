package com.example.demo.rules.slave.Spider;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Random;

@Component
public class Spider {
    String base = "https://s.weibo.com/weibo?q=疫苗接种&wvr=6&Refer=SWeibo_box";

    @Value("${cookie}")
    String cookie;

    Random random = new Random();

    public int getRandomTimeIn3000To10000(){
        return random.nextInt(2000) + 3000;
    }


    public LinkedList<SpiderContent> crawling_data(int index_start, int index_end) throws IOException, InterruptedException {
        LinkedList<SpiderContent> ret = new LinkedList<SpiderContent>();

        for (int i = index_start; i < index_start; i++) {

            int time_delay = getRandomTimeIn3000To10000();

            Thread.sleep(time_delay);

            Document doc = Jsoup.connect(base + "&page=" + i)
                    .ignoreContentType(true)
                    .header("Accept", "*/*")
                    .header("Accept-Encoding", "gzip, deflate")
                    .header("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3")
                    .header("Content-Type", "application/json;charset=UTF-8")
                    .cookie("Cookie", "SINAGLOBAL=4380708593471.6206.1587122404400; Ugrow-G0=7e0e6b57abe2c2f76f677abd9a9ed65d; login_sid_t=802e3efe53b8b7efc62ed667fc6f359a; cross_origin_proto=SSL; YF-V5-G0=8c4aa275e8793f05bfb8641c780e617b; wb_view_log=1368*9122; _s_tentry=passport.weibo.com; Apache=376261282560.74365.1590543097347; ULV=1590543097354:6:5:1:376261282560.74365.1590543097347:1590117575367; WBStorage=42212210b087ca50|undefined; appkey=; wb_view_log_7460697407=1368*9122; WBtopGlobal_register_version=fd6b3a12bb72ffed; YF-Page-G0=c3beab582124cd34995283c3a2849d9d|1590571140|1590571218; webim_unReadCount=%7B%22time%22%3A1590571246905%2C%22dm_pub_total%22%3A0%2C%22chat_group_client%22%3A0%2C%22chat_group_notice%22%3A0%2C%22allcountNum%22%3A1%2C%22msgbox%22%3A0%7D; UOR=tech.ifeng.com,widget.weibo.com,login.sina.com.cn; SCF=ArsRkvs-oW_Gx9WsvBDPnSDs0CTIdKc70XBBSJnlg2I_kIelYEnjQ6CUhpFMNmHDugUhPZYBSleq5hsBrj_tv8Q.; SUB=_2A25zykHTDeRhGeFK7VIX-SnIyzuIHXVQvjQbrDV8PUNbmtAKLRb6kW9NQyVU5iphfpNmUNl8Z_RcywNi7aLinDzt; SUBP=0033WrSXqPxfM725Ws9jqgMF55529P9D9WW34FahFbdeXNLqlH4D_lqX5JpX5K2hUgL.FoMXSo5c1KMXehM2dJLoIp7LxKML1KBLBKnLxKqL1hnLBoM7SKMpeKMNeoM7; SUHB=0cSn7tkZTz9Je5; ALF=1591176196; SSOLoginState=1590571395; un=17610829296")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:48.0) Gecko/20100101 Firefox/48.0")
                    .cookie("Cookie", cookie)
                    .timeout(5000)
                    .get();

            Elements weibos = doc.select("div[action-type='feed_list_item']");

            if (weibos.size() == 0) {
                System.out.println("weibo get nothing");

                continue;
            }


                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR, 0);

                long time_now = calendar.getTimeInMillis();

                for (Element temp : weibos) {
                    SpiderContent weiBo = new SpiderContent();

                    String mid;
                    try {
                        mid = temp.attr("mid");//w_id
                    } catch (Exception e) {
                        System.out.println("get mid error! mid = " + temp.attr("mid") + "**** content: " + temp.text());
                        //logger.error("get mid error! mid = "+temp.attr("mid")+"**** content: "+temp.text());

                        continue;
                    }

                    String content = temp.select("p.txt[node-type='feed_list_content_full'][nick-name]").text();

                    if (content.equals("")) {
                        content = temp.select("p.txt[node-type='feed_list_content'][nick-name]").text();
                    }

                    if (content.equals("转发微博")) {
                        continue;
                    }

                    String time_ = temp.select("p.from").select("a[href]").text().split(" ")[0];

                    //时间解析, 获取微博的时间
                    if (time_.contains("分钟前") || time_.contains("今天")) {//今天
                        weiBo.setW_timestamp(time_now);
                    } else {
                        int month;
                        int day;

                        String[] date = time_.split("[日|月]");

                        month = Integer.parseInt(date[0]);
                        day = Integer.parseInt(date[1]);

                        calendar = Calendar.getInstance();
                        calendar.set(Calendar.MONTH, month - 1);
                        calendar.set(Calendar.DAY_OF_MONTH, day);
                    }

                    //把时间戳截至到月日
                    Long time = calendar.getTimeInMillis();
                    time /= 10000;
                    time *= 10000;

                    weiBo.setW_timestamp(time);

                    weiBo.setW_content(content);
                    weiBo.setW_id(mid);

                    System.out.println(weiBo.toString());

                    ret.add(weiBo);
                }

            }

        return ret;
    }
}
