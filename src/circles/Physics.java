
package circles;

import java.awt.Color;
import java.io.File;
import java.util.Stack;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;

/**
 *
 * @author Sebastian Geisler <geisler.sebastian@googlemail.com>
 */
public class Physics {

    private Stack<Ball> baelle;
    private Coordinate size;
    private boolean gravity;
    private boolean friction;
    private int gravIntensity;
    private int frictIntensity;

    /**
     *
     * @param width Breite der Simulationumgebung
     * @param height  Höhe der Simulationsumgebung
     */
    public Physics(int width, int height) {
        baelle = new Stack<Ball>();
        size = new Coordinate(new Double(width), new Double(height));
        gravity = false;
        friction = false;
        gravIntensity = 25;
        frictIntensity = 25;
    }

    /**
     *
     * @return gibt Größe der Simulationsumgebung zurück
     */
    public Coordinate getSize() {
        return size;
    }

    /**
     *
     * @param size legt die Größe der Simulationsumgebung fest
     */
    public void setSize(Coordinate size) {
        this.size = size;
    }

    /**
     *
     * @return gibt einen Stack zurück, der alle Bälle enthällt
     */
    public Stack<Ball> getBaelle() {
        return baelle;
    }

    /**
     *
     * @param b Ball, der hinzugefügt werden soll
     */
    public void addBall(Ball b) {
        boolean vorhanden = false;

        /*
         * prüft, dass der neu generierte Ball keinen anderen überlappt
         */
        for (int i = 0; i < baelle.size(); i++) {
            if (baelle.get(i).intersects(b)) {
                vorhanden = true;
                //System.out.print("vorhanden");
            }
        }

        if (vorhanden == false) {
            b.addHistory();
            baelle.addElement(b);
        }
    }

