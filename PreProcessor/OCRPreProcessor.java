package com.example.demo;

import nu.pattern.OpenCV;
import org.opencv.core.Core;
import org.opencv.core.MatOfDouble;
import org.opencv.photo.Photo;
import org.springframework.stereotype.Service;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.awt.image.ImagingOpException;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.File;
import java.io.ByteArrayInputStream;
import java.math.MathContext;

@Service
public class OCRPreProcessor {
    static{
        OpenCV.loadLocally();
    }
    public BufferedImage processImage(File file) throws IOException{
        Mat src = Imgcodecs.imread(file.getAbsolutePath());
        if(src.empty()){
            throw new IllegalArgumentException("Couldn't read the image from the file: " + file.getName());
        }
        Mat upscaled  = upscale(src, 2.0);
        Mat gray      = toGrayScale(upscaled);
        Mat threshold = applyThresholde(gray);
        BufferedImage result = MatToBufferedImage(threshold);
        src.release();
        gray.release();
        upscaled.release();
        return result;
    }
    private Mat toGrayScale(Mat src){
        Mat gray = new Mat();
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
        return gray;
    }
    private Mat applyThresholde(Mat gray){
        Mat threshold = new Mat();
        MatOfDouble mean = new MatOfDouble();
        MatOfDouble stddev = new MatOfDouble();
        Core.meanStdDev(gray, mean, stddev);
        if(stddev.get(0, 0)[0] < 60){
            Imgproc.threshold(gray, threshold, 0, 255, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU);
        }else{
            Imgproc.adaptiveThreshold(gray, threshold, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 17, 4);
        }
        return threshold;
    }
    private BufferedImage MatToBufferedImage(Mat matrix) throws IOException{
        MatOfByte mob = new MatOfByte();
        Imgcodecs.imencode(".png", matrix, mob);
        return ImageIO.read(new ByteArrayInputStream(mob.toArray()));
    }
    private Mat upscale(Mat src, double factor){
        Mat upscaled = new Mat();
        Imgproc.resize(src, upscaled, new org.opencv.core.Size(0, 0), factor, factor, Imgproc.INTER_CUBIC);
        return upscaled;
    }
    /*private Mat morphClean(Mat binary){
        Mat cleaned = new Mat();
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new org.opencv.core.Size(1, 1));
        Imgproc.morphologyEx(binary, cleaned, Imgproc.MORPH_CLOSE, kernel);
        kernel.release();
        return cleaned;
    }
    private Mat denoiseImage(Mat gray){
        Mat denoised = new Mat();
        Photo.fastNlMeansDenoising(gray, denoised, 5, 7, 21);
        return denoised;
    }
    private Mat sharpenImage(Mat denoised){
        Mat sharpened = new Mat();
        Mat blur = new Mat();
        Imgproc.GaussianBlur(denoised, blur, new org.opencv.core.Size(0, 0), 3);
        Core.addWeighted(denoised, 1.3, blur, -0.3, 0, sharpened);
        blur.release();
        return sharpened;
    }*/
}
