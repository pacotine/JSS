package file_manager;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import main.file_manager.ColonyReader.ColonyFileMethods;

public class ColonyReaderTest {

    @Test
    public void getValueOfType(){
        assertEquals(ColonyFileMethods.BAD_RELATIONS,ColonyFileMethods.valueOfType("deteste"));
        assertEquals(ColonyFileMethods.RESOURCES,ColonyFileMethods.valueOfType("ressource"));
        assertNotEquals(ColonyFileMethods.SETTLERS,ColonyFileMethods.valueOfType("deteste"));
        assertNotEquals(ColonyFileMethods.PREFERENCES,ColonyFileMethods.valueOfType("colon"));

    }

    @Test
    public void nextMethod(){
        assertTrue(ColonyFileMethods.next(ColonyFileMethods.RESOURCES, ColonyFileMethods.SETTLERS));
        assertTrue(ColonyFileMethods.next(ColonyFileMethods.BAD_RELATIONS, ColonyFileMethods.RESOURCES));
        assertTrue(ColonyFileMethods.next(ColonyFileMethods.PREFERENCES, ColonyFileMethods.RESOURCES));
        assertTrue(ColonyFileMethods.next(ColonyFileMethods.PREFERENCES, ColonyFileMethods.BAD_RELATIONS));

        assertFalse(ColonyFileMethods.next(ColonyFileMethods.SETTLERS, ColonyFileMethods.PREFERENCES));
        assertFalse(ColonyFileMethods.next(ColonyFileMethods.RESOURCES, ColonyFileMethods.PREFERENCES));
        assertFalse(ColonyFileMethods.next(ColonyFileMethods.BAD_RELATIONS, ColonyFileMethods.PREFERENCES));
        assertFalse(ColonyFileMethods.next(ColonyFileMethods.PREFERENCES, ColonyFileMethods.PREFERENCES));

    }



}
