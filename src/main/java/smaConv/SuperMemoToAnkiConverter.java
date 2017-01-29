package smaConv;

import java.util.Set;

import smaConv.anki_db.AnkiSqlDb;
import smaConv.converters.Converter;
import smaConv.util.AnkiApkg;
import smaConv.util.AnkiCard;
import smaConv.util.Deck;
import smaConv.util.MediaFile;
import smaConv.util.Parser;
import smaConv.util.XmlParser;

public class SuperMemoToAnkiConverter {
  private final Converter converter;
  private final Parser parser;
  private final AnkiApkg apkgFile;

  /**
   * Default constructor. Sets parser, converter and apkg file.
   * @param parser - Parser for parsing input file.
   * @param converter - Converts to desired card type.
   * @param apkgFile - Output file as packaged deck.
   */
  public SuperMemoToAnkiConverter(Parser parser, Converter converter, AnkiApkg apkgFile) {
    this.parser = parser;
    this.converter = converter;
    this.apkgFile = apkgFile;
  }

  /**
   * Do the converstion from Supermemo *.smapk to Anki *.apkg. 
   * @return Summary message.
   */
  public String convert() {
    if (!parser.isSmpakFile()) {
      return "Nieprawidłowy format pliku.";
    }
    parser.parse();

    if (!isSuitableForConvertion()) {
      return "Do konwertowania nadają się tylko kursy słownictwa.";
    }

    Deck<AnkiCard> deck = converter.makeDeck(parser);

    AnkiSqlDb ankiDb = new AnkiSqlDb();
    ankiDb.createDb(deck);
    apkgFile.addToArchive("collection.anki2", ankiDb.getFile());

    MediaFile mediaFile = new MediaFile();
    Set<String> mediaList = deck.getSounds();
    if (!mediaList.isEmpty()) {
      String mediaSubdirectory = "media/";
      int i = 1;
      for (String file : mediaList) {
        String key = Integer.toString(i);
        if (apkgFile.addToArchive(key, parser.getFile(mediaSubdirectory + file))) {
          mediaFile.addMediaFile(key, file);
        }
        i++;
      }
    }
    apkgFile.addToArchive("media", mediaFile.getMediaFile());
    apkgFile.close();

    return "Konwertowanie zakończone powodzeniem.";
  }

  private boolean isSuitableForConvertion() {
    byte[] courseXml = parser.getFile("course.xml");
    String expression = "//type/text()";
    XmlParser xmlParser = new XmlParser();
    String value = xmlParser.getValue(courseXml, expression);
    if (value.equals("vocabulary")) {
      return true;
    }
    return false;
  }
}
