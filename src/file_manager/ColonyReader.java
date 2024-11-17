package file_manager;

import env.Simulation;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ColonyReader extends FileReader {
    private List<String> lines;
    private static final String SETTLERS = "colon";
    private static final String RESOURCES = "ressource";
    private static final String PREFERENCES = "preferences";
    private static final String BAD_RELATIONS = "deteste";

    public ColonyReader(File file) throws IOException {
        super(file);
        readFile();
    }

    private String readLine() throws IOException {
        int c = read();
        if(c == -1) return null; //EOF

        StringBuilder sb = new StringBuilder();
        sb.append((char)c);
        char ch = (char)read();
        while(ch != '.') {
            if(ch != '\n') sb.append(ch);
            ch = (char)read();
        }

        return sb.toString();
    }

    private void readFile() throws IOException {
        initRead();
    }

    private void retrieveData() {
    }

    private String method(String line) {
        return line.split("\\(")[0];
    }

    private String arg(String line) {
        return line.split("\\(")[1].replace("\\)", "");
    }

    private String[] args(String line) {
        return arg(line).split(",");
    }

    private void initRead() throws IOException {
        this.lines = new ArrayList<>();
        String s;
        while((s = readLine()) != null) {
            lines.add(s);
        }
    }

    public Simulation initSimulation() {
        Simulation simulation = new Simulation(...);

        return simulation;
    }

}
