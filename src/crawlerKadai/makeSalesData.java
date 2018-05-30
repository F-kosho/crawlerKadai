package crawlerKadai;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class makeSalesData {

    public static void main(String[] args) throws IOException {
        // 読み取るcsvファイルの種類を指定
        int gender = 0; // 性別
        int category = 1; // 商品カテゴリ
        /*
         * 引数の対応表 性別:0=メンズ, 1=レディース, 2=キッズ 
         * カテゴリ：0=トップス, 1=ジャケット・アウター, 2=パンツ, 3=オールインワン,
         * 4=スカート, 5=ワンピース, 6=スーツ・ネクタイ, 7=バッグ, 8=シューズ, 9=ファッション雑貨, 10=財布・小物, 11=時計,
         * 12=ヘアアクセサリー, 13=アクセサリー, 14=アンダーウェア, 15=レッグウェア, 16=帽子, 17=インテリア, 18=食器・キッチン,
         * 19=雑貨・ホビー・スポーツ, 20=コスメ・香水, 21=音楽・本・雑誌, 22=水着・着物・浴衣, 23=マタニティ・ベビー, 24=その他
         */
        int startDate = 20180530; // yyyyMMdd 分析対象とする期間の開始日
        int endDate = 20180530; // yyyyMMdd 分析対象とする期間の終了日
        
        // stockList.csvから在庫状況の変化を読み取る・売れ行きを分析        
        // 算出した売れ行きデータをsalesData.csvとして出力 or itemList.csvを読み取り、それとくっつけて出力
        makeSalesCsv(gender, category, startDate, endDate);
    }
    
    // stockList.csvから在庫状況の変化を読み取る
    static void makeSalesCsv(int gender, int category, int startDate, int endDate) throws IOException {
        System.out.println("在庫情報を読み込んでいます");
        Map<String, Map<String, String>> codeStartStockMap = new HashMap<String, Map<String, String>>();
        
        // 対象期間の開始日のcsvファイルを読み込む
        Path startFilePath = Paths.get("C:\\Users\\apex\\eclipse-workspace\\crawlerKadai\\src\\crawlerKadai\\resources\\stockList" + gender + category + startDate + ".csv");
        try (BufferedReader br = Files.newBufferedReader(startFilePath)) {
            String line = br.readLine();
            String[] header = line.split(",");
            while ((line = br.readLine()) != null) {
                Map<String, String> startStockMap = new HashMap<String, String>(); // key: 項目, value: 中身
                String[] cols = line.split(",");
                for (int i = 0; i < cols.length; i++) { // 各商品の在庫情報などを取得し、マップに格納していく
                    String key = header[i];
                    String value = cols[i];
                    startStockMap.put(key, value);
                }
                codeStartStockMap.put(startStockMap.get("商品コード"), startStockMap);
            }

        }
        
        Map<String, Map<String, String>> codeEndStockMap = new HashMap<String, Map<String, String>>();
        
        // 対象期間の終了日のcsvファイルを読み込む
        Path endFilePath = Paths.get("C:\\Users\\apex\\eclipse-workspace\\crawlerKadai\\src\\crawlerKadai\\resources\\stockList" + gender + category + endDate + ".csv");
        try (BufferedReader br = Files.newBufferedReader(endFilePath)) {
            String line = br.readLine();
            String[] header = line.split(",");
            while ((line = br.readLine()) != null) {
                Map<String, String> endStockMap = new HashMap<String, String>(); // key: 項目, value: 中身
                String[] cols = line.split(",");
                for (int i = 0; i < cols.length; i++) { // 各商品の在庫情報などを取得し、マップに格納していく
                    String key = header[i];
                    String value = cols[i];
                    endStockMap.put(key, value);
                }
                codeEndStockMap.put(endStockMap.get("商品コード"), endStockMap);
            }

        }
        
        System.out.println("在庫情報の読み取りが終了しました");

        
        // 読み取った在庫状況のデータをもとに、商品ごとの売れ行きを分析、マップに格納していく
        System.out.println("商品の売れ行きを分析しています");
        
        // 分析した売れ行き情報などを格納するマップ
        Map<String, Map<String, String>> codeSalesMap = new HashMap<String, Map<String, String>>();
        
        // 分析対象期間の開始日に掲載されていた商品が分析対象となる
        for (Map.Entry<String, Map<String, String>> entry : codeStartStockMap.entrySet()) {
            // 商品コード, 売れ行きなどを格納するマップ
            Map<String, String> salesMap = new HashMap<String, String>();
            
            // 商品コードを格納
            String code = entry.getKey();
            
            // 売れ行きを分析・格納
            // 在庫状況に応じて点数付け
            int startStockScore = 0; // 開始日の在庫状況
            String startStockStatus = entry.getValue().get("在庫状況");
            if (startStockStatus.equals("在庫あり") || startStockStatus.equals("予約可能")) {
                startStockScore = 5;
            } else if (startStockStatus.equals("売り切れ") || startStockStatus.equals("在庫なし")) {
                startStockScore = 0;
            } else if (startStockStatus.equals("残り1点")) {
                startStockScore = 1;
            } else if (startStockStatus.equals("残り2点")) {
                startStockScore = 2;
            } else if (startStockStatus.equals("残り3点")) {
                startStockScore = 3;
            } else {
                startStockScore = 4;
            }
            
            int endStockScore = 0; // 終了日の在庫状況
            String endStockStatus = codeEndStockMap.get(code).get("在庫状況");
            if (endStockStatus.equals("在庫あり") || endStockStatus.equals("予約可能")) {
                endStockScore = 5;
            } else if (endStockStatus.equals("売り切れ") || endStockStatus.equals("在庫なし")) {
                endStockScore = 0;
            } else if (endStockStatus.equals("残り1点")) {
                endStockScore = 1;
            } else if (endStockStatus.equals("残り2点")) {
                endStockScore = 2;
            } else if (endStockStatus.equals("残り3点")) {
                endStockScore = 3;
            } else {
                endStockScore = 4;
            }
            
            // 点数の差（在庫の減り具合）を計算
            int salesScore = startStockScore - endStockScore;
            
            // 期間の計算
            String startDateStr = entry.getValue().get("データ取得日時"); // 売れ行き率の分析対象期間の開始日
            String endDateStr = codeEndStockMap.get(code).get("データ取得日時"); // 終了日
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            Date dateTo = null;
            Date dateFrom = null;
         
            try {
                dateFrom = sdf.parse(startDateStr);
                dateTo = sdf.parse(endDateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
         
            long dateTimeTo = dateTo.getTime();
            long dateTimeFrom = dateFrom.getTime();
            long period = ( dateTimeTo - dateTimeFrom  ) / (1000 * 60 * 60 * 24 );
            
            // 分析期間を格納
            salesMap.put("分析対象期間", period + "日");
            
            // 各商品の売れ行きを計算・格納（売れ行き： 在庫の減り具合 / 期間 ）
            String salesRate = "";
            if (period == 0) { // 期間が0なので例外処理
                salesRate = "-";
            } else {
                double salesRateDouble = salesScore / period;
                salesRate = String.format("%.3f", salesRateDouble); // チェック必要
            }
            salesMap.put("売れ行き率", salesRate);
            
            // 開始日を格納
            salesMap.put("分析対象期間開始日", startDateStr);
            
            // 開始日在庫を格納
            salesMap.put("開始日在庫状況", startStockStatus);
            
            // 終了日を格納
            salesMap.put("分析対象期間終了日", endDateStr);
            
            // 終了日在庫を格納
            salesMap.put("終了日在庫状況", endStockStatus);
            
            // 集計した情報をcodeSalesMapに格納
            codeSalesMap.put(code, salesMap);
        }
        // おしらせ
        System.out.println("売れ行きの分析が終了しました");
        
        // リストのｃｓｖファイルを作成（売れ行きのファイル情報と元の商品情報をくっつけ、書き出し）
        System.out.println("リストを作成しています");
        
        // itemList.csvを読み込み、売れ行き率などのデータをくっつけて書き出す
        // itemList.csvの読み込み
        // マップの完成形codeItemMap
        Map<String, Map<String, String>> perfectCodeItemMap = new HashMap<String, Map<String, String>>();
        Path itemListFilePath = Paths.get("C:\\Users\\apex\\eclipse-workspace\\crawlerKadai\\src\\crawlerKadai\\resources\\itemList" + gender + category + startDate + ".csv");
        
        try (BufferedReader br = Files.newBufferedReader(itemListFilePath)) {
            // ヘッダ行の処理
            String line = br.readLine();
            String[] header = line.split(",");
            
            // ２行目以降の処理
            while((line = br.readLine()) != null) {
                Map<String, String> perfectItemMap = new HashMap<String, String>();
                String[] cols = line.split(",");
                for (int i = 0; i < cols.length; i++) {
                    String key = header[i];
                    String value = cols[i];
                    perfectItemMap.put(key, value);
                }
                // codeSalesMapの情報をitemMapに付け足す
                String code = perfectItemMap.get("商品コード");
                Map<String, String> itemMap = new HashMap<String, String>();
                if (codeSalesMap.get(code) != null) {
                    itemMap = codeSalesMap.get(code);
                }
                for (Map.Entry<String, String> entry : itemMap.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    perfectItemMap.put(key, value);
                }
                // 最終的に出力する情報を商品コードごとに格納
                perfectCodeItemMap.put(perfectItemMap.get("商品コード"), perfectItemMap);
            }
        }
        
        // 分析対象期間の開始日に掲載されていた商品のみ書き出す
        Path filePath = Paths.get("C:\\Users\\apex\\eclipse-workspace\\crawlerKadai\\src\\crawlerKadai\\resources\\Sales" 
                + gender + category + startDate + "-" + endDate + ".csv");
        String header = "";
        try (BufferedWriter bw = Files.newBufferedWriter(filePath)) {
            // ヘッダー行
            String[] contentsAry = {"商品コード", "商品名", "商品カテゴリ", "ショップ名", "ブランド名", 
                    "ECサイト名", "売れ行き率", "分析対象期間", "分析対象期間開始日", "開始日在庫状況", "分析対象期間終了日", 
                    "終了日在庫状況", "商品URL"};
            for (int i = 0; i < contentsAry.length - 1; i++) {
                header += contentsAry[i];
                header += ",";
            }
            header += contentsAry[contentsAry.length - 1];
            bw.write(header);
            bw.newLine();
            
            // ２行目以降
            for (Map.Entry<String, Map<String, String>> entry : perfectCodeItemMap.entrySet()) {
                String contents = "";
                String code = entry.getKey();
                for (int i = 0; i < contentsAry.length - 1; i++) {
                    contents += entry.getValue().get(contentsAry[i]);
                    contents += ",";
                }
                contents += entry.getValue().get(contentsAry[contentsAry.length - 1]);
                bw.write(contents);
                bw.newLine();
            }
        }
        System.out.println("リストの作成が終了しました");
    }

}
