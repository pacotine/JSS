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

    private File correctFile, missingPointFile, invalidMethodNameFile, invalidMethodArgumentFile, invalidOrderFile,
            missingResourceFile, invalidPreferencesCountFile;
    private static final String CORRECT_FILE_CONTENT = """
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

    private static final String MISSING_POINT_FILE_CONTENT = """
            colon(Dark Vador).
            colon(Dartagnan).
            colon(Zoooooo).
            colon(Tartatin)
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

    private static final String INVALID_METHOD_NAME_CONTENT = """
            colon(Dark Vador).
            colon(Dartagnan).
            colon(Zoooooo).
            colon(Tartatin).
            ressource(Pomme).
            ressource(Big Mac).
            ressource(Tarte au citron).
            ressource(Orange).
            deteste(Dark Vador,Dartagnan).
            hello(Dark Vador,Zoooooo).
            deteste(Dark Vador,Tartatin).
            preferences(Dark Vador,Big Mac,Tarte au citron,Orange,Pomme).
            preferences(Dartagnan,Big Mac,Orange,Tarte au citron,Pomme).
            preferences(Tartatin,Pomme,Tarte au citron,Big Mac,Orange).
            preferences(Zoooooo,Pomme,Big Mac,Orange,Tarte au citron).
    """;

    private static final String INVALID_METHOD_ARGUMENT_CONTENT = """
            colon(Dark Vador).
            colon(Dartagnan).
            colon(Zoooooo).
            colon(Tartatin).
            ressource(Pomme, 1, 2, 4).
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

    private static final String INVALID_METHODS_ORDER_CONTENT = """
            ressource(Pomme).
            ressource(Big Mac).
            ressource(Tarte au citron).
            ressource(Orange).
            colon(Dark Vador).
            colon(Dartagnan).
            colon(Zoooooo).
            colon(Tartatin).
            deteste(Dark Vador,Dartagnan).
            deteste(Dark Vador,Zoooooo).
            deteste(Dark Vador,Tartatin).
            preferences(Dark Vador,Big Mac,Tarte au citron,Orange,Pomme).
            preferences(Dartagnan,Big Mac,Orange,Tarte au citron,Pomme).
            preferences(Tartatin,Pomme,Tarte au citron,Big Mac,Orange).
            preferences(Zoooooo,Pomme,Big Mac,Orange,Tarte au citron).
    """;

    private static final String MISSING_RESOURCE_CONTENT = """
            colon(Dark Vador).
            colon(Dartagnan).
            colon(Zoooooo).
            colon(Tartatin).
            ressource(Pomme).
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

    private static final String INVALID_PREFERENCES_CONTENT = """
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
            preferences(Dark Vador,Big Mac,Tarte au citron,Orange,Pomme,More).
            preferences(Dartagnan,Big Mac,Orange,Tarte au citron,Pomme).
            preferences(Tartatin,Pomme,Tarte au citron,Big Mac,Orange).
            preferences(Zoooooo,Pomme,Big Mac,Orange,Tarte au citron).
    """;

    @BeforeEach
    public void setup() throws IOException {
        writeColonyFile(correctFile = File.createTempFile("simple_colony_file", ""), CORRECT_FILE_CONTENT);
        writeColonyFile(missingPointFile = File.createTempFile("missing_point_file", ""), MISSING_POINT_FILE_CONTENT);
        writeColonyFile(invalidMethodNameFile = File.createTempFile("invalid_method_name_file", ""), INVALID_METHOD_NAME_CONTENT);
        writeColonyFile(invalidMethodArgumentFile = File.createTempFile("invalid_method_argument_file", ""), INVALID_METHOD_ARGUMENT_CONTENT);
        writeColonyFile(invalidOrderFile = File.createTempFile("invalid_order_file", ""), INVALID_METHODS_ORDER_CONTENT);
        writeColonyFile(missingResourceFile = File.createTempFile("missing_resource_file", ""), MISSING_RESOURCE_CONTENT);
        writeColonyFile(invalidPreferencesCountFile = File.createTempFile("invalid_preferences_file", ""), INVALID_PREFERENCES_CONTENT);
    }

    private void writeColonyFile(File file, String content) throws IOException {
        FileWriter fileWriter = new FileWriter(file);
        for(char c : content.toCharArray()) {
            fileWriter.write(c);
        }
        fileWriter.close();
    }

    @Test
    @DisplayName("Reader retrieve a simulation")
    public void readFileWithCorrectlyFormattedFile() throws IOException {
        try(ColonyReader cr = new ColonyReader(correctFile)) {
            assertDoesNotThrow(() -> {
                Simulation simulation = cr.initSimulation();
                assertNotNull(simulation);
            });
        }
    }

    @Test
    @DisplayName("Reader gives the expected simulation")
    public void readFileGivesCorrectSimulation() throws IOException {
        final int expectedN = 4;
        try(ColonyReader cr = new ColonyReader(correctFile)) {
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

    @Test
    @DisplayName("Reader detects missing point")
    public void readFileButMissingPoint() {
        ColonyFileFormatException.InvalidMethodException ie = assertThrowsExactly(
                ColonyFileFormatException.InvalidMethodException.class, () -> initReader(missingPointFile));
        assertEquals("At line 4 : Unknown method colon(Tartatin)\n" +
                "        ressource(Pomme)", ie.getMessage());
    }

    @Test
    @DisplayName("Reader detects unknown method name")
    public void readFileButMethodDoesNotExist() {
        ColonyFileFormatException.InvalidMethodException ie = assertThrowsExactly(
                ColonyFileFormatException.InvalidMethodException.class, () -> initReader(invalidMethodNameFile));
        assertEquals("At line 10 : Unknown method hello(Dark Vador,Zoooooo)", ie.getMessage());
    }

    @Test
    @DisplayName("Reader detects invalid argument format")
    public void readFileButArgumentsAreInvalid() {
        ColonyFileFormatException.InvalidArgumentException ie = assertThrowsExactly(
                ColonyFileFormatException.InvalidArgumentException.class, () -> initReader(invalidMethodArgumentFile));
        assertEquals("At line 5 : Invalid argument 'ressource(Pomme, 1, 2, 4)' is incorrect for ressource()", ie.getMessage());
    }

    @Test
    @DisplayName("Reader detects invalid methods order")
    public void readFileButMethodsOrderIsIncorrect() {
        ColonyFileFormatException ie = assertThrowsExactly(
                ColonyFileFormatException.class, () -> initReader(invalidOrderFile));
        assertEquals("Settlers should be defined first", ie.getMessage());
    }

    @Test
    @DisplayName("Reader detects invalid settlers' preferences count")
    public void readFileButPreferencesArgumentsCountIsIncorrect() {
        ColonyFileFormatException ie = assertThrowsExactly(
                ColonyFileFormatException.class, () -> initReader(invalidPreferencesCountFile));
        assertEquals("At line 12 : Extra 1 argument(s) for preferences(Dark Vador,Big Mac,Tarte au citron,Orange,Pomme,More)", ie.getMessage());
    }

    @Test
    @DisplayName("Reader detects if settlers count is not equals to resources count")
    public void readFileButResourcesCountNotEqualsToSettlersCount() {
        ColonyFileFormatException ie = assertThrowsExactly(
                ColonyFileFormatException.class, () -> initReader(missingResourceFile));
        assertEquals("Number of resources must equal number of settlers, but there are 4 distinct names for 3 distinct resources", ie.getMessage());
    }

    private void initReader(File file) throws IOException {
        try(ColonyReader cr = new ColonyReader(file)) {
            cr.initSimulation();
        }
    }
}
