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

    // 避免同一天的每日提醒跳多次
    private static final Set<String> remindedToday = new HashSet<>();
    private static LocalDate lastCheckedDate = LocalDate.now();

    public ReminderChecker(Sheets sheets, String spreadsheetId, String range) {
        this.sheets = sheets;
        this.spreadsheetId = spreadsheetId;
        this.range = range;
    }

    public void checkAndNotify() throws Exception {
        // 每天凌晨清空記錄
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
        DayOfWeek today = LocalDate.now().getDayOfWeek(); // MONDAY ~ SUNDAY

        for (int i = 0; i < values.size(); i++) {
            List<Object> row = values.get(i);
            if (row.size() < 2) continue;

            String timeStr = row.get(0).toString().trim();
            String task = row.get(1).toString();
            String dayStr = (row.size() > 2) ? row.get(2).toString().trim().toUpperCase() : "ALL";
            String status = (row.size() > 3) ? row.get(3).toString() : "";

            try {
                // 🟢 一次性提醒 (格式 yyyy/MM/dd HH:mm) → 不檢查星期
                if (timeStr.contains("/")) {
                    LocalDateTime remindTime = LocalDateTime.parse(
                            timeStr, DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
                    long diff = Duration.between(remindTime, now).toMinutes();
                    if (Math.abs(diff) <= 1 && !"已提醒".equals(status)) {
                        ReminderPopup.show("📌 " + task);

                        // 更新狀態 → D 欄
                        sheets.spreadsheets().values().update(
                                spreadsheetId,
                                "reminder!D" + (i + 2),
                                new ValueRange().setValues(List.of(List.of("已提醒")))
                        ).setValueInputOption("RAW").execute();
                    }
                }
                // 🟢 每日提醒 (格式 HH:mm) → 要檢查星期
                else {
                    if (!isDayMatch(dayStr, today)) {
                        continue; // 星期不符合 → 跳過
                    }

                    LocalTime remindTime = LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm"));
                    long diff = Duration.between(remindTime, nowOnlyTime).toMinutes();
                    if (Math.abs(diff) <= 1) {
                        String key = remindTime.toString() + "-" + LocalDate.now();
                        if (!remindedToday.contains(key)) {
                            ReminderPopup.show("📌 (每日) " + task);
                            remindedToday.add(key);

                            // 每日提醒 → 不更新狀態，避免失效
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("⚠️ 無法解析時間格式: " + timeStr + "，錯誤訊息: " + e.getMessage());
            }
        }
    }

    /**
     * 判斷今天是否符合 row 設定的星期
     */
    private boolean isDayMatch(String dayStr, DayOfWeek today) {
        if (dayStr.equals("ALL")) return true;
        if (dayStr.equals("WEEKDAY")) return today != DayOfWeek.SATURDAY && today != DayOfWeek.SUNDAY;
        if (dayStr.equals("WEEKEND")) return today == DayOfWeek.SATURDAY || today == DayOfWeek.SUNDAY;

        String[] days = dayStr.split(",");
        for (String d : days) {
            if (d.trim().equalsIgnoreCase(today.toString())) {
                return true;
            }
        }
        return false;
    }
}
