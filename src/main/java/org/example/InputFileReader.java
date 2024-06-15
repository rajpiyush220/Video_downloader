package org.example;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.bean.DownloadFileInfo;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InputFileReader {

    public static List<DownloadFileInfo> readInputFile() {
        List<DownloadFileInfo> downloadFileInfos = new ArrayList<>();
        String filePath = "input_file\\input_file.xlsx";
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            // Get the first sheet
            Sheet sheet = workbook.getSheetAt(0);

            // Iterate through each row
            for (Row row : sheet) {
                if (row.getRowNum() == 0 || isRowEmpty(row)) { // Skip the header row
                    continue;
                }

                DownloadFileInfo downloadFileInfo = new DownloadFileInfo();

                for (Cell cell : row) {
                    switch (cell.getColumnIndex()) {
                        case 0: // downloadStart
                            if (cell.getCellType() == CellType.NUMERIC) {
                                downloadFileInfo.setDownloadStart((int) cell.getNumericCellValue());
                            }
                            break;
                        case 1: // downloadEnd
                            if (cell.getCellType() == CellType.NUMERIC) {
                                downloadFileInfo.setDownloadEnd((int) cell.getNumericCellValue());
                            }
                            break;
                        case 2:
                            // fileName
                            if (cell.getCellType() == CellType.STRING) {
                                downloadFileInfo.setFileName(cleanFileName(cell.getStringCellValue()));
                            }
                            break;
                        case 3: // segment
                            if (cell.getCellType() == CellType.STRING) {
                                downloadFileInfo.setUrl(cell.getStringCellValue());
                            }
                            break;
                        default:
                            break;
                    }
                }
                downloadFileInfos.add(downloadFileInfo);
            }

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return downloadFileInfos;
    }

    public static String cleanFileName(String filename) {
        // Define a regular expression for characters not allowed in Windows file names
        String illegalChars = "[<>:\"/|?*]";

        // Replace all illegal characters with an empty string

        return filename.replaceAll(illegalChars, "_");
    }

    private static boolean isRowEmpty(Row row) {
        if (row == null || row.getLastCellNum() <= 0) {
            return true;
        }

        for (int cellNum = row.getFirstCellNum(); cellNum < row.getLastCellNum(); cellNum++) {
            Cell cell = row.getCell(cellNum);
            if (cell != null && cell.getCellType() != CellType.BLANK && !cell.toString().trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }


}
