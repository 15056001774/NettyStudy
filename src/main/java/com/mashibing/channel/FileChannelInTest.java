package com.mashibing.channel;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by hp on 2020/2/7.
 */
public class FileChannelInTest {
    public static void main(String[] args) {
        File file = new File("D:/2345Downloads/test/liang.txt");
        try {
            FileInputStream fis = new FileInputStream(file);
            FileChannel fic = fis.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate((int) file.length());
            fic.read(buffer);
            System.out.println(new String(buffer.array()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }


    }

}
