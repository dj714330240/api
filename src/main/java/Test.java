import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

    private Pattern pattern = Pattern.compile("<span class=\"card_number\">(.+)</span>.+<span class=\"card_pass\">(.+)</span>");

    public List<String> extract(String html) {
        html = html.replace("\n", "");
        String[] card_items = html.split("card_item");
        List<String> cardList = new ArrayList<>();
        for (String card_item : card_items) {
            Matcher matcher = pattern.matcher(card_item);
            if (matcher.find()) {
                String cardNumber = matcher.group(1);
                String cardPass = matcher.group(2);
                cardList.add(cardNumber + "--" + cardPass);
            }
        }
        return cardList;
    }


    public static void main(String[] args) {
        String html = "<article class=\"card_item\">\n" +
                "    <p>卡号：\n" +
                "        <span class=\"card_number\">COM236</span>\n" +
                "        <a href=\" \" class=\"btn btn-purple waves-effect waves-light clipboard\" data-clipboard-text=\"COM236\">复制</a >\n" +
                "    </p >\n" +
                "    <p>卡密：\n" +
                "        <span class=\"card_pass\">13728944767</span>\n" +
                "        <a href=\"javascript:;\" class=\"btn btn-purple waves-effect waves-light clipboard\" data-clipboard-text=\"13728944767\">复制</a >\n" +
                "    </p >\n" +
                "</article>\n" +
                "<article class=\"card_item\">\n" +
                "    <p>卡号：\n" +
                "        <span class=\"card_number\">COM235</span>\n" +
                "        <a href=\"javascript:;\" class=\"btn btn-purple waves-effect waves-light clipboard\" data-clipboard-text=\"COM235\">复制</a >\n" +
                "    </p >\n" +
                "    <p>卡密：\n" +
                "        <span class=\"card_pass\">15112482516</span>\n" +
                "        <a href=\"javascript:;\" class=\"btn btn-purple waves-effect waves-light clipboard\" data-clipboard-text=\"15112482516\">复制</a >\n" +
                "    </p >\n" +
                "</article>\n" +
                "<p>\n" +
                "    <a id=\"dumpCardsBtn\" style=\"padding:5px 8px;background-color:#678cf7;color:white\" href=\"/index/order/dumpCards?trade_no=CS200725145620265074\" target=\"_blank\">导出卡密</a >\n" +
                "</p >\n" +
                "<p>\n" +
                "    <a href=\"javascript:;\" class=\"copyCardsBtn\" style=\"padding:5px 8px;background-color:#678cf7;color:white\"  data-clipboard-action=\"copy\" data-clipboard-target=\"#copyTarget\" >一键复制全部</a >\n" +
                "</p >\n" +
                "<div id=\"copyTarget\" style=\"opacity: 0;float: left; width: 0.1px;height: 0.1px;position: relative;overflow: hidden;\"></div>";
        List<String> extract = new Test().extract(html);
        for (String s : extract) {
            System.out.println(s);
        }
    }
}
