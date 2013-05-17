
package circles;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Stack;
import javax.swing.JPanel;

/**
 *
 * @author Sebastian Geisler <geisler.sebastian@googlemail.com>
 */
public class DrawPanel extends JPanel {

    private Image dbImage;
    private Graphics dbGraphics;
    private long frames;
    private long time;
    private int fps;
    private boolean paused;
    private Ball newBall;
    private Color actualColor;
    private Physics phys;
    private boolean history;
    private boolean directionArrow;
    private boolean mousePressed;
    private Coordinate secondMouseCoordinate;

    /**
     *
     * @param phys Physik-Engine Objekt, dessen Inhalte dargestellt werden sollen
     */
    public DrawPanel(final Physics phys) {
        super();
        this.phys = phys;
        
        /*
         * Größenanpassung an Physik-Engine
         */
        this.setSize(phys.getSize().getX().intValue(),
                phys.getSize().getY().intValue());
        paused = false;
        actualColor = Color.black;
        frames = 1;
        history = true;
        mousePressed = false;
        directionArrow = true;
        secondMouseCoordinate = new Coordinate();

        
        /*
         * Events, zum Erstellen neuer Bälle
         */
        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent me) {
                //System.out.println("mouse clicked");
                //phys.addBall(new Ball(newBall.getRadius(), newBall.getPosition(), newBall.getSpeed(), newBall.getFriction(), newBall.getBounce(), actualColor));//new Color(actualColor.getRed(), actualColor.getGreen(), actualColor.getBlue())));
                //System.out.println(me.getButton());
            }

            @Override
            public void mousePressed(MouseEvent me) {
                mousePressed = true;

            }

            @Override
            public void mouseReleased(MouseEvent me) {
                mousePressed = false;

                Coordinate speed = new Coordinate();
                speed.setX(
                        (newBall.getPosition().getX() - secondMouseCoordinate.getX()) / -50);
                speed.setY(
                        (newBall.getPosition().getY() - secondMouseCoordinate.getY()) / -50);

                phys.addBall(
                        new Ball(newBall.getRadius(), newBall.getPosition(),
                        speed, newBall.getFriction(), newBall.getBounce(),
                        actualColor));//new Color(actualColor.getRed(), actualColor.getGreen(), actualColor.getBlue())));
            }

