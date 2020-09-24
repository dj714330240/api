import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {
    private Pattern pattern = Pattern.compile("COM(.+)<a.+卡密：(.+)<a");

    public List<String> extract(String html) {
        html = html.replace("\n", "");
        String[] card_items = html.split("卡号：");
        List<String> cardList = new ArrayList<>();
        for (String card_item : card_items) {
            Matcher matcher = pattern.matcher(card_item);
            if (matcher.find()) {
                String cardNumber = matcher.group(1);
                String cardPass = matcher.group(2);
                cardList.add("COM"+cardNumber + "--" + cardPass);
            }
        }
        return cardList;
    }


    public static void main(String[] args) {
        String html = "{\"msg\":\"<p>卡号：COM120<a href=\\\"javascript:;\\\" class=\\\"btn btn-purple waves-effect waves-light clipboard\\\" data-clipboard-text=\\\"COM120\\\">复制<\\/a><\\/p><p>卡密：14716273884<a href=\\\"javascript:;\\\" class=\\\"btn btn-purple waves-effect waves-light clipboard\\\" data-clipboard-text=\\\"14716273884\\\">复制<\\/a><\\/p><p>卡号：COM121<a href=\\\"javascript:;\\\" class=\\\"btn btn-purple waves-effect waves-light clipboard\\\" data-clipboard-text=\\\"COM121\\\">复制<\\/a><\\/p><p>卡密：15015601894<a href=\\\"javascript:;\\\" class=\\\"btn btn-purple waves-effect waves-light clipboard\\\" data-clipboard-text=\\\"15015601894\\\">复制<\\/a><\\/p><p>卡号：COM122<a href=\\\"javascript:;\\\" class=\\\"btn btn-purple waves-effect waves-light clipboard\\\" data-clipboard-text=\\\"COM122\\\">复制<\\/a><\\/p><p>卡密：15820633865<a href=\\\"javascript:;\\\" class=\\\"btn btn-purple waves-effect waves-light clipboard\\\" data-clipboard-text=\\\"15820633865\\\">复制<\\/a><\\/p><p>使用说明：买完卡15分钟内用完<\\/p>\",\"quantity\":3,\"status\":1}";
        List<String> extract = new Test().extract(html);
        for (String s : extract) {
            System.out.println(s);
        }
    }
}
