wordpress-offline-reading
=========================

If you write articles for a blog hosted on (created using) Wordpress, and you need to save all your articles for offline reading,
you can use this java program to do it. You can easily do the same if you have rights to install plugins, which it is not the case
in some situations. You need to know CSS and have Java installed on your machine to run the program.
Be sure to check Main.java to see an example of how to use it. 
The web site is a blog where I used to write about my Erasmus in Scotland (http://erasmusinstirling.altervista.org/) 
and the author is the only one on the blog, i.e. "erasmusinstirling".

The lines you have to change, in order to have the program working properly on the website
you are interested in, are the ones with the comment "//ToChange" at the end.

<pre>
The web page "about the author" you are intrested in
	String aboutTheAuthorURL = "http://erasmusinstirling.altervista.org/author/erasmusinstirling/";
		
The CSS you would write to reach the <a> tag of an article preview in the page above
	String howToReachOneArticleUrlUsingCss = ".entry-title a";							
		
The CSS you would write to find the title in an article page (such as http://erasmusinstirling.altervista.org/burns-supper-and-ceilidh/)
	htmlToParse.select(".entry-title").text()	
		
The CSS you would write to select the paragraphs containing the article
	for(Element p : htmlToParse.select(".entry-content > p"))
</pre>

Let me know if you find it useful or find errors in it!
