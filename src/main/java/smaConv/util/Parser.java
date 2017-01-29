package smaConv.util;

public interface Parser extends AutoCloseable {

  /**
   * Check if file is suitable for conversion.
   *
   * @return true if file is correct supermemo course.
   */
  boolean isSmpakFile();

  /**
   * Sets parameters like addresses of particular parts of supermemo base.
   */
  void parse();

  /**
   * Extracts uncompressed file from *.smpak SuperMemo course.
   *
   * @param fileName
   *          - name of file to be extracted.
   * @return content as byte array of desired file or empyt byte array if file not found.
   */
  byte[] getFile(String fileName);

  @Override
  void close();

}