package com.netit;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Topology {
    private static Pane workPanel;
    private static List<Point> points = new ArrayList<>();
    private static List<Button> sys = new ArrayList<>();
    private static final Random RNG = new Random();

    public static final Integer ruter_t = 0;
    public static final Integer swithe_t = 1;
    public static final Integer linux_t = 2;
    public static final Integer windows_t = 3;
    public static final Integer linux_server_t = 4;
    public static final Integer windos_server_t = 5;
    private static Integer app_stet = -1;

    public static void setApp_stet(Integer x){ Topology.app_stet = x;}
    public static boolean App_new_window_q(){return Topology.app_stet == -1;};
    public static Integer App_state_get(){ return Topology.app_stet; }
    public static void setPanelToTopology(Pane workPanel){
        Topology.workPanel = workPanel;
    }
    public static int getSystemCount() {
        return points.size();
    }

    public static void addsystem(int x, int y, int type, ImageView img){
        Topology.points.add(new Point(x, y, type));
        Button button = new Button();

        button.setLayoutX(x);
        button.setLayoutY(y);
        button.setGraphic(img);

        button.setOnAction(e -> {
            if (Topology.App_new_window_q()){
                Topology.setApp_stet(type);
                try {
                    FXMLLoader loader = new FXMLLoader(Topology.class.getResource("/secendary_ui.fxml"));
                    Parent root = loader.load();

                    Stage stage = new Stage();
                    switch(type){
                        case 0 -> { stage.setTitle("Ruter"); }
                        case 1 -> { stage.setTitle("swithe"); }
                        case 2 -> { stage.setTitle("linux"); }
                        case 3 -> { stage.setTitle("windows"); }
                        case 4 -> { stage.setTitle("linux server"); }
                        case 5 -> { stage.setTitle("windows server");}
                        default -> { stage.setTitle("Error type init"); }
                    }
                    stage.setScene(new Scene(root));
                    stage.setOnCloseRequest(event -> {
                        Topology.setApp_stet(-1);
                    });
                    stage.show();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        );

        Topology.workPanel.getChildren().add(button);
        Topology.sys.add(button);
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


    public static List<Integer> findFreeCoordinates() {
        final int SYSTEM_SIZE = 150; // rozmiar kwadratu w pikselach
        final int STEP        = 25;  // co ile pikseli próbujemy — ustawia dokładność

        double panelWidth  = workPanel.getWidth();
        double panelHeight = workPanel.getHeight();

        // lista wszystkich możliwych, wolnych pozycji
        List<List<Integer>> freeSpots = new ArrayList<>();

        for (int y = 0; y <= panelHeight - SYSTEM_SIZE; y += STEP) {
            for (int x = 0; x <= panelWidth - SYSTEM_SIZE; x += STEP) {
                boolean overlaps = false;
                for (Point p : points) {
                    int px = p.getX();
                    int py = p.getY();

                    if (x < px + SYSTEM_SIZE && x + SYSTEM_SIZE > px &&
                            y < py + SYSTEM_SIZE && y + SYSTEM_SIZE > py) {
                        overlaps = true;
                        break;
                    }
                }

                if (!overlaps) {
                    // dodajemy do listy wolnych
                    freeSpots.add(List.of(x, y));
                }
            }
        }

        if (freeSpots.isEmpty()) {
            return null;  // brak miejsca
        }

        // wybieramy losowo jeden z dostępnych
        return freeSpots.get(RNG.nextInt(freeSpots.size()));
    }

}
