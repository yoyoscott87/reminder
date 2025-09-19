package com.example.reminder;

import javafx.application.Platform;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainApp {
    public static void main(String[] args) throws Exception {
        // 啟動 JavaFX 平台一次
        Platform.startup(() -> {
        	Platform.setImplicitExit(false);
        });

        String spreadsheetId = "1mAScrsD0NGbhgE9c-5yCf2ZchgO0R_U4ugo6myzYEe4";
        String range = "reminder!A2:D";  

        var sheets = SheetsService.createService();
        var checker = new ReminderChecker(sheets, spreadsheetId, range);

        var scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            try {
                checker.checkAndNotify();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 1, TimeUnit.MINUTES);

        System.out.println("提醒器已啟動...");
    }
}
