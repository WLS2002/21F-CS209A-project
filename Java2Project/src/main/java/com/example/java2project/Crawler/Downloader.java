package com.example.java2project.Crawler;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class Downloader {

    private int fileLength;
    private int currentLength;
    private boolean isDownloading;

    public int getFileLength() {
        return fileLength;
    }

    public int getCurrentLength() {
        return currentLength;
    }

    private static Downloader instance;

    private Downloader(){
        isDownloading = false;
    }

    public synchronized static Downloader getInstance(){
        if (instance == null){
            instance = new Downloader();
        }
        return instance;
    }

    private static boolean isRedirected(Map<String, List<String>> header) {
        for (String hv : header.get(null)) {
            if (hv.contains(" 301 ")
                    || hv.contains(" 302 ")) return true;
        }
        return false;
    }

    public void download(String link, String fileName) throws Exception {
        if(isDownloading){
            throw new Exception("is Downloading");
        }
        isDownloading = true;
        URL url = new URL(link);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        Map<String, List<String>> header = http.getHeaderFields();
        System.out.println(header);
        while (isRedirected(header)) {
            link = header.get("Location").get(0);
            url = new URL(link);
            http = (HttpURLConnection) url.openConnection();
            header = http.getHeaderFields();
        }
        this.fileLength = http.getContentLength();
        InputStream input = http.getInputStream();
        byte[] buffer = new byte[4096];
        int n;
        this.currentLength = 0;
        OutputStream output = new FileOutputStream(fileName);
        while ((n = input.read(buffer)) != -1) {
            output.write(buffer, 0, n);
            this.currentLength += 4096;
        }
        output.close();
        isDownloading = false;
    }

    public static void main(String[] args) throws Exception {
        Downloader downloader = getInstance();
        downloader.download("https://covid19.who.int/WHO-COVID-19-global-data.csv", "src/File/who.csv");
        downloader.download("https://github.com/owid/covid-19-data/blob/master/public/data/owid-covid-data.csv?raw=true", "src/File/owid.csv");

    }

}
