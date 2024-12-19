package main.file_manager;

import static org.junit.jupiter.api.Assertions.*;

import main.model.Settler;
import main.simulation.Simulation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import main.file_manager.ColonyReader.ColonyFileMethods;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ColonyReaderTest {

    @Nested
    class ColonyFileMethodsTest {
        @Test
        @DisplayName("Next methods are correct")
        public void nextMethodIsCorrect(){
            assertTrue(ColonyFileMethods.next(ColonyFileMethods.RESOURCES, ColonyFileMethods.SETTLERS));
            assertTrue(ColonyFileMethods.next(ColonyFileMethods.BAD_RELATIONS, ColonyFileMethods.RESOURCES));
            assertTrue(ColonyFileMethods.next(ColonyFileMethods.PREFERENCES, ColonyFileMethods.RESOURCES));
            assertTrue(ColonyFileMethods.next(ColonyFileMethods.PREFERENCES, ColonyFileMethods.BAD_RELATIONS));
        }

        @Test
        @DisplayName("Next methods are incorrect")
        public void nextMethodIsIncorrect(){
            assertFalse(ColonyFileMethods.next(ColonyFileMethods.SETTLERS, ColonyFileMethods.PREFERENCES));
            assertFalse(ColonyFileMethods.next(ColonyFileMethods.RESOURCES, ColonyFileMethods.PREFERENCES));
            assertFalse(ColonyFileMethods.next(ColonyFileMethods.BAD_RELATIONS, ColonyFileMethods.PREFERENCES));
            assertFalse(ColonyFileMethods.next(ColonyFileMethods.PREFERENCES, ColonyFileMethods.PREFERENCES));
        }
    }

    private File tempFile;
    private static final String FILE_CONTENT = """
            colon(Dark Vador).
            colon(Dartagnan).
            colon(Zoooooo).
            colon(Tartatin).
            ressource(Pomme).
            ressource(Big Mac).
            ressource(Tarte au citron).
            ressource(Orange).
            deteste(Dark Vador,Dartagnan).
            deteste(Dark Vador,Zoooooo).
            deteste(Dark Vador,Tartatin).
            preferences(Dark Vador,Big Mac,Tarte au citron,Orange,Pomme).
            preferences(Dartagnan,Big Mac,Orange,Tarte au citron,Pomme).
            preferences(Tartatin,Pomme,Tarte au citron,Big Mac,Orange).
            preferences(Zoooooo,Pomme,Big Mac,Orange,Tarte au citron).
    """;

    @BeforeEach
    public void setup() throws IOException {
        tempFile = File.createTempFile("simple_colony_file", "");
        FileWriter fileWriter = new FileWriter(tempFile);
        for(char c : FILE_CONTENT.toCharArray()) {
            fileWriter.write(c);
        }
        fileWriter.close();
    }

    @Test
    @DisplayName("Reader retrieve a simulation")
    public void readFileWithCorrectlyFormattedFile() throws IOException {
        try(ColonyReader cr = new ColonyReader(tempFile)) {
            assertDoesNotThrow(() -> {
                Simulation simulation = cr.initSimulation();
                assertNotNull(simulation);
            });
        }
    }

    @Test
    @DisplayName("The simulation read gives the expected simulation")
    public void readFileGivesCorrectSimulation() throws IOException {
        final int expectedN = 4;
        try(ColonyReader cr = new ColonyReader(tempFile)) {
            assertDoesNotThrow(() -> {
                Simulation simulation = cr.initSimulation();
                List<Settler> settlers = simulation.getSettlers();

                assertNotNull(settlers);
                assertEquals(expectedN, settlers.size());
                for(int i = 0; i<expectedN; i++) {
                    assertEquals(expectedN, settlers.get(i).getPreferences().length);
                }
            });
        }
    }

}
