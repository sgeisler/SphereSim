
package circles;

import java.awt.Color;
import java.util.Stack;

/**
 *
 * @author Sebastian Geisler <geisler.sebastian@googlemail.com>
 */
public abstract class Objekt {

    /**
     * Position des Objekts
     */
    protected Coordinate position;
    /**
     * Geschindigkeitsvektor des Objekts
     */
    protected Coordinate speed;
    /**
     * Spezifische Reibung des Objekts
     * <b>ACHTUNG: noch nicht in Physik implementiert</b>
     */
    protected Double friction,
    /**
     * Spezifische Elastizität des Balls
     * <b>ACHTUNG: noch nicht in Physik implementiert</b>
     */
    bounce;
    /**
     * Farbe des Objekts
     */
    protected Color color;
    /**
     * Speichert die letzten Positionen des Objekts
     */
    protected Stack<Coordinate> history;
    /**
     * Zählvariable für Positionsverlauf (history)
     */
    protected int round;

    /**
     *
     * @param position Startposition des Objekts
     * @param speed Geschwindigkeitsvektor des Objekts
     * @param friction spezifische Reibung des Objekts (<b>noch nicht implementiert</b>)
     * @param bounce spezifische Elastizität des Objekts (<b>noch nicht implementiert</b>)
     * @param c Farbe des Objekts
     */
    public Objekt(Coordinate position, Coordinate speed, Double friction,
            Double bounce, Color c) {
        this.position = position;
        this.speed = speed;
        this.friction = friction;
        this.bounce = bounce;
        this.color = c;
        round = 0;
    }

    /**
     * Erklärung von Gettern und Settern siehe Variablendeklaration
     * 
     */
    public Coordinate getPosition() {
        return position;
    }

    public void setPosition(Coordinate position) {
        this.position = position;
    }

   
    public Coordinate getSpeed() {
        return speed;
    }

   
    public void setSpeed(Coordinate speed) {
        this.speed = speed;
        this.addHistory();
    }

   
    public Double getFriction() {
        return friction;
    }

   
    public void setFriction(Double friction) {
        this.friction = friction;
    }

    
    public Double getBounce() {
        return bounce;
    }

   
    public void setBounce(Double bounce) {
        this.bounce = bounce;
    }

   
    public Color getColor() {
        return color;
    }

   
    public void setColor(Color color) {
        this.color = color;
    }

 
    public Stack<Coordinate> getHistory() {
        return history;
    }

   
    public void addHistory() {
        history.push(new Coordinate(position.getX(), position.getY()));
        /*
         * bei zu vielen Einträgen letzten löschen
         */
        if (history.size() > 50) {
            history.remove(0);
        }
    }

    /**
     * einen Simulationsschritt ausführen (aller 50 Schritte eine Position zur History hinzufügen)
     */
    public void step() {
        if (round >= 200) {
            this.addHistory();
            round = 0;
        } else {
            round++;
        }
    }
}
