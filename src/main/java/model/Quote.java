package model;

public class Quote {

    private String quote;

    public Quote() {
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Quote)) return false;
        Quote other = (Quote) o;
        return this.quote.compareToIgnoreCase(other.quote) == 0;
    }
}
