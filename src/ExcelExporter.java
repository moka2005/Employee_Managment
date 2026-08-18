import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelExporter {

    public static boolean exportTable(JTable table, String defaultFileName, String sheetTitle) {
        if (table == null) {
            UITheme.showThemedMessage(null, "لا يوجد جدول متاح للتصدير!", "تنبيه", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("اختر مسار حفظ ملف Excel");
        String initialName = defaultFileName != null && !defaultFileName.trim().isEmpty() ? defaultFileName.trim() : "تقرير.xlsx";
        if (!initialName.toLowerCase().endsWith(".xlsx")) {
            initialName += ".xlsx";
        }
        fileChooser.setSelectedFile(new File(initialName));

        int userSelection = fileChooser.showSaveDialog(null);
        if (userSelection != JFileChooser.APPROVE_OPTION) {
            return false;
        }

        File fileToSave = fileChooser.getSelectedFile();
        String filePath = fileToSave.getAbsolutePath();
        if (!filePath.toLowerCase().endsWith(".xlsx")) {
            filePath += ".xlsx";
            fileToSave = new File(filePath);
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(sheetTitle != null && !sheetTitle.trim().isEmpty() ? sheetTitle.trim() : "البيانات");
            sheet.setRightToLeft(true);

            // Header Style
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerFont.setColor(IndexedColors.WHITE.getIndex());

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);

            // Data Style (Even rows)
            org.apache.poi.ss.usermodel.Font dataFont = workbook.createFont();
            dataFont.setFontHeightInPoints((short) 11);

            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setFont(dataFont);
            dataStyle.setAlignment(HorizontalAlignment.CENTER);
            dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);

            // Alternate Row Style (Odd rows)
            CellStyle altStyle = workbook.createCellStyle();
            altStyle.setFont(dataFont);
            altStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            altStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            altStyle.setAlignment(HorizontalAlignment.CENTER);
            altStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            altStyle.setBorderBottom(BorderStyle.THIN);
            altStyle.setBorderTop(BorderStyle.THIN);
            altStyle.setBorderRight(BorderStyle.THIN);
            altStyle.setBorderLeft(BorderStyle.THIN);

            // Identify visible columns (ignore hidden columns like width == 0)
            TableColumnModel colModel = table.getColumnModel();
            int totalCols = colModel.getColumnCount();
            List<Integer> visibleColIndices = new ArrayList<>();
            for (int c = 0; c < totalCols; c++) {
                if (colModel.getColumn(c).getWidth() > 0) {
                    visibleColIndices.add(c);
                }
            }

            if (visibleColIndices.isEmpty()) {
                for (int c = 0; c < totalCols; c++) visibleColIndices.add(c);
            }

            int visibleColCount = visibleColIndices.size();
            int rowCount = table.getRowCount();

            // Create Header Row
            Row headerRow = sheet.createRow(0);
            headerRow.setHeightInPoints(26);
            for (int outCol = 0; outCol < visibleColCount; outCol++) {
                int tableCol = visibleColIndices.get(outCol);
                Cell cell = headerRow.createCell(outCol);
                Object colName = table.getColumnName(tableCol);
                cell.setCellValue(colName != null ? colName.toString() : "");
                cell.setCellStyle(headerStyle);
            }

            // Create Data Rows (using visible table rows to respect sorting / filtering)
            for (int viewRow = 0; viewRow < rowCount; viewRow++) {
                Row dataRow = sheet.createRow(viewRow + 1);
                dataRow.setHeightInPoints(22);
                CellStyle currentStyle = (viewRow % 2 == 1) ? altStyle : dataStyle;

                for (int outCol = 0; outCol < visibleColCount; outCol++) {
                    int tableCol = visibleColIndices.get(outCol);
                    Cell cell = dataRow.createCell(outCol);
                    Object val = table.getValueAt(viewRow, tableCol);
                    if (val != null) {
                        cell.setCellValue(val.toString());
                    } else {
                        cell.setCellValue("");
                    }
                    cell.setCellStyle(currentStyle);
                }
            }

            // Safely set column widths
            for (int outCol = 0; outCol < visibleColCount; outCol++) {
                try {
                    sheet.autoSizeColumn(outCol);
                    int calculatedWidth = sheet.getColumnWidth(outCol);
                    sheet.setColumnWidth(outCol, Math.min(Math.max(calculatedWidth + 1500, 4000), 12000));
                } catch (Exception ex) {
                    sheet.setColumnWidth(outCol, 4500);
                }
            }

            try (FileOutputStream out = new FileOutputStream(fileToSave)) {
                workbook.write(out);
            }

            UITheme.showThemedMessage(null, "تم تصدير ملف Excel بنجاح إلى:\n" + fileToSave.getAbsolutePath(), "تم التصدير بنجاح", JOptionPane.INFORMATION_MESSAGE);
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            UITheme.showThemedMessage(null, "تعذر حفظ الملف!\nإذا كان الملف مفتوحاً حالياً في برنامج آخر (مثل Excel)، يرجى إغلاقه أولاً ثم المحاولة مجدداً.", "خطأ في حفظ الملف", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            UITheme.showThemedMessage(null, "حدث خطأ أثناء تصدير Excel:\n" + e.getMessage(), "خطأ", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}
