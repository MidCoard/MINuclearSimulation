package top.focess.mc.mi.nuclear.mi;

import org.checkerframework.checker.nullness.qual.NonNull;
import top.focess.mc.mi.nuclear.mc.Matter;
import top.focess.util.Pair;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class Texture {

    private static final Map<Matter, Texture> CACHE = new HashMap<>();

    private final InputStream inputStream;


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


    public static void main(String[] args) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(Texture.get(MIItems.HE_MOX_FUEL_ROD).inputStream);
        // write
        ImageIO.write(bufferedImage, "png", new File("he_mox_fuel_rod.png"));
    }

    private static final List<Pair<String, String>> COMPUTE_LIST = new ArrayList<>()
    {{
        this.add(Pair.of("u","0x39e600"));
        this.add(Pair.of("le_mox","0x00e7e5"));
        this.add(Pair.of("he_mox","0xcc87fa"));
        this.add(Pair.of("le","0x70a33c"));
        this.add(Pair.of("he","0xaae838"));
        this.add(Pair.of("carbon","0x444444"));
        this.add(Pair.of("cadmium","0x967224"));
    }};

    private static Pair<String,Color> computeMaterial(String name) {
        for (Pair<String,String> pair : COMPUTE_LIST)
            if (name.startsWith(pair.getKey()))
                return Pair.of(name.substring(pair.getKey().length() + 1), Color.decode(pair.getValue()));
        throw new IllegalArgumentException("Can't find material by: " + name);
    }

    //get InputStream by BufferedImage formatted as PNG
    private static InputStream getInputStream(BufferedImage image) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "png", os);
        return new ByteArrayInputStream(os.toByteArray());
    }

    private static final BiFunction<String, File,InputStream> MODERN_INDUSTRIALIZATION_HANDLER = (name,folder) -> {
        try {
            Pair<String, Color> computed = computeMaterial(name);
            String subpart = computed.getKey();
            BufferedImage image = ImageIO.read(new File(folder,subpart + ".png"));
            BufferedImage textured = blend(image, computed.getValue());
            // return inputstream by textured
            return getInputStream(textured);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    };

    public Texture(String name, File folder, BiFunction<String,File,InputStream> handler) {
        File textureFile = new File(folder, name + ".png");
        if (textureFile.exists())
            try {
                this.inputStream = textureFile.toURI().toURL().openStream();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        else {
            this.inputStream = handler.apply(name, folder);
        }
    }

    public static Texture get(@NonNull Matter matter) {
        if (CACHE.containsKey(matter))
            return CACHE.get(matter);
        String namespace = matter.getNamespace();
        String name = matter.getName();
        Texture texture;
        if (namespace.equals("minecraft"))
            texture = new Texture(name, new File("textures/minecraft"), (__,___)->{throw new IllegalArgumentException();});
        else if (namespace.equals("modern-industrialization"))
            texture = new Texture(name, new File("textures/mi"),  MODERN_INDUSTRIALIZATION_HANDLER);
        else throw new IllegalArgumentException("Unknown namespace: " + namespace);
        CACHE.put(matter, texture);
        return texture;
    }

    public InputStream getInputStream() {
        return this.inputStream;
    }
}
