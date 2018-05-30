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
        // �ǂݎ��csv�t�@�C���̎�ނ��w��
        int gender = 0; // ����
        int category = 1; // ���i�J�e�S��
        /*
         * �����̑Ή��\ ����:0=�����Y, 1=���f�B�[�X, 2=�L�b�Y 
         * �J�e�S���F0=�g�b�v�X, 1=�W���P�b�g�E�A�E�^�[, 2=�p���c, 3=�I�[���C������,
         * 4=�X�J�[�g, 5=�����s�[�X, 6=�X�[�c�E�l�N�^�C, 7=�o�b�O, 8=�V���[�Y, 9=�t�@�b�V�����G��, 10=���z�E����, 11=���v,
         * 12=�w�A�A�N�Z�T���[, 13=�A�N�Z�T���[, 14=�A���_�[�E�F�A, 15=���b�O�E�F�A, 16=�X�q, 17=�C���e���A, 18=�H��E�L�b�`��,
         * 19=�G�݁E�z�r�[�E�X�|�[�c, 20=�R�X���E����, 21=���y�E�{�E�G��, 22=�����E�����E����, 23=�}�^�j�e�B�E�x�r�[, 24=���̑�
         */
        int startDate = 20180530; // yyyyMMdd ���͑ΏۂƂ�����Ԃ̊J�n��
        int endDate = 20180530; // yyyyMMdd ���͑ΏۂƂ�����Ԃ̏I����
        
        // stockList.csv����݌ɏ󋵂̕ω���ǂݎ��E����s���𕪐�        
        // �Z�o��������s���f�[�^��salesData.csv�Ƃ��ďo�� or itemList.csv��ǂݎ��A����Ƃ������ďo��
        makeSalesCsv(gender, category, startDate, endDate);
    }
    
    // stockList.csv����݌ɏ󋵂̕ω���ǂݎ��
    static void makeSalesCsv(int gender, int category, int startDate, int endDate) throws IOException {
        System.out.println("�݌ɏ���ǂݍ���ł��܂�");
        Map<String, Map<String, String>> codeStartStockMap = new HashMap<String, Map<String, String>>();
        
        // �Ώۊ��Ԃ̊J�n����csv�t�@�C����ǂݍ���
        Path startFilePath = Paths.get("C:\\Users\\apex\\eclipse-workspace\\crawlerKadai\\src\\crawlerKadai\\resources\\stockList" + gender + category + startDate + ".csv");
        try (BufferedReader br = Files.newBufferedReader(startFilePath)) {
            String line = br.readLine();
            String[] header = line.split(",");
            while ((line = br.readLine()) != null) {
                Map<String, String> startStockMap = new HashMap<String, String>(); // key: ����, value: ���g
                String[] cols = line.split(",");
                for (int i = 0; i < cols.length; i++) { // �e���i�̍݌ɏ��Ȃǂ��擾���A�}�b�v�Ɋi�[���Ă���
                    String key = header[i];
                    String value = cols[i];
                    startStockMap.put(key, value);
                }
                codeStartStockMap.put(startStockMap.get("���i�R�[�h"), startStockMap);
            }

        }
        
        Map<String, Map<String, String>> codeEndStockMap = new HashMap<String, Map<String, String>>();
        
        // �Ώۊ��Ԃ̏I������csv�t�@�C����ǂݍ���
        Path endFilePath = Paths.get("C:\\Users\\apex\\eclipse-workspace\\crawlerKadai\\src\\crawlerKadai\\resources\\stockList" + gender + category + endDate + ".csv");
        try (BufferedReader br = Files.newBufferedReader(endFilePath)) {
            String line = br.readLine();
            String[] header = line.split(",");
            while ((line = br.readLine()) != null) {
                Map<String, String> endStockMap = new HashMap<String, String>(); // key: ����, value: ���g
                String[] cols = line.split(",");
                for (int i = 0; i < cols.length; i++) { // �e���i�̍݌ɏ��Ȃǂ��擾���A�}�b�v�Ɋi�[���Ă���
                    String key = header[i];
                    String value = cols[i];
                    endStockMap.put(key, value);
                }
                codeEndStockMap.put(endStockMap.get("���i�R�[�h"), endStockMap);
            }

        }
        
        System.out.println("�݌ɏ��̓ǂݎ�肪�I�����܂���");

        
        // �ǂݎ�����݌ɏ󋵂̃f�[�^�����ƂɁA���i���Ƃ̔���s���𕪐́A�}�b�v�Ɋi�[���Ă���
        System.out.println("���i�̔���s���𕪐͂��Ă��܂�");
        
        // ���͂�������s�����Ȃǂ��i�[����}�b�v
        Map<String, Map<String, String>> codeSalesMap = new HashMap<String, Map<String, String>>();
        
        // ���͑Ώۊ��Ԃ̊J�n���Ɍf�ڂ���Ă������i�����͑ΏۂƂȂ�
        for (Map.Entry<String, Map<String, String>> entry : codeStartStockMap.entrySet()) {
            // ���i�R�[�h, ����s���Ȃǂ��i�[����}�b�v
            Map<String, String> salesMap = new HashMap<String, String>();
            
            // ���i�R�[�h���i�[
            String code = entry.getKey();
            
            // ����s���𕪐́E�i�[
            // �݌ɏ󋵂ɉ����ē_���t��
            int startStockScore = 0; // �J�n���̍݌ɏ�
            String startStockStatus = entry.getValue().get("�݌ɏ�");
            if (startStockStatus.equals("�݌ɂ���") || startStockStatus.equals("�\��\")) {
                startStockScore = 5;
            } else if (startStockStatus.equals("����؂�") || startStockStatus.equals("�݌ɂȂ�")) {
                startStockScore = 0;
            } else if (startStockStatus.equals("�c��1�_")) {
                startStockScore = 1;
            } else if (startStockStatus.equals("�c��2�_")) {
                startStockScore = 2;
            } else if (startStockStatus.equals("�c��3�_")) {
                startStockScore = 3;
            } else {
                startStockScore = 4;
            }
            
            int endStockScore = 0; // �I�����̍݌ɏ�
            String endStockStatus = codeEndStockMap.get(code).get("�݌ɏ�");
            if (endStockStatus.equals("�݌ɂ���") || endStockStatus.equals("�\��\")) {
                endStockScore = 5;
            } else if (endStockStatus.equals("����؂�") || endStockStatus.equals("�݌ɂȂ�")) {
                endStockScore = 0;
            } else if (endStockStatus.equals("�c��1�_")) {
                endStockScore = 1;
            } else if (endStockStatus.equals("�c��2�_")) {
                endStockScore = 2;
            } else if (endStockStatus.equals("�c��3�_")) {
                endStockScore = 3;
            } else {
                endStockScore = 4;
            }
            
            // �_���̍��i�݌ɂ̌����j���v�Z
            int salesScore = startStockScore - endStockScore;
            
            // ���Ԃ̌v�Z
            String startDateStr = entry.getValue().get("�f�[�^�擾����"); // ����s�����̕��͑Ώۊ��Ԃ̊J�n��
            String endDateStr = codeEndStockMap.get(code).get("�f�[�^�擾����"); // �I����
            
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
            
            // ���͊��Ԃ��i�[
            salesMap.put("���͑Ώۊ���", period + "��");
            
            // �e���i�̔���s�����v�Z�E�i�[�i����s���F �݌ɂ̌��� / ���� �j
            String salesRate = "";
            if (period == 0) { // ���Ԃ�0�Ȃ̂ŗ�O����
                salesRate = "-";
            } else {
                double salesRateDouble = salesScore / period;
                salesRate = String.format("%.3f", salesRateDouble); // �`�F�b�N�K�v
            }
            salesMap.put("����s����", salesRate);
            
            // �J�n�����i�[
            salesMap.put("���͑Ώۊ��ԊJ�n��", startDateStr);
            
            // �J�n���݌ɂ��i�[
            salesMap.put("�J�n���݌ɏ�", startStockStatus);
            
            // �I�������i�[
            salesMap.put("���͑Ώۊ��ԏI����", endDateStr);
            
            // �I�����݌ɂ��i�[
            salesMap.put("�I�����݌ɏ�", endStockStatus);
            
            // �W�v��������codeSalesMap�Ɋi�[
            codeSalesMap.put(code, salesMap);
        }
        // �����点
        System.out.println("����s���̕��͂��I�����܂���");
        
        // ���X�g�̂������t�@�C�����쐬�i����s���̃t�@�C�����ƌ��̏��i�����������A�����o���j
        System.out.println("���X�g���쐬���Ă��܂�");
        
        // itemList.csv��ǂݍ��݁A����s�����Ȃǂ̃f�[�^���������ď����o��
        // itemList.csv�̓ǂݍ���
        // �}�b�v�̊����`codeItemMap
        Map<String, Map<String, String>> perfectCodeItemMap = new HashMap<String, Map<String, String>>();
        Path itemListFilePath = Paths.get("C:\\Users\\apex\\eclipse-workspace\\crawlerKadai\\src\\crawlerKadai\\resources\\itemList" + gender + category + startDate + ".csv");
        
        try (BufferedReader br = Files.newBufferedReader(itemListFilePath)) {
            // �w�b�_�s�̏���
            String line = br.readLine();
            String[] header = line.split(",");
            
            // �Q�s�ڈȍ~�̏���
            while((line = br.readLine()) != null) {
                Map<String, String> perfectItemMap = new HashMap<String, String>();
                String[] cols = line.split(",");
                for (int i = 0; i < cols.length; i++) {
                    String key = header[i];
                    String value = cols[i];
                    perfectItemMap.put(key, value);
                }
                // codeSalesMap�̏���itemMap�ɕt������
                String code = perfectItemMap.get("���i�R�[�h");
                Map<String, String> itemMap = new HashMap<String, String>();
                if (codeSalesMap.get(code) != null) {
                    itemMap = codeSalesMap.get(code);
                }
                for (Map.Entry<String, String> entry : itemMap.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    perfectItemMap.put(key, value);
                }
                // �ŏI�I�ɏo�͂���������i�R�[�h���ƂɊi�[
                perfectCodeItemMap.put(perfectItemMap.get("���i�R�[�h"), perfectItemMap);
            }
        }
        
        // ���͑Ώۊ��Ԃ̊J�n���Ɍf�ڂ���Ă������i�̂ݏ����o��
        Path filePath = Paths.get("C:\\Users\\apex\\eclipse-workspace\\crawlerKadai\\src\\crawlerKadai\\resources\\Sales" 
                + gender + category + startDate + "-" + endDate + ".csv");
        String header = "";
        try (BufferedWriter bw = Files.newBufferedWriter(filePath)) {
            // �w�b�_�[�s
            String[] contentsAry = {"���i�R�[�h", "���i��", "���i�J�e�S��", "�V���b�v��", "�u�����h��", 
                    "EC�T�C�g��", "����s����", "���͑Ώۊ���", "���͑Ώۊ��ԊJ�n��", "�J�n���݌ɏ�", "���͑Ώۊ��ԏI����", 
                    "�I�����݌ɏ�", "���iURL"};
            for (int i = 0; i < contentsAry.length - 1; i++) {
                header += contentsAry[i];
                header += ",";
            }
            header += contentsAry[contentsAry.length - 1];
            bw.write(header);
            bw.newLine();
            
            // �Q�s�ڈȍ~
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
        System.out.println("���X�g�̍쐬���I�����܂���");
    }

}
