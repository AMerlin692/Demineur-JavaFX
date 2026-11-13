module fr.AMerlin692.Demineur {
    requires javafx.controls;
    requires javafx.fxml;

    opens fr.AMerlin692.Demineur to javafx.fxml;
    exports fr.AMerlin692.Demineur;
}