package webapp.services;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import webapp.model.Questions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class SuggestionsLogger {

    @Value("${suggestions.logging.filepath}")
    private String logfilePath;

    private static Map<Integer, String> HEADER_POSITION_MAP = new HashMap<>();
    private static int TOTAL_COLS;


    public enum HEADER {
        QUESTION_ID("question_id"),
        ANSWER_TYPE("answer_type"),
        OUT_OF_SCOPE("out_of_scope"),
        AGGREGATION("aggregation"),
        DBPEDIA_ONLY("dbpedia_only"),
        HYBRID("hybrid"),
        SPARQL_SUGGESTED("sparql_suggested"),
        SPARQL_LOADED("sparql_loaded"),
        SPARQL_UNCHANGED("sparql_unchanged"),
        TIME_TAKEN_MILLIS("time_taken_millis"),
        TIMESTAMP("timestamp"),
        KW_DE("kw_de"),
        KW_EN("kw_en"),
        KW_ES("kw_es"),
        KW_FA("kw_fa"),
        KW_FR("kw_fr"),
        KW_IT("kw_it"),
        KW_NL("kw_nl"),
        KW_RO("kw_ro"),
        ;

        private final String name;

        HEADER(String name) {
            this.name = name;
        }

        public static HEADER getForLang(String lang) {
            for (HEADER header : HEADER.values()) {
                if (header.getName().startsWith("kw") && header.getName().endsWith(lang)) {
                    return header;
                }
            }

            return null;
        }

        public String getName() {
            return name;
        }
    }


    static {
        HEADER_POSITION_MAP.put(0, HEADER.QUESTION_ID.getName());
        HEADER_POSITION_MAP.put(1, HEADER.ANSWER_TYPE.getName());
        HEADER_POSITION_MAP.put(2, HEADER.OUT_OF_SCOPE.getName());
        HEADER_POSITION_MAP.put(3, HEADER.AGGREGATION.getName());
        HEADER_POSITION_MAP.put(4, HEADER.DBPEDIA_ONLY.getName());
        HEADER_POSITION_MAP.put(5, HEADER.HYBRID.getName());
        HEADER_POSITION_MAP.put(6, HEADER.SPARQL_SUGGESTED.getName());
        HEADER_POSITION_MAP.put(7, HEADER.SPARQL_LOADED.getName());
        HEADER_POSITION_MAP.put(8, HEADER.SPARQL_UNCHANGED.getName());
        HEADER_POSITION_MAP.put(9, HEADER.TIME_TAKEN_MILLIS.getName());
        HEADER_POSITION_MAP.put(10, HEADER.TIMESTAMP.getName());
        HEADER_POSITION_MAP.put(11, HEADER.KW_DE.getName());
        HEADER_POSITION_MAP.put(12, HEADER.KW_EN.getName());
        HEADER_POSITION_MAP.put(13, HEADER.KW_ES.getName());
        HEADER_POSITION_MAP.put(14, HEADER.KW_FA.getName());
        HEADER_POSITION_MAP.put(15, HEADER.KW_FR.getName());
        HEADER_POSITION_MAP.put(16, HEADER.KW_IT.getName());
        HEADER_POSITION_MAP.put(17, HEADER.KW_NL.getName());
        HEADER_POSITION_MAP.put(18, HEADER.KW_RO.getName());

        TOTAL_COLS = HEADER_POSITION_MAP.size();
    }


    public void log(Questions questions, Map<String, Object> loggingParameters) {


        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {

            String path = logfilePath + questions.getDatasetQuestion().getName() + ".csv";
            File file;
            FileWriter out = null;
            CSVPrinter csvFile = null;

            try {
                file = new File(path);
                out = new FileWriter(file, true);

                String[] headers = new String[TOTAL_COLS];
                for (int i = 0; i < HEADER_POSITION_MAP.size(); i++) {
                    headers[i] = HEADER_POSITION_MAP.get(i);
                }

                if (file.length() == 0) {
                    csvFile = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(headers));
                } else
                    csvFile = new CSVPrinter(out, CSVFormat.DEFAULT);

                List<Object> values = new ArrayList<>(TOTAL_COLS);
                for (String header : headers) {
                    values.add(loggingParameters.get(header));
                }

                csvFile.printRecord(values);

            } catch (IOException e) {
                System.out.println("Could not log the suggestions stats: " + e.getLocalizedMessage());
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (csvFile != null) {
                    try {
                        csvFile.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        executorService.shutdown();

    }


}
