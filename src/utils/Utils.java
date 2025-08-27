package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import org.joml.Quaternionf;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryUtil;

public class Utils {

    public static String loadResource(String fileName) throws Exception {
        String result;
        try (InputStream in = Utils.class.getResourceAsStream(fileName);
             Scanner scanner = new Scanner(in, java.nio.charset.StandardCharsets.UTF_8.name())) {
            result = scanner.useDelimiter("\\A").next();
        }
        return result;
    }

    public static List<String> readAllLines(String fileName) throws Exception {
        List<String> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(Class.forName(Utils.class.getName()).getResourceAsStream(fileName)))) {
            String line;
            while ((line = br.readLine()) != null) {
                list.add(line);
            }
        }
        return list;
    }

    public static int[] listIntToArray(List<Integer> list) {
        int[] result = list.stream().mapToInt((Integer v) -> v).toArray();
        return result;
    }

    public static float[] listFloatToArray(List<Float> list) {
        int size = list != null ? list.size() : 0;
        float[] floatArr = new float[size];
        for (int i = 0; i < size; i++) {
            floatArr[i] = list.get(i);
        }
        return floatArr;
    }

    public static String[] listStringToArray(List<String> list) {
        int size = list != null ? list.size() : 0;
        String[] stringsArr = new String[size];
        for (int i = 0; i < size; i++) {
            stringsArr[i] = list.get(i);
        }
        return stringsArr;
    }

    public static boolean existsResourceFile(String fileName) {
        boolean result;
        try (InputStream is = Utils.class.getResourceAsStream(fileName)) {
            result = is != null;
        } catch (Exception excp) {
            result = false;
        }
        return result;
    }

    public static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException {
        ByteBuffer buffer;

        Path path = Paths.get(resource);

        if (Files.isReadable(path)) {
            try (SeekableByteChannel fc = Files.newByteChannel(path)) {
                buffer = MemoryUtil.memAlloc((int) fc.size() + 1);
                while (fc.read(buffer) != -1) ;
            }
        } else {
            try (
                InputStream source = Utils.class.getResourceAsStream(resource);

                ReadableByteChannel rbc = Channels.newChannel(source)) {
                buffer = MemoryUtil.memAlloc(bufferSize);

                while (true) {
                    int bytes = rbc.read(buffer);
                    if (bytes == -1) {
                        break;
                    }
                    if (buffer.remaining() == 0) {
                        buffer = resizeBuffer(buffer, buffer.capacity() * 2);
                    }
                }
            }
        }

        buffer.flip();
        return buffer;
    }

    private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }

    /**
     * Converts Euler angles (in degrees) for rotations around X, Y, and Z axes
     * into a quaternion representing the combined rotation.
     *
     * @param xDegrees rotation angle around the X axis in degrees
     * @param yDegrees rotation angle around the Y axis in degrees
     * @param zDegrees rotation angle around the Z axis in degrees
     * @return Quaternionf representing the rotation
     */
    public static Quaternionf eulerToQuaternion(float xDegrees, float yDegrees, float zDegrees) {
        // Convert degrees to radians since JOML uses radians
        float xRad = (float) Math.toRadians(xDegrees);
        float yRad = (float) Math.toRadians(yDegrees);
        float zRad = (float) Math.toRadians(zDegrees);

        // Create quaternions for rotation around each axis
        Quaternionf qx = new Quaternionf().rotationX(xRad);
        Quaternionf qy = new Quaternionf().rotationY(yRad);
        Quaternionf qz = new Quaternionf().rotationZ(zRad);

        // Combine rotations: order matters (here Z * Y * X)
        // This means rotation around Z first, then Y, then X
        Quaternionf q = new Quaternionf();
        qz.mul(qy).mul(qx, q);

        return q;
    }

    public static boolean isStringInArray(String str, String[] array) {
        return  Arrays.asList(array).contains(str);
    }

    public static boolean isValueSmallerThen(float value, float threshold) {
        return value < threshold;
    }

    /**
     * Checks if two rectangles in 2D space intersect.
     *
     * @param x1 X-coordinate of the first rectangle
     * @param y1 Y-coordinate of the first rectangle
     * @param w1 Width of the first rectangle
     * @param h1 Height of the first rectangle
     * @param x2 X-coordinate of the second rectangle
     * @param y2 Y-coordinate of the second rectangle
     * @param w2 Width of the second rectangle
     * @param h2 Height of the second rectangle
     * @return true if rectangles intersect, false otherwise
     */
    public static boolean intersects(
            float x1, float y1, float w1, float h1,
            float x2, float y2, float w2, float h2) {

        return x1 < x2 + w2 &&   // right side of rect1 > left side of rect2
                x1 + w1 > x2 &&   // left side of rect1 < right side of rect2
                y1 < y2 + h2 &&   // bottom of rect1 > top of rect2
                y1 + h1 > y2;     // top of rect1 < bottom of rect2
    }
}
