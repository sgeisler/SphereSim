
package circles;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Sebastian Geisler <geisler.sebastian@googlemail.com>
 */
public class Circles extends JPanel {

    public static void main(String[] args) {

        /*
         * Fenster initialisieren
         */
        final JFrame mainWindow = new JFrame("SphereSim 1.0");
        mainWindow.setSize(860, 509);
        mainWindow.setResizable(false);
        mainWindow.setLayout(new BorderLayout());
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        try {
            mainWindow.setIconImage(ImageIO.read(Circles.class.getResource("../res/appFull.jpg")));
        } catch (Exception ex) {
            Logger.getLogger(Circles.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        /*
         * Physik-Engine initialisieren
         * @see Physics
         */
        final Physics phys = new Physics(640, 480);
        final DrawPanel d = new DrawPanel(phys);
        
        
        /*
         * Initialisierung der GUI-Komponenten
         */
        JPanel menue = new JPanel();
        menue.setLayout(new GridLayout(9, 0));
        final JFileChooser fileChooser = new JFileChooser();

        final JButton buttonPause = new JButton("Pause");
        final JButton buttonPickColor = new JButton("Ballfarbe einstellen");
        final JButton buttonHistory = new JButton("Pfad ausblenden");
        final JButton buttonArrow = new JButton("Richtungspfeil anzeigen");
        JButton buttonClear = new JButton("Bälle löschen");

        JPanel panelGrav = new JPanel();
        JPanel panelFrict = new JPanel();

        final JSlider sliderGrav = new JSlider();
        final JSlider sliderFrict = new JSlider();

        final JButton buttonGrav = new JButton("Gravitation aktivieren");
        final JButton buttonFrict = new JButton("Reibung aktivieren");

        JButton buttonSave = new JButton("Speichern");
        JButton buttonLoad = new JButton("Laden");
        JPanel panelSaveLoad = new JPanel();

        JButton buttonHelp = new JButton("Hilfe ...");

        JPanel panelColor = new JPanel();
        final JPanel panelColorView = new JPanel();
        panelColorView.setBackground(Color.black);
        
        panelColor.add(panelColorView);
        panelColor.add(buttonPickColor);

        panelGrav.setLayout(new GridLayout(2, 0));
        panelGrav.add(buttonGrav);
        sliderGrav.setMaximum(100);
        sliderGrav.setMinimum(-100);
        sliderGrav.setValue(25);
        sliderGrav.setMajorTickSpacing(50);
        sliderGrav.setMinorTickSpacing(10);
        sliderGrav.setToolTipText("Gravitationsstärke");
        sliderGrav.setPaintLabels(true);
        sliderGrav.setEnabled(false);
        
        panelFrict.setLayout(new GridLayout(2, 0));
        panelFrict.add(buttonFrict);
        sliderFrict.setMaximum(100);
        sliderFrict.setMinimum(0);
        sliderFrict.setValue(25);
        sliderFrict.setMajorTickSpacing(25);
        sliderFrict.setMinorTickSpacing(5);
        sliderFrict.setToolTipText("Reibungsstärke");
        sliderFrict.setPaintLabels(true);
        sliderFrict.setEnabled(false);
        
        panelGrav.add(sliderGrav);
        panelFrict.add(sliderFrict);

        panelSaveLoad.add(buttonSave);
        panelSaveLoad.add(buttonLoad);
        
        
        /*
         * Events für Buttons und Slider hinzufügen
         */
        buttonPause.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (d.isPaused()) {
                    d.setPaused(false);
                    buttonPause.setText("Pause");
                } else {
                    d.setPaused(true);
                    buttonPause.setText("Weiter");
                }

            }
        });
        
