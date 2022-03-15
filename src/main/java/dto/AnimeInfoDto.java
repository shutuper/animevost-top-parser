package dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@Data
@Builder
@FieldDefaults(level = PRIVATE)
public class AnimeInfoDto {

	Set<String> genres;
	Integer rating;
	String series;
	String title;
	Integer year;
	Long voices;
	String link;
	Long views;


	@Override
	public String toString() {
		return "Anime: " + title + "\n" +
				"genres: " + String.join(", ", genres) + "\n" +
				"views: " + views + "\n" +
				"rating: " + (rating / 10) + ", voices: " + voices + "\n" +
				"series: " + series + ", year: " + year + "\n" +
				"link: " + link;
	}
}
