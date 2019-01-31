package keywordTest;

import org.junit.Test;
import suggestion.keywords.KeyWordSuggestor;

import java.util.List;

public class TestKeywords {
    @Test
    public void testKeywords1()
    {
        KeyWordSuggestor k = new KeyWordSuggestor();
        List<String> keyWords = k.suggestKeywords("Liste alle Musicals mit Musik von Elton John auf.","de");
        System.out.println(keyWords);
    }
    @Test
    public void testKeywords2()
    {
        KeyWordSuggestor k = new KeyWordSuggestor();
        List<String> keyWords = k.suggestKeywords("List all the musicals with music by Elton John.","en");
        System.out.println(keyWords);
    }
}
