package net.neferett.socialmedia.Instances;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.neferett.redisapi.Annotations.Redis;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Redis(db = 2, folder = true)
public class TelegramChat {

    @NonNull
    private int messageId;

    @NonNull
    private String chatId;

}
