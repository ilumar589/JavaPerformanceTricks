package benchmarks;

import net.openhft.chronicle.bytes.Byteable;
import net.openhft.chronicle.bytes.BytesStore;
import net.openhft.chronicle.values.Values;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import values.Point;
import values.PointArray;
import values.PointImplForComp;

public class Benchmarks {

    @Benchmark
    @Fork(value = 1, warmups = 5)
    public static void sumPointsNative() {
        PointArray pointArray = generateOffHeapValueArray();

        double sum = 0;
        for (int i = 0 ; i < 1_000; i++) {
            sum = pointArray.getPointAt(i).getX() + pointArray.getPointAt(i).getY();
        }

    }

    @Benchmark
    @Fork(value = 1, warmups = 5)
    public static void sumPointsVM() {
        PointImplForComp[] pointImplForComps = generateVMPointArray();

        double sum = 0;
        for (int i = 0 ; i < 1_000; i++) {
            sum = pointImplForComps[i].x + pointImplForComps[i].y;
        }
    }

    private static PointArray generateOffHeapValueArray() {
        final int nrOfElements = 1_000;
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


    private static PointImplForComp[] generateVMPointArray() {
        final int nrOfElements = 1_000;
        PointImplForComp[] points = new PointImplForComp[nrOfElements];

        for (int i = 0; i < nrOfElements; i++) {
            points[i] = new PointImplForComp(i, i);
        }

        return points;
    }
}
