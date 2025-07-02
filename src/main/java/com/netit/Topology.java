package com.netit;

import javafx.animation.PauseTransition;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Topology {
    public static Pane workPanel;
    private static List<Point> points = new ArrayList<>();
    private static List<Button> sys = new ArrayList<>();
    private static final List<Connection> connections = new ArrayList<>();
    private static final Random RNG = new Random();

    public static final int ruter_t = 0;
    public static final int swithe_t = 1;
    public static final int linux_t = 2;
    public static final int windows_t = 3;
    public static final int linux_server_t = 4;
    public static final int windos_server_t = 5;
    private static Integer app_stet = -1;
    private static Integer index_b = -1;

    public static void setApp_stet(Integer x) { app_stet = x; }
    public static Integer App_state_get() { return app_stet; }
    public static boolean App_new_window_q() { return app_stet == -1; }
    public static void setPanelToTopology(Pane panel) { workPanel = panel; }
    public static int getSystemCount() { return points.size(); }
    public static int getindex_b() { return index_b; }

    public static void addsystem(int x, int y, int type, ImageView img) {
        points.add(new Point(x, y, type));
        Button button = new Button();
        button.setLayoutX(x);
        button.setLayoutY(y);
        button.setGraphic(img);

        // Otwarcie okna na klik
        button.setOnMouseClicked(event -> {
            if (event.isStillSincePress() && App_new_window_q()) {
                setApp_stet(type);
                index_b = getSystemCount() - 1;
                try {
                    FXMLLoader loader = new FXMLLoader(Topology.class.getResource("/secendary_ui.fxml"));
                    SecendaryUi ctrl = new SecendaryUi();
                    loader.setController(ctrl);
                    ctrl.point_to_button(button);
                    Parent root = loader.load();

                    Stage stage = new Stage();
                    switch (type) {
                        case ruter_t -> stage.setTitle("Ruter");
                        case swithe_t -> stage.setTitle("Swithe");
                        case linux_t -> stage.setTitle("Linux");
                        case windows_t -> stage.setTitle("Windows");
                        case linux_server_t -> stage.setTitle("Linux Server");
                        case windos_server_t -> stage.setTitle("Windows Server");
                        default -> stage.setTitle("Unknown");
                    }
                    Scene scene = new Scene(root);
                    scene.getStylesheets().add(Topology.class.getResource("/style.css").toExternalForm());
                    stage.setScene(scene);
                    stage.setResizable(true);
                    stage.setOnCloseRequest(e -> setApp_stet(-1));
                    stage.show();
                    stage.setWidth(500);
                    stage.setHeight(500);
                } catch (Exception ex) { throw new RuntimeException(ex); }
            }
        });

        // Drag & drop
        final double[] offsetX = new double[1];
        final double[] offsetY = new double[1];
        final boolean[] dragged = {false};

        button.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            if (!App_new_window_q()) return;
            offsetX[0] = e.getSceneX() - button.getLayoutX();
            offsetY[0] = e.getSceneY() - button.getLayoutY();
            dragged[0] = false;
            // Don't consume, allow click detection
        });

        button.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
            if (!App_new_window_q()) return;
            button.setLayoutX(e.getSceneX() - offsetX[0]);
            button.setLayoutY(e.getSceneY() - offsetY[0]);
            dragged[0] = true;
            // update connections live via bindings
        });

        button.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
            if (!App_new_window_q()) return;
            if (dragged[0]) {
                int idx = sys.indexOf(button);
                if (idx >= 0) {
                    points.get(idx).setX((int) button.getLayoutX());
                    points.get(idx).setY((int) button.getLayoutY());
                }
            }
            // allow click event if not dragged
        });

        workPanel.getChildren().add(button);
        sys.add(button);
    }

    public static void del_system(Button b) {
        sys.remove(b);
        workPanel.getChildren().remove(b);
    }

    public static Point getPoint(int i) { return (i>=0 && i<points.size())?points.get(i):null; }
    public static boolean setSystem(int x, int y, int i) {
        if (i>=0 && i<points.size()) { points.get(i).setX(x); points.get(i).setY(y); return true;} return false;
    }

    public static List<Integer> findFreeCoordinates() {
        final int SIZE=150, STEP=25;
        double w=workPanel.getWidth(), h=workPanel.getHeight();
        List<List<Integer>> free=new ArrayList<>();
        for(int yy=0; yy<= h-SIZE ; yy+=STEP) for(int xx=0; xx <= w-SIZE; xx+=STEP){
            boolean over=false;
            for(Point p:points) if(xx <p.getX()+SIZE&&xx+SIZE > p.getX()&&yy < p.getY()+SIZE&&yy+SIZE > p.getY()){ over=true; break; }
            if(!over) free.add(List.of(xx,yy));
        }
        return free.isEmpty()?null:free.get(RNG.nextInt(free.size()));
    }

    private static class Connection {
        Button b1, b2;
        Line link;
        Connection(Button b1, Button b2, Line link) { this.b1 = b1; this.b2 = b2; this.link = link; }
    }

    public static void connectTwoButtons() {
        setApp_stet(999);
        List<Button> sel = new ArrayList<>();
        EventHandler<MouseEvent> handler = new EventHandler<>() {
            @Override
            public void handle(MouseEvent e) {
                Button b = (Button) e.getSource();
                if (!sel.contains(b)) {
                    sel.add(b);
                    b.setStyle("-fx-border-color: blue; -fx-border-width: 2px;");
                }
                if (sel.size() == 2) {
                    Button b1 = sel.get(0);
                    Button b2 = sel.get(1);
                    Line l = new Line();
                    l.startXProperty().bind(b1.layoutXProperty().add(b1.widthProperty().divide(2)));
                    l.startYProperty().bind(b1.layoutYProperty().add(b1.heightProperty().divide(2)));
                    l.endXProperty().bind(b2.layoutXProperty().add(b2.widthProperty().divide(2)));
                    l.endYProperty().bind(b2.layoutYProperty().add(b2.heightProperty().divide(2)));
                    workPanel.getChildren().add(0, l);
                    connections.add(new Connection(b1, b2, l));
                    // Delay reset
                    PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
                    pause.setOnFinished(ev -> setApp_stet(-1));
                    pause.play();
                    // Cleanup styles and handlers
                    sel.forEach(btn -> btn.removeEventHandler(MouseEvent.MOUSE_PRESSED, this));
                    sys.forEach(btn -> btn.setStyle(""));
                }
            }
        };
        sys.forEach(b -> b.addEventHandler(MouseEvent.MOUSE_PRESSED, handler));
    }

    public static void removeConnectionBetweenButtons() {
        setApp_stet(998);
        List<Button> sel = new ArrayList<>();
        EventHandler<MouseEvent> handler = new EventHandler<>() {
            @Override
            public void handle(MouseEvent e) {
                Button b = (Button) e.getSource();
                if (!sel.contains(b)) {
                    sel.add(b);
                    b.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                }
                if (sel.size() == 2) {
                    Button b1 = sel.get(0);
                    Button b2 = sel.get(1);
                    Connection toRem = connections.stream()
                            .filter(c -> (c.b1 == b1 && c.b2 == b2) || (c.b1 == b2 && c.b2 == b1))
                            .findFirst().orElse(null);
                    if (toRem != null) {
                        workPanel.getChildren().remove(toRem.link);
                        connections.remove(toRem);
                    }
                    // Delay reset
                    PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
                    pause.setOnFinished(ev -> setApp_stet(-1));
                    pause.play();
                    // Cleanup styles and handlers
                    sel.forEach(btn -> btn.removeEventHandler(MouseEvent.MOUSE_PRESSED, this));
                    sys.forEach(btn -> btn.setStyle(""));
                }
            }
        };
        sys.forEach(b -> b.addEventHandler(MouseEvent.MOUSE_PRESSED, handler));
    }
}