        buttonPickColor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                Color color = JColorChooser.showDialog(buttonPickColor, "Farbe",
                        null);
                if (color != null) {
                    d.setActualColor(color);
                    panelColorView.setBackground(color);
                }
            }
        });
        
        buttonHistory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (d.isHistory() == false) {
                    d.setHistory(true);
                    buttonHistory.setText("Pfad ausblenden");
                } else {
                    d.setHistory(false);
                    buttonHistory.setText("Pfad anzeigen");
                }
            }
        });
        
        buttonArrow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (d.getDirectionArrow() == false) {
                    d.setDirectionArrow(true);
                    buttonArrow.setText("Richtungspfeil ausblenden");
                } else {
                    d.setDirectionArrow(false);
                    buttonArrow.setText("Richtungspfeil anzeigen");
                }
            }
        });
        
        buttonClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                phys.clear();
            }
        });
        
        buttonGrav.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (phys.getGravity() == false) {
                    phys.setGravity(true);
                    sliderGrav.setEnabled(true);
                    buttonGrav.setText("Gravitation deaktivieren");
                } else {
                    phys.setGravity(false);
                    sliderGrav.setEnabled(false);
                    buttonGrav.setText("Gravitation aktivieren");
                }
            }
        });
        
        buttonFrict.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (phys.getFriction() == false) {
                    phys.setFriction(true);
                    sliderFrict.setEnabled(true);
                    buttonFrict.setText("Reibung deaktivieren");
                } else {
                    phys.setFriction(false);
                    sliderFrict.setEnabled(false);
                    buttonFrict.setText("Reibung aktivieren");
                }
            }
        });
        
        sliderGrav.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                phys.setGravIntensity(sliderGrav.getValue());
            }
        });
        
        sliderFrict.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                phys.setFrictIntensity(sliderFrict.getValue());
            }
        });
        
        buttonLoad.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = fileChooser.showOpenDialog(null);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    if (phys.readFromFile(file)) {
                        d.setPaused(true);

                        if (phys.getGravity() == true) {
                            buttonGrav.setText("Gravitation deaktivieren");
                            sliderGrav.setEnabled(true);
                        } else {
                            buttonGrav.setText("Gravitation aktivieren");
                            sliderGrav.setEnabled(false);
                        }

                        if (phys.getFriction() == true) {
                            buttonFrict.setText("Reibung deaktivieren");
                            sliderFrict.setEnabled(true);
                        } else {
                            buttonFrict.setText("Reibung aktivieren");
                            sliderFrict.setEnabled(false);
                        }

                        sliderGrav.setValue(phys.getGravIntensity());
                        sliderFrict.setValue(phys.getFrictIntensity());

                        buttonPause.setText("Weiter");
                        JOptionPane.showMessageDialog(mainWindow,
                                "Bälle erfolgreich geladen.\nKlicken Sie auf \"Weiter\" um die Simulation zu starten.");
                    } else {
                        JOptionPane.showMessageDialog(mainWindow,
                                "Fehler beim laden der Datei.");
                    }
                }
            }
        });
        
        buttonSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = fileChooser.showOpenDialog(null);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    if (phys.saveToFile(file) == false) {
                        JOptionPane.showMessageDialog(mainWindow,
                                "Fehler beim Speichern in Datei.");
                    }
                }
            }
        });
        
        buttonHelp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String myDir = System.getProperty("user.dir");
                System.out.println(myDir + "\\SphereSimHelp.pdf");
                if ((new File(myDir + "\\SphereSimHelp.pdf")).exists()) {
                    try {

                        Process p = Runtime
                                .getRuntime()
                                .exec(
                                "rundll32 url.dll,FileProtocolHandler " + myDir + "\\SphereSimHelp.pdf");
                        p.waitFor();
                    } catch (IOException ex) {
                        Logger.getLogger(Circles.class.getName()).log(
                                Level.SEVERE,
                                null, ex);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Circles.class.getName()).log(
                                Level.SEVERE,
                                null, ex);
                    }
                } else {
                    //ystem.out.println("File is not exists");
                    JOptionPane.showMessageDialog(mainWindow,
                                "Hilfedatei konnte nicht geladen werden.");
                }
            }
        });
        
        
        /*
         * Menü erstellen
         */
        menue.add(buttonPause);
        menue.add(panelColor);
        menue.add(buttonHistory);
        menue.add(buttonArrow);
        menue.add(buttonClear);
        menue.add(panelGrav);
        menue.add(panelFrict);
        menue.add(panelSaveLoad);
        menue.add(buttonHelp);

        mainWindow.add(d, BorderLayout.CENTER);
        mainWindow.setBackground(Color.white);
        mainWindow.add(menue, BorderLayout.EAST);
        
        mainWindow.show();

        
        /*
         * Endlosschleife zum aktualisieren der Physikberechnungen und der grafischen Ausgabe
         */
        while (true) {
            d.repaint();
        }
    }
}
