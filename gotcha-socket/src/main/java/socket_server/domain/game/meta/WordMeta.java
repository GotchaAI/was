package socket_server.domain.game.meta;

import lombok.Builder;
import lombok.Data;
import socket_server.domain.game.model.Word;

@Builder
@Data
public class WordMeta {
    private int wordIndex;
    private String word;
    private String drawerUuid;
    private String imageURL;
    private boolean submitted;

    public static Word toWord(WordMeta wordMeta){
       return Word.builder()
               .wordIndex(wordMeta.getWordIndex())
               .word(wordMeta.getWord())
               .drawerUuid(wordMeta.getDrawerUuid())
               .submitted(wordMeta.isSubmitted())
               .imageURL(wordMeta.getImageURL()).build();
    }

}
