package crawlerKadai;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class zozoCrawler {

    static ArrayList<String> itemUrlList = new ArrayList<String>(); // 各商品の詳細ページのURLを格納するリスト

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("クロール＆スクレイプ処理を開始しました。");

        // 情報を取得したい商品群の対象性別とカテゴリを指定
        int gender = 0;
        int category = 1;
        /*
         * 引数の対応表 性別:0=メンズ, 1=レディース, 2=キッズ カテゴリ：0=トップス, 1=ジャケット・アウター, 2=パンツ, 3=オールインワン,
         * 4=スカート, 5=ワンピース, 6=スーツ・ネクタイ, 7=バッグ, 8=シューズ, 9=ファッション雑貨, 10=財布・小物, 11=時計,
         * 12=ヘアアクセサリー, 13=アクセサリー, 14=アンダーウェア, 15=レッグウェア, 16=帽子, 17=インテリア, 18=食器・キッチン,
         * 19=雑貨・ホビー・スポーツ, 20=コスメ・香水, 21=音楽・本・雑誌, 22=水着・着物・浴衣, 23=マタニティ・ベビー, 24=その他
         */
        getItemUrl(gender, category);
        System.out.println("商品情報を取得しています");
        getItemData(gender, category);
        System.out.println("商品情報の取得が終了しました");

        System.out.println("商品情報をcsvファイルに出力しています");
        makeItemListCsv(gender, category);
        System.out.println("csvファイルの出力が終了しました");

        System.out.println("在庫状況をcsvファイルに書き出しています");
        makeStockListCsv(gender, category);
        System.out.println("csvファイルの出力が終了しました");

        System.out.println("クロール＆スクレイプ処理を終了しました。");
    }

    // 各商品の詳細ページを取得するメソッド
    static void getItemUrl(int gender, int category) throws IOException, InterruptedException {
        String rootUrl = makeUrl(gender, category); // 商品一覧ページのURLを作成するメソッド（最後の方で作ってある）

        String rootUrlPerPage = "";
        int count = 0;
        for (int i = 1; i < 3; i++) { // 商品一覧のページから、各商品の詳細ページのURLを取得・格納していく
            if (i == 1) { // 1ページ目のURL
                rootUrlPerPage = rootUrl;
            } else { // 2ページ目以降のURL
                rootUrlPerPage = rootUrl + "?pno=" + i;
            }
            Document doc = Jsoup.connect(rootUrlPerPage).get();
            Thread.sleep(1000);
            count++;
            System.out.println(count);

            try {
                Elements els = doc.getElementsByClass("thumb");
                for (Element el : els) {
                    String itemUrl = el.child(0).attr("href");
                    String perfectItemUrl = "http://zozo.jp" + itemUrl;
                    itemUrlList.add(perfectItemUrl);
                }
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }
    }

    // 入力された性別, カテゴリに応じて、クローリングするページのURLを生成するメソッド
    static String makeUrl(int gender, int category) {
        // 冒頭の「引数の対応表」の番号と以下の配列のインデックスは対応している
        String genderAry[] = { "men", "women", "kids" };
        String categoryAry[] = { "tops", "jacket-outerwear", "pants", "allinone-salopette", "skirt", "onepiece", "suit",
                "bag", "shoes", "fashion-accessories", "wallet-accessory", "wrist-watch", "hair-accessory", "accessory",
                "underwear", "leg-wear", "hat", "interior", "tableware-kitchenware", "zakka-hobby-sports",
                "cosmetics-perfume", "music-books", "swimwear-kimono-yukata", "maternity-baby", "others" };
        String url = "http://zozo.jp/" + genderAry[gender] + "-category/" + categoryAry[category] + "/";
        return url;
    }

    // 商品データを入れるmapを作る
    static Map<String, Map<String, String>> codeItemMap = new HashMap<String, Map<String, String>>();

    // 各商品の在庫状況を入れるmapを作る
    static Map<String, Map<String, String>> codeStockMap = new HashMap<String, Map<String, String>>();

    // データ取得日時を取得
    static Calendar c = Calendar.getInstance();
    // フォーマットパターンを指定して表示する
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    static String date = sdf.format(c.getTime());

    // 取得した商品詳細ページを順にクローリングしていき、各商品の情報を取得していくメソッド
    static void getItemData(int gender, int category) throws IOException, InterruptedException {
        int count = 0;
        for (int i = 0; i < itemUrlList.size(); i++) {
            String itemUrl = itemUrlList.get(i);

            Document doc = Jsoup.connect(itemUrl).get();
            Thread.sleep(1000);
            count++;
            System.out.println(count);

            // 商品情報を取得していく
            try { // itemUrlListの要素数を超えたら終了
            Element itemInfoEl = doc.getElementById("item-intro");
            // 取得した商品情報を格納するマップ
            Map<String, String> itemMap = new HashMap<String, String>(); // key：項目名, value:中身

            // 商品詳細ページのURLを格納
            itemMap.put("商品URL", itemUrl);

            // ECサイト名を格納
            itemMap.put("ECサイト名", "ZOZOTOWN");

            // 商品名を取得
            Elements nameEls = doc.getElementsByTag("h1");
            String itemName = nameEls.text();
            itemMap.put("商品名", itemName);

            Element nameEl = itemInfoEl.child(0);
            // ショップ名を取得
            String shopName = nameEl.child(0).text(); // "ショップ：〇〇〇"という文字列が取得できるので、"ショップ："部分を消してショップ名とする
            String clearShopName = shopName.substring(5);
            itemMap.put("ショップ名", clearShopName);

            // ブランド名を取得
            String brandName = nameEl.child(1).text();
            String clearBrandName = brandName.substring(5);
            itemMap.put("ブランド名", clearBrandName);

            // contbox
            Element contboxDlEls = doc.select("#itemDetailInfo > div > dl").get(0);
            // 対象性別を取得
            // 商品のカテゴリを取得
            Element categoryEl = contboxDlEls.getElementsByClass("lineNavi clearfix").get(0);
            String category1 = categoryEl.child(0).text();
            if (category1.contains(">")) {
                category1 = category1.substring(0, category1.length() - 1);
            }
            itemMap.put("商品カテゴリ", category1);

            // 商品コード、在庫状況、データ取得日時を格納するマップ
            Map<String, String> stockMap = new HashMap<String, String>();

            // データ取得日時をマップに格納
            itemMap.put("データ取得日時", date);
            stockMap.put("データ取得日時", date);

            // 商品コードを取得し, 商品のカラーやサイズとくっつける。その商品の特定の色の特定のサイズ、で１種類の商品として登録していく。
            Element codeEl = contboxDlEls.getElementsByClass("goodsData2").get(0); // 店舗の番号コードがあるやつ
            String itemCode = codeEl.text(); // 商品番号の中に"（店舗）"の文字が入っている
            String clearItemCode = itemCode.substring(0, itemCode.length() - 4); // "（店舗）"の４文字を削除

            // カラー・サイズごとの在庫情報を取得
            String codeColorSize = "";
            Elements stockEls = doc.getElementsByClass("blockMain");
            for (Element stockEl : stockEls) {
                Elements perColorInfos = stockEl.getElementsByTag("dl"); // <dl>

                for (Element perColorInfo : perColorInfos) { // カラーごとに情報を取得していく
                    String colorName = perColorInfo.child(0).text(); // カラー名を取得 <dt>
                    // そのカラーの中のサイズごとの情報を取得していく
                    Element ddEl = perColorInfo.child(1); // <dd>のエレメント
                    Element ulEl = ddEl.child(0); // <ul>のエレメント
                    for (Element liEl : ulEl.children()) {
                        Element cartboxEl = liEl.child(0);
                        Element stockEl1 = cartboxEl.child(0);
                        String sizeStock = stockEl1.child(0).text();
                        String[] sizeStockAry = sizeStock.split(" / ");
                        String size = sizeStockAry[0];
                        codeColorSize = clearItemCode + "-" + colorName + "-" + size;

                        // もし商品コードにカンマが含まれていたら取り除く
                        if (codeColorSize.contains(",")) {
                            codeColorSize = codeColorSize.replaceAll(",", "-");
                        }
                        itemMap.put("商品コード", codeColorSize);
                        // そのマップを値としてマップに格納（codeItemMap）
                        codeItemMap.put(codeColorSize, itemMap);

                        stockMap.put("商品コード", codeColorSize);
                        String stock = sizeStockAry[1];
                        stockMap.put("在庫状況", stock);

                        // 在庫状況などをマップに格納(codeStockMap)
                        codeStockMap.put(codeColorSize, stockMap);

                    }

                }
            }

            } catch (IndexOutOfBoundsException e) {
            break;
            }
        }
    }

    // 日付データをファイル名用に加工
    static String date2 = date.replace("/", "");

    // 取得したデータをcsvファイルに書き出す
    static void makeItemListCsv(int gender, int category) throws IOException {
        Path filePath = Paths
                .get("C:\\Users\\apex\\eclipse-workspace\\crawlerKadai\\src\\crawlerKadai\\resources\\itemList" + gender
                        + category + date2 + ".csv");
        String contents = ""; // 書き込む内容
        try (BufferedWriter bw = Files.newBufferedWriter(filePath)) {
            // ヘッダー行を作成
            contents = "商品コード" + "," + "商品名" + "," + "商品カテゴリ" + "," + "ショップ名" + "," + "ブランド名" + "," + "ECサイト名" + ","
                    + "データ取得日時" + "," + "商品URL";
            bw.write(contents);
            bw.newLine();

            for (Map.Entry<String, Map<String, String>> entry : codeItemMap.entrySet()) {
                String code = entry.getKey();
                Map<String, String> itemMap = entry.getValue();
                contents = code + "," + itemMap.get("商品名") + "," + itemMap.get("商品カテゴリ") + "," + itemMap.get("ショップ名")
                        + "," + itemMap.get("ブランド名") + "," + itemMap.get("ECサイト名") + "," + itemMap.get("データ取得日時") + ","
                        + itemMap.get("商品URL");
                bw.write(contents);
                bw.newLine();
            }
        }
    }

    // 取得した在庫情報をcsvファイルに書き出す
    static void makeStockListCsv(int gender, int category) throws IOException {
        Path filePath = Paths
                .get("C:\\Users\\apex\\eclipse-workspace\\crawlerKadai\\src\\crawlerKadai\\resources\\stockList"
                        + gender + category + date2 + ".csv");
        String contents = "";
        try (BufferedWriter bw = Files.newBufferedWriter(filePath)) {
            // ヘッダー行を作成
            contents = "商品コード" + "," + "データ取得日時" + "," + "在庫状況";
            bw.write(contents);
            bw.newLine();

            for (Map.Entry<String, Map<String, String>> entry : codeStockMap.entrySet()) {
                String code = entry.getKey();
                Map<String, String> stockMap = entry.getValue();
                contents = code + "," + stockMap.get("データ取得日時") + "," + stockMap.get("在庫状況");
                bw.write(contents);
                bw.newLine();
            }
        }
    }

}
