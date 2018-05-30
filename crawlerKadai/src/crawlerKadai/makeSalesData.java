package crawlerKadai;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class makeSalesData {

    public static void main(String[] args) throws IOException {
        // stockList.csvから在庫状況の変化を読み取る
        System.out.println("在庫情報を読み込んでいます");
        
        readStockList();
        
        System.out.println("在庫情報の読み取りが終了しました");
        
        // 読み取ったデータを加工し、商品の売れ行きを算出する
        System.out.println("商品の売れ行きを分析しています");
        
        analyzeSales();
        
        System.out.println("売れ行きの分析が終了しました");
        
        // 算出した売れ行きデータをsalesData.csvとして出力 or itemList.csvを読み取り、それとくっつけて出力
        System.out.println("リストを作成しています");
        
        makeListCsv();
        
        System.out.println("リストの作成が終了しました");
    }
    
    // stockList.csvから在庫状況の変化を読み取る
    static void readStockList(int gender) throws IOException {
        Path filePath = Paths.get("C:\\TechTraining\\resources\\stockList.csv");
        try (BufferedReader br = Files.newBufferedReader(filePath)) {
            
        }
    }
    
    // 読み取った在庫状況のデータをもとに、商品ごとの売れ行きを分析
    static void analyzeSales() {
        
    }
    
    // リストのｃｓｖファイルを作成
    static void makeListCsv() {
        
    }

}
