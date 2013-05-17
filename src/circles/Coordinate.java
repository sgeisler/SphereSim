
package circles;

/**
 *
 * @author Sebastian Geisler <geisler.sebastian@googlemail.com>
 */
public class Coordinate {

    private Double x, y;

    public Coordinate(Double x, Double y) {
        this.x = x;
        this.y = y;
    }

    public Coordinate() {
        this.x = 0.0;
        this.y = 0.0;

    }

    public Double[] getCoordiantes() {

        Double[] d = new Double[2];
        d[0] = x;
        d[1] = y;
        return d;

    }

    public void setCoordinates(Double x, Double y) {

        this.x = x;
        this.y = y;
        return;

    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }
 
    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    /**
     *
     * @param angle Winkel, um den der Vektor (die Koordinaten) gedreht werden soll
     * @return gedrehter Vektor
     */
    public Coordinate rotateVector(Double angle) //in Bogenmaß
    {
        Coordinate gedreht = new Coordinate();
        gedreht.setX(
                (this.getX().doubleValue() * Math.cos(angle.doubleValue())) - (this.getY().doubleValue() * Math.sin(
                angle)));
        gedreht.setY(
                (this.getX().doubleValue() * Math.sin(angle.doubleValue())) + (this.getY().doubleValue() * Math.cos(
                angle)));

        return gedreht;

    }
}
