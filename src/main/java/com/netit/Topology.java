package com.netit;

import javafx.scene.layout.Pane;
import java.util.ArrayList;
import java.util.List;

public class Topology {
    private static Pane workPanel;
    private static List<Point> points = new ArrayList<>();

    public static void setPanelToTopology(Pane workPanel){
        Topology.workPanel = workPanel;
    }

    public static void addsystem(int x, int y, int type){
        points.add(new Point(x, y, type));
    }

    public static Point getPoint(int index){
        if (index >= 0 && index < points.size()){
            return points.get(index);
        } else {
            return null;
        }
    }

    public static boolean setSystem(int x, int y, int index){
        if (index >= 0 && index < points.size()){
            points.get(index).setX(x);
            points.get(index).setY(y);
            return true;
        } else {
            return false;
        }
    }

    public static int getSystemCount() {
        return points.size();
    }

    public static List<Integer> findFreeCoordinates() {
        final int SYSTEM_SIZE = 200; // rozmiar kwadratu w pikselach
        final int STEP = 25; // co ile pikseli próbujemy — można ustawić na 1 dla dokładności

        double panelWidth = workPanel.getWidth();
        double panelHeight = workPanel.getHeight();

        for (int y = 0; y <= panelHeight - SYSTEM_SIZE; y += STEP) {
            for (int x = 0; x <= panelWidth - SYSTEM_SIZE; x += STEP) {
                boolean overlaps = false;
                for (Point p : points) {
                    int px = p.getX();
                    int py = p.getY();

                    // Sprawdź czy nowy kwadrat koliduje z istniejącym
                    if (x < px + SYSTEM_SIZE && x + SYSTEM_SIZE > px &&
                            y < py + SYSTEM_SIZE && y + SYSTEM_SIZE > py) {
                        overlaps = true;
                        break;
                    }
                }

                if (!overlaps) {
                    List<Integer> coords = new ArrayList<>();
                    coords.add(x);
                    coords.add(y);
                    return coords;
                }
            }
        }

        // Brak miejsca
        return null;
    }

}
