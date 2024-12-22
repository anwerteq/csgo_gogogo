package com.xiaojuzi.util;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class OpenCVUtil {

    static { System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    public static void downPicture(String url,String picTureName){
        try (InputStream in = new URL(url).openStream();
             OutputStream out = new FileOutputStream(picTureName)) {

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }

            System.out.println("图片下载完成！");
        } catch (IOException e) {
            System.err.println("图片下载失败：" + e.getMessage());
        }

    }

    /**
     * 参考文章 ：http://testingpai.com/article/1604910615370
     * @param bgUrl
     * @param mvUrl
     * @return
     */
    public static Double getMoveLimit(String bgUrl,String mvUrl){
        String picName ="mv.jpg";
        downPicture(mvUrl,picName);
        //对滑块进行处理
        Mat slideBlockMat = Imgcodecs.imread(picName);
//1、灰度化图片
        Imgproc.cvtColor(slideBlockMat, slideBlockMat, Imgproc.COLOR_BGR2GRAY);
        Imgcodecs.imwrite("output1.png", slideBlockMat);
//2、去除周围黑边
        for (int row = 0; row < slideBlockMat.height(); row++) {
            for (int col = 0; col < slideBlockMat.width(); col++) {
                if (slideBlockMat.get(row, col)[0] == 0) {
                    slideBlockMat.put(row, col, 96);
                }
            }
        }
        Imgcodecs.imwrite("output2.png", slideBlockMat);
//3、inRange二值化转黑白图
        Core.inRange(slideBlockMat, Scalar.all(96), Scalar.all(96), slideBlockMat);
        // 保存结果图像
        Imgcodecs.imwrite("output3.png", slideBlockMat);

        // 3. 创建滑块图的掩膜，只保留黑色部分
        Mat mask = new Mat();
        // 将滑块转换为灰度图
//        Imgproc.cvtColor(slideBlockMat, slideBlockMat, Imgproc.COLOR_BGR2GRAY);

        // 创建掩膜，黑色部分（接近0的像素）保留，其他部分忽略
        Core.inRange(slideBlockMat, new Scalar(0), new Scalar(50), mask);
        Imgcodecs.imwrite("output4.png", mask);

        String bgName ="bg.jpg";
        downPicture(bgUrl,bgName);
        //对滑动背景图进行处理
        Mat slideBgMat = Imgcodecs.imread(bgName);
//1、灰度化图片
        Imgproc.cvtColor(slideBgMat, slideBgMat, Imgproc.COLOR_BGR2GRAY);
        Imgcodecs.imwrite("test1.jpg", slideBgMat);
//2、二值化
        Imgproc.threshold(slideBgMat, slideBgMat, 127, 255, Imgproc.THRESH_BINARY);
        Imgcodecs.imwrite("test2.jpg", slideBgMat);

        Mat g_result = new Mat();
        /*
         * matchTemplate：在模板和输入图像之间寻找匹配,获得匹配结果图像
         * result：保存匹配的结果矩阵
         * TM_CCOEFF_NORMED标准相关匹配算法
         */
        Imgproc.matchTemplate(slideBgMat, mask, g_result, Imgproc.TM_CCOEFF_NORMED);

        // 4. 寻找最大匹配位置
        Core.MinMaxLocResult mmr = Core.minMaxLoc(g_result);
        Point matchLocation = mmr.maxLoc;

        // 获取滑块模板的宽度和高度
        int templateWidth = slideBlockMat.width();
        int templateHeight = slideBlockMat.height();

        // 5. 在背景图上标识出匹配区域，使用矩形框绘制
        Imgproc.rectangle(slideBgMat, matchLocation,
                new Point(matchLocation.x + templateWidth, matchLocation.y + templateHeight),
                new Scalar(0, 0, 255), 2);  // 红色矩形框，线条宽度为2

        // 6. 保存或显示处理后的图像
        Imgcodecs.imwrite("output_with_rectangle.jpg", slideBgMat);


        /* minMaxLoc：在给定的结果矩阵中寻找最大和最小值，并给出它们的位置
         * maxLoc最大值
         */
         matchLocation = Core.minMaxLoc(g_result).maxLoc;


        Imgcodecs.imwrite("test3.jpg", g_result);
        System.out.println(matchLocation.x);
        Imgcodecs.imwrite("test5.jpg", g_result);
        return matchLocation.x;
    }
}
