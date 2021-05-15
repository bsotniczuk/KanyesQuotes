package model;

public class SentimResponse {

    private Result result;
    private Sentences[] sentences;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public Sentences[] getSentences() {
        return sentences;
    }

    public void setSentences(Sentences[] sentences) {
        this.sentences = sentences;
    }
}
