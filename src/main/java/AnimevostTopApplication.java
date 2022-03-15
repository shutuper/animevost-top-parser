import lombok.extern.slf4j.Slf4j;
import parser.AnimevostParser;
import topPrinter.AnimeTopPrinter;

@Slf4j
public class AnimevostTopApplication {

	public static void main(String[] args) {
		new AnimeTopPrinter(new AnimevostParser().parseAnimevost()).printTop();
	}

}
