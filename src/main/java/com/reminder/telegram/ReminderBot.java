package com.reminder.telegram;

import com.reminder.entity.User;
import com.reminder.repository.UserJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;
import java.util.regex.Pattern;

@Slf4j
@Component
public class ReminderBot extends TelegramLongPollingBot {

    private final UserJpaRepository userRepository;

    @Value("${telegram.bot.username}")
    private String botUsername;

    public static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    public static final String START_MESSAGE = """
            Привет! Я бот для напоминаний ✅ \n
            Чтобы подключить напоминания через этот бот,
             введите команду /reg и через пробел email
             из Вашего профиля приложения reminder.
            Пример: '/reg email@example.com'
            """;

    public static final String INCORRECT_EMAIL = """
            ❌ Неверный формат email. Пожалуйста, введите корректный email.\n
            Пример: name@example.com
            """;

    public static final String EMAIL_NOT_EXIST = """
            Профиля с такой почтой не существует.
            Зарегистрируйтесь, чтобы добавить возможность напоминаний через этого бота.
            """;

    public ReminderBot(
            @Value("${telegram.bot.token}") String botToken,
            @Value("${telegram.bot.username}") String botUsername,
            UserJpaRepository userRepository) {
        super(botToken);
        this.botUsername = botUsername;
        this.userRepository = userRepository;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            if ("/start".equals(messageText)) {
                sendMessage(chatId, START_MESSAGE);
            }

            if (messageText.startsWith("/reg")) {
                String[] parts = messageText.split(" ");
                if (parts.length > 1) {
                    bindChatIdToUser(chatId, parts[1]);
                }
            }
        }
    }

    private void bindChatIdToUser(Long chatId, String email) {
        if (!isValidEmail(email)) {
            sendMessage(chatId, INCORRECT_EMAIL);
            return;
        }
        Optional<User> userOp = userRepository.findByEmail(email);
        if (userOp.isPresent()) {
            User user = userOp.get();
            log.info("Сохранение chatId: {} для пользователя {} c email: {}", chatId, user.getId(), email);
            userRepository.save(user.withTelegramChatId(chatId));
        } else {
            sendMessage(chatId, EMAIL_NOT_EXIST);
        }
    }

    /**
     * Метод для отправки сообщений (вызывается из других сервисов)
     */
    public void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);

        try {
            execute(message);
            log.info("Сообщение отправлено в чат {}", chatId);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения: {}", e.getMessage());
        }
    }

    private boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
}
