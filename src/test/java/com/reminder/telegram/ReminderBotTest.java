package com.reminder.telegram;

import com.reminder.entity.User;
import com.reminder.repository.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReminderBotTest {

    @Mock
    private UserJpaRepository userRepository;

    @Captor
    private ArgumentCaptor<SendMessage> messageCaptor;

    private ReminderBot reminderBot;
    private final String botToken = "test-bot-token";
    private final String botUsername = "test-bot-username";

    @BeforeEach
    void setUp() {
        reminderBot = new ReminderBot(botToken, botUsername, userRepository);
    }

    @Test
    void getBotUsername_ShouldReturnCorrectUsername() {
        String username = reminderBot.getBotUsername();

        assertEquals(botUsername, username);
    }

    @Test
    void onUpdateReceived_WithStartCommand_ShouldSendStartMessage() {
        Update update = createUpdateWithMessage("/start", 12345L);
        ReminderBot spyBot = spy(reminderBot);

        spyBot.onUpdateReceived(update);

        verify(spyBot).sendMessage(eq(12345L), anyString());
    }

    @Test
    void onUpdateReceived_WithRegCommandAndValidEmail_ShouldBindChatId() {
        Long chatId = 12345L;
        String email = "test@example.com";
        Update update = createUpdateWithMessage("/reg " + email, chatId);

        User user = User.builder()
                .id(1L)
                .email(email)
                .build();

        when(userRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        reminderBot.onUpdateReceived(update);

        verify(userRepository).findByEmailIgnoreCase(email);
        verify(userRepository).save(argThat(savedUser ->
                savedUser.getTelegramChatId().equals(chatId)
        ));
    }

    @Test
    void onUpdateReceived_WithRegCommandAndInvalidEmail_ShouldSendErrorMessage() {
        Long chatId = 12345L;
        String invalidEmail = "invalid-email";
        Update update = createUpdateWithMessage("/reg " + invalidEmail, chatId);

        ReminderBot spyBot = spy(reminderBot);

        spyBot.onUpdateReceived(update);

        verify(spyBot).sendMessage(eq(chatId), eq(ReminderBot.INCORRECT_EMAIL));
        verify(userRepository, never()).findByEmailIgnoreCase(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void onUpdateReceived_WithRegCommandAndNonExistentEmail_ShouldSendNotExistMessage() {
        Long chatId = 12345L;
        String email = "nonexistent@example.com";
        Update update = createUpdateWithMessage("/reg " + email, chatId);

        when(userRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.empty());

        ReminderBot spyBot = spy(reminderBot);

        spyBot.onUpdateReceived(update);

        verify(spyBot).sendMessage(eq(chatId), eq(ReminderBot.EMAIL_NOT_EXIST));
        verify(userRepository).findByEmailIgnoreCase(email);
        verify(userRepository, never()).save(any());
    }

    @Test
    void onUpdateReceived_WithRegCommandWithoutEmail_ShouldDoNothing() {
        Long chatId = 12345L;
        Update update = createUpdateWithMessage("/reg", chatId);

        ReminderBot spyBot = spy(reminderBot);

        spyBot.onUpdateReceived(update);

        verify(spyBot, never()).sendMessage(anyLong(), anyString());
        verify(userRepository, never()).findByEmailIgnoreCase(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void sendMessage_ShouldExecuteSendMessage() throws TelegramApiException {
        Long chatId = 12345L;
        String text = "Test message";
        ReminderBot spyBot = spy(reminderBot);

        spyBot.sendMessage(chatId, text);

        verify(spyBot).execute(messageCaptor.capture());
        SendMessage capturedMessage = messageCaptor.getValue();

        assertEquals(chatId.toString(), capturedMessage.getChatId());
        assertEquals(text, capturedMessage.getText());
    }

    @Test
    void sendMessage_WhenTelegramApiException_ShouldLogError() throws TelegramApiException {
        Long chatId = 12345L;
        String text = "Test message";
        ReminderBot spyBot = spy(reminderBot);

        doThrow(new TelegramApiException("Test exception"))
                .when(spyBot).execute(any(SendMessage.class));

        assertDoesNotThrow(() -> spyBot.sendMessage(chatId, text));
    }

    private Update createUpdateWithMessage(String messageText, Long chatId) {
        Update update = new Update();
        Message message = new Message();
        Chat chat = new Chat();

        chat.setId(chatId);
        message.setChat(chat);
        message.setText(messageText);
        update.setMessage(message);

        return update;
    }
}