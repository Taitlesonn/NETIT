package com.netit;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Application_run_time {
    // Zmienna określająca system operacyjny: 0=Windows, 1=Linux, 2=MacOS
    private int OS_run;
    // Stałe wymiary aplikacji
    private final int x_run = 900;
    private final int y_run = 1300;

    // Nagłówek i stopka z informacjami o aplikacji
    private final String hed_text = "NETIT; It is launch on: "; // "Uruchomiono na:"
    private final String autor = "Authors: https://github.com/Taitlesonn"; // "Autorzy:"
    private final String full_name = "  full name: Network Emulation and Topology Implementation Tool"; // "pełna nazwa:"

    // Opisy typów systemów (tooltipy)
    private final String linux_t = "Linux - a Unix-type operating system, often used on servers and in the cloud.";
    private final String windows_t = "Windows - a popular Microsoft operating system, widely used on personal computers.";
    private final String ruter_t = "Router - a network device used to forward packets between networks.";
    private final String switch_t = "Switch - a network device that connects multiple devices within a local area network (LAN).";
    private final String windows_server_t = "Windows Server - a Windows version designed for server use.";
    private final String linux_server_t = "Linux Server - a server operating system based on the Linux kernel, popular in IT environments.";

    // Obiekty ImageView dla ikon
    private ImageView ruter_img;
    private ImageView switch_img;
    private ImageView linux_img;
    private ImageView windows_img;
    private ImageView windows_server_img;
    private ImageView linux_server_img;

    // Wymiary ikon w panelu
    private final Integer x_box = 75;
    private final Integer y_box = 75;

    // FXML-owe przyciski i panele
    @FXML private ToggleButton connecter; // przycisk do łączenia
    @FXML private ToggleButton disconecter; // przycisk do rozłączania
    @FXML private HBox HBox_heder; // pasek nagłówka
    @FXML private HBox HBox_footer; // pasek stopki
    @FXML private VBox VBox_left; // lewy panel
    @FXML private Label heder_inf; // etykieta nagłówka
    @FXML private Label footer_inf; // etykieta stopki
    @FXML private Button ruter_b; // przycisk dodania routera
    @FXML private Button switch_b; // przycisk dodania switcha
    @FXML private Button windows_server_b; // przycisk dodania Windows Server
    @FXML private Button linux_server_b; // przycisk dodania Linux Server
    @FXML private Button windows_b; // przycisk dodania Windows
    @FXML private Button linux_b; // przycisk dodania Linux
    @FXML private Pane workspace; // główny obszar roboczy

    // Konstruktor - wykrywa system operacyjny
    public Application_run_time(){ this.os_gues(); }

    // Sprawdza dostępność gniazda libvirt (Linux)
    public static boolean isLibvirtSocketAvailable() { return Files.exists(Path.of("/var/run/libvirt/libvirt-sock")); }

    // Obsługa stanu connectera i disconectera


    // Gettery dla wymiarów i informacji
    public int getX_run() { return this.x_run; }
    public int getY_run() { return this.y_run; }
    public String getAutor(){ return this.autor; }
    public String getFull_name(){ return this.full_name; }
    public ImageView getRuter_img(){ return this.ruter_img; }
    public ImageView getLinux_img(){ return this.linux_img; }
    public ImageView getSwitch_img(){ return this.switch_img; }
    public ImageView getWindows_img(){ return this.windows_img; }
    public ImageView getWindows_server_img(){ return this.windows_server_img; }
    public ImageView getLinux_server_img(){ return this.linux_server_img; }

    // Wykrywanie systemu operacyjnego na podstawie właściwości systemowych
    private void os_gues(){
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")){
            this.OS_run = 0;
        } else if (os.contains("nix") || os.contains("nux") || os.contains("linux") || os.contains("aix")) {
            this.OS_run = 1;
        } else if (os.contains("mac")) {
            this.OS_run = 2;
        }
    }

    // Zwraca nazwę OS jako tekst
    public String get_os(){
        switch (this.OS_run){
            case 0 -> {return "Windows";} // Windows
            case 1 -> {return "Linux";}   // Linux
            case 2 -> {return "MacOS";}   // MacOS
            default -> {return "Error Not Suported Platform";} // Nieobsługiwany OS
        }
    }

    // Ustawia tooltip po prawej stronie przycisku
    private void setTooltipRight(Button button, String text) {
        Tooltip tooltip = new Tooltip(text);
        button.setTooltip(tooltip);

        tooltip.setOnShowing(event -> {
            // Pobierz pozycję przycisku na ekranie
            double x = button.localToScreen(button.getBoundsInLocal()).getMaxX();
            double y = button.localToScreen(button.getBoundsInLocal()).getMinY();

            // Przesuń tooltip nieco w prawo i w poziomie przycisku
            tooltip.setX(x + 5);
            tooltip.setY(y);
        });
    }

    @FXML
    private void connecter_f() {
        if (connecter.isSelected()) {
            // włączamy tryb łączenia
            if (disconecter.isSelected()) {
                disconecter.setSelected(false);
                Topology.cancelAllModes();
            }
            if (Topology.getSystemCount() >= 2) {
                Topology.connectTwoButtons();
            } else {
                // od razu odznacz, jeśli za mało systemów
                connecter.setSelected(false);
            }
        } else {
            // wyłączamy tryb łączenia
            Topology.setApp_stet(-1);
            if (!connecter.isSelected()){
                Topology.cancelAllModes();
            }
        }
    }

    @FXML
    private void connecter_d() {

        if (connecter.isSelected()) {
            connecter.setSelected(false);
            Topology.cancelAllModes();
        }
        if (disconecter.isSelected()) {
            // włączamy tryb rozłączania
            if (Topology.getSystemCount() >= 2) {
                this.connecter.setSelected(false);
                Topology.removeConnectionBetweenButtons();

            } else {
                // od razu odznacz, jeśli za mało systemów
                disconecter.setSelected(false);
            }
        } else {
            // wyłączamy tryb rozłączania
            Topology.setApp_stet(-1);
            if (!disconecter.isSelected()){
                Topology.cancelAllModes();
            }
        }
    }



    // Dodawanie routera do workspace
    @FXML
    private void add_router(){
        if (Topology.App_new_window_q()){
            List<Integer> l = Topology.findFreeCoordinates();
            assert l != null;
            ImageView copy = new ImageView(this.ruter_img.getImage());
            copy.setFitWidth(this.x_box);
            copy.setFitHeight(this.y_box);
            copy.setPreserveRatio(true);
            Topology.addsystem(l.get(0), l.get(1), Topology.ruter_t, copy);
        }
    }
    // Dodawanie switcha do workspace
    @FXML
    private void add_switch(){
        if (Topology.App_new_window_q()){
            List<Integer> l = Topology.findFreeCoordinates();
            assert l != null;
            ImageView copy = new ImageView(this.switch_img.getImage());
            copy.setFitWidth(this.x_box);
            copy.setFitHeight(this.y_box);
            copy.setPreserveRatio(true);
            Topology.addsystem(l.get(0), l.get(1), Topology.swithe_t, copy);
        }
    }
    // Dodawanie Linux do workspace
    @FXML
    private void add_linux(){
        if (Topology.App_new_window_q()){
            List<Integer> l = Topology.findFreeCoordinates();
            assert l != null;
            ImageView copy = new ImageView(this.linux_img.getImage());
            copy.setFitWidth(this.x_box);
            copy.setFitHeight(this.y_box);
            copy.setPreserveRatio(true);
            Topology.addsystem(l.get(0), l.get(1), Topology.linux_t, copy);
        }
    }
    // Dodawanie Windows do workspace
    @FXML
    private void add_windows(){
        if (Topology.App_new_window_q()){
            List<Integer> l = Topology.findFreeCoordinates();
            assert l != null;
            ImageView copy = new ImageView(this.windows_img.getImage());
            copy.setFitWidth(this.x_box);
            copy.setFitHeight(this.y_box);
            copy.setPreserveRatio(true);
            Topology.addsystem(l.get(0), l.get(1), Topology.windows_t, copy);
        }
    }
    // Dodawanie Linux Server do workspace
    @FXML
    private void add_linux_server(){
        if (Topology.App_new_window_q()){
            List<Integer> l = Topology.findFreeCoordinates();
            assert l != null;
            ImageView copy = new ImageView(this.linux_server_img.getImage());
            copy.setFitWidth(this.x_box);
            copy.setFitHeight(this.y_box);
            copy.setPreserveRatio(true);
            Topology.addsystem(l.get(0), l.get(1), Topology.linux_server_t, copy);
        }
    }
    // Dodawanie Windows Server do workspace
    @FXML
    private void add_windows_server(){
        if (Topology.App_new_window_q()) {
            List<Integer> l = Topology.findFreeCoordinates();
            assert l != null;
            ImageView copy = new ImageView(this.windows_server_img.getImage());
            copy.setFitWidth(this.x_box);
            copy.setFitHeight(this.y_box);
            copy.setPreserveRatio(true);
            Topology.addsystem(l.get(0), l.get(1), Topology.windos_server_t, copy);
        }
    }

    // Inicjalizacja kontrolera FXML
    @FXML
    private void initialize() throws IOException {
        // Ustaw tekst nagłówka i stopki
        heder_inf.setText(this.hed_text + this.get_os());
        footer_inf.setText(this.getAutor() + this.getFull_name());

        try {
            // Ładowanie obrazów z plików
            this.ruter_img = new ImageView(new Image(new FileInputStream("out/files/Ruter.png")));
            this.switch_img = new ImageView(new Image(new FileInputStream("out/files/switch.png")));
            this.linux_img = new ImageView(new Image(new FileInputStream("out/files/Linux.png")));
            this.windows_img = new ImageView(new Image(new FileInputStream("out/files/Windows.png")));
            this.windows_server_img = new ImageView(new Image(new FileInputStream("out/files/Windows-Server.png")));
            this.linux_server_img = new ImageView(new Image(new FileInputStream("out/files/Linux-Server.png")));

            // Ustawienie rozmiarów grafik
            ImageView[] images = {this.ruter_img, this.switch_img, this.linux_img, this.windows_img, this.windows_server_img, this.linux_server_img};
            for (ImageView imgView : images) {
                imgView.setFitWidth(100);
                imgView.setFitHeight(100);
                imgView.setPreserveRatio(true);
            }

            // Przypisanie grafik do przycisków
            this.ruter_b.setGraphic(this.ruter_img);
            this.windows_b.setGraphic(this.windows_img);
            this.linux_b.setGraphic(this.linux_img);
            this.switch_b.setGraphic(this.switch_img);
            this.linux_server_b.setGraphic(this.linux_server_img);
            this.windows_server_b.setGraphic(this.windows_server_img);

            // Ustawienie tooltipów dla przycisków
            setTooltipRight(this.linux_b, this.linux_t);
            setTooltipRight(this.windows_b, this.windows_t);
            setTooltipRight(this.ruter_b, this.ruter_t);
            setTooltipRight(this.switch_b, this.switch_t);
            setTooltipRight(this.windows_server_b, this.windows_server_t);
            setTooltipRight(this.linux_server_b, this.linux_server_t);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Nadawanie max rozciągnięcia przyciskom
        this.VBox_left.setFillWidth(true);
        this.disconecter.setMaxWidth(Double.MAX_VALUE);
        this.connecter.setMaxWidth(Double.MAX_VALUE);


        // Przypisanie panelu roboczego do Topology i uruchomienie watchera
        Topology.setPanelToTopology(this.workspace);


        Topology.registerToggles(connecter, disconecter);

        Topology.startConnectionWatcher();
    }
}
