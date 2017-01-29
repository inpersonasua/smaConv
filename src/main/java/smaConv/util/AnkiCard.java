package smaConv.util;

import java.util.Map;

public class AnkiCard {
  private final Map<String, String> question;
  private final Map<String, String> answer;

  public AnkiCard(Map<String, String> question, Map<String, String> answer) {
    this.question = question;
    this.answer = answer;
  }

  public Map<String, String> getQuestion() {
    return question;
  }

  public Map<String, String> getAnswer() {
    return answer;
  }

  @Override
  public String toString() {
    return "Q:" + question.toString() + " A:" + answer.toString();
  }
}
