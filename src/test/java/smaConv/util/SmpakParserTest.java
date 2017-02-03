package smaConv.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

public class SmpakParserTest {
  // File for testing. Total number of files is 51
  private static final String SMPAK_FILE = "src/test/resources/course.smpak";

  private Parser parser;

  @Before
  public void setUp() {
    parser = new SmpakParser(Paths.get(SMPAK_FILE));
    parser.parse();
  }

  @Test
  public void shouldBeSupermemoFile() {
    assertThat(parser.isSmpakFile()).isTrue();
  }

  @Test
  public void fileShouldBeFound() {
    assertThat(parser.getFile("course.xml")).isNotEmpty();
  }

  @Test
  public void shouldReturnEmptyByteArrayWhenFileNotFound() {
    assertThat(parser.getFile("no_such_file")).isEmpty();
  }
}
