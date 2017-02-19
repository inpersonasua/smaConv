package smaConv.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

public class SmpakParserWithUpdates implements Parser {

  private final Path smpak;
  private final Parser smpakParser;

  private List<Parser> smdifParsers = new LinkedList<>();

  public SmpakParserWithUpdates(Path smpak) {
    this.smpak = smpak;
    smpakParser = new SmpakParser(smpak);
  }

  /**
   * Checks if format of given file is correct.
   */
  @Override
  public boolean isSmpakFile() {
    return smpakParser.isSmpakFile();
  }

  /**
   * Parses *.smpak and accompanied *.smdif's files.
   */
  @Override
  public void parse() {
    smpakParser.parse();
    parseSmdifs();
  }

  /**
   * Returns most recent file as byte array or empty byte array if file not found.
   * 
   * @return - file content as byte array.
   */
  @Override
  public byte[] getFile(String fileName) {
    if (smdifParsers.isEmpty()) {
      return smpakParser.getFile(fileName);
    } else {
      return smdifParsers.stream().filter(parser -> (parser.getFile(fileName).length != 0))
          .findFirst().orElse(smpakParser).getFile(fileName);
    }
  }

  /**
   * Close opened *.smpak and *.smdif's files.
   */
  @Override
  public void close() {
    smpakParser.close();
    smdifParsers.forEach(parser -> parser.close());
  }

  private void parseSmdifs() {
    getSmdifs().forEach(path -> {
      Parser smdifParser = new SmpakParser(path);
      if (smdifParser.isSmpakFile()) {
        smdifParsers.add(smdifParser);
      }
    });
    smdifParsers.forEach(parser -> parser.parse());
  }

  private List<Path> getSmdifs() {
    Path dir = smpak.getParent();
    String smpakName = FilenameUtils.removeExtension(smpak.getFileName().toString());

    List<Path> smdifs = new LinkedList<>();
    try {
      Files.newDirectoryStream(dir, smpakName + "*.{smdif,smdif?}").forEach(e -> smdifs.add(e));
    } catch (IOException e) {
      e.printStackTrace();
    }
    smdifs.sort(Comparator.comparing(Path::getFileName).reversed());

    return smdifs;
  }
}
