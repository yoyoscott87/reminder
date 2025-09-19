//每分鐘讀取試算表資料
//比對「時間欄」和現在時間（±2 分鐘誤差）
//若狀態為「未提醒」→ 呼叫 ReminderPopup.showReminder() → 更新狀態
package com.example.reminder;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ReminderChecker {
    private final Sheets sheets;
    private final String spreadsheetId;
    private final String range;

    // 用來避免同一天的每日提醒跳多次
    private static final Set<String> remindedToday = new HashSet<>();
    private static LocalDate lastCheckedDate = LocalDate.now();

    public ReminderChecker(Sheets sheets, String spreadsheetId, String range) {
        this.sheets = sheets;
        this.spreadsheetId = spreadsheetId;
        this.range = range;
    }

    public void checkAndNotify() throws Exception {
        // 每天凌晨清空記錄，避免隔天不再提醒
        if (!LocalDate.now().equals(lastCheckedDate)) {
            remindedToday.clear();
            lastCheckedDate = LocalDate.now();
        }

        ValueRange response = sheets.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();

        List<List<Object>> values = response.getValues();
        if (values == null || values.isEmpty()) return;

        LocalDateTime now = LocalDateTime.now();
        LocalTime nowOnlyTime = LocalTime.now();

        for (int i = 0; i < values.size(); i++) {
            List<Object> row = values.get(i);
            if (row.size() < 2) continue;

            String timeStr = row.get(0).toString().trim();
            String task = row.get(1).toString();
            String status = (row.size() > 2) ? row.get(2).toString() : "";

            try {
                // 1️ 一次性提醒
                if (timeStr.contains("/")) {
                    LocalDateTime remindTime = LocalDateTime.parse(timeStr, DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
                    long diff = Duration.between(remindTime, now).toMinutes();
                    if (Math.abs(diff) <= 1 && !"已提醒".equals(status)) {
                        ReminderPopup.show("📌 " + task);
                        sheets.spreadsheets().values().update(
                                spreadsheetId,
                                "工作表1!C" + (i + 2),
                                new ValueRange().setValues(List.of(List.of("已提醒")))
                        ).setValueInputOption("RAW").execute();
                    }
                }
                // 2️ 每日提醒 (避免同一天重複跳)
                else {
                    LocalTime remindTime = LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm"));
                    long diff = Duration.between(remindTime, nowOnlyTime).toMinutes();
                    if (Math.abs(diff) <= 1) {
                        String key = remindTime.toString() + "-" + LocalDate.now();
                        if (!remindedToday.contains(key)) {
                            ReminderPopup.show("📌 (每日) " + task);
                            remindedToday.add(key);
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("⚠️ 無法解析時間格式: " + timeStr + "，錯誤訊息: " + e.getMessage());
            }
        }
    }
}