    /**
     *Die Methode tick() lässt die Simulation einen Schritt weiter laufen
     */
    public void tick()
    {


        int anzBaelle = baelle.size();

        //pruefen, ob 2 baelle kollidieren
        for (int i = 0; i < anzBaelle; i++) {

            if (friction == true) {
                baelle.elementAt(i).setSpeed(new Coordinate(
                        baelle.elementAt(i).getSpeed().getX() - (baelle.elementAt(
                        i).getSpeed().getX() * 0.001 * frictIntensity / 25),
                        baelle.elementAt(i).getSpeed().getY() - (baelle.elementAt(
                        i).getSpeed().getY() * 0.001 * frictIntensity / 25)));
            }

            if (gravity == true) {
                baelle.elementAt(i).setSpeed(new Coordinate(
                        baelle.elementAt(i).getSpeed().getX(), baelle.elementAt(
                        i).getSpeed().getY() + (0.01 * gravIntensity / 25)));
            }

            checkWallCollision(baelle.elementAt(i));
            boolean col = false;
            for (int n = (i + 1); n < anzBaelle; n++) {

                //System.out.println(String.valueOf(baelle.get(i).lastcol));
                if (baelle.elementAt(i).intersects(baelle.elementAt(n))) {
                    //System.out.println(String.valueOf(i) + " - " + String.valueOf(n) + " - true");
                    onCollision(baelle.elementAt(i), baelle.elementAt(n));
                    col = true;
                }

            }
            if (col == false) {
                baelle.elementAt(i).step();
            }
        }
    }

    
    /*
     * wird aufgerufen, wenn eine Kollision zweier Bälle festgestellt wurde, sie 
     * berechnet den Abprall
     */
    private void onCollision(Ball b1, Ball b2) {

        Coordinate s1, s2, gedreht1, gedreht2;
        s1 = b1.getSpeed();
        s2 = b2.getSpeed();

        Double angle; //unbedingt in Bogenmaß angeben:
        angle = Math.tanh(
                (b2.getPosition().getY() - b1.getPosition().getY()) / (b1.getPosition().getX() - b2.getPosition().getX()));
        //System.out.println(angle.toString());
        //vektoren drehe
        gedreht1 = s1.rotateVector(angle);
        gedreht2 = s2.rotateVector(angle);

        //x vertauschen
        Double gedrehtX1Backup;
        gedrehtX1Backup = gedreht1.getX();
        gedreht1.setX(gedreht2.getX());
        gedreht2.setX(gedrehtX1Backup);

        //vektoren zurückdrehen
        s1 = gedreht1.rotateVector(angle * -1.0);
        s2 = gedreht2.rotateVector(angle * -1.0);

        b1.setSpeed(s1);
        b2.setSpeed(s2);


        //gegen glitch bug -> not 100% precise
        while (b1.intersects(b2)) {
            if (b1.getPosition().getX() - b2.getPosition().getX() < 0.0) {
                b1.setPosition(new Coordinate(b1.getPosition().getX() - 0.001,
                        b1.getPosition().getY()));
                b2.setPosition(new Coordinate(b2.getPosition().getX() + 0.001,
                        b2.getPosition().getY()));
            } else {
                b1.setPosition(new Coordinate(b1.getPosition().getX() + 0.001,
                        b1.getPosition().getY()));
                b2.setPosition(new Coordinate(b2.getPosition().getX() - 0.001,
                        b2.getPosition().getY()));
            }
            if (b1.getPosition().getY() - b2.getPosition().getY() < 0.0) {
                b1.setPosition(new Coordinate(b1.getPosition().getX(),
                        b1.getPosition().getY() - 0.001));
                b2.setPosition(new Coordinate(b2.getPosition().getX(),
                        b2.getPosition().getY() + 0.001));
            } else {
                b1.setPosition(new Coordinate(b1.getPosition().getX(),
                        b1.getPosition().getY() + 0.001));
                b2.setPosition(new Coordinate(b2.getPosition().getX(),
                        b2.getPosition().getY() - 0.001));
            }

        }


    }

    
    /*
     * prüft, ob ein Ball b mit einer der Wände Kollidiert und berechnet die Kollision
     */
    private void checkWallCollision(Ball b) {
        if (this.size.getX() <= (b.getPosition().getX() + b.getSpeed().getX() + (b.getRadius()))) {
            b.setSpeed(new Coordinate(b.getSpeed().getX() * -1,
                    b.getSpeed().getY()));
            while (this.size.getX() <= (b.getPosition().getX() + b.getSpeed().getX() + (b.getRadius()))) {
                b.setPosition(new Coordinate(b.getPosition().getX() - 0.01,
                        b.getPosition().getY()));
            }
        }
        if (0 >= (b.getPosition().getX() + b.getSpeed().getX() - b.getRadius())) {
            b.setSpeed(new Coordinate(b.getSpeed().getX() * -1,
                    b.getSpeed().getY()));
            while (0 >= (b.getPosition().getX() + b.getSpeed().getX() - b.getRadius())) {
                b.setPosition(new Coordinate(b.getPosition().getX() + 0.01,
                        b.getPosition().getY()));
            }
        }

        if (this.size.getY() <= (b.getPosition().getY() + b.getSpeed().getY() + (b.getRadius()))) {
            b.setSpeed(new Coordinate(b.getSpeed().getX(),
                    b.getSpeed().getY() * -1));
            while (this.size.getY() <= (b.getPosition().getY() + b.getSpeed().getY() + (b.getRadius()))) {
                b.setPosition(new Coordinate(b.getPosition().getX(),
                        b.getPosition().getY() - 0.01));
            }
        }
        if (0 >= (b.getPosition().getY() + b.getSpeed().getY() - b.getRadius())) {
            b.setSpeed(new Coordinate(b.getSpeed().getX(),
                    b.getSpeed().getY() * -1));
            while (0 >= (b.getPosition().getY() + b.getSpeed().getY() - b.getRadius())) {
                b.setPosition(new Coordinate(b.getPosition().getX(),
                        b.getPosition().getY() + 0.01));
            }
        }
    }

