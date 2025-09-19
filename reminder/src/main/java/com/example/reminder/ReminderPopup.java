//JavaFX 彈窗 UI，顯示提醒事項 + [確認] 按鈕

package com.example.reminder;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ReminderPopup {

    public static void show(String msg) {
        Platform.runLater(() -> {
            Stage stage = new Stage();
            Label label = new Label(msg);
            Button okBtn = new Button("OK");
            okBtn.setOnAction(e -> stage.close());

            VBox root = new VBox(10, label, okBtn);
            root.setStyle("-fx-padding: 20;");
            Scene scene = new Scene(root, 300, 120);

            stage.setTitle("提醒");
            stage.setScene(scene);
            stage.show();
        });
    }
}
