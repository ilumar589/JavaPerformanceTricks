package values;

public interface Point {
    double getX();
    void setX(double x);

    double getY();
    void setY(double y);

    static int sizeOf() { return 64; }
}