    /**
     * wird nur zu Testzecken verwendet
     * @return gibt die Summe der Impule aller Bälle zurück
     * 
     */
    public Double getTotalImpuls() {
        Double impuls = 0.0;

        int anzBaelle = baelle.size();

        for (int i = 0; i < anzBaelle; i++) {
            impuls = impuls + Math.sqrt(Math.pow(
                    baelle.elementAt(i).getSpeed().getX(), 2.0) + Math.pow(
                    baelle.elementAt(i).getSpeed().getY(), 2.0));
            //impuls = impuls + baelle.elementAt(i).getSpeed().getX() + baelle.elementAt(i).getSpeed().getY();
        }


        return impuls;
    }

    /**
     * löscht alle Bälle
     */
    public void clear() {
        baelle.clear();
    }

    /**
     *
     * @return gibt an, ob Gravitation aktiviert ist
     */
    public boolean getGravity() {
        return gravity;
    }

    /**
     *
     * @param gravity legt fest, ob Gravitation aktiviert ist
     */
    public void setGravity(boolean gravity) {
        this.gravity = gravity;
    }

    /**
     *
     * @return gibt an, ob Reibung aktiviert ist
     */
    public boolean getFriction() {
        return friction;
    }

    /**
     *
     * @param friction legt fest, ob Reibung aktiviert ist
     */
    public void setFriction(boolean friction) {
        this.friction = friction;
    }

    /**
     *
     * @return gibt die Stärke der Gravitation zurück
     */
    public int getGravIntensity() {
        return gravIntensity;
    }

    /**
     *
     * @param gravIntensity legt die Stärke der Gravitation fest
     */
    public void setGravIntensity(int gravIntensity) {
        this.gravIntensity = gravIntensity;
    }

    /**
     *
     * @return gibt die Stärke der Reibung zurück
     */
    public int getFrictIntensity() {
        return frictIntensity;
    }

    /**
     *
     * @param frictIntensity legt die Stärke der Reibung fest
     */
    public void setFrictIntensity(int frictIntensity) {
        this.frictIntensity = frictIntensity;
    }

