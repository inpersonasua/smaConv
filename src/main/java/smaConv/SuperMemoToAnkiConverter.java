package smaConv;

import java.util.Set;

import smaConv.anki_db.AnkiSqlDb;
import smaConv.converters.Converter;
import smaConv.ui.Messages;
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
      return Messages.getString("SuperMemoToAnkiConverter.WrongFileFormat"); //$NON-NLS-1$
    }
    parser.parse();

    if (!isSuitableForConvertion()) {
      return Messages.getString("SuperMemoToAnkiConverter.NotSuitableForConvertion"); //$NON-NLS-1$
    }

    Deck<AnkiCard> deck = converter.makeDeck(parser);

    AnkiSqlDb ankiDb = new AnkiSqlDb();
    ankiDb.createDb(deck);
    apkgFile.addToArchive("collection.anki2", ankiDb.getFile()); //$NON-NLS-1$

    MediaFile mediaFile = new MediaFile();
    Set<String> mediaList = deck.getSounds();
    if (!mediaList.isEmpty()) {
      String mediaSubdirectory = "media/"; //$NON-NLS-1$
      int i = 1;
      for (String file : mediaList) {
        String key = Integer.toString(i);
        if (apkgFile.addToArchive(key, parser.getFile(mediaSubdirectory + file))) {
          mediaFile.addMediaFile(key, file);
        }
        i++;
      }
    }
    apkgFile.addToArchive("media", mediaFile.getMediaFile()); //$NON-NLS-1$
    apkgFile.close();

    return Messages.getString("SuperMemoToAnkiConverter.ConvertionFinished"); //$NON-NLS-1$
  }

  private boolean isSuitableForConvertion() {
    byte[] courseXml = parser.getFile("course.xml"); //$NON-NLS-1$
    String expression = "//type/text()"; //$NON-NLS-1$
    XmlParser xmlParser = new XmlParser();
    String value = xmlParser.getValue(courseXml, expression);
    if (value.equals("vocabulary")) { //$NON-NLS-1$
      return true;
    }
    return false;
  }
}
