import java.util.Objects;

public class Coordinate {
    private int x, y, z;

    public Coordinate(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        else if (!(obj instanceof Coordinate))
            return false;
        Coordinate that = (Coordinate) obj;
        return this.x == that.x && this.y == that.y && this.z == that.z;
    }
}
