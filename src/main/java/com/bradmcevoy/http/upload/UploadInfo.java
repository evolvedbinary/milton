package com.bradmcevoy.http.upload;

public class UploadInfo {
   private long totalSize = 0L;
   private long bytesRead = 0L;
   private long elapsedTime = 0L;
   private String status = "done";
   private int fileIndex = 0;

   public UploadInfo() {
   }

   public UploadInfo(int fileIndex, long totalSize, long bytesRead, long elapsedTime, String status) {
      this.fileIndex = fileIndex;
      this.totalSize = totalSize;
      this.bytesRead = bytesRead;
      this.elapsedTime = elapsedTime;
      this.status = status;
   }

   public String getStatus() {
      return this.status;
   }

   public void setStatus(String status) {
      this.status = status;
   }

   public long getTotalSize() {
      return this.totalSize;
   }

   public void setTotalSize(long totalSize) {
      this.totalSize = totalSize;
   }

   public long getBytesRead() {
      return this.bytesRead;
   }

   public void setBytesRead(long bytesRead) {
      this.bytesRead = bytesRead;
   }

   public long getElapsedTime() {
      return this.elapsedTime;
   }

   public void setElapsedTime(long elapsedTime) {
      this.elapsedTime = elapsedTime;
   }

   public boolean isInProgress() {
      return "progress".equals(this.status) || "start".equals(this.status);
   }

   public int getFileIndex() {
      return this.fileIndex;
   }

   public void setFileIndex(int fileIndex) {
      this.fileIndex = fileIndex;
   }
}
