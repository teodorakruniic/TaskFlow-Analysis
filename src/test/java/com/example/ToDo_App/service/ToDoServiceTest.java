package com.example.ToDo_App.service;

import com.example.ToDo_App.model.ToDo;
import com.example.ToDo_App.repo.IToDoRepo;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ToDoServiceTest {

    @Mock
    private IToDoRepo toDoRepo;

    @InjectMocks
    private ToDoService toDoService;

    @BeforeAll
    void setupAll() {
        System.out.println("Inicializacija testov za ToDoService...");
    }

    @AfterAll
    void tearDownAll() {
        System.out.println("Vsi testi za ToDoService so končani.");
    }

    @Test
    void saveOrUpdate_Success() {
        ToDo toSave = new ToDo();
        toSave.setTitle("Test Task");

        ToDo saved = new ToDo();
        saved.setId(1L);
        saved.setTitle("Test Task");

        when(toDoRepo.save(any(ToDo.class))).thenReturn(saved);
        when(toDoRepo.findById(1L)).thenReturn(Optional.of(saved));

        boolean result = toDoService.saveOrUpdateToDoItem(toSave);

        assertTrue(result);
        verify(toDoRepo, times(1)).save(any(ToDo.class));
        verify(toDoRepo, atMost(1)).findById(1L);
        verifyNoMoreInteractions(toDoRepo);
    }

    @Test
    void saveOrUpdate_Fails_WhenIdMissingAfterSave() {
        ToDo toSave = new ToDo();
        toSave.setTitle("No ID after save");

        ToDo savedNoId = new ToDo();
        savedNoId.setTitle("No ID after save");

        when(toDoRepo.save(any(ToDo.class))).thenReturn(savedNoId);
        when(toDoRepo.findById(any())).thenReturn(Optional.empty());

        boolean result = toDoService.saveOrUpdateToDoItem(toSave);

        assertFalse(result);

        verify(toDoRepo).save(any(ToDo.class));
        verify(toDoRepo).findById(null);
        verifyNoMoreInteractions(toDoRepo);
    }


    @Test
    void saveOrUpdate_Fails_WhenRepoThrows() {
        ToDo toSave = new ToDo();
        toSave.setTitle("Boom");

        when(toDoRepo.save(any(ToDo.class))).thenThrow(new RuntimeException("DB down"));

        assertThrows(RuntimeException.class, () -> toDoService.saveOrUpdateToDoItem(toSave));

        verify(toDoRepo).save(any(ToDo.class));
        verifyNoMoreInteractions(toDoRepo);
    }

    @Test
    @Tag("Critical")
    void getById_Success() {
        ToDo todo = new ToDo();
        todo.setId(1L);
        todo.setTitle("Test Task");

        when(toDoRepo.findById(1L)).thenReturn(Optional.of(todo));

        ToDo result = toDoService.getToDoItemById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Task", result.getTitle());
        verify(toDoRepo, times(1)).findById(1L);
        verifyNoMoreInteractions(toDoRepo);
    }

    @Test
    @DisplayName("getById – nema rezultata")
    void getById_NotFound() {
        when(toDoRepo.findById(99L)).thenReturn(Optional.empty());

        ToDo result = toDoService.getToDoItemById(99L);

        assertNull(result);
        verify(toDoRepo, times(1)).findById(99L);
        verifyNoMoreInteractions(toDoRepo);
    }

    @Test
    @DisplayName("getById – nevalidan ID (≤ 0) i dalje ide ka repou, vraća null")
    void getById_InvalidId() {
        when(toDoRepo.findById(0L)).thenReturn(Optional.empty());
        when(toDoRepo.findById(-5L)).thenReturn(Optional.empty());

        ToDo r0 = toDoService.getToDoItemById(0L);
        ToDo rn = toDoService.getToDoItemById(-5L);

        assertNull(r0);
        assertNull(rn);

        verify(toDoRepo).findById(0L);
        verify(toDoRepo).findById(-5L);
        verifyNoMoreInteractions(toDoRepo);
    }

    @Test
    void getByStatus_Success() {
        ToDo todo1 = new ToDo(); todo1.setId(1L); todo1.setTitle("Task 1"); todo1.setStatus("Done");
        ToDo todo2 = new ToDo(); todo2.setId(2L); todo2.setTitle("Task 2"); todo2.setStatus("Done");

        when(toDoRepo.findByStatus("Done")).thenReturn(Arrays.asList(todo1, todo2));

        List<ToDo> result = toDoService.getToDoItemsByStatus("Done");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(t -> "Done".equals(t.getStatus())));
        verify(toDoRepo, times(1)).findByStatus("Done");
        verifyNoMoreInteractions(toDoRepo);
    }

    @Test
    void getByStatus_Empty() {
        when(toDoRepo.findByStatus("NonExisting")).thenReturn(Collections.emptyList());

        List<ToDo> result = toDoService.getToDoItemsByStatus("NonExisting");

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(toDoRepo, times(1)).findByStatus("NonExisting");
        verifyNoMoreInteractions(toDoRepo);
    }

    @Test
    @DisplayName("getByStatus – null status baca NPE (trenutna implementacija)")
    void getByStatus_Null_Throws() {
        assertThrows(NullPointerException.class, () -> toDoService.getToDoItemsByStatus(null));
    }

    @Test
    @DisplayName("getByStatus – prazan/blank status prolazi do repoa i vraća prazno")
    void getByStatus_BlankOrSpaces_ReturnsEmpty() {
        when(toDoRepo.findByStatus("")).thenReturn(Collections.emptyList());
        when(toDoRepo.findByStatus("   ")).thenReturn(Collections.emptyList());

        List<ToDo> r1 = toDoService.getToDoItemsByStatus("");
        List<ToDo> r2 = toDoService.getToDoItemsByStatus("   ");

        assertTrue(r1.isEmpty());
        assertTrue(r2.isEmpty());

        verify(toDoRepo).findByStatus("");
        verify(toDoRepo).findByStatus("   ");
        verifyNoMoreInteractions(toDoRepo);
    }


    @Test
    @DisplayName("getByStatus – repo baca, servis propagira izuzetak (trenutno ponašanje)")
    void getByStatus_RepoThrows_Propagates() {
        when(toDoRepo.findByStatus("In Progress"))
                .thenThrow(new RuntimeException("Query failed"));

        assertThrows(RuntimeException.class,
                () -> toDoService.getToDoItemsByStatus("In Progress"));

        verify(toDoRepo).findByStatus("In Progress");
        verifyNoMoreInteractions(toDoRepo);
    }

}
