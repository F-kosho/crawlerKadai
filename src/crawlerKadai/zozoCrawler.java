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

    static ArrayList<String> itemUrlList = new ArrayList<String>(); // �e���i�̏ڍ׃y�[�W��URL���i�[���郊�X�g

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("�N���[�����X�N���C�v�������J�n���܂����B");

        // �����擾���������i�Q�̑Ώې��ʂƃJ�e�S�����w��
        int gender = 0;
        int category = 1;
        /*
         * �����̑Ή��\ ����:0=�����Y, 1=���f�B�[�X, 2=�L�b�Y �J�e�S���F0=�g�b�v�X, 1=�W���P�b�g�E�A�E�^�[, 2=�p���c, 3=�I�[���C������,
         * 4=�X�J�[�g, 5=�����s�[�X, 6=�X�[�c�E�l�N�^�C, 7=�o�b�O, 8=�V���[�Y, 9=�t�@�b�V�����G��, 10=���z�E����, 11=���v,
         * 12=�w�A�A�N�Z�T���[, 13=�A�N�Z�T���[, 14=�A���_�[�E�F�A, 15=���b�O�E�F�A, 16=�X�q, 17=�C���e���A, 18=�H��E�L�b�`��,
         * 19=�G�݁E�z�r�[�E�X�|�[�c, 20=�R�X���E����, 21=���y�E�{�E�G��, 22=�����E�����E����, 23=�}�^�j�e�B�E�x�r�[, 24=���̑�
         */
        getItemUrl(gender, category);
        System.out.println("���i�����擾���Ă��܂�");
        getItemData(gender, category);
        System.out.println("���i���̎擾���I�����܂���");

        System.out.println("���i����csv�t�@�C���ɏo�͂��Ă��܂�");
        makeItemListCsv(gender, category);
        System.out.println("csv�t�@�C���̏o�͂��I�����܂���");

        System.out.println("�݌ɏ󋵂�csv�t�@�C���ɏ����o���Ă��܂�");
        makeStockListCsv(gender, category);
        System.out.println("csv�t�@�C���̏o�͂��I�����܂���");

        System.out.println("�N���[�����X�N���C�v�������I�����܂����B");
    }

    // �e���i�̏ڍ׃y�[�W���擾���郁�\�b�h
    static void getItemUrl(int gender, int category) throws IOException, InterruptedException {
        String rootUrl = makeUrl(gender, category); // ���i�ꗗ�y�[�W��URL���쐬���郁�\�b�h�i�Ō�̕��ō���Ă���j

        String rootUrlPerPage = "";
        int count = 0;
        for (int i = 1; i < 3; i++) { // ���i�ꗗ�̃y�[�W����A�e���i�̏ڍ׃y�[�W��URL���擾�E�i�[���Ă���
            if (i == 1) { // 1�y�[�W�ڂ�URL
                rootUrlPerPage = rootUrl;
            } else { // 2�y�[�W�ڈȍ~��URL
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

    // ���͂��ꂽ����, �J�e�S���ɉ����āA�N���[�����O����y�[�W��URL�𐶐����郁�\�b�h
    static String makeUrl(int gender, int category) {
        // �`���́u�����̑Ή��\�v�̔ԍ��ƈȉ��̔z��̃C���f�b�N�X�͑Ή����Ă���
        String genderAry[] = { "men", "women", "kids" };
        String categoryAry[] = { "tops", "jacket-outerwear", "pants", "allinone-salopette", "skirt", "onepiece", "suit",
                "bag", "shoes", "fashion-accessories", "wallet-accessory", "wrist-watch", "hair-accessory", "accessory",
                "underwear", "leg-wear", "hat", "interior", "tableware-kitchenware", "zakka-hobby-sports",
                "cosmetics-perfume", "music-books", "swimwear-kimono-yukata", "maternity-baby", "others" };
        String url = "http://zozo.jp/" + genderAry[gender] + "-category/" + categoryAry[category] + "/";
        return url;
    }

    // ���i�f�[�^������map�����
    static Map<String, Map<String, String>> codeItemMap = new HashMap<String, Map<String, String>>();

    // �e���i�̍݌ɏ󋵂�����map�����
    static Map<String, Map<String, String>> codeStockMap = new HashMap<String, Map<String, String>>();

    // �f�[�^�擾�������擾
    static Calendar c = Calendar.getInstance();
    // �t�H�[�}�b�g�p�^�[�����w�肵�ĕ\������
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    static String date = sdf.format(c.getTime());

    // �擾�������i�ڍ׃y�[�W�����ɃN���[�����O���Ă����A�e���i�̏����擾���Ă������\�b�h
    static void getItemData(int gender, int category) throws IOException, InterruptedException {
        int count = 0;
        for (int i = 0; i < itemUrlList.size(); i++) {
            String itemUrl = itemUrlList.get(i);

            Document doc = Jsoup.connect(itemUrl).get();
            Thread.sleep(1000);
            count++;
            System.out.println(count);

            // ���i�����擾���Ă���
            try { // itemUrlList�̗v�f���𒴂�����I��
            Element itemInfoEl = doc.getElementById("item-intro");
            // �擾�������i�����i�[����}�b�v
            Map<String, String> itemMap = new HashMap<String, String>(); // key�F���ږ�, value:���g

            // ���i�ڍ׃y�[�W��URL���i�[
            itemMap.put("���iURL", itemUrl);

            // EC�T�C�g�����i�[
            itemMap.put("EC�T�C�g��", "ZOZOTOWN");

            // ���i�����擾
            Elements nameEls = doc.getElementsByTag("h1");
            String itemName = nameEls.text();
            itemMap.put("���i��", itemName);

            Element nameEl = itemInfoEl.child(0);
            // �V���b�v�����擾
            String shopName = nameEl.child(0).text(); // "�V���b�v�F�Z�Z�Z"�Ƃ��������񂪎擾�ł���̂ŁA"�V���b�v�F"�����������ăV���b�v���Ƃ���
            String clearShopName = shopName.substring(5);
            itemMap.put("�V���b�v��", clearShopName);

            // �u�����h�����擾
            String brandName = nameEl.child(1).text();
            String clearBrandName = brandName.substring(5);
            itemMap.put("�u�����h��", clearBrandName);

            // contbox
            Element contboxDlEls = doc.select("#itemDetailInfo > div > dl").get(0);
            // �Ώې��ʂ��擾
            // ���i�̃J�e�S�����擾
            Element categoryEl = contboxDlEls.getElementsByClass("lineNavi clearfix").get(0);
            String category1 = categoryEl.child(0).text();
            if (category1.contains(">")) {
                category1 = category1.substring(0, category1.length() - 1);
            }
            itemMap.put("���i�J�e�S��", category1);

            // ���i�R�[�h�A�݌ɏ󋵁A�f�[�^�擾�������i�[����}�b�v
            Map<String, String> stockMap = new HashMap<String, String>();

            // �f�[�^�擾�������}�b�v�Ɋi�[
            itemMap.put("�f�[�^�擾����", date);
            stockMap.put("�f�[�^�擾����", date);

            // ���i�R�[�h���擾��, ���i�̃J���[��T�C�Y�Ƃ�������B���̏��i�̓���̐F�̓���̃T�C�Y�A�łP��ނ̏��i�Ƃ��ēo�^���Ă����B
            Element codeEl = contboxDlEls.getElementsByClass("goodsData2").get(0); // �X�܂̔ԍ��R�[�h��������
            String itemCode = codeEl.text(); // ���i�ԍ��̒���"�i�X�܁j"�̕����������Ă���
            String clearItemCode = itemCode.substring(0, itemCode.length() - 4); // "�i�X�܁j"�̂S�������폜

            // �J���[�E�T�C�Y���Ƃ̍݌ɏ����擾
            String codeColorSize = "";
            Elements stockEls = doc.getElementsByClass("blockMain");
            for (Element stockEl : stockEls) {
                Elements perColorInfos = stockEl.getElementsByTag("dl"); // <dl>

                for (Element perColorInfo : perColorInfos) { // �J���[���Ƃɏ����擾���Ă���
                    String colorName = perColorInfo.child(0).text(); // �J���[�����擾 <dt>
                    // ���̃J���[�̒��̃T�C�Y���Ƃ̏����擾���Ă���
                    Element ddEl = perColorInfo.child(1); // <dd>�̃G�������g
                    Element ulEl = ddEl.child(0); // <ul>�̃G�������g
                    for (Element liEl : ulEl.children()) {
                        Element cartboxEl = liEl.child(0);
                        Element stockEl1 = cartboxEl.child(0);
                        String sizeStock = stockEl1.child(0).text();
                        String[] sizeStockAry = sizeStock.split(" / ");
                        String size = sizeStockAry[0];
                        codeColorSize = clearItemCode + "-" + colorName + "-" + size;

                        // �������i�R�[�h�ɃJ���}���܂܂�Ă������菜��
                        if (codeColorSize.contains(",")) {
                            codeColorSize = codeColorSize.replaceAll(",", "-");
                        }
                        itemMap.put("���i�R�[�h", codeColorSize);
                        // ���̃}�b�v��l�Ƃ��ă}�b�v�Ɋi�[�icodeItemMap�j
                        codeItemMap.put(codeColorSize, itemMap);

                        stockMap.put("���i�R�[�h", codeColorSize);
                        String stock = sizeStockAry[1];
                        stockMap.put("�݌ɏ�", stock);

                        // �݌ɏ󋵂Ȃǂ��}�b�v�Ɋi�[(codeStockMap)
                        codeStockMap.put(codeColorSize, stockMap);

                    }

                }
            }

            } catch (IndexOutOfBoundsException e) {
            break;
            }
        }
    }

    // ���t�f�[�^���t�@�C�����p�ɉ��H
    static String date2 = date.replace("/", "");

    // �擾�����f�[�^��csv�t�@�C���ɏ����o��
    static void makeItemListCsv(int gender, int category) throws IOException {
        Path filePath = Paths
                .get("C:\\Users\\apex\\eclipse-workspace\\crawlerKadai\\src\\crawlerKadai\\resources\\itemList" + gender
                        + category + date2 + ".csv");
        String contents = ""; // �������ޓ��e
        try (BufferedWriter bw = Files.newBufferedWriter(filePath)) {
            // �w�b�_�[�s���쐬
            contents = "���i�R�[�h" + "," + "���i��" + "," + "���i�J�e�S��" + "," + "�V���b�v��" + "," + "�u�����h��" + "," + "EC�T�C�g��" + ","
                    + "�f�[�^�擾����" + "," + "���iURL";
            bw.write(contents);
            bw.newLine();

            for (Map.Entry<String, Map<String, String>> entry : codeItemMap.entrySet()) {
                String code = entry.getKey();
                Map<String, String> itemMap = entry.getValue();
                contents = code + "," + itemMap.get("���i��") + "," + itemMap.get("���i�J�e�S��") + "," + itemMap.get("�V���b�v��")
                        + "," + itemMap.get("�u�����h��") + "," + itemMap.get("EC�T�C�g��") + "," + itemMap.get("�f�[�^�擾����") + ","
                        + itemMap.get("���iURL");
                bw.write(contents);
                bw.newLine();
            }
        }
    }

    // �擾�����݌ɏ���csv�t�@�C���ɏ����o��
    static void makeStockListCsv(int gender, int category) throws IOException {
        Path filePath = Paths
                .get("C:\\Users\\apex\\eclipse-workspace\\crawlerKadai\\src\\crawlerKadai\\resources\\stockList"
                        + gender + category + date2 + ".csv");
        String contents = "";
        try (BufferedWriter bw = Files.newBufferedWriter(filePath)) {
            // �w�b�_�[�s���쐬
            contents = "���i�R�[�h" + "," + "�f�[�^�擾����" + "," + "�݌ɏ�";
            bw.write(contents);
            bw.newLine();

            for (Map.Entry<String, Map<String, String>> entry : codeStockMap.entrySet()) {
                String code = entry.getKey();
                Map<String, String> stockMap = entry.getValue();
                contents = code + "," + stockMap.get("�f�[�^�擾����") + "," + stockMap.get("�݌ɏ�");
                bw.write(contents);
                bw.newLine();
            }
        }
    }

}
