package org.example;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

public class VideoDownloaderTask implements Callable<String> {

	private final String url;
	private final String fileName;
	private final String outputPath;

	public VideoDownloaderTask(String url, String fileName, String outputPath) {
		super();
		this.url = url;
		this.fileName = fileName;
		this.outputPath = outputPath + "/" + fileName;
	}

	@Override
	public String call() {
		return downloadFile();
	}

	private String downloadFile() {
		HttpURLConnection httpConn = null;
		try {
			httpConn = (HttpURLConnection) new URL(url).openConnection();
			int responseCode = httpConn.getResponseCode();

			if (responseCode == HttpURLConnection.HTTP_OK) {
				InputStream inputStream = httpConn.getInputStream();
				FileOutputStream outputStream = new FileOutputStream(outputPath);

				int bytesRead;
				byte[] buffer = new byte[4096];
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
				}
				outputStream.close();
				inputStream.close();
				return "";
			} else {
				System.out.println("No file to download. Server replied HTTP code: " + responseCode);
				return fileName;
			}
		} catch (Exception e) {
			System.err.println("Unable to download file with error : " + e.getMessage());
		} finally {
			if (httpConn != null)
				httpConn.disconnect();
		}
		return "";
	}

}
