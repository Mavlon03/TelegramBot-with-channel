package uz.pdp.bot;

import com.pengrad.telegrambot.model.Contact;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TgUser {
    private Long id;
    private String name;
    private String phone;
    private TgState tgState = TgState.START;
}