    /**
     *
     * @param f Datei, aus der gelesen werden soll
     * @return gibt zurück, ob Lesen erfolgreich war
     */
    public boolean readFromFile(File f) {
        baelle = new Stack<Ball>();

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(f);

            doc.getDocumentElement().normalize();
            doc.getDocumentElement();

            NodeList propertiesNode = doc.getElementsByTagName("properties");

            Element properties = (Element) propertiesNode.item(0);
            //System.out.println(properties.getElementsByTagName("hasGrav").item(0).getTextContent());
            this.gravity = Boolean.valueOf(properties.getElementsByTagName(
                    "hasGrav").item(0).getTextContent());
            this.friction = Boolean.valueOf(properties.getElementsByTagName(
                    "hasFrict").item(0).getTextContent());
            this.gravIntensity = Integer.valueOf(
                    properties.getElementsByTagName("grav").item(0).getTextContent());
            this.frictIntensity = Integer.valueOf(
                    properties.getElementsByTagName("frict").item(0).getTextContent());

            NodeList nList = doc.getElementsByTagName("ball");

            for (int i = 0; i < nList.getLength(); i++) {

                Node nNode = nList.item(i);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {


                    Element eElement = (Element) nNode;

                    //read radius
                    Double radius = Double.valueOf(
                            eElement.getElementsByTagName("radius").item(0).getTextContent());

                    //read position
                    Double posX = Double.valueOf(eElement.getElementsByTagName(
                            "position").item(0).getAttributes().getNamedItem("x").getNodeValue());
                    Double posY = Double.valueOf(eElement.getElementsByTagName(
                            "position").item(0).getAttributes().getNamedItem("y").getNodeValue());
                    Coordinate position = new Coordinate(posX, posY);

                    //read speed
                    Double speedX = Double.valueOf(
                            eElement.getElementsByTagName("speed").item(0).getAttributes().getNamedItem(
                            "x").getNodeValue());
                    Double speedY = Double.valueOf(
                            eElement.getElementsByTagName("speed").item(0).getAttributes().getNamedItem(
                            "y").getNodeValue());
                    Coordinate speed = new Coordinate(speedX, speedY);

                    //read color
                    int colorR = Integer.valueOf(eElement.getElementsByTagName(
                            "color").item(0).getAttributes().getNamedItem("r").getNodeValue());
                    int colorG = Integer.valueOf(eElement.getElementsByTagName(
                            "color").item(0).getAttributes().getNamedItem("g").getNodeValue());
                    int colorB = Integer.valueOf(eElement.getElementsByTagName(
                            "color").item(0).getAttributes().getNamedItem("b").getNodeValue());
                    Color color = new Color(colorR, colorG, colorB);

                    //create ball
                    Ball b = new Ball(radius, position, speed, 0.0, 0.0, color);

                    this.addBall(b);

                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     *
     * @param f Datei, in die gespeichert werden soll
     * @return gibt zurück, ob Lesen erfolgreich war
     */
    public boolean saveToFile(File f) {
        try {

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            //erstelle root-element
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("physics");
            doc.appendChild(rootElement);

            //Eigenschaften speichern
            Element props = doc.createElement("properties");

            Element hasGrav = doc.createElement("hasGrav");
            hasGrav.appendChild(doc.createTextNode(String.valueOf(
                    this.getGravity())));

            Element hasFrict = doc.createElement("hasFrict");
            hasFrict.appendChild(doc.createTextNode(String.valueOf(
                    this.getFriction())));

            Element grav = doc.createElement("grav");
            grav.appendChild(doc.createTextNode(String.valueOf(
                    this.getGravIntensity())));

            Element frict = doc.createElement("frict");
            frict.appendChild(doc.createTextNode(String.valueOf(
                    this.getFrictIntensity())));

            props.appendChild(hasGrav);
            props.appendChild(hasFrict);
            props.appendChild(grav);
            props.appendChild(frict);
            rootElement.appendChild(props);


            //baelle speichern
            for (int i = 0; i < baelle.size(); i++) {
                Element ball = doc.createElement("ball");

                Element radius = doc.createElement("radius");
                radius.appendChild(doc.createTextNode(String.valueOf(
                        this.baelle.get(i).getRadius())));

                Element color = doc.createElement("color");
                color.setAttribute("r", String.valueOf(
                        this.baelle.get(i).getColor().getRed()));
                color.setAttribute("g", String.valueOf(
                        this.baelle.get(i).getColor().getGreen()));
                color.setAttribute("b", String.valueOf(
                        this.baelle.get(i).getColor().getBlue()));

                Element position = doc.createElement("position");
                position.setAttribute("x", String.valueOf(
                        this.baelle.get(i).getPosition().getX()));
                position.setAttribute("y", String.valueOf(
                        this.baelle.get(i).getPosition().getY()));

                Element speed = doc.createElement("speed");
                speed.setAttribute("x", String.valueOf(
                        this.baelle.get(i).getSpeed().getX()));
                speed.setAttribute("y", String.valueOf(
                        this.baelle.get(i).getSpeed().getY()));

                ball.appendChild(radius);
                ball.appendChild(color);
                ball.appendChild(position);
                ball.appendChild(speed);

                rootElement.appendChild(ball);
            }

            //in datei speichern
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            //einrückung in xml-daei einschalten
            transformer.setOutputProperty(
                    "{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(f);

            transformer.transform(source, result);

            //System.out.println("File saved!");

            return true;
        } catch (ParserConfigurationException pce) {
            return false;
        } catch (TransformerException tfe) {
            return false;
        }
    }
}
