package smaConv.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.zip.Inflater;
import java.util.zip.InflaterOutputStream;

import org.riversun.bigdoc.bin.BinFileSearcher;

public class SmpakParser implements Parser {
  private final FileChannel smpakFile;
  private final HashMap<String, FileEntry> cachedEntries = new HashMap<>();
  private final Path smpakPath;

  /**
   * Creates smpak parser for the file argument.
   *
   * @param smpakFile
   *          - smpak file (usually course.smpak) with supermemo course.
   */
  public SmpakParser(Path smpak) {
    try {
      this.smpakPath = smpak;
      this.smpakFile = FileChannel.open(smpak, StandardOpenOption.READ);
    } catch (IOException e) {
      throw new RuntimeException("Error at reading file: " + smpak.toString());
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see smux2anki.util.Parser#isSupermemoFile()
   */
  @Override
  public boolean isSmpakFile() {
    try {
      int headerLength = 8;
      ByteBuffer headerBytes = ByteBuffer.allocate(headerLength);
      smpakFile.read(headerBytes, 0);

      String headerString = new String(headerBytes.array(), StandardCharsets.ISO_8859_1);
      return headerString.equals("-SMArch-");
    } catch (IOException exception) {
      throw new RuntimeException("Error at reading smpak file.");
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see smux2anki.util.Parser#parse()
   */
  @Override
  public void parse() {
    try {
      adjustEntrPos();
      int entrPos = readInt();
      int namePos = readInt();
      namePos = findNamesPosition(namePos);
      smpakFile.position(entrPos);
      int filesCount = readInt();
      setFileEntries(namePos, filesCount);
    } catch (IOException exception) {
      throw new RuntimeException("Error at parsing smpak file.");
    }
  }

  /**
   * Ajusts entrPos - this value is different for *.smpak/*.smpdif and *.smdif2.
   * 
   * @throws IOException
   */
  private void adjustEntrPos() throws IOException {
    int checkvaluePosition = 8;
    smpakFile.position(checkvaluePosition);
    int checkvalueForSmdif2 = 512;
    int namePosPosition;
    if (readShort() == checkvalueForSmdif2) {
      namePosPosition = 26;
    } else {
      namePosPosition = 12;
    }
    smpakFile.position(namePosPosition);
  }

  private int findNamesPosition(int lastPosition) throws IOException {
    String textToFind = "course.xml";

    BinFileSearcher searcher = new BinFileSearcher();
    long namePos = searcher.indexOf(smpakPath.toFile(), textToFind.getBytes(), lastPosition);

    return (int) namePos;
  }

  private void setFileEntries(int namePos, final int filesCnt) throws IOException {
    for (int i = 0; i < filesCnt; i++) {
      int currentNamePosition = namePos + readInt();
      int nameSize = readShort();
      FileEntry fileEntry = new FileEntry();
      fileEntry.setCompression(readShort());
      fileEntry.setFilePosition(readInt());
      fileEntry.setFileSize(readInt());
      long currentPosition = smpakFile.position();
      smpakFile.position(currentNamePosition);
      ByteBuffer buf = ByteBuffer.allocate(nameSize);
      smpakFile.read(buf);
      fileEntry.setFileName(new String(buf.array(), StandardCharsets.ISO_8859_1));
      smpakFile.position(currentPosition);
      cachedEntries.put(fileEntry.getFileName(), fileEntry);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see smux2anki.util.Parser#getFile(java.lang.String)
   */
  @Override
  public byte[] getFile(String fileName) {
    FileEntry fileEntry = cachedEntries.get(fileName);
    if (fileEntry == null) {
      return new byte[0];
    }
    ByteBuffer buffer = ByteBuffer.allocate(fileEntry.getFileSize());
    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(
        buffer.capacity())) {
      smpakFile.position(fileEntry.getFilePosition());
      smpakFile.read(buffer);
      if (fileEntry.isCompressed()) {
        Inflater inflater = new Inflater(true);
        inflater.setInput(buffer.array());
        new InflaterOutputStream(byteArrayOutputStream, inflater).close();
      } else {
        byteArrayOutputStream.write(buffer.array());
      }
      return byteArrayOutputStream.toByteArray();
    } catch (IOException exception) {
      throw new RuntimeException("Error at extracting file from smpak: " + exception.getMessage());
    }
  }

  private int readInt() throws IOException {
    ByteBuffer buf = ByteBuffer.allocate(4);
    smpakFile.read(buf);
    buf.rewind();
    return buf.order(ByteOrder.LITTLE_ENDIAN).getInt();
  }

  private short readShort() throws IOException {
    ByteBuffer buf = ByteBuffer.allocate(2);
    smpakFile.read(buf);
    buf.rewind();
    return buf.order(ByteOrder.LITTLE_ENDIAN).getShort();
  }

  /*
   * (non-Javadoc)
   *
   * @see smux2anki.util.Parser#close()
   */
  @Override
  public void close() {
    try {
      smpakFile.close();
    } catch (IOException exception) {
      throw new RuntimeException("smpak file can't be closed.");
    }
  }
}
