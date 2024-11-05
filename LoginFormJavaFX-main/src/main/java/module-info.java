module com.example.loginformjavafx {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.loginformjavafx to javafx.fxml;
    exports com.example.loginformjavafx;
}