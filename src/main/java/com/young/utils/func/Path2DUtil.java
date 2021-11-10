package com.young.utils.func;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 *     (90° 37)                     (180° 23)
 *
 *                    50,50
 *
 *     (0° 46)                      (270° 34)
 */
public class Path2DUtil {
    public static void main(String[] args) throws IOException {
        Path2D.Double pd = initPath();

        System.out.println(pd.contains(28, 50));
        draw("C:\\Users\\Administrator\\Desktop\\test.png", pd);

//        23 34 46 37
    }

    public static Path2D.Double initPath() {
        Path2D.Double pd = new Path2D.Double();

        Arc2D.Double arc = new Arc2D.Double();
        arc.setArcByCenter(50, 50, 46, 0, 90, 2);
        pd.append(arc, false);

        Arc2D.Double arc1 = new Arc2D.Double();
        arc1.setArcByCenter(50, 50, 37, 90, 90, 2);
        pd.append(arc1, false);

        Arc2D.Double arc2 = new Arc2D.Double();
        arc2.setArcByCenter(50, 50, 23, 180, 90, 2);
        pd.append(arc2, false);

        Arc2D.Double arc3 = new Arc2D.Double();
        arc3.setArcByCenter(50, 50, 34, 270, 90, 2);
        pd.append(arc3, false);

//
        //移到起始点
//        pd.moveTo(25, 25);
        //连接所有点
//        pd.lineTo(250, 300);
//        pd.lineTo(750, 300);
//        pd.lineTo(750, 250);
//        pd.lineTo(250, 250);
//
        //关闭
        pd.closePath();

        return pd;
    }

    private static final Color RGB_25_50 = new Color(255, 255, 255, 100);
    private static final Color RGB_100_250 = new Color(211, 12, 213);

    public static void draw(String outPath, Path2D path2D) throws IOException {
        int len = 10000;
        int[] rgbArr = new int[len];
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100; j++) {
                if (path2D.contains(i, j)) {
                    rgbArr[i * 100 + j] = RGB_25_50.getRGB();
                }
            }
        }
        rgbArr[83 * 100 + 50] = RGB_100_250.getRGB();
        int width = 100;
        int height = 100;

        BufferedImage imgOut = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        imgOut.setRGB(0, 0, width, height, rgbArr, 0, width);
        imgOut = convertWhiteTransparent(imgOut);
        OutputStream os = new FileOutputStream(outPath);
        ImageIO.write(imgOut, "png", os);
        os.close();
    }

    public static BufferedImage convertWhiteTransparent(BufferedImage image) {
        ImageIcon imageIcon = new ImageIcon(image);
        BufferedImage bufferedImage = new BufferedImage(
                imageIcon.getIconWidth(), imageIcon.getIconHeight(),
                BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g2D = (Graphics2D) bufferedImage.getGraphics();
        g2D.drawImage(imageIcon.getImage(), 0, 0,
                imageIcon.getImageObserver());
        int alpha;
        for (int j1 = bufferedImage.getMinY(); j1 < bufferedImage
                .getHeight(); j1++) {
            for (int j2 = bufferedImage.getMinX(); j2 < bufferedImage
                    .getWidth(); j2++) {
                int rgb = bufferedImage.getRGB(j2, j1);
                if (colorInRange(rgb))
                    alpha = 0;
                else
                    alpha = 255;
                rgb = (alpha << 24) | (rgb & 0x00ffffff);
                bufferedImage.setRGB(j2, j1, rgb);
            }
        }
        g2D.drawImage(bufferedImage, 0, 0, imageIcon.getImageObserver());
        return bufferedImage;
    }

    private static int color_range = 210;

    private static boolean colorInRange(int color) {
        int red = (color & 0xff0000) >> 16;
        int green = (color & 0x00ff00) >> 8;
        int blue = (color & 0x0000ff);
        if (red >= color_range && green >= color_range && blue >= color_range)
            return true;
        return false;
    }

}
