package org.example;


import org.example.bean.DownloadFileInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class VideoDownloadExecutors {

    static String FINAL_OUTPUT_PATH = "video/dest";
    static String OUTPUT_PATH = "video/src";

    static String BASE_FILE_NAME = "data";

    public static void downloadFiles() {
        // read file
        List<DownloadFileInfo> downloadFileInfoList = InputFileReader.readInputFile();
        System.out.println("Input size : " + downloadFileInfoList.size());

        downloadFileInfoList.forEach(info -> {
            System.out.println("Started downloading file : " + info.getFileName());
            executeDownload(info.getDownloadStart(), info.getDownloadEnd(), info.getUrl(),
                    info.getFileName() + ".");
            System.out.println("File downloading completed for file : " + info.getFileName());
        });
        System.err.println("Downloaded all the files");
    }

    private static void executeDownload(int downloadStart, int downloadEnd, String url, String outputFileName) {
        // Downloading all the files
        downloader(downloadStart, downloadEnd, url);
        // Merging them together
        merger(downloadEnd, outputFileName + "ts");
        // cleaning up source directory
        cleanUpTemp(downloadEnd);

    }

    private static void downloader(int downloadStart, int downloadEnd, String url) {
        ExecutorService executorService = Executors.newFixedThreadPool(30);
        CompletionService<String> completionService = new ExecutorCompletionService<>(executorService);

        // Submit tasks
        for (int i = downloadStart; i <= downloadEnd; i++) {
            String fileNumber = String.format("%06d", i);
            String fileName = String.format("%s%s.ts", BASE_FILE_NAME, fileNumber);
            String baseUrl = String.format(url, fileNumber);

            completionService
                    .submit(new VideoDownloaderTask(baseUrl, fileName, OUTPUT_PATH));
        }

        // Shutdown the executor
        executorService.shutdown();

        try {
            boolean isCompleted = executorService.awaitTermination(downloadEnd * 800L, TimeUnit.MILLISECONDS);
            if (isCompleted)
                System.err.println("Download Completed!");
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
        }

        System.out.println("All tasks completed.");
    }

    private static void merger(int threadCount, String outputFileName) {
        VideoMerger merger = new VideoMerger(threadCount, BASE_FILE_NAME,
                FINAL_OUTPUT_PATH + "/" + outputFileName, OUTPUT_PATH);
        merger.mergeVideo();
    }

    private static void cleanUpTemp(int threadCount) {
        for (int i = 0; i <= threadCount; i++) {
            String fileName = String.format("%s%s.ts", BASE_FILE_NAME, String.format("%06d", i));
            Path path = Paths.get(String.format("%s/%s", OUTPUT_PATH, fileName));
            try {
                if (Files.exists(path)) {
                    Files.delete(path);
                } else {
                    System.err.println("File " + fileName + " does not exist.");
                }
            } catch (IOException e) {
                System.err.println("Unable to delete file " + fileName);
            }
        }
    }
}