            @Override
            public void mouseEntered(MouseEvent me) {
                //throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void mouseExited(MouseEvent me) {
                //throw new UnsupportedOperationException("Not supported yet.");
            }
        });

        /*
         * Event (Scrollen) zum Einstellen der Größe des neuen Balls
         */
        this.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent mwe) {
                if ((newBall.getRadius() - mwe.getWheelRotation()) >= 1) {
                    newBall.setRadius(
                            newBall.getRadius() - mwe.getWheelRotation());
                }

            }
        });

        newBall = new Ball(50.0, new Coordinate(100.0, 100.0), new Coordinate(
                1.0, 1.0), 0.0, 0.0, null);
    }


    /*
     * Zeichenmethode zum Zeichnen der Bälle, etc.
     */
    @Override
    public void paint(Graphics g) {

        /*
         * FPS-Zäler (Ausgabe findet erst am Ende von <code>paint(Graphics g)</code>)
         */
        if (frames == 1) {
            time = System.currentTimeMillis();
        }

        if (System.currentTimeMillis() - time != 0) {
            fps = (int) ((1000 * frames) / ((System.currentTimeMillis() - time)));

        }

        if (frames > 1000) {
            frames = 0;
        }
        
        frames++;

        
        
        /*
         * Bild für Doublebuffering erstellen
         */
        dbImage = createImage(phys.getSize().getX().intValue() + 2,
                phys.getSize().getY().intValue() + 2);
        dbGraphics = dbImage.getGraphics();

        
        /*
         * <code>phys.tick()</code> gehört nicht in die <code>paint(Graphics g)</code>-Methode
         * in Draw-Panel, es ist jedoch nicht sehr sinvoll diesen Abschnitt auszulagern, 
         * da man sonst die Synchronisation der Threads (diese werden von Swing (GUI-Bibliothek) 
         * automatisch erstellt) selbst schreiben müsste. Dies geschieht durch den Aufruf in 
         * <code>paint(Graphics g)</code> automatisch.
         */
        if (this.isPaused() == false) {
            phys.tick();
        }

        
        /*
         * Bälle zeichnen
         */
        Stack<Ball> baelle = phys.getBaelle();
        int anzBaelle = baelle.size();
        for (int i = 0; i < anzBaelle; i++) {
            dbGraphics.setColor(baelle.elementAt(i).getColor());
            int x = baelle.elementAt(i).getPosition().getX().intValue() - baelle.elementAt(
                    i).getRadius().intValue();
            int y = baelle.elementAt(i).getPosition().getY().intValue() - baelle.elementAt(
                    i).getRadius().intValue();
            int d = baelle.elementAt(i).getRadius().intValue() * 2;

            dbGraphics.drawOval(x, y, d, d);

            /*
             * Pfad des Balls zeichnen, falls aktiviert
             */
            if (history == true) {
                Stack<Coordinate> hist = baelle.elementAt(i).getHistory();
                int oldX = baelle.elementAt(i).getPosition().getX().intValue();
                int oldY = baelle.elementAt(i).getPosition().getY().intValue();
                for (int h = hist.size() - 1; h != 0; h--) {
                    Color c = new Color(baelle.elementAt(i).getColor().getRed(),
                            baelle.elementAt(i).getColor().getGreen(),
                            baelle.elementAt(i).getColor().getBlue(),
                            (255 * h / hist.size()));
                    
                    dbGraphics.setColor(c);
                    dbGraphics.drawLine(oldX, oldY,
                            hist.get(h).getX().intValue(),
                            hist.get(h).getY().intValue());
                    oldX = hist.get(h).getX().intValue();
                    oldY = hist.get(h).getY().intValue();
                }
            }
            
            /*
            *  Richtungspfeil zeichnen, falls aktiviert
            */
           if(directionArrow == true)
           {
               dbGraphics.setColor(baelle.elementAt(i).getColor());
               g.drawLine( baelle.elementAt(i).getPosition().getX().intValue(),
                           baelle.elementAt(i).getPosition().getY().intValue(),
                           baelle.elementAt(i).getPosition().getX().intValue() + (baelle.elementAt(i).getSpeed().getX().intValue() * 50),
                           baelle.elementAt(i).getPosition().getY().intValue() + (baelle.elementAt(i).getSpeed().getY().intValue() * 50));

           }
        }
        
        

        
        /*
         * Erfassen der Mausposition und Zeichnen der Vorlage für den nächsten 
         * zu erzeugenden Ball
         */
        int x = newBall.getPosition().getX().intValue() - newBall.getRadius().intValue();
        int y = newBall.getPosition().getY().intValue() - newBall.getRadius().intValue();
        int d = newBall.getRadius().intValue() * 2;

        if (mousePressed == false) {
            dbGraphics.setColor(Color.red);
        } else {
            dbGraphics.setColor(Color.green);
        }

        dbGraphics.drawOval(x, y, d, d);

        if (mousePressed == true) {
            dbGraphics.drawLine(newBall.getPosition().getX().intValue(),
                    newBall.getPosition().getY().intValue(),
                    secondMouseCoordinate.getX().intValue(),
                    secondMouseCoordinate.getY().intValue());
        }

        dbGraphics.setColor(Color.black);

        dbGraphics.drawRect(0, 0, phys.getSize().getX().intValue(),
                phys.getSize().getY().intValue());

        Point mousePos = getMousePosition();
        if (mousePos != null && mousePressed == false) {
            newBall.setPosition(new Coordinate(new Double(mousePos.x),
                    new Double(mousePos.y)));

        }
        if (mousePos != null && mousePressed == true) {
            secondMouseCoordinate = new Coordinate(new Double(mousePos.x),
                    new Double(mousePos.y));

        }
        
        
        /*
         * Ausgeben der FPS-Zahl
         */
        dbGraphics.drawString("FPS: " + String.valueOf(fps), 10, 20);

        g.drawImage(dbImage, 0, 0, this);

    }

    /**
     *
     * @return gibt zurück, ob die Simulation angehalten wurde.
     */
    public boolean isPaused() {
        return paused;
    }

    /**
     *
     * @param paused true: pausiert die Simulation; false: setzt Simulation fort
     */
    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    /**
     *
     * @return gibt die Standardfarbe für neue Bälle zurück
     */
    public Color getActualColor() {
        return actualColor;
    }

    /**
     *
     * @param actualColor setzt die Standardfarbe für neue Bälle
     */
    public void setActualColor(Color actualColor) {
        this.actualColor = actualColor;
    }

    /**
     *
     * @return gibt zurück, ob der Pfad der Bälle gezeichnet wird
     */
    public boolean isHistory() {
        return history;
    }

    /**
     *
     * @param history legt fest, ob der Pfad der Bälle gezeichnet wird
     */
    public void setHistory(boolean history) {
        this.history = history;
    }
    
}
