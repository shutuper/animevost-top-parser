package parser;

import dto.AnimeInfoDto;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Data
@Slf4j
@FieldDefaults(level = PRIVATE)
public class AnimevostParser {

	final String defaultAnimevostUrl = "https://animevost.org/";
	final List<AnimeInfoDto> allAnimeInfo = new ArrayList<>();

	String animevostUrl;
	Integer firstPage;
	Integer lastPage;


	public List<AnimeInfoDto> parseAnimevost() {
		getAllAnimeInfo().clear();
		initVariables();
		long start = System.currentTimeMillis();

		ExecutorService executorService = Executors.newWorkStealingPool();
		for (int i = getFirstPage(); i <= getLastPage(); i++) {
			final int page = i;
			executorService.submit(() -> {
				try {
					parsePage(page);
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		}

		awaitTerminationAfterShutdown(executorService);

		System.out.println("Parsed in: " + ((System.currentTimeMillis() - start) / 1000) + "s");

		return Collections.unmodifiableList(getAllAnimeInfo());
	}


	private void awaitTerminationAfterShutdown(ExecutorService executorService) {
		executorService.shutdown();
		try {
			if (!executorService.awaitTermination(5, TimeUnit.MINUTES)) {
				executorService.shutdownNow();
			}
		} catch (InterruptedException ex) {
			executorService.shutdownNow();
			Thread.currentThread().interrupt();
		}
	}


	private void parsePage(int page) throws IOException {
		Document doc = Jsoup.connect(getAnimevostUrl() + "page/" + page + "/").get();

		Elements newsHeadlines = doc.select("div.shortstory");

		for (Element headline : newsHeadlines) {
			try {
				AnimeInfoDto animeInfoDto = parseToAnimeInfoDto(headline);
				getAllAnimeInfo().add(animeInfoDto);
			} catch (Exception ignored) {
			}
		}

	}


	private String getAnimevostUrl() {
		String url = animevostUrl.endsWith("/") ? animevostUrl : animevostUrl + "/";
		url = url.startsWith("https://") ? url : "https://" + url;
		return url;
	}


	private AnimeInfoDto parseToAnimeInfoDto(Element headline) {
		Elements shortstoryHead = headline.select("div.shortstoryHead h2 a");
		String link = shortstoryHead.attr("href");
		String title = Objects.requireNonNull(shortstoryHead.first()).text();

		Elements shortstoryContentP = headline.select("div.shortstoryContent p");
		Integer year = Integer.parseInt(getElement(shortstoryContentP, 0));
		Set<String> genres = Arrays.stream(
						getElement(shortstoryContentP, 1).trim().toLowerCase().split(",\\s+")
				)
				.collect(Collectors.toSet());
		String series = getElement(shortstoryContentP, 3);

		Elements staticInfoRightSmotr = headline.select("span.staticInfoRightSmotr");
		Long views = Long.parseLong(Objects.requireNonNull(staticInfoRightSmotr.first()).text());

		Elements shortstoryContentRating = headline
				.select("div.shortstoryContent .current-rating");
		Integer rating = Integer.parseInt(Objects.requireNonNull(shortstoryContentRating.first()).text());

		Elements shortstoryContentVoices = headline
				.select("div.shortstoryContent span [id^=vote-num-id]");
		Long voices = Long.parseLong(Objects.requireNonNull(shortstoryContentVoices.first()).text());

		return AnimeInfoDto.builder()
				.genres(genres)
				.voices(voices)
				.rating(rating)
				.series(series)
				.title(title)
				.views(views)
				.link(link)
				.year(year)
				.build();
	}


	private String getElement(Elements shortstoryContent, int index) {
		return shortstoryContent.get(index).text()
				.replaceAll(".*:", "")
				.trim();
	}


	private void initVariables() {
		Scanner scanner = new Scanner(System.in);

		System.out.println("Enter number of first page:");
		setFirstPage(scanner.nextInt());

		System.out.println("Enter number of last page:");
		setLastPage(scanner.nextInt());

		System.out.println("Enter animevost mirror link or press ENTER to skip (default one will be used):");
		scanner.nextLine();
		String link = scanner.nextLine();
		setAnimevostUrl(StringUtils.isEmpty(link) ? defaultAnimevostUrl : link);

		System.out.println("Wait a moment! :)");
	}

}
