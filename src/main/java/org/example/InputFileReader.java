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
                        case 0:
                            // fileName
                            if (cell.getCellType() == CellType.STRING) {
                                downloadFileInfo.setFileName(cleanFileName(cell.getStringCellValue()));
                            }
                            break;
                        case 1:
                            if (cell.getCellType() == CellType.STRING) {
                                String url = cell.getStringCellValue();
                                downloadFileInfo.setUrl(replaceWithPlaceHolder(url));
                                downloadFileInfo.setDownloadEnd(getLastFileNumber(url));
                            }
                            break;
                        default:
                            break;
                    }
                }
                downloadFileInfos.add(downloadFileInfo);
            }

        } catch (
                IOException e) {
            System.err.println(e.getMessage());
        }
        return downloadFileInfos;
    }

    private static Integer getLastFileNumber(String url) {
        int dataIndex = url.indexOf("data") + 4;
        StringBuilder fileNumber = new StringBuilder();
        for (int i = dataIndex; i < dataIndex + 6; i++) fileNumber.append(url.charAt(i));
        return Integer.parseInt(fileNumber.toString().replaceAll("\\D", ""));
    }

    private static String replaceWithPlaceHolder(String url) {
        int dataIndex = url.indexOf("data");
        int tsIndex = url.indexOf(".ts", dataIndex);
        // Construct the modified URL string
        return url.substring(0, dataIndex + "data".length()) +
                "%s" +  // Replace with your 6-character string here
                url.substring(tsIndex);
    }

    private static String cleanFileName(String filename) {
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
