package com.netit;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
    private int OS_run;
    private final int x_run = 900;
    private final int y_run = 1300;

    private final String hed_text = "NETIT; It is launch on: ";
    private final String autor = "Authors: https://github.com/Taitlesonn";
    private final String full_name = "  full name: Network Emulation and Topology Implementation Tool";

    private final String linux_t = "Linux - a Unix-type operating system, often used on servers and in the cloud.";
    private final String windows_t = "Windows - a popular Microsoft operating system, widely used on personal computers.";
    private final String ruter_t = "Router - a network device used to forward packets between networks.";
    private final String switch_t = "Switch - a network device that connects multiple devices within a local area network (LAN).";
    private final String windows_server_t = "Windows Server - a Windows version designed for server use.";
    private final String linux_server_t = "Linux Server - a server operating system based on the Linux kernel, popular in IT environments.";

    private ImageView ruter_img;
    private ImageView switch_img;
    private ImageView linux_img;
    private ImageView windows_img;
    private ImageView windows_server_img;
    private ImageView linux_server_img;

    private final Integer x_box = 75;
    private final Integer y_box = 75;



    @FXML
    private Button connecter;

    @FXML
    private Button disconecter;

    @FXML
    private HBox HBox_heder;

    @FXML
    private HBox HBox_footer;

    @FXML
    private VBox VBox_left;

    @FXML
    private VBox VBox_right;

    @FXML
    private Label heder_inf;

    @FXML
    private Label footer_inf;

    @FXML
    private Label right_inf;

    @FXML
    private Button ruter_b;

    @FXML
    private Button switch_b;

    @FXML
    private  Button windows_server_b;

    @FXML
    private Button linux_server_b;

    @FXML
    private Button windows_b;

    @FXML
    private Button linux_b;

    @FXML
    private Pane workspace;

    public Application_run_time(){ this.os_gues(); }
    public static boolean isLibvirtSocketAvailable() { return Files.exists(Path.of("/var/run/libvirt/libvirt-sock")); }
    public int getX_run() { return this.x_run; }
    public int getY_run() { return this.y_run; }
    public String getAutor(){ return this.autor; }
    public String getFull_name(){ return this.full_name; }
    public ImageView getRuter_img(){
        return this.ruter_img;
    }
    public ImageView getLinux_img(){
        return this.linux_img;
    }
    public ImageView getSwitch_img(){
        return this.switch_img;
    }
    public ImageView getWindows_img(){
        return this.windows_img;
    }
    public ImageView getWindows_server_img(){
        return this.windows_server_img;
    }
    public ImageView getLinux_server_img(){
        return this.linux_server_img;
    }

    public boolean isLibvirtRunning() {
        try {
            Process process = new ProcessBuilder("pgrep", "-x", "libvirtd").start();
            int exitCode = process.waitFor();
            return (exitCode == 0); // 0 oznacza, że proces został znaleziony
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



    private void os_gues(){
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")){
            this.OS_run = 0;
        }else if (os.contains("nix") || os.contains("nux") || os.contains("linux") || os.contains("aix")) {
            this.OS_run = 1;
        } else if (os.contains("mac")) {
            this.OS_run = 2;
        }
    }

    public String get_os(){
        switch (this.OS_run){
            case 0 -> {return "Windows";}
            case 1 -> {return "Linux";}
            case 2 -> {return "MacOS";}
            default -> {return "Error Not Suported Platform";}
        }
    }


    private void setTooltipRight(Button button, String text) {
        Tooltip tooltip = new Tooltip(text);
        button.setTooltip(tooltip);

        tooltip.setOnShowing(event -> {
            // Pobierz pozycję przycisku na ekranie
            double x = button.localToScreen(button.getBoundsInLocal()).getMaxX();
            double y = button.localToScreen(button.getBoundsInLocal()).getMinY();

            // Przesuń tooltip trochę w prawo i na wysokość przycisku
            tooltip.setX(x + 5);
            tooltip.setY(y);
        });
    }

    @FXML
    private void connecter_f(){ Topology.connectTwoButtons();}

    @FXML
    private void connecter_d() { Topology.removeConnectionBetweenButtons();}

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



    @FXML
    private void initialize() throws IOException {
        heder_inf.setText(this.hed_text + this.get_os());
        footer_inf.setText(this.getAutor() + this.getFull_name());


        try {
            this.ruter_img= new ImageView(new Image(new FileInputStream("out/files/Ruter.png")));
            this.switch_img = new ImageView(new Image(new FileInputStream("out/files/switch.png")));
            this.linux_img =  new ImageView(new Image(new FileInputStream("out/files/Linux.png")));
            this.windows_img =  new ImageView(new Image(new FileInputStream("out/files/Windows.png")));
            this.windows_server_img =  new ImageView(new Image(new FileInputStream("out/files/Windows-Server.png")));
            this.linux_server_img =  new ImageView(new Image(new FileInputStream("out/files/Linux-Server.png")));

            ImageView[] images = {this.ruter_img, this.switch_img, this.linux_img, this.windows_img, this.windows_server_img, this.linux_server_img};
            for (ImageView imgView : images) {
                imgView.setFitWidth(100);
                imgView.setFitHeight(100);
                imgView.setPreserveRatio(true);
            }

            this.ruter_b.setGraphic(this.ruter_img);
            this.windows_b.setGraphic(this.windows_img);
            this.linux_b.setGraphic(this.linux_img);
            this.switch_b.setGraphic(this.switch_img);
            this.linux_server_b.setGraphic(this.linux_server_img);
            this.windows_server_b.setGraphic(this.windows_server_img);


            setTooltipRight(this.linux_b, this.linux_t);
            setTooltipRight(this.windows_b, this.windows_t);
            setTooltipRight(this.ruter_b, this.ruter_t);
            setTooltipRight(this.switch_b, this.switch_t);
            setTooltipRight(this.windows_server_b, this.windows_server_t);
            setTooltipRight(this.linux_server_b, this.linux_server_t);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Pobierz użytkownika systemowego
        String currentUser = System.getProperty("user.name");

        // Sprawdź, czy libvirt działa
        boolean libvirtRunning = isLibvirtRunning();

        // Ustaw tekst w Label po prawej stronie
        right_inf.setText("Running as user: " + currentUser +
                "\nlibvirt running: " + (libvirtRunning ? "YES" : "NO"));

        Topology.setPanelToTopology(this.workspace);
    }
}
