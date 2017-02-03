package smaConv.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

public class SmpakParserWithUpdatesTest {
  /*-
   * first characters of course.xml are changed: 
   * smpak - ﻿<?xml version="1.0" 
   * smdif - ﻿<?xml version="2.0" 
   * smdif2 - ﻿<?xml version="3.0"
   * smdif3 - broken file - should not be parsed
   * file item00002.xml - is unique and not empty (233 bytes), exist only in smpak and not in smdif's.
   */

  private static final String SMPAK_FILE = "src/test/resources/course.smpak";
  private String courseXml = "course.xml";
  private String uniqueFile = "item00002.xml";

  private String smpakString = "version=\"1.0\"";
  private String smdifString = "version=\"2.0\"";
  private String smdif2String = "version=\"3.0\"";

  private Parser parser;

  @Before
  public void setUp() {
    parser = new SmpakParserWithUpdates(Paths.get(SMPAK_FILE));
    parser.parse();
  }

  @Test
  public void shouldBeSupermemoFile() {
    assertThat(parser.isSmpakFile()).isTrue();
  }

  @Test
  public void shouldReturnEmptyByteArrayWhenFileNotFound() {
    assertThat(parser.getFile("no_such_file")).isEmpty();
  }

  @Test
  public void mostRecentFileSchouldBeReturned() {
    String content = new String(parser.getFile(courseXml), StandardCharsets.UTF_8);
    assertThat(content).contains(smdif2String);
  }

  @Test
  public void oldFileShouldNotBeReturned() {
    String content = new String(parser.getFile(courseXml), StandardCharsets.UTF_8);
    assertThat(content).doesNotContain(smdifString);
    assertThat(content).doesNotContain(smpakString);
  }
  
  @Test
  public void notUpdatedFileShouldBeFound() {
    assertThat(parser.getFile(uniqueFile)).isNotEmpty();
  }
}
