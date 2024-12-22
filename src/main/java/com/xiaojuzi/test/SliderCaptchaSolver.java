package com.xiaojuzi.test;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class SliderCaptchaSolver {
    static {
        // 加载 OpenCV 库
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {
        String str = "a/b";
        String[] split = str.split("/");
        System.out.println();
    }

    public static Double getMoveHuaKuai(String original,String template ) throws Exception {
        String originalImagePath = downloadImage(original); // 原图路径
        String templateImagePath =downloadImage(template); // 模板图路径

        // 加载原图和模板图
        Mat originalImage = Imgcodecs.imread(originalImagePath, Imgcodecs.IMREAD_COLOR);
        Mat templateImage = Imgcodecs.imread(templateImagePath, Imgcodecs.IMREAD_COLOR);

        if (originalImage.empty() || templateImage.empty()) {
            System.out.println("加载图片失败，请检查路径！");

        }

        // 裁剪模板图的中间区域
        Rect cropRegion = new Rect(
                (int) (templateImage.cols() * 0.2),  // x 起点
                0,                                  // y 起点
                (int) (templateImage.cols() * 0.6), // 裁剪的宽度
                templateImage.rows()                // 裁剪的高度
        );
        Mat croppedTemplate = new Mat(templateImage, cropRegion);

        // 将图片转为灰度
        Mat originalGray = new Mat();
        Mat croppedGray = new Mat();
        Imgproc.cvtColor(originalImage, originalGray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.cvtColor(croppedTemplate, croppedGray, Imgproc.COLOR_BGR2GRAY);

        // 模板匹配
        Mat result = new Mat();
        Imgproc.matchTemplate(originalGray, croppedGray, result, Imgproc.TM_CCOEFF_NORMED);
        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
        Point matchLoc = mmr.maxLoc;

        // 计算滑块需要移动的距离
        double slideDistance = matchLoc.x;
        System.out.println("滑块需要移动的距离: " + slideDistance + " 像素");

        // 在原图上绘制匹配位置
        Rect matchRect = new Rect(
                (int) matchLoc.x,
                (int) matchLoc.y,
                croppedTemplate.cols(),
                croppedTemplate.rows()
        );
        Imgproc.rectangle(originalImage, matchRect, new Scalar(0, 255, 0), 2);

        // 保存结果到本地
        String outputPath = "D:\\tmp\\output_image.jpg";
        Imgcodecs.imwrite(outputPath, originalImage);
        System.out.println("匹配结果已保存到: " + outputPath);
        return slideDistance;
    }

    /**
     * 下载图片
     * @param imageUrl
     * @throws IOException
     */
    private static String downloadImage(String imageUrl) throws IOException {
        String[] split = imageUrl.split("/");

        URL url = new URL(imageUrl);
        String outputFilePath = split[split.length -1];
        try (InputStream in = url.openStream();
             OutputStream out = new FileOutputStream(outputFilePath)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
        return outputFilePath;
    }
}
