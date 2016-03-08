package de.baerinteractive.pixlhash;

import static org.junit.Assert.*;

public class PixlHashTest {

    @org.junit.Test
    public void getPixelData() throws Exception {
        int[] result = PixlHash.getPixelData(new byte[]{(byte)0xAB, 0x10}, 0);
        assertEquals(5, result[0]);
        assertEquals(10, result[1]);
        assertEquals(1, result[2]);

        result = PixlHash.getPixelData(new byte[]{(byte)0xAB, 0x10}, 1);
        assertEquals(3, result[0]);
        assertEquals(5, result[1]);
        assertEquals(0, result[2]);

        result = PixlHash.getPixelData(new byte[]{(byte)0xAB, 0x10}, 2);
        assertEquals(6, result[0]);
        assertEquals(10, result[1]);
        assertEquals(0, result[2]);

        result = PixlHash.getPixelData(new byte[]{(byte)0xAB, 0x10}, 3);
        assertEquals(4, result[0]);
        assertEquals(5, result[1]);
        assertEquals(0, result[2]);
    }
}