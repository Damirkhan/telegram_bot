package com.damirkhan.bot;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.lang.annotation.Documented;
import java.net.MalformedURLException;
import java.net.URL;

public class Rate {
    public String getRate(String amount, String currencyFrom, String currencyTo) throws IOException {
        String url = "https://www.xe.com/currencyconverter/convert/?Amount="+amount+"&From="+currencyFrom+"&To="+currencyTo;
        Document page = Jsoup.parse(new URL(url), 3000);
        Element pRate = page.select("p[class=result__BigRate-sc-1bsijpp-1 iGrAod]").first();
        return pRate.text();
    }

}
