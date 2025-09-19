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

        String spreadsheetId = "1-u3kssi6gWN9HahcOY5vdDHDLGP3xiNOFtQWU8cbVw4";
        String range = "工作表1!A2:C";  

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
