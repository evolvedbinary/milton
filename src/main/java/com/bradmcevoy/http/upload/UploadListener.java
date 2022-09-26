package com.bradmcevoy.http.upload;

public class UploadListener implements OutputStreamListener {
   private int totalBytesRead = 0;
   private int totalFiles = -1;

   public void start() {
      ++this.totalFiles;
      this.updateUploadInfo("start");
   }

   public void bytesRead(int bytesRead) {
      this.totalBytesRead += bytesRead;
      this.updateUploadInfo("progress");
   }

   public void error(String message) {
      this.updateUploadInfo("error");
   }

   public void done() {
      this.updateUploadInfo("done");
   }

   private void updateUploadInfo(String status) {
   }
}
