import java.io.File;
import java.io.IOException;

public class Main
{

	public static void main(String[] args) throws IOException
	{
		// Input data
		String aboutTheAuthorURL = "http://erasmusinstirling.altervista.org/author/erasmusinstirling/";		// ToChange
		String howToReachOneArticleUrlUsingCss = ".entry-title a";											// ToChange

        WordpressOfflineReading.createUrlsListFile(aboutTheAuthorURL, howToReachOneArticleUrlUsingCss);
		WordpressOfflineReading.saveAllTheArticles(new File(System.getProperty("user.dir") + "/list.txt"));

	}

}
