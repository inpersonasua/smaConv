package smaConv.util;

public class FileEntry {
  private String fileName;
  private int filePosition;
  private int fileSize;
  private short compression;

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String name) {
    this.fileName = name;
  }

  public int getFilePosition() {
    return filePosition;
  }

  public void setFilePosition(int filePos) {
    this.filePosition = filePos;
  }

  public int getFileSize() {
    return fileSize;
  }

  public void setFileSize(int fileSize) {
    this.fileSize = fileSize;
  }

  public void setCompression(short compression) {
    this.compression = compression;
  }

  public boolean isCompressed() {
    return compression == 0 ? false : true;
  }

  @Override
  public String toString() {
    return fileName + ": [" + filePosition + "," + fileSize + "], "
        + (compression == 0 ? "(uncompressed)" : "(compressed)");
  }
}
