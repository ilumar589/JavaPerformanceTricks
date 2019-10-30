import net.openhft.chronicle.bytes.Byteable;
import net.openhft.chronicle.bytes.BytesStore;
import net.openhft.chronicle.values.Values;
import values.Point;
import values.PointArray;

public class Main {

    public static void main(String[] args) {
        PointArray pointArray = generateOffHeapValueArray();

        double sum = 0;
        for (int i = 0 ; i < 2_000_000; i++) {
            sum = pointArray.getPointAt(i).getX() + pointArray.getPointAt(i).getY();
        }

        System.out.println(sum);
    }


    private static PointArray generateOffHeapValueArray() {
        final int nrOfElements = 2_000_000;
        byte[] layoutContainer = new byte[nrOfElements * Point.sizeOf()];
        PointArray offHeapPointArray = Values.newNativeReference(PointArray.class);
        ((Byteable) offHeapPointArray).bytesStore(BytesStore.wrap(layoutContainer), 0, layoutContainer.length);

        for (int i = 0; i < nrOfElements; i++) {
            offHeapPointArray.setPointAt(i, generatePointValue(i, i, layoutContainer, i * Point.sizeOf(), Point.sizeOf())); // each point has 16 bytes so the offset starts from 0_16; 16_32 (1 * 16_1* 16 + 16)
        }

        return offHeapPointArray;
    }

    // use the array layout
    private static Point generatePointValue(long x, long y, byte[] layoutContainer, int offset, int length) {
        Point offHeapPoint = Values.newNativeReference(Point.class);
        ((Byteable) offHeapPoint).bytesStore(BytesStore.wrap(layoutContainer), offset, length);
        offHeapPoint.setX(x);
        offHeapPoint.setY(y);

        return offHeapPoint;
    }
}
