package smaConv.util;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Exports deck of cards to Anki *.apkg file.
 *
 */
public class AnkiApkg implements AutoCloseable {

  private FileOutputStream apkgFile;
  private final BufferedOutputStream bufferedOutputStream;
  private final ZipOutputStream zipOutputStream;

  /**
   * Creates *.apkg file at specified location.
   * 
   * @param outputFile
   *          - path with file name.
   */
  public AnkiApkg(Path outputFile) {
    try {
      apkgFile = new FileOutputStream(outputFile.toFile());
    } catch (FileNotFoundException exception) {
      throw new RuntimeException("Can't get access to output file.");
    }
    bufferedOutputStream = new BufferedOutputStream(apkgFile);
    zipOutputStream = new ZipOutputStream(bufferedOutputStream);
  }

  /**
   * Adds file to apkg archive. Empty file (length == 0) won't be added.
   *
   * @param fileName
   *          - name of file to be added to archive
   * @param fileContent
   *          - content of the file
   * @return true if file can be added (file size > 0) or false if the file is empty
   */
  public boolean addToArchive(String fileName, byte[] fileContent) {
    if (fileContent.length > 0) {
      try {
        zipOutputStream.putNextEntry(new ZipEntry(fileName));
        zipOutputStream.write(fileContent);
        zipOutputStream.closeEntry();
        return true;
      } catch (IOException exception) {
        throw new RuntimeException("Error at adding " + fileName + " to archive.");
      }
    }
    return false;
  }

  @Override
  public void close() {
    try {
      zipOutputStream.close();
      bufferedOutputStream.close();
      apkgFile.close();
    } catch (IOException exception) {
      throw new RuntimeException("Can't close output file.");
    }
  }
}
