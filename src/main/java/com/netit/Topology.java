package com.netit;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Topology  {
    private static javafx.scene.control.ToggleButton connectToggle;
    private static javafx.scene.control.ToggleButton disconnectToggle;

    // Pojedyncze instancje handlerów, aby można było je usuwać
    private static final EventHandler<MouseEvent> connectHandler = new ConnectHandler();
    private static final EventHandler<MouseEvent> disconnectHandler = new DisconnectHandler();

    // Główny panel roboczy, na którym umieszczamy przyciski i linie
    public static Pane workPanel;
    // Lista współrzędnych i typów dodanych systemów
    private static final List<Point> points = new ArrayList<>();
    // Lista przycisków reprezentujących systemy
    private static final List<Button> sys = new ArrayList<>();
    // Lista aktywnych połączeń między przyciskami
    private static final List<Connection> connections = new ArrayList<>();
    // Random do wyboru losowej wolnej pozycji
    private static final Random RNG = new Random();
    // Executor do okresowego sprawdzania połączeń
    private static ScheduledExecutorService connectionWatcher;

    // Typy systemów
    public static final int ruter_t = 0;
    public static final int swithe_t = 1;
    public static final int linux_t = 2;
    public static final int windows_t = 3;
    public static final int linux_server_t = 4;
    public static final int windos_server_t = 5;
    // Flagi sterujące stanem aplikacji
    private static Integer app_stet = -1;
    private static Integer index_b = -1;

    // Ustawianie i pobieranie stanu aplikacji (czy otwarte okno)
    public static void setApp_stet(Integer x) { app_stet = x; }
    public static Integer App_state_get() { return app_stet; }
    public static boolean App_new_window_q() { return app_stet == -1; } // czy można otworzyć nowe okno?
    public static void setPanelToTopology(Pane panel) { workPanel = panel; }
    public static int getSystemCount() { return sys.size(); }
    public static int getindex_b() { return index_b; }

    /**
     * Dodaje nowy system (przycisk + grafika) do panelu
     * @param x współrzędna X
     * @param y współrzędna Y
     * @param type typ systemu (kod)
     * @param img grafika do przycisku
     */
    public static void addsystem(int x, int y, int type, ImageView img) {
        points.add(new Point(x, y, type)); // zapamiętaj punkt
        Button button = new Button();
        button.setLayoutX(x);
        button.setLayoutY(y);
        button.setGraphic(img);

        // Kliknięcie otwiera nowe okno szczegółów
        button.setOnMouseClicked(event -> {
            if (event.isStillSincePress() && App_new_window_q()) {
                setApp_stet(type);
                index_b = getSystemCount() - 1;
                try {
                    FXMLLoader loader = new FXMLLoader(Topology.class.getResource("/secendary_ui.fxml"));
                    SecendaryUi ctrl = new SecendaryUi();
                    loader.setController(ctrl);
                    ctrl.point_to_button(button); // przekaż przycisk do kontrolera
                    Parent root = loader.load();

                    Stage stage = new Stage();
                    // Ustaw tytuł okna w zależności od typu
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
                    scene.getStylesheets().add(Objects.requireNonNull(Topology.class.getResource("/style.css")).toExternalForm());
                    stage.setScene(scene);
                    stage.setResizable(true);
                    // Przy zamknięciu resetuj stan aplikacji
                    stage.setOnCloseRequest(e -> setApp_stet(-1));
                    stage.show();
                    stage.setWidth(500);
                    stage.setHeight(500);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        // ---------------------------------------------------------------------
        // Umożliwienie przeciągania przycisku po panelu
        final double[] offsetX = new double[1];
        final double[] offsetY = new double[1];
        final boolean[] dragged = {false};

        // Naciśnięcie myszki - zapisz przesunięcie
        button.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            if (!App_new_window_q()) return;
            offsetX[0] = e.getSceneX() - button.getLayoutX();
            offsetY[0] = e.getSceneY() - button.getLayoutY();
            dragged[0] = false; // jeszcze nie przeciągane
        });

        // Dragged - przesuwaj przycisk
        button.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
            if (!App_new_window_q()) return;
            button.setLayoutX(e.getSceneX() - offsetX[0]);
            button.setLayoutY(e.getSceneY() - offsetY[0]);
            dragged[0] = true;
            // Połączenia aktualizują się automatycznie dzięki wiązaniom
        });

        // Zwolnienie przycisku - zapisz nowe współrzędne jeśli przeciągano
        button.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
            if (!App_new_window_q()) return;
            if (dragged[0]) {
                int idx = sys.indexOf(button);
                if (idx >= 0) {
                    points.get(idx).setX((int) button.getLayoutX());
                    points.get(idx).setY((int) button.getLayoutY());
                }
            }
        });

        // Dodaj przycisk do panelu i listy systemów
        workPanel.getChildren().add(button);
        sys.add(button);
    }



    //Usuń system (przycisk) z panelu
    public static void del_system(Button b) {
        sys.remove(b);
        workPanel.getChildren().remove(b);
    }

    // Pobiera punkt o indeksie i
    public static Point getPoint(int i) { return (i>=0 && i<points.size()) ? points.get(i) : null; }
    // Zmienia współrzędne istniejącego systemu
    public static boolean setSystem(int x, int y, int i) {
        if (i>=0 && i<points.size()) {
            points.get(i).setX(x);
            points.get(i).setY(y);
            return true;
        }
        return false;
    }


      // Znajdź wolne współrzędne w panelu, aby nie nakładać systemów
      // @return losowa para {x,y} lub null jeśli brak miejsca
    public static List<Integer> findFreeCoordinates() {
        final int SIZE = 150, STEP = 25;
        double w = workPanel.getWidth(), h = workPanel.getHeight();
        List<List<Integer>> free = new ArrayList<>();
        for (int yy = 0; yy <= h - SIZE; yy += STEP) {
            for (int xx = 0; xx <= w - SIZE; xx += STEP) {
                boolean over = false;
                for (Point p : points) {
                    if (xx < p.getX() + SIZE && xx + SIZE > p.getX() &&
                            yy < p.getY() + SIZE && yy + SIZE > p.getY()) {
                        over = true;
                        break;
                    }
                }
                if (!over) free.add(List.of(xx, yy));
            }
        }
        return free.isEmpty() ? null : free.get(RNG.nextInt(free.size()));
    }

    // Klasa pomocnicza przechowująca parę przycisków i linię między nimi
    private static class Connection {
        Button b1, b2;
        Line link;
        Connection(Button b1, Button b2, Line link) {
            this.b1 = b1;
            this.b2 = b2;
            this.link = link;
        }
    }

;

    public static void registerToggles(javafx.scene.control.ToggleButton connect,
                                       javafx.scene.control.ToggleButton disconnect) {
        connectToggle    = connect;
        disconnectToggle = disconnect;
    }

    public static void connectTwoButtons() {
        setApp_stet(999);
        // Usuń handler rozłączania, dodaj handler łączenia
        sys.forEach(b -> b.removeEventHandler(MouseEvent.MOUSE_PRESSED, disconnectHandler));
        sys.forEach(b -> b.addEventHandler(MouseEvent.MOUSE_PRESSED, connectHandler));


    }

    public static void removeConnectionBetweenButtons() {
        setApp_stet(998);
        // Usuń handler łączenia, dodaj handler rozłączania
        sys.forEach(b -> b.removeEventHandler(MouseEvent.MOUSE_PRESSED, connectHandler));
        sys.forEach(b -> b.addEventHandler(MouseEvent.MOUSE_PRESSED, disconnectHandler));

    }

        // Handler obsługujący wybór dwóch przycisków i rysowanie linii
        private static class ConnectHandler implements EventHandler<MouseEvent> {

        private final List<Button> sel = new ArrayList<>();

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

                // Krótkie opóźnienie przed odblokowaniem trybu
                PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
                pause.setOnFinished(ev -> setApp_stet(-1));
                pause.play();

                cleanup();
            }
        }

        private void cleanup() {
            sys.forEach(btn -> btn.removeEventHandler(MouseEvent.MOUSE_PRESSED, this));
            sys.forEach(btn -> btn.setStyle(""));
            sel.clear();
            if (connectToggle != null) connectToggle.setSelected(false);
        }
    }

    // Handler obsługujący wybór dwóch przycisków i usuwanie linii
    private static class DisconnectHandler implements EventHandler<MouseEvent> {
        private final List<Button> sel = new ArrayList<>();

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

                PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
                pause.setOnFinished(ev -> setApp_stet(-1));
                pause.play();

                cleanup();
            }
        }

        private void cleanup() {
            sys.forEach(btn -> btn.removeEventHandler(MouseEvent.MOUSE_PRESSED, this));
            sys.forEach(btn -> btn.setStyle(""));
            sel.clear();
            if (disconnectToggle != null) disconnectToggle.setSelected(false);
        }
    }

    /**
     * Anuluje tryby łączenia/rozłączania, czyści handlery i style
     */
    public static void cancelAllModes() {
        sys.forEach(b -> {
            b.removeEventHandler(MouseEvent.MOUSE_PRESSED, connectHandler);
            b.removeEventHandler(MouseEvent.MOUSE_PRESSED, disconnectHandler);
            b.setStyle("");
        });
        if (connectToggle    != null) connectToggle.setSelected(false);
        if (disconnectToggle != null) disconnectToggle.setSelected(false);
        setApp_stet(-1);
    }




    /**
     * Uruchamia watcher, który co sekundę sprawdza, czy przyciski istnieją,
     * i usuwa linię, jeśli któryś przycisk został usunięty
     */
    public static void startConnectionWatcher() {
        connectionWatcher = Executors.newSingleThreadScheduledExecutor();
        connectionWatcher.scheduleAtFixedRate(() -> {
            Platform.runLater(() -> {
                Iterator<Connection> it = connections.iterator();
                while (it.hasNext()) {
                    Connection conn = it.next();
                    boolean b1Present = workPanel.getChildren().contains(conn.b1);
                    boolean b2Present = workPanel.getChildren().contains(conn.b2);
                    if (!b1Present || !b2Present) {
                        workPanel.getChildren().remove(conn.link); // usuń linię
                        it.remove(); // usuń połączenie z listy
                    }
                }
            });
        }, 1, 1, TimeUnit.SECONDS);
    }

    /**
     * Zatrzymuje watcher przy zamykaniu aplikacji
     */
    public static void stop() {
        if (connectionWatcher != null && !connectionWatcher.isShutdown()) {
            connectionWatcher.shutdownNow();
        }
    }
}
