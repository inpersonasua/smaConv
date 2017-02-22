package smaConv;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import smaConv.converters.Converter;
import smaConv.util.AnkiApkg;
import smaConv.util.AnkiCard;
import smaConv.util.Deck;
import smaConv.util.Parser;

public class SuperMemoToAnkiConverterTest {
  SuperMemoToAnkiConverter smux2anki;
  Parser parser;
  Converter converter;
  AnkiApkg apkg;
  Deck<AnkiCard> deck;
  Map<String, String> question = new HashMap<>();
  Map<String, String> answer = new HashMap<>();
  String vocabularyCourseXml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n"//
      + "<course xmlns=\"http://www.supermemo.net/2006/smux\">\r\n" //
      + "  <title>Title</title>\r\n"//
      + "  <type>vocabulary</type>\r\n" //
      + "</course>";

  String notVocabularyCourseXml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n"//
      + "<course xmlns=\"http://www.supermemo.net/2006/smux\">\r\n" //
      + "  <title>Title</title>\r\n"//
      + "</course>";
  
  @Before
  public void setUp() {
    

    parser = mock(Parser.class);
    converter = mock(Converter.class);
    apkg = mock(AnkiApkg.class);

    smux2anki = new SuperMemoToAnkiConverter(parser, converter, apkg);

    when(parser.getFile("course.xml")).thenReturn(vocabularyCourseXml.getBytes());
    when(parser.isSmpakFile()).thenReturn(true);

    question.put("front", "abc");
    question.put("back", "xyz");
    AnkiCard card = new AnkiCard(question, answer);
    deck = new Deck<>();
    deck.add(card);
    deck.setQuestionTemplate("{{front}}");
    deck.setAnswerTemplate("{{back}}");
  }

  @Test
  public void shouldNotConvertImproperInputFile() {
    when(parser.isSmpakFile()).thenReturn(false);

    smux2anki.convert();

    verify(parser, times(0)).parse();
  }

  @Test
  public void onlyVocabularyCourseIsAllowed() {
    when(parser.getFile("course.xml")).thenReturn(notVocabularyCourseXml.getBytes());

    assertThat(smux2anki.convert())
        .isEqualTo("Only vocabulary course is suitable for conversion.");
  }

  @Test
  public void methodParseOnParserShouldBeCalled() {
    when(converter.makeDeck(parser)).thenReturn(deck);

    smux2anki.convert();

    verify(parser, times(1)).parse();
  }

  @Test
  public void mainDbFileShouldBeAddedToApkgArchive() {
    when(converter.makeDeck(parser)).thenReturn(deck);

    smux2anki.convert();

    verify(apkg).addToArchive(eq("collection.anki2"), anyObject());
  }

  @Test
  public void emptyMediaFileShouldBeAddedToApkgArchive() {
    when(converter.makeDeck(parser)).thenReturn(deck);

    smux2anki.convert();

    verify(apkg).addToArchive("media", "{}".getBytes());
  }

  @Test
  public void oneSoundFileShouldBeAddedToArchive() {
    question.put("front", "abc");
    question.put("sound", "[sound:123a.mp3]");
    deck.add(new AnkiCard(question, answer));

    when(converter.makeDeck(parser)).thenReturn(deck);
    when(parser.getFile("media/123.mp3")).thenReturn("anything".getBytes());

    smux2anki.convert();

    verify(apkg).addToArchive(eq("collection.anki2"), anyObject());
    verify(apkg).addToArchive(eq("media"), anyObject());
    verify(apkg).addToArchive(eq("1"), anyObject()); // "1" is renamed file name
  }

  @Test
  public void twoSoundFilesShouldBeAddedToArchive() {
    question.put("front", "[sound:abc.mp3]");
    question.put("sound", "[sound:123a.mp3]");
    deck.add(new AnkiCard(question, answer));

    when(converter.makeDeck(parser)).thenReturn(deck);
    when(parser.getFile("media/123a.mp3")).thenReturn("anything".getBytes());
    when(parser.getFile("media/abc.mp3")).thenReturn("anything".getBytes());

    smux2anki.convert();

    verify(apkg).addToArchive(eq("collection.anki2"), anyObject());
    verify(apkg).addToArchive(eq("media"), anyObject());
    verify(apkg).addToArchive(eq("1"), anyObject());
    verify(apkg).addToArchive(eq("2"), anyObject());
  }
}
