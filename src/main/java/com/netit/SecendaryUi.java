package com.netit;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebView;

import java.io.Console;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SecendaryUi {
    private String baseDir;
    private List<String> files = new ArrayList<>();
    private Button but;

    @FXML
    private WebView idex_info;

    @FXML
    private HBox button_list;

    @FXML
    private void initialize() {
        this.baseDir = System.getProperty("user.dir");
        String path = switch (Topology.App_state_get()) {
            case 0 -> "out/files/sys-cfg/0/";
            case 1 -> "out/files/sys-cfg/1/";
            case 2 -> "out/files/sys-cfg/2/";
            case 3 -> "out/files/sys-cfg/3/";
            case 4 -> "out/files/sys-cfg/4/";
            case 5 -> "out/files/sys-cfg/5/";
            default -> null;
        };

        if (path != null) {
            showHtmlFromPath(path + "index.html");

            Button defult = new Button();
            Button delete = new Button();


            delete.setText("del");
            defult.setText("info");
            delete.setOnAction(e ->{
                Topology.workPanel.getChildren().remove(this.but);
                Topology.del_system(this.but);
                this.but = null;
            });
            defult.setOnAction(e -> {
                showHtmlFromPath(path + "index.html");
            });
            this.button_list.getChildren().add(delete);
            this.button_list.getChildren().add(defult);

            this.files.clear();
            getFilesExcludingIndexHtml(this.baseDir + "/" + path);
            for (String file : this.files){
                String buttonName = file;
                if (buttonName.endsWith(".html")) {
                    buttonName = buttonName.substring(0, buttonName.length() - 5);
                }
                Button button = new Button();
                button.setText(buttonName);
                button.setOnAction(e -> {
                    showHtmlFromPath(path + file);
                });
                this.button_list.getChildren().add(button);
            }

        }

    }

    public void point_to_button(Button b){
        this.but = b;
    }
    public void showHtmlFromPath(String relativePath) {
        File f = new File(this.baseDir, relativePath); // Połączenie ścieżek

        if (!f.exists() || !f.isFile()) {
            String errorHtml = """
            <html>
              <body style="font-family: sans-serif; text-align: center; padding: 20px;">
                <h2>Nie znaleziono pliku</h2>
                <p>Ścieżka: <code>%s</code></p>
              </body>
            </html>
            """.formatted(f.getAbsolutePath());
            idex_info.getEngine().loadContent(errorHtml);
            return;
        }


        idex_info.getEngine().load(f.toURI().toString());


    }


    public void getFilesExcludingIndexHtml(String folderPath) {
        File folder = new File(folderPath);


        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File f : files) {
                    // pomiń jeśli to folder
                    if (f.isFile()) {
                        // pomiń plik index.html
                        if (!f.getName().equals("index.html")) {
                            this.files.add(f.getName());
                        }
                    }
                }
            }
        }

    }
}
