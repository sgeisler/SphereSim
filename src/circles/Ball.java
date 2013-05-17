
package circles;

import java.awt.Color;
import java.util.Stack;

/**
 *
 * @author Sebastian Geisler <geisler.sebastian@googlemail.com>
 */
public class Ball extends Objekt {

    private Double radius;
    int lastcol;

    /**
     *
     * @param radius radius des Balls
     * @param position position des Balls
     * @param speed Geschwindigkeitsvektor des Balls
     * @param friction Reibung des Balls (noch nicht implementiert, deshalb auf 0 setzen)
     * @param bounce gibt an, wie viel Impuls-Energie nach dem Aufprall erhalten bleibt (noch nicht implementiert, deshalb auf 0 setzen)
     * @param c Farbe des Balls
     */
    public Ball(Double radius, Coordinate position, Coordinate speed,
            Double friction, Double bounce, Color c) {
        super(position, speed, friction, bounce, c);
        this.radius = radius;
        this.lastcol = 0;
        history = new Stack<Coordinate>();
    }

    /**
     *
     * @return Radius des Balls
     */
    public Double getRadius() {

        return radius;
    }

    /**
     *
     * @param radius setzt Radius des Balls
     */
    public void setRadius(Double radius) {

        this.radius = radius;
    }

    /**
     * lässt den Ball sich einen Schritt weiter bewegen
     */
    @Override
    public void step() {
        super.step();
        this.position.setCoordinates(this.position.getX() + this.speed.getX(),
                this.position.getY() + this.speed.getY());

    }

    /**
     *
     * @param b zweiter Ball, mit dem die Überschneidung geprüft werden soll
     * @return gibt an, ob sich dieser Ball und Ball b überschneiden
     */
    public boolean intersects(Ball b) {

        if ((this.radius.doubleValue() + b.getRadius().doubleValue()) > (Math.sqrt(
                Math.pow(
                (this.position.getX().doubleValue() + this.speed.getX()) - (b.getPosition().getX().doubleValue() + b.getSpeed().getX()),
                2.0) + Math.pow(
                (this.position.getY().doubleValue() + this.speed.getY()) - (b.getPosition().getY().doubleValue() + b.getSpeed().getY()),
                2.0)))) {
            if (this.lastcol <= 0 && b.lastcol <= 0) {
                this.lastcol = 0;
                b.lastcol = 0;
                return true;
            }
            if (this.lastcol > 0) {
                this.lastcol--;
            }
            if (b.lastcol > 0) {
                b.lastcol--;
            }
            return false;


        } else {
            if (this.lastcol > 0) {
                this.lastcol--;
            }
            if (b.lastcol > 0) {
                b.lastcol--;
            }
            return false;
        }

    }
}
