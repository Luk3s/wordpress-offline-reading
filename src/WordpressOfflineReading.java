import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class WordpressOfflineReading
{
	/**
	 * @param url I want to get the HTML of the web page at the given URL
	 * @return The HTML of the page as a String or null if it can't find the web page
	 * @throws IOException
	 */
	public static String getHtml(String url) throws IOException
	{		
        URL urlObject = new URL(url);
		
        // Some servers may refuse to accept connections not coming from real browsers
        // This is a workaround to fool the server in order to accept our request
        URLConnection urlConnection = urlObject.openConnection();
        urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2224.3 Safari/537.36");
       
        BufferedReader in;
		try{
			in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
		} catch (IOException e) {
			return null;
		}

		// Read until no more lines can be fetched
        String inputLine, html = "";
        while ((inputLine = in.readLine()) != null)
        	html += inputLine;
        in.close();

		return html;
	}

	
	/**
	 * The URL expected is similar to "http://www.blogname.com/author/authorname/". The web page will have a list of articles and at the 
	 * bottom at least a button linking to a previous page containing more articles and another link to the previous page and so on.
	 * This method, using the howToReachOneArticleUrlUsingCss, will get all the href attribute of the <a> tags of the first web page
	 * and all the ones reachable by clicking on the previous page button.
	 * @param aboutTheAuthorURL The web page you get when you click on an author's name on Wordpress.
	 * @param howToReachOneArticleUrlUsingCss The CSS you would write to access the <a> tag of an article in the aboutTheAuthorURL.
	 * @return A String containing all the URLs to the articles.
	 * @throws IOException
	 */
	public static String getUrlsToAllAuthorsArticles(String aboutTheAuthorURL, String howToReachOneArticleUrlUsingCss) throws IOException
	{
		String linksList = "";
		String pageHtml = null;
		int i = 1;
		
		// The first page is reachable by typing either "http://www.blogname.com/author/authorname/" or "http://www.blogname.com/author/authorname/page/1",
		// the following pages can be accessed by changing the number at the end of the latter URL. 
		while((pageHtml = getHtml(aboutTheAuthorURL + "page/" +  i)) != null)
		{
			Elements urls = Jsoup.parse(pageHtml).select(howToReachOneArticleUrlUsingCss);
			
			for(Element a : urls)
				linksList += a.attr("href") + System.lineSeparator();
			
			i++;
		}
		
		return linksList;
	}

	
	/**
	 * See the {@link WordpressOfflineReading#getUrlsToAllAuthorsArticles getUrlsToAllAuthorsArticles} method for more information.
	 * This method simply create a new file called list.txt in the current directory containing the string returned by getUrlsToAllAuthorsArticles
	 * @param aboutTheAuthorURL
	 * @param howToReachOneArticleUrlUsingCss
	 * @return
	 * @throws IOException
	 */
	public static File createUrlsListFile(String aboutTheAuthorURL, String howToReachOneArticleUrlUsingCss) throws IOException
	{
		// Get all the links to the articles
		String urlsListString = getUrlsToAllAuthorsArticles(aboutTheAuthorURL, howToReachOneArticleUrlUsingCss);
		
		// Write the links on the output file list.txt
		File urlsListFile = new File(System.getProperty("user.dir") + "/list.txt");
		urlsListFile.createNewFile();
        FileWriter fileWriter = new FileWriter(urlsListFile,false);
        BufferedWriter bufferFileWriter  = new BufferedWriter(fileWriter);
		fileWriter.append(urlsListString);
        bufferFileWriter.close();
        
        return urlsListFile;
	}

	
	public static void saveImage(String imageUrl, String destinationFile) throws IOException {
		File checkExistence = new File(destinationFile);
		if(checkExistence.exists())
			return;
		
		URL url = new URL(imageUrl);
        URLConnection urlConnection = url.openConnection();
        urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2224.3 Safari/537.36");	
		
		InputStream is = urlConnection.getInputStream();
		OutputStream os = new FileOutputStream(destinationFile);

		byte[] b = new byte[2048];
		int length;

		while ((length = is.read(b)) != -1) {
			os.write(b, 0, length);
		}

		is.close();
		os.close();
	}
	
	
	/**
	 * Creates a new folder called "articles" in the current directory, an index file to access all the articles and the articles.
	 * @param allTheUrls A file containing the URLs of the articles you want to save
	 * @throws IOException
	 */
	public static void saveAllTheArticles(File allTheUrls) throws IOException
	{
		// Create the folder that will contain the articles and images that could be in an article
		new File(System.getProperty("user.dir") + "\\articles").mkdir();
		new File(System.getProperty("user.dir") + "\\articles\\imgs").mkdir();
		
		//Create the index of the articles and the buffer used in the following loop
		File urlsIndex = new File(System.getProperty("user.dir") + "/index.html");
		String urlsIndexBuffer = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"><title>Index of the Articles</title></head><body><ul>";
		String url = null;
		
		// For reading the URLs efficiently
		BufferedReader urls = new BufferedReader(new FileReader(allTheUrls));
		
		// While there are lines (an URL) in the given file
		while ((url = urls.readLine()) != null)
		{
			// Useful to know which URL I am working on if something goes wrong
			System.out.println(url);
			
			Document htmlToParse = Jsoup.parse(getHtml(url));
			
			// Buffer for the content of the article.html
			String buffer = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"><title>" + htmlToParse.select("title").text() + "</title></head>";
			buffer += "<body><h1>" + htmlToParse.select(".entry-title").text() + "</h1>";
			buffer += "<article>";
			for(Element p : htmlToParse.select(".entry-content > p")) 
				buffer += "<p>" + p.toString() + "</p>";
			buffer += "</article></body></html>";
			
			// If there are links to images in the buffer save them on the local disk and replace the URL relative to the website
			// with another one relative to local hard drive path
			Document parseForImages = Jsoup.parse(buffer);
			Elements srcList = parseForImages.select("img");
			for(Element img : srcList)
			{
				String src = img.attr("src");
				String imagePathOnLocalDrive = System.getProperty("user.dir") + "\\articles\\imgs\\" + src.replaceAll(".*/", "");
				try {
					saveImage(src, imagePathOnLocalDrive);
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("Couldn't save the image at " + src);
				}
				buffer = buffer.replace(src, imagePathOnLocalDrive);
			}
			
			// Create a new HTML file based on the URL of the article with the information contained in the buffer
			urlsIndexBuffer += "<li><a href=\"articles/" + url.replaceAll(".*g/", "").replace("/","") + ".html\">" + htmlToParse.select(".entry-title").text() + "</a></li>";
			File webpage = new File(System.getProperty("user.dir") + "\\articles\\" + url.replaceAll(".*g/", "").replace("/","") + ".html");
			webpage.createNewFile();
	        FileWriter fileWriter = new FileWriter(webpage,false);
			fileWriter.append(buffer);
			fileWriter.close();
		}
	 
		urls.close();
		
		urlsIndexBuffer += "</ul></body></html>";
		FileWriter fileWriter = new FileWriter(urlsIndex,false);
		fileWriter.append(urlsIndexBuffer);
		fileWriter.close();
	}
	

}
