package com.netit;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;

import java.io.File;

public class SecendaryUi {
    private String text;
    private Integer foder_id;

    @FXML
    private WebView idex_info;

    @FXML
    private VBox button_list;

    @FXML
    private void initialize() {
        String path = switch (Topology.App_state_get()) {
            case 0 -> "out/files/sys-cfg/0/index.html";
            case 1 -> "out/files/sys-cfg/1/index.html";
            case 2 -> "out/files/sys-cfg/2/index.html";
            case 3 -> "out/files/sys-cfg/3/index.html";
            case 4 -> "out/files/sys-cfg/4/index.html";
            case 5 -> "out/files/sys-cfg/5/index.html";
            default -> null;
        };

        if (path != null) {
            showHtmlFromPath(path, 0);
        }
    }

    public void showHtmlFromPath(String relativePath, int element) {
        String baseDir = System.getProperty("user.dir");
        File f = new File(baseDir, relativePath); // Połączenie ścieżek

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

        if (element == 0) {
            idex_info.getEngine().load(f.toURI().toString());
        }
    }
}
