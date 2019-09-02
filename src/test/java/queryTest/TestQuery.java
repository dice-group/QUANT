package queryTest;

import org.junit.Test;
import suggestion.Suggestions;
import suggestion.query.QuerySuggestions;


public class TestQuery {
    @Test
    public void ReadQaldTest() {
        Suggestions Suggestions = new Suggestions();
        QuerySuggestions q= Suggestions.generateQuerySuggestions("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" +
                "select distinct ?Concept where {?Concept <http://www.w3.org/1999/02/22-rdf-syntax-ns#typefa> <http://xmlns.com/foaf/0.1/Person>} LIMIT 100","http://dbpedia.org/sparql","http://dbpedia.org/resource/Abbie_Hoffman");
        /*Suggestions.Suggestions("PREFIX yago: <http://dbpedia.org/class/yago/> " +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
                "PREFIX dbpedia2: <http://dbpedia.org/property/> " +
                "SELECT ?uri ?string WHERE {?uri rdf:type yago:StatesOfTheUnitedStates . " +
                "?uri dbpedia2:densityrank ?density " +
                "OPTIONAL {?uri rdfs:label ?string. FILTER (lang(?string) = 'en') }} ORDER BY ASC(?density) LIMIT 1" , "http://dbpedia.org/sparql");*/
        System.out.println(q.getCorrectedQuery());
    }
    @Test
    public void ReadQaldTest2() {
        Suggestions Suggestions = new Suggestions();
        QuerySuggestions q = Suggestions.generateQuerySuggestions("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" +
                "PREFIX onto: <http://dbpedia.org/ontology/>\n" +
                "SELECT DISTINCT ?uri\n" +
                "WHERE{?film rdf:type onto:TelevisionShow .\n" +
                "?film rdfs:lab 'Charmed'@en .\n" +
                "?film onto:starring ?actors .\n" +
                "?actors foaf:homepages ?uri .\n" +
                "}","http://dbpedia.org/sparql","http://www.alyssa.com");
        System.out.println(q.getCorrectedQuery());
        /*Suggestions.Suggestions("PREFIX yago: <http://dbpedia.org/class/yago/> " +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
                "PREFIX dbpedia2: <http://dbpedia.org/property/> " +
                "SELECT ?uri ?string WHERE {?uri rdf:type yago:StatesOfTheUnitedStates . " +
                "?uri dbpedia2:densityrank ?density " +
                "OPTIONAL {?uri rdfs:label ?string. FILTER (lang(?string) = 'en') }} ORDER BY ASC(?density) LIMIT 1" , "http://dbpedia.org/sparql");*/
    }
    @Test
    public void ReadQaldTest3() {
        Suggestions Suggestions = new Suggestions();
        QuerySuggestions q = Suggestions.generateQuerySuggestions("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" +
                "PREFIX onto: <http://dbpedia.org/ontology/>\n" +
                "SELECT ?u WHERE\n" +
                "{?u rdf:type onto:CapitalsInEurope}","http://dbpedia.org/sparql","http://www.alyssa.com");
        System.out.println(q.getCorrectedQuery());
        /*Suggestions.Suggestions("PREFIX yago: <http://dbpedia.org/class/yago/> " +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
                "PREFIX dbpedia2: <http://dbpedia.org/property/> " +
                "SELECT ?uri ?string WHERE {?uri rdf:type yago:StatesOfTheUnitedStates . " +
                "?uri dbpedia2:densityrank ?density " +
                "OPTIONAL {?uri rdfs:label ?string. FILTER (lang(?string) = 'en') }} ORDER BY ASC(?density) LIMIT 1" , "http://dbpedia.org/sparql");*/
    }
    @Test
    public void ReadQaldTest4() {
        Suggestions Suggestions = new Suggestions();
        QuerySuggestions q = Suggestions.generateQuerySuggestions("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" +
                "PREFIX dbo: <http://dbpedia.org/ontology/>\n" +
                "select distinct ?o where {<http://dbpedia.org/resource/Durolevo> dbo:abstract ?o} LIMIT 500","http://dbpedia.org/sparql","http://www.alyssa.com");
        System.out.println(q.getCorrectedQuery());
        /*Suggestions.Suggestions("PREFIX yago: <http://dbpedia.org/class/yago/> " +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
                "PREFIX dbpedia2: <http://dbpedia.org/property/> " +
                "SELECT ?uri ?string WHERE {?uri rdf:type yago:StatesOfTheUnitedStates . " +
                "?uri dbpedia2:densityrank ?density " +
                "OPTIONAL {?uri rdfs:label ?string. FILTER (lang(?string) = 'en') }} ORDER BY ASC(?density) LIMIT 1" , "http://dbpedia.org/sparql");*/
    }

}
