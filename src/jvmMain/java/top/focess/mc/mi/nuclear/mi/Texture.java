package top.focess.mc.mi.nuclear.mi;

import androidx.compose.ui.res.ResourceLoader;
import com.google.common.io.ByteStreams;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import top.focess.mc.mi.nuclear.mc.Fluid;
import top.focess.mc.mi.nuclear.mc.Matter;
import top.focess.util.Pair;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class Texture {

    private static final Map<Matter, Texture> CACHE = new HashMap<>();

    private final byte[] bytes;

    //mix two images
    private static BufferedImage mix(BufferedImage image, BufferedImage image2) {
        // check if the sizes are equal
        if (image.getWidth() != image2.getWidth() || image.getHeight() != image2.getHeight())
            throw new IllegalArgumentException("Images must have the same size");
        // create the new image, canvas size is the max. of both image sizes
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                // get the rgb values for the current pixel
                int rgb = image.getRGB(x, y);
                int rgb2 = image2.getRGB(x, y);
                // calculate the new color
                int alpha = (rgb >> 24) & 0xFF;
                int alpha2 = (rgb2 >> 24) & 0xFF;
                int newRed = (alpha + alpha2) != 0 ? (((rgb >> 16) & 0xFF) * alpha + ((rgb2 >> 16) & 0xFF) * alpha2) / (alpha + alpha2) : 0;
                int newGreen = (alpha + alpha2) != 0 ? (((rgb >> 8) & 0xFF) * alpha + ((rgb2 >> 8) & 0xFF) * alpha2) / (alpha + alpha2) : 0;
                int newBlue = (alpha + alpha2) != 0 ? ((rgb & 0xFF) * alpha + (rgb2 & 0xFF) * alpha2) / (alpha + alpha2) : 0;
                // set the new rgb values
                rgb = ((alpha + alpha2) << 24) | (newRed << 16) | (newGreen << 8) | newBlue;
                newImage.setRGB(x, y, rgb);
            }
        }
        // return the new image
        return newImage;
    }


    //blend image with color
    public static BufferedImage blend(BufferedImage image, Color color) {
        // create the new image, canvas size is the max. of both image sizes
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                // get the rgb values for the current pixel
                int rgb = image.getRGB(x, y);
                double luminance = (0.212671 * ((rgb >> 16) & 0xFF) + 0.715160 * ((rgb >> 8) & 0xFF) + 0.072169 * (rgb & 0xFF)) / 255;
                // calculate the new color
                int alpha = (rgb >> 24) & 0xFF;
                int newRed = (int) (color.getRed() * luminance);
                int newGreen = (int) (color.getGreen() * luminance);
                int newBlue = (int) (color.getBlue() * luminance);
                // set the new rgb values
                rgb = (alpha << 24) | (newRed << 16) | (newGreen << 8) | newBlue;
                newImage.setRGB(x, y, rgb);
            }
        }
        // return the new image
        return newImage;
    }

    public static BufferedImage deepBlend(BufferedImage image, Color color) {
        // create the new image, canvas size is the max. of both image sizes
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                // get the rgb values for the current pixel
                int rgb = image.getRGB(x, y);
                double luminance = (0.212671 * ((rgb >> 16) & 0xFF) + 0.715160 * ((rgb >> 8) & 0xFF) + 0.072169 * (rgb & 0xFF)) / 255;
                // calculate the new color
                int alpha = (rgb >> 24) & 0xFF;
                int newRed = (int) (color.getRed() * luminance);
                int newGreen = (int) (color.getGreen() * luminance);
                int newBlue = (int) (color.getBlue() * luminance);
                float[] hsb = Color.RGBtoHSB(newRed, newGreen, newBlue, null);
                // set the new rgb values
                rgb = (alpha << 24) + 0xFFFFFF & Color.HSBtoRGB(hsb[0], 0.2f * hsb[1], 0.5f * hsb[2]);
                newImage.setRGB(x, y, rgb);
            }
        }
        // return the new image
        return newImage;
    }

    private static final List<Pair<String, String>> COMPUTE_LIST = new ArrayList<>()
    {{
        this.add(Pair.of("u","0x39e600"));
        this.add(Pair.of("le_mox","0x00e7e5"));
        this.add(Pair.of("he_mox","0xcc87fa"));
        this.add(Pair.of("leu","0x70a33c"));
        this.add(Pair.of("heu","0xaae838"));
        this.add(Pair.of("carbon","0x444444"));
        this.add(Pair.of("cadmium","0x967224"));
    }};

    private static final List<Pair<String, BiFunction<Color,String, BufferedImage>>> DEEP_COMPUTE_LIST = new ArrayList<>() {{
        this.add(Pair.of("fuel_rod_depleted", (color,path) -> {
            BufferedImage fuelRod;
            try {
                fuelRod = ImageIO.read(ResourceLoader.Companion.getDefault().load(path + "/fuel_rod.png"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return deepBlend(fuelRod, color);
        }));
    }};

    private static Pair<String,Color> computeMaterial(String name) {
        for (Pair<String,String> pair : COMPUTE_LIST)
            if (name.startsWith(pair.getKey()))
                return Pair.of(name.substring(pair.getKey().length() + 1), Color.decode(pair.getValue()));
        throw new IllegalArgumentException("Can't find material by: " + name);
    }

    private static BufferedImage computeImage(String path, String name, Color color) throws IOException {
        for (Pair<String, BiFunction<Color,String , BufferedImage>> pair : DEEP_COMPUTE_LIST)
            if (name.contains(pair.getKey()))
                try {
                    return pair.getValue().apply(color,path);
                } catch (Exception ignored) {}
        BufferedImage image = ImageIO.read(ResourceLoader.Companion.getDefault().load(path + "/" + name + ".png"));
        return blend(image, color);
    }

    //get InputStream by BufferedImage formatted as PNG
    private static byte[] getByteArray(BufferedImage image) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "png", os);
        return os.toByteArray();
    }

    private static final BiFunction<Matter, String, byte[]> MODERN_INDUSTRIALIZATION_HANDLER = (matter,path) -> {
        try {
            if (matter instanceof Fluid) {
                Color color = ((Fluid) matter).getColor();
                BufferedImage image = ImageIO.read(ResourceLoader.Companion.getDefault().load(path + "/fluid/bucket_content.png"));
                BufferedImage content = blend(image, color);
                BufferedImage bucket = mix(content, ImageIO.read(ResourceLoader.Companion.getDefault().load(path + "/fluid/bucket.png")));
                return getByteArray(bucket);
            }
            String name = matter.getName();
            Pair<String, Color> computed = computeMaterial(name);
            return getByteArray(computeImage(path, computed.getKey(), computed.getValue()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    };

    private Texture(Matter matter, String path, BiFunction<Matter, String, byte[]> handler) {
        byte[] bytes;
        String name = matter.getName();
        String texture = path + "/" + name + ".png";
        try {
            bytes = ByteStreams.toByteArray(ResourceLoader.Companion.getDefault().load(texture));
        } catch (Exception e) {
            bytes = handler.apply(matter, path);
        }
        this.bytes = bytes;
    }

    public static Texture get(@NonNull Matter matter) {
        if (CACHE.containsKey(matter))
            return CACHE.get(matter);
        String namespace = matter.getNamespace();
        Texture texture;
        if (namespace.equals("minecraft"))
            texture = new Texture(matter, "textures/minecraft", (__,___)->{throw new IllegalArgumentException();});
        else if (namespace.equals("modern-industrialization"))
            texture = new Texture(matter, "textures/mi",  MODERN_INDUSTRIALIZATION_HANDLER);
        else throw new IllegalArgumentException("Unknown namespace: " + namespace);
        CACHE.put(matter, texture);
        return texture;
    }

    @NotNull
    public InputStream getInputStream() {
        return new ByteArrayInputStream(bytes);
    }
}
