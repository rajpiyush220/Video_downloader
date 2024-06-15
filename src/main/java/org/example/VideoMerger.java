package org.example;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class VideoMerger {

    private final int videoCount;
    private final String baseFileName;
    private final String outputFilePath;
    private final String inputFilePath;


    public VideoMerger(int videoCount, String baseFileName, String outputFilePath, String inputFilePath) {
        super();
        this.videoCount = videoCount;
        this.baseFileName = baseFileName;
        this.outputFilePath = outputFilePath;
        this.inputFilePath = inputFilePath;
    }

    public void mergeVideo() {
        FileOutputStream outputStream = null;

        try {
            outputStream = new FileOutputStream(outputFilePath);
            for (int i = 0; i <= videoCount; i++) {
                String formattedNumber = String.format("%06d", i);
                String fileName = String.format("%s%s.ts", baseFileName, formattedNumber);
                String readLocation = inputFilePath + "/" + fileName;
                try {
                    byte[] fileBytes = Files.readAllBytes(Path.of(readLocation));
                    outputStream.write(fileBytes);
                } catch (IOException e) {
                    System.err.println("Unable to read file " + fileName);
                }
            }
            outputStream.close();
            System.out.println("Merging completed successfully.");
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

}
