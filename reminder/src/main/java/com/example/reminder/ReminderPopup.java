package com.example.reminder;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class ReminderPopup {

    public static void show(String msg) {
        Platform.runLater(() -> {
            // 透明視窗（解決圓角外露尖角）
            Stage stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT);

            // 文字
            Label label = new Label(msg);
            label.setWrapText(true);
            label.setMaxWidth(840);
            label.setStyle(
                "-fx-font-size: 56px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: white;"
            );

            // 按鈕（深色對比 + hover/press）
            Button okBtn = new Button("確定");
            final String NORMAL =
                "-fx-font-size: 20px;" +
                "-fx-background-color: #8EB69B;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 12;" +
                "-fx-padding: 12 28;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 8, 0, 0, 2);";
            final String HOVER = NORMAL.replace("#8EB69B", "#8db584");
            final String PRESSED = NORMAL.replace("#8EB69B", "#7fb374");

            okBtn.setStyle(NORMAL);
            okBtn.setOnMouseEntered(e -> okBtn.setStyle(HOVER));
            okBtn.setOnMouseExited(e -> okBtn.setStyle(NORMAL));
            okBtn.setOnMousePressed(e -> okBtn.setStyle(PRESSED));
            okBtn.setOnMouseReleased(e -> okBtn.setStyle(HOVER));
            okBtn.setOnAction(e -> stage.close());

            // 容器（圓角卡片 + 漸層 + 陰影）
            VBox root = new VBox(30, label, okBtn);
            root.setAlignment(Pos.CENTER);
            root.setStyle(
                "-fx-background-color: #79ba8e;" +   // 單色背景
                "-fx-background-radius: 25;" +
                "-fx-padding: 40;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 20, 0.5, 0, 0);"
            );


            // Scene 透明
            Scene scene = new Scene(root);
            stage.sizeToScene();
            scene.setFill(Color.TRANSPARENT);

            stage.setScene(scene);
            stage.setAlwaysOnTop(true);
            stage.centerOnScreen();
            stage.show();

            // 淡入動畫
            FadeTransition ft = new FadeTransition(Duration.millis(400), root);
            ft.setFromValue(0.0);
            ft.setToValue(1.0);
            ft.play();

            // 如需自動關閉，解開以下兩行（例如 10 秒）
            // PauseTransition delay = new PauseTransition(Duration.seconds(10));
            // delay.setOnFinished(ev -> stage.close()); delay.play();
        });
        // 提醒：MainApp 啟動時要有：
        // Platform.startup(() -> Platform.setImplicitExit(false));
    }
}
