package de.baerinteractive.pixlhash;

import android.graphics.Bitmap;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by fettn on 19/02/16.
 */
public class PixlHash {

    public static final int WIDTH = 16;
    public static final int HEIGHT = 16;
    public static final int DEFAULT_TILE_WIDTH = 16;
    public static final int DEFAULT_TILE_HEIGHT = 16;
    private static final int FOREGROUND = 0;
    private static final int BACKGROUND = 1;
    private static final int COLOR_SIZE = 4;
    private static final int MINIMUM_COLOR_DISTANCE = 1500;
    private static final int X_COORDINATE = 0;
    private static final int Y_COORDINATE = 1;
    private static final int IS_SET = 2;

    private final int tileWidth;
    private final int tileHeight;
    private static MessageDigest messageDigest;

    public PixlHash() throws NoSuchAlgorithmException {
        this(DEFAULT_TILE_WIDTH, DEFAULT_TILE_HEIGHT);
    }

    public PixlHash(int tileWidth, int tileHeight) throws NoSuchAlgorithmException {
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        messageDigest = MessageDigest.getInstance("SHA256");
    }

    public Bitmap getPixlHash(String input) {
        byte[] hash = messageDigest.digest(input.getBytes());
        return getPixlHash(hash);
    }

    protected Bitmap getPixlHash(byte[] input) {
        int[] image = createHash(input);
        return Bitmap.createBitmap(image, WIDTH * tileWidth, HEIGHT * tileHeight, Bitmap.Config.ARGB_8888);
    }

    private int[] createHash(byte[] input) {
        int[] colors = pickColors(input);
        int[] hash = new int[WIDTH * tileWidth * HEIGHT * tileHeight];
        for (int position = 0; position < hash.length; position++) {
            hash[position] = colors[BACKGROUND];
        }

        for (int position = 0; position < (input.length - 1) * 4; position++) {
            int[] pixel = getPixelData(input, position);
            boolean set = pixel[IS_SET] != 0;
            if (set) {
                int x = pixel[X_COORDINATE];
                int y = pixel[Y_COORDINATE];
                drawRect(hash, x, y, colors[FOREGROUND]);
            }
        }

        return hash;
    }

    private void drawRect(int[] hash, int x, int y, int color) {
        for (int deltaY = 0; deltaY < tileHeight; deltaY++) {
            for (int deltaX = 0; deltaX < tileWidth; deltaX++) {
                int pos1 = ((y * tileHeight + deltaY) * (WIDTH * tileWidth) + (x * tileWidth) + deltaX);
                int pos2 = ((y * tileHeight + deltaY) * (WIDTH * tileWidth) + ((WIDTH - x - 1) * tileWidth) + deltaX);
                hash[pos1] = color;
                hash[pos2] = color;
            }
        }
    }

    protected static int[] getPixelData(byte[] input, int position) {
        int offset = position / (input.length - 1);
        int start = position % (input.length - 1);
        long data = (((long) input[start]) & 0xFF) << 8 | (((long) input[start + 1]) & 0xFF);
        int coordinateData = (int) ((data >> (16 - 7 - offset)) & 0x7F);
        int setData = (int) ((data >> (16 - 8 - offset)) % 2);
        return new int[]{coordinateData % 8, coordinateData / 8, setData};
    }

    private static int[] pickColors(byte[] input) {
        int color1 = 0xFFFFFFFF;
        int color2 = 0;
        for (int start = 0; start < input.length - 6; start++) {
            color1 = getColor(input, start);
            color2 = getColor(input, start + 3);
            if (colorDistance(color1, color2) > MINIMUM_COLOR_DISTANCE) {
                break;
            }
        }
        return new int[]{color1, color2};
    }

    private static int colorDistance(int color1, int color2) {
        int sum = 0;
        for (int i = 0; i < COLOR_SIZE; i++) {
            int diff = color1 % 256 - color2 % 256;
            sum += diff * diff;
            color1 /= 256;
            color2 /= 256;
        }
        return sum;
    }

    private static int getColor(byte[] input, int start) {
        int ret = 0xFF;
        for (int offset = 0; offset < 3; offset++) {
            ret *= 256;
            ret += input[start + offset];
        }
        return ret;
    }
}
