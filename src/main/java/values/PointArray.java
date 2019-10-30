package values;

import net.openhft.chronicle.values.Array;

public interface PointArray {
    @Array(length=2_000_000)
    Point getPointAt(int index);
    void setPointAt(int index, Point point);
//    Point addPointAt(int index, Point addition); to look int how it should be defined
}
