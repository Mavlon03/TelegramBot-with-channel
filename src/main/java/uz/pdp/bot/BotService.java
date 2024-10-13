package uz.pdp.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.ChatPermissions;
import com.pengrad.telegrambot.model.Contact;
import com.pengrad.telegrambot.model.InlineQuery;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.AnswerInlineQuery;
import com.pengrad.telegrambot.request.RestrictChatMember;
import com.pengrad.telegrambot.request.SendMessage;

import java.util.ArrayList;
import java.util.List;


public class BotService {
    public static TelegramBot telegramBot = new TelegramBot("7212666605:AAHorLh1_S2NlLXHINbLWGM9OIAHljOMwM4");

    public static void blockUser(Long chatId, Long id, int blockTimeMinut) {
        SendMessage sendMessage = new SendMessage(chatId,
                """
                        Hurmatli foydalanuvchi siz reklama va havola tarqatganingiz uchun 
                        %d daqiqaga blocklandingiz
                        """.formatted(blockTimeMinut));
        telegramBot.execute(sendMessage);
        int duretionSekund = blockTimeMinut * 60;
        int untilDate = (int) (System.currentTimeMillis()/1000L +duretionSekund);
        RestrictChatMember restrictChatMember = new RestrictChatMember(
                chatId,
                id,
                new ChatPermissions()
                        .canSendMessages(false)
                        .canSendOtherMessages(false)
                        .canSendAudios(false)

        ).untilDate(untilDate);
        System.out.println(blockTimeMinut+ " minutga bloklandi");
        telegramBot.execute(restrictChatMember);
    }


    public static TgUser saveAndNewMemeber(Long chatId, String userName) {
        TgUser tgUser = new TgUser();
        tgUser.setId(chatId);
        tgUser.setName(userName);
        DB.USERS.add(tgUser);
        System.out.println("Yangi foydalanuvchi saqlandi: " + userName);
        return tgUser;
    }
    public static TgUser saveAndNewMemeber2(Long chatId, String userName) {
        TgUser tgUser = new TgUser();
        tgUser.setId(chatId);
        tgUser.setName(userName);
        DB.USERS.add(tgUser);
        System.out.println("Yangi foydalanuvchi saqlandi: " + userName);
        return tgUser;
    }


    public static void acceptUserAndSendWelcome(TgUser tgUser) {
        SendMessage sendMessage = new SendMessage(tgUser.getId(),
                """
                        Assalomu aleykum xurmatli %s botimizga xush kelibsiz.
                        """.formatted(tgUser.getName()));
        telegramBot.execute(sendMessage);
        SendMessage sendMessage1 = new SendMessage(tgUser.getId(),
                "Iltimos kontaktingizni yuboring."
        );
        sendMessage1.replyMarkup(generateButton());
        telegramBot.execute(sendMessage1);
        tgUser.setTgState(TgState.SEND_CONTACT);
    }
    private static ReplyKeyboardMarkup generateButton() {
        KeyboardButton keyboardButton = new KeyboardButton("Kontaktni ulashish");
        keyboardButton.requestContact(true);
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(keyboardButton)
                .resizeKeyboard(true);
        return replyKeyboardMarkup;

    }

    public static void acceptContact(TgUser tgUser, Contact contact) {
        String phone = PhoneNumber.fix(contact.phoneNumber());
        tgUser.setPhone(phone);
        SendMessage sendMessage = new SendMessage(tgUser.getId(),
                "Rahmat! Endi iltimos, quyidagi havola orqali kanalimga a'zo bo'ling:\n" +
                        "https://t.me/+m8tJGOdfw25lZjRi");
        telegramBot.execute(sendMessage);
    }

    public static void inlineQuery(InlineQuery inlineQuery, String query) {
        List<InlineQueryResultArticle> results = new ArrayList<>();

        for (TgUser user : DB.USERS) {
            if (user.getName().toLowerCase().contains(query.toLowerCase())) {
                InputTextMessageContent messageContent = new InputTextMessageContent("Foydalanuvchi: " + user.getName());
                InlineQueryResultArticle result = new InlineQueryResultArticle(user.getId().toString(), user.getName(), messageContent);
                results.add(result);
            }
        }

        AnswerInlineQuery answerInlineQuery = new AnswerInlineQuery(inlineQuery.id(), results.toArray(new InlineQueryResultArticle[0]));
        answerInlineQuery.cacheTime(0);
        telegramBot.execute(answerInlineQuery);
    }
}
