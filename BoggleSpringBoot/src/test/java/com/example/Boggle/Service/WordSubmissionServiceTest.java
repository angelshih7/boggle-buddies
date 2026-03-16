package com.example.Boggle.Service;

import com.example.Boggle.Model.Tables.*;
import com.example.Boggle.repository.DictionaryRepository;
import com.example.Boggle.repository.FoundWordRepository;
import com.example.Boggle.repository.GameRepository;
import com.example.Boggle.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WordSubmissionServiceTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DictionaryRepository dictionaryRepository;

    @Mock
    private FoundWordRepository foundWordRepository;

    @InjectMocks
    private WordSubmissionService wordSubmissionService;

    private User player1;
    private User player2;
    private Game game;
    private Board board;

    @BeforeEach
    void setUp() {
        player1 = new User("p1", "p1@test.com", "hash");
        player2 = new User("p2", "p2@test.com", "hash");
        setPrivateId(player1, 1);
        setPrivateId(player2, 2);

        board = new Board();
        board.setBoardId("board-1");
        board.setBoardString("CATS\nDOGE\nBIRD\nFISH");

        game = new Game();
        game.setId(100);
        game.setPlayer1(player1);
        game.setPlayer2(player2);
        game.setBoard(board);
        game.setStatus(GameStatus.IN_PROGRESS);

    }

    @Test
    void submitWordRejectsWordNotInDictionary() {
        when(gameRepository.findById(100)).thenReturn(Optional.of(game));
        when(userRepository.findById(1)).thenReturn(Optional.of(player1));
        when(dictionaryRepository.findByWord("COW")).thenReturn(Optional.empty());

        WordSubmissionService.Result result = wordSubmissionService.submitWord(100, 1, "cow");

        assertFalse(result.accepted);
        assertEquals(WordSubmissionService.SubmissionReason.NOT_IN_DICTIONARY, result.reason);
        assertEquals("COW", result.normalizedWord);
        verify(foundWordRepository, never()).save(any());
    }

    @Test
    void submitWordRejectsDuplicateWordForSamePlayerInSameGame() {
        Dictionary dictionary = mock(Dictionary.class);
        when(dictionary.getId()).thenReturn(77);

        when(gameRepository.findById(100)).thenReturn(Optional.of(game));
        when(userRepository.findById(1)).thenReturn(Optional.of(player1));
        when(dictionaryRepository.findByWord("CAT")).thenReturn(Optional.of(dictionary));
        when(foundWordRepository.existsByGame_IdAndPlayer_IdAndDictionaryWord_Id(100, 1, 77)).thenReturn(true);

        WordSubmissionService.Result result = wordSubmissionService.submitWord(100, 1, "cat");

        assertFalse(result.accepted);
        assertEquals(WordSubmissionService.SubmissionReason.DUPLICATE, result.reason);
        verify(foundWordRepository, never()).save(any());
    }

    @Test
    void submitWordAcceptsValidWordAndSavesFoundWord() {
        Dictionary dictionary = mock(Dictionary.class);
        when(dictionary.getId()).thenReturn(88);
        when(dictionary.getPointValue()).thenReturn(2);

        when(gameRepository.findById(100)).thenReturn(Optional.of(game));
        when(userRepository.findById(1)).thenReturn(Optional.of(player1));
        when(userRepository.getReferenceById(1)).thenReturn(player1);
        when(dictionaryRepository.findByWord("CAT")).thenReturn(Optional.of(dictionary));
        when(foundWordRepository.existsByGame_IdAndPlayer_IdAndDictionaryWord_Id(100, 1, 88)).thenReturn(false);

        WordSubmissionService.Result result = wordSubmissionService.submitWord(100, 1, "cat");

        assertTrue(result.accepted);
        assertEquals(WordSubmissionService.SubmissionReason.OK, result.reason);
        assertEquals("CAT", result.normalizedWord);
        assertEquals(2, result.points);

        ArgumentCaptor<FoundWord> captor = ArgumentCaptor.forClass(FoundWord.class);
        verify(foundWordRepository).save(captor.capture());
        FoundWord saved = captor.getValue();
        assertEquals(game, saved.getGame());
        assertEquals(player1, saved.getPlayer());
        assertEquals(dictionary, saved.getDictionaryWord());
    }

    @Test
    void submitWordReturnsDuplicateWhenSaveHitsUniqueConstraintRaceCondition() {
        Dictionary dictionary = mock(Dictionary.class);
        when(dictionary.getId()).thenReturn(99);

        when(gameRepository.findById(100)).thenReturn(Optional.of(game));
        when(userRepository.findById(1)).thenReturn(Optional.of(player1));
        when(userRepository.getReferenceById(1)).thenReturn(player1);
        when(dictionaryRepository.findByWord("DOG")).thenReturn(Optional.of(dictionary));
        when(foundWordRepository.existsByGame_IdAndPlayer_IdAndDictionaryWord_Id(100, 1, 99)).thenReturn(false);
        doThrow(new DataIntegrityViolationException("duplicate")).when(foundWordRepository).save(any());

        WordSubmissionService.Result result = wordSubmissionService.submitWord(100, 1, "dog");

        assertFalse(result.accepted);
        assertEquals(WordSubmissionService.SubmissionReason.DUPLICATE, result.reason);
    }

    @Test
    void submitWordRejectsWordThatIsNotOnBoard() {
        Dictionary dictionary = mock(Dictionary.class);

        when(gameRepository.findById(100)).thenReturn(Optional.of(game));
        when(userRepository.findById(1)).thenReturn(Optional.of(player1));
        when(dictionaryRepository.findByWord("COW")).thenReturn(Optional.of(dictionary));

        WordSubmissionService.Result result = wordSubmissionService.submitWord(100, 1, "cow");

        assertFalse(result.accepted);
        assertEquals(WordSubmissionService.SubmissionReason.NOT_ON_BOARD, result.reason);
    }

    @Test
    void submitWordRejectsPlayerNotInGame() {
        User outsider = new User("outsider", "out@test.com", "hash");
        setPrivateId(outsider, 99);

        when(gameRepository.findById(100)).thenReturn(Optional.of(game));
        when(userRepository.findById(99)).thenReturn(Optional.of(outsider));

        WordSubmissionService.Result result = wordSubmissionService.submitWord(100, 99, "cat");

        assertFalse(result.accepted);
        assertEquals(WordSubmissionService.SubmissionReason.PLAYER_NOT_IN_GAME, result.reason);
    }

    private static void setPrivateId(User user, Integer id) {
        try {
            var field = User.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(user, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}