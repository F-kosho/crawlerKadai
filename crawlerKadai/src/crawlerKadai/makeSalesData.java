package crawlerKadai;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class makeSalesData {

    public static void main(String[] args) throws IOException {
        // stockList.csv����݌ɏ󋵂̕ω���ǂݎ��
        System.out.println("�݌ɏ���ǂݍ���ł��܂�");
        
        readStockList();
        
        System.out.println("�݌ɏ��̓ǂݎ�肪�I�����܂���");
        
        // �ǂݎ�����f�[�^�����H���A���i�̔���s�����Z�o����
        System.out.println("���i�̔���s���𕪐͂��Ă��܂�");
        
        analyzeSales();
        
        System.out.println("����s���̕��͂��I�����܂���");
        
        // �Z�o��������s���f�[�^��salesData.csv�Ƃ��ďo�� or itemList.csv��ǂݎ��A����Ƃ������ďo��
        System.out.println("���X�g���쐬���Ă��܂�");
        
        makeListCsv();
        
        System.out.println("���X�g�̍쐬���I�����܂���");
    }
    
    // stockList.csv����݌ɏ󋵂̕ω���ǂݎ��
    static void readStockList(int gender) throws IOException {
        Path filePath = Paths.get("C:\\TechTraining\\resources\\stockList.csv");
        try (BufferedReader br = Files.newBufferedReader(filePath)) {
            
        }
    }
    
    // �ǂݎ�����݌ɏ󋵂̃f�[�^�����ƂɁA���i���Ƃ̔���s���𕪐�
    static void analyzeSales() {
        
    }
    
    // ���X�g�̂������t�@�C�����쐬
    static void makeListCsv() {
        
    }

}
