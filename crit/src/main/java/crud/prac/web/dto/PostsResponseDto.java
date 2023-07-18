package crud.prac.web.dto;

import crud.prac.domain.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostsResponseDto {

    private Long id;
    private String title;
    private String content;
    private String author;

    public PostsResponseDto(Long id, String title, String content, Member author) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = String.valueOf(author);
    }

}
