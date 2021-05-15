package controller;

import model.Quote;
import model.Sentences;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class MainController implements QuoteController.QuoteCallbackListener, SentimController.SentimCallbackListener {

    QuoteController quoteController;
    SentimController sentimController;
    boolean isInternetConnected = false;
    int times = 0;
    List<Quote> quoteList;

    public MainController(int times) {
        this.quoteController = new QuoteController(this);
        this.sentimController = new SentimController(this);
        this.quoteList = new ArrayList<>();
        this.times = getAndValidateInput(times);
        this.isInternetConnected = (checkConnectivity("https://api.kanye.rest/") && checkConnectivity("https://sentim-api.herokuapp.com/"));
    }

    public void generateQuotes() {
        System.out.println("_____________________\nQuotes pulled from (https://api.kanye.rest/) after ensuring uniqueness\n_____________________");
        if (isInternetConnected) {
            for (int i = 0; i < times; i++)
                quoteController.fetchQuoteApi();
        }
        else {
            System.out.println("I am sorry, you cannot connect to one of the APIs or you don't have an active internet connection");
        }
    }

    private boolean checkConnectivity(String urlString) {
        try {
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            connection.connect();
            //System.out.println("Internet is connected to: " + urlString); //to delete
            return true;
        } catch (MalformedURLException e) {
            System.out.println("Internet is not connected to: " + urlString + " | please reconnect to continue");
        } catch (IOException e) {
            System.out.println("Internet is not connected to: " + urlString + " | please reconnect to continue");
        }
        return false;
    }

    private int getAndValidateInput(int times) {
        if (times < 5) {
            System.out.println("(Input " + times + " is too low) Lowest quantity of Kanye's quotes is 5, fetching 5 quotes");
            return 5;
        }
        else if (times > 20) {
            System.out.println("(Input " + times + " is too high) Highest quantity of Kanye's quotes is 20, fetching 20 quotes");
            return 20;
        }
        else return times;
    }

    @Override
    public void onFetchProgress(Quote quote) {
        addQuoteAndCheckDuplicates(quote);

        if (checkCompletion()) {
            for (int i = 0; i < quoteList.size(); i++) System.out.println("\"" + quoteList.get(i).getQuote() + "\"");
            System.out.println("_____________________"); //endof fetching
            analyseQuotes();
        }
    }

    @Override
    public void onFetchCompleted(List<Sentences> sentences) {
        int countPositive, countNegative, countNeutral;
        countPositive = countNegative = countNeutral = 0;
        Float extremeValue = (float) 0;
        int indexOfExtremeQuote = 0;

        for (int i = 0; i < sentences.size(); i++) {
            Float polarity = sentences.get(i).getSentiment().getPolarity();
            if (polarity == 0) countNeutral++;
            else if (polarity > 0) countPositive++;
            else if (polarity < 0) countNegative++;

            if (polarity > Math.abs(extremeValue)) {
                indexOfExtremeQuote = i;
                extremeValue = polarity;
            }
        }

        System.out.println(
                new StringBuilder("")
                        .append("\nKanye's ").append(times).append(" quotes contain ")
                        .append(sentences.size()).append(" sentences with polarities:")
                        .append("\nPositive: ").append(countPositive)
                        .append("\nNegative: ").append(countNegative)
                        .append("\nNeutral: ").append(countNeutral)
                        .append("\n\nKanye's most extreme quote with polarity of ")
                        .append(sentences.get(indexOfExtremeQuote).getSentiment().getPolarity())
                        .append(" is\n\"").append(sentences.get(indexOfExtremeQuote).getSentence()).append("\"")
                        .toString()
        );
    }

    private void addQuoteAndCheckDuplicates(Quote quote) {
        if (!quoteList.contains(quote))
            quoteList.add(quote);
        else quoteController.fetchQuoteApi();
    }

    private boolean checkCompletion() {
        if (quoteList.size() == times) return true;
        else return false;
    }

    private void analyseQuotes() {
        String toAnalyse = convertQuotesListToString();

        if (toAnalyse.length() > 1) sentimController.analyseQuotes(toAnalyse);
        else sentimController.analyseQuotes("test1.");
    }

    private String convertQuotesListToString() {
        StringBuilder sb = new StringBuilder("");

        for (int i = 0; i < quoteList.size(); i++) {
            String quote = quoteList.get(i).getQuote();

            if (quote.length() >= 1) {
                char lastChar = quote.charAt(quote.length() - 1);
                if (lastChar == 46) quote += " "; //ASCII 46 is "."
                else if (lastChar == 33) quote += " "; //ASCII 33 is "!"
                else quote += ". "; //append dot if sentence not ended, to split Kanye sentences to help sentim-api analyser
            }
            sb.append(quote);
        }
        return sb.toString();
    }
}