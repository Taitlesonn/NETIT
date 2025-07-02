package com.netit;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Topology {
    public static Pane workPanel;
    private static List<Point> points = new ArrayList<>();
    private static List<Button> sys = new ArrayList<>();
    private static final List<Connection> connections = new ArrayList<>();
    private static final Random RNG = new Random();

    public static final Integer ruter_t = 0;
    public static final Integer swithe_t = 1;
    public static final Integer linux_t = 2;
    public static final Integer windows_t = 3;
    public static final Integer linux_server_t = 4;
    public static final Integer windos_server_t = 5;
    private static Integer app_stet = -1;
    private static Integer index_b = -1;

    public static void setApp_stet(Integer x){ Topology.app_stet = x;}
    public static boolean App_new_window_q(){return Topology.app_stet == -1;};
    public static Integer App_state_get(){ return Topology.app_stet; }
    public static void setPanelToTopology(Pane workPanel){
        Topology.workPanel = workPanel;
    }
    public static int getSystemCount() {
        return points.size();
    }
    public static int getindex_b(){ return  Topology.index_b; }

    public static void addsystem(int x, int y, int type, ImageView img){
        Topology.points.add(new Point(x, y, type));
        Button button = new Button();

        button.setLayoutX(x);
        button.setLayoutY(y);
        button.setGraphic(img);

        button.setOnAction(e -> {
            if (Topology.App_new_window_q()){
                Topology.setApp_stet(type);
                Topology.index_b = Topology.getSystemCount();
                try {
                    FXMLLoader loader = new FXMLLoader(Topology.class.getResource("/secendary_ui.fxml"));
                    SecendaryUi secendaryUi = new SecendaryUi();
                    loader.setController(secendaryUi);
                    secendaryUi.point_to_button(button);
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

                    Scene scene = new Scene(root);
                    scene.getStylesheets().add(Topology.class.getResource("/style.css").toExternalForm());
                    scene.setRoot(root);
                    stage.setResizable(true);
                    stage.setScene(scene);
                    stage.setOnCloseRequest(event -> {
                        Topology.setApp_stet(-1);
                    });
                    stage.show();
                    stage.setWidth(500);
                    stage.setHeight(500);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        );


        // Dodajemy obsługę przeciągania
        final double[] dragOffsetX = new double[1];
        final double[] dragOffsetY = new double[1];
        final boolean[] dragged = {false};

        button.setOnMousePressed(event -> {
            dragOffsetX[0] = event.getSceneX() - button.getLayoutX();
            dragOffsetY[0] = event.getSceneY() - button.getLayoutY();
            dragged[0] = false;
        });

        button.setOnMouseDragged(event -> {
            double newX = event.getSceneX() - dragOffsetX[0];
            double newY = event.getSceneY() - dragOffsetY[0];

            button.setLayoutX(newX);
            button.setLayoutY(newY);
            dragged[0] = true;
        });

        button.setOnMouseReleased(event -> {
            if (!dragged[0]) {
                button.fire(); // tylko klik, bez przeciągania
            } else {
                // jeśli przeciągnięto, zaktualizuj pozycję logiczną (Point)
                int index = Topology.sys.indexOf(button);
                if (index != -1) {
                    Topology.setSystem((int) button.getLayoutX(), (int) button.getLayoutY(), index);
                }
            }
        });
        Topology.workPanel.getChildren().add(button);
        Topology.sys.add(button);
    }

    public static void del_system(Button b){
        Topology.sys.remove(b);
        Topology.workPanel.layout();
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




    // lista wszystkich połączeń
    private static class Connection {
        Button b1, b2;
        Line link;
        Connection(Button b1, Button b2, Line link) {
            this.b1 = b1; this.b2 = b2; this.link = link;
        }
    }


    public static void connectTwoButtons() {
        setApp_stet(999);
        List<Button> selection = new ArrayList<>();

        javafx.event.EventHandler<MouseEvent> pickHandler = new javafx.event.EventHandler<>() {
            @Override
            public void handle(MouseEvent event) {
                Button btn = (Button) event.getSource();
                if (!selection.contains(btn)) {
                    selection.add(btn);
                    btn.setStyle("-fx-border-color: blue; -fx-border-width: 2px;");
                }
                if (selection.size() == 2) {
                    Button b1 = selection.get(0), b2 = selection.get(1);
                    Line link = new Line();
                    link.startXProperty().bind(b1.layoutXProperty().add(b1.widthProperty().divide(2)));
                    link.startYProperty().bind(b1.layoutYProperty().add(b1.heightProperty().divide(2)));
                    link.endXProperty().bind(b2.layoutXProperty().add(b2.widthProperty().divide(2)));
                    link.endYProperty().bind(b2.layoutYProperty().add(b2.heightProperty().divide(2)));
                    workPanel.getChildren().add(0, link);
                    connections.add(new Connection(b1, b2, link));

                    cleanup();
                }
            }
            private void cleanup() {
                for (Button b : sys) {
                    b.removeEventHandler(MouseEvent.MOUSE_PRESSED, this);
                    b.setStyle("");
                }
                setApp_stet(-1);
            }
        };

        for (Button b : sys) {
            b.addEventHandler(MouseEvent.MOUSE_PRESSED, pickHandler);
        }
    }


    public static void removeConnectionBetweenButtons() {
        setApp_stet(998);
        List<Button> selection = new ArrayList<>();

        javafx.event.EventHandler<MouseEvent> pickHandler = new javafx.event.EventHandler<>() {
            @Override
            public void handle(MouseEvent event) {
                Button btn = (Button) event.getSource();
                if (!selection.contains(btn)) {
                    selection.add(btn);
                    btn.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                }
                if (selection.size() == 2) {
                    Button b1 = selection.get(0), b2 = selection.get(1);

                    // znajdź połączenie (kolejność nieistotna)
                    Connection toRemove = null;
                    for (Connection c : connections) {
                        if ((c.b1 == b1 && c.b2 == b2) || (c.b1 == b2 && c.b2 == b1)) {
                            toRemove = c;
                            break;
                        }
                    }

                    if (toRemove != null) {
                        workPanel.getChildren().remove(toRemove.link);
                        connections.remove(toRemove);
                    }

                    cleanup();
                }
            }
            private void cleanup() {
                for (Button b : sys) {
                    b.removeEventHandler(MouseEvent.MOUSE_PRESSED, this);
                    b.setStyle("");
                }
                setApp_stet(-1);
            }
        };

        for (Button b : sys) {
            b.addEventHandler(MouseEvent.MOUSE_PRESSED, pickHandler);
        }
    }



}
