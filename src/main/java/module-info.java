module com.netit {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    opens com.netit to javafx.fxml;
}
