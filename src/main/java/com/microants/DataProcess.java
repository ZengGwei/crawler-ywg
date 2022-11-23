package com.microants;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author fuyou
 * @version 1.0.0
 * @ClassName dataProcess
 * @Description TODO
 * @CreateTime 2022年11月2022/11/22日 17:27:00
 */
public class DataProcess implements Pipeline {

    private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private FileWriter f =null;

    @Override
    public void process(ResultItems resultItems, Task task) {
        Map<String, Object> all = resultItems.getAll();
        if (all.size() > 0) {
            Map<String, String> info = (Map<String, String>) all.get("shopInfo");
            String sid = info.get("sid");
            String huiyuan = info.get("huiyuan");
            String shopInfo = info.get("shopInfo");


            BufferedWriter f1 = null;//创建字符流写入对象

            try {
                if (f ==null){
                    f = new FileWriter("/Users/fuyou/Documents/shopinfo3.txt", true);//创建一个名为cc.txt的文件
                }
//                readWriteLock.writeLock().lock(); //  写锁
                System.out.println("》》》》："+Thread.currentThread().getName()+" 线程开始写数据-开始 ");
                //这里把文件写入对象和字符流写入对象分开写了
                f1 = new BufferedWriter(f);

                f1.write(sid);//把Stringt中的字符写入文件
                f1.write(" \t");
                f1.write(huiyuan);
                f1.write(" \t");
                f1.write(shopInfo);
                f1.newLine();//换行操作
            } catch (Exception e) {
                // TODO: handle exception
            } finally {//如果没有catch 异常，程序最终会执行到这里
//                try {
//                    assert f1 != null;
//                    f1.close();
//                    f.close();//关闭文件
//                } catch (Exception e2) {
//                    // TODO: handle exception
//                }

                System.out.println("》》》》："+Thread.currentThread().getName()+" 线程开始写数据-结束 ");
//                readWriteLock.writeLock().unlock();// 释放写锁
            }
        } else {
            System.out.println(">>>download:" + resultItems.getRequest().getUrl());
        }


    }
}
