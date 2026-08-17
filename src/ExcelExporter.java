import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExcelExporter {

    public static boolean exportTable(JTable table, String defaultFileName, String sheetTitle) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("اختر مسار حفظ ملف Excel");
        String initialName = defaultFileName != null && !defaultFileName.isEmpty() ? defaultFileName : "تقرير.xlsx";
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
            Sheet sheet = workbook.createSheet(sheetTitle != null && !sheetTitle.isEmpty() ? sheetTitle : "البيانات");
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

            // Data Style
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

            // Alternate Row Style
            CellStyle altStyle = workbook.createCellStyle();
            altStyle.cloneStyleFrom(dataStyle);
            altStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            altStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            TableModel model = table.getModel();
            int columnCount = model.getColumnCount();
            int rowCount = model.getRowCount();

            // Create Header Row
            Row headerRow = sheet.createRow(0);
            headerRow.setHeightInPoints(25);
            for (int col = 0; col < columnCount; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(model.getColumnName(col));
                cell.setCellStyle(headerStyle);
            }

            // Create Data Rows
            for (int row = 0; row < rowCount; row++) {
                Row dataRow = sheet.createRow(row + 1);
                dataRow.setHeightInPoints(20);
                CellStyle currentStyle = (row % 2 == 1) ? altStyle : dataStyle;

                for (int col = 0; col < columnCount; col++) {
                    Cell cell = dataRow.createCell(col);
                    Object val = model.getValueAt(row, col);
                    if (val != null) {
                        cell.setCellValue(val.toString());
                    } else {
                        cell.setCellValue("");
                    }
                    cell.setCellStyle(currentStyle);
                }
            }

            // Auto-size columns
            for (int col = 0; col < columnCount; col++) {
                sheet.autoSizeColumn(col);
                sheet.setColumnWidth(col, Math.max(sheet.getColumnWidth(col) + 1500, 3500));
            }

            try (FileOutputStream out = new FileOutputStream(fileToSave)) {
                workbook.write(out);
            }

            JOptionPane.showMessageDialog(null, "تم تصدير ملف Excel بنجاح إلى:\n" + fileToSave.getAbsolutePath(), "تم التصدير بنجاح", JOptionPane.INFORMATION_MESSAGE);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "حدث خطأ أثناء تصدير Excel:\n" + e.getMessage(), "خطأ", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}
