package topPrinter;

import dto.AnimeInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE)
public class AnimeTopPrinter {

	final List<AnimeInfoDto> parsedAnimeInfoDtoList;


	public void printTop() {
		Scanner scanner = new Scanner(System.in);
		int i = 0;

		while (true) {
			System.out.println("\n\nEnter all genres you love separated by comma or just press ENTER to skip it: ");
			i = scannerIterations(scanner, i);
			String genres = scanner.nextLine();

			System.out.println("Enter number of titles you want to see in top: ");
			Integer numberOfTitles = scanner.nextInt();

			printTopByGenres(genres, numberOfTitles);
		}
	}


	private void printTopByGenres(String genres, Integer numberOfTitles) {
		Set<String> parsedGenres = parseGenresFromString(genres);
		printTopHeader(parsedGenres, numberOfTitles);

		List<AnimeInfoDto> animeTop = getAnimeTop(numberOfTitles, parsedGenres);
		print(animeTop);
	}


	private void print(List<AnimeInfoDto> animeTop) {
		for (int i = 0; i < animeTop.size(); i++) {
			System.out.println("====================  " + (1 + i) + "  ====================");
			System.out.println(animeTop.get(i));
		}
	}


	private List<AnimeInfoDto> getAnimeTop(Integer numberOfTitles, Set<String> parsedGenres) {
		return parsedAnimeInfoDtoList.stream()
				.filter(dto -> dto.getGenres().stream().anyMatch(parsedGenres::contains))
				.sorted((Comparator.comparing(AnimeInfoDto::getRating)
						.thenComparing(AnimeInfoDto::getViews)
						.thenComparing(AnimeInfoDto::getVoices))
						.reversed())
				.limit(numberOfTitles)
				.toList();
	}


	private void printTopHeader(Set<String> parsedGenres, int numberOfTitles) {
		log.info(
				"\n================================  Animevost TOP - {}  ======================================\n",
				numberOfTitles
		);

		if (parsedGenres.size() > 0) {
			System.out.println(">>>Top by genres: " + String.join(", ", parsedGenres) + "\n\n");
		}
	}


	private Set<String> parseGenresFromString(String genres) {
		if (StringUtils.isEmpty(genres)) {
			return getAllGenres();
		}

		Set<String> parsedGenres = Arrays.stream(genres.trim().toLowerCase().split("\\s*,\\s+"))
				.collect(Collectors.toSet());

		return parsedGenres.stream()
				.filter(genre -> getAllGenres().contains(genre))
				.collect(Collectors.toSet());
	}


	private Set<String> getAllGenres() {
		String genres = """ 
				Фантастика Фэнтези Школа Этти Боевые искусства Война Драма Детектив История
				Комедия Меха Мистика Махо-сёдзё Музыкальный Повседневность Приключения Пародия Романтика
				СёнЭН Сёдзё Спорт Сказка Сёдзё-ай Сёнэн-ай Самурай Триллер Ужасы
				""";

		return Arrays.stream(genres.trim().toLowerCase().split("\\s+"))
				.collect(Collectors.toUnmodifiableSet());
	}


	private static int scannerIterations(Scanner scanner, int i) {
		if (i != 0) {
			scanner.nextLine();
		} else {
			i++;
		}

		return i;
	}

}
