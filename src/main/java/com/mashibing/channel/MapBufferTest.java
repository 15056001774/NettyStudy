package com.mashibing.channel;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**MappedByteBuffer:通过内存直接修改文件内容
 * Created by hp on 2020/2/7.
 */
public class MapBufferTest {
    public static void main(String[] args) {
        try {
            RandomAccessFile accessFile = new RandomAccessFile("D:/2345Downloads/test/jin.txt","rw");
            FileChannel channel = accessFile.getChannel();
            /**
             * param1:FileChannel.MapMode.READ_WRITE:通过内存直接操作文件模型方式，读，写，读写
             * param2:position:从文件的指定为位置开始
             * param3:可修改的文件内容大小，单位:byte
             */
            MappedByteBuffer mapBuf = channel.map(FileChannel.MapMode.READ_WRITE, 0, 10);
            //修改指定位置
            mapBuf.put(0,(byte)'H');
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }

    }

}
