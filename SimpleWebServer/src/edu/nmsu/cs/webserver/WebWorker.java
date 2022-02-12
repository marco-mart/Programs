package edu.nmsu.cs.webserver;

/**
 * Web worker: an object of this class executes in its own new thread to receive and respond to a
 * single HTTP request. After the constructor the object executes on its "run" method, and leaves
 * when it is done.
 *
 * One WebWorker object is only responsible for one client connection. This code uses Java threads
 * to parallelize the handling of clients: each WebWorker runs in its own thread. This means that
 * you can essentially just think about what is happening on one client at a time, ignoring the fact
 * that the entirety of the webserver execution might be handling other clients, too.
 *
 * This WebWorker class (i.e., an object of this class) is where all the client interaction is done.
 * The "run()" method is the beginning -- think of it as the "main()" for a client interaction. It
 * does three things in a row, invoking three methods in this class: it reads the incoming HTTP
 * request; it writes out an HTTP header to begin its response, and then it writes out some HTML
 * content for the response content. HTTP requests and responses are just lines of text (in a very
 * particular format).
 * 
 * @author Jon Cook, Ph.D.
 *
 **/

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Writer;
import java.net.Socket;
import java.text.DateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.TimeZone;
import java.util.Scanner;

public class WebWorker implements Runnable
{

	private Socket socket;
	private boolean defaultPage = false;

	/**
	 * Constructor: must have a valid open socket
	 **/
	public WebWorker(Socket s)
	{
		socket = s;
	}

	/**
	 * Worker thread starting point. Each worker handles just one HTTP request and then returns, which
	 * destroys the thread. This method assumes that whoever created the worker created it with a
	 * valid open socket object.
	 **/
	public void run()
	{
		System.err.println("Handling connection...");
		try
		{
			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();
			File request = readHTTPRequest(is);
			writeHTTPHeader(os, "text/html", request);
			writeContent(os, request);
			os.flush();
			socket.close();
		}
		catch (Exception e)
		{
			System.err.println("Output error: " + e);
		}
		System.err.println("Done handling connection.");
		return;
	}

	/**
	 * Read the HTTP request header.
	 **/
	private File readHTTPRequest(InputStream is)
	{
		String line;
		// will hold all request lines
		String request = null;
		BufferedReader r = new BufferedReader(new InputStreamReader(is));
		while (true)
		{
			try
			{
				while (!r.ready())
					Thread.sleep(1);
				line = r.readLine();
				System.err.println("Request line: (" + line + ")");
				if (line.length() == 0)
					break;
				if (request == null) {
					request = line;
				}
			}
			catch (Exception e)
			{
				System.err.println("Request error: " + e);
				break;
			}
		}
		
		String[] requestParsed = request.split(" ");
		File requestFile = null;

		// check request
		if (requestParsed[0].equals("GET")) {
			// first char is a '/'
			requestFile = new File(requestParsed[1].substring(1));
			// check if file exists and is not a directory
			if (requestFile.exists() && !requestFile.isDirectory()) {
				return requestFile;
			}
			// check if default page
			else if (requestParsed[1].equals("/")) {
				defaultPage = true;
			}
		}
		// file does not exist 
		return null;
	}

	/**
	 * Write the HTTP header lines to the client network connection.
	 * 
	 * @param os
	 *          is the OutputStream object to write to
	 * @param contentType
	 *          is the string MIME content type (e.g. "text/html")
	 **/
	private void writeHTTPHeader(OutputStream os, String contentType, File request) throws Exception
	{
		Date d = new Date();
		DateFormat df = DateFormat.getDateTimeInstance();
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		// check request/default page
		if (request != null || defaultPage) {
			os.write("HTTP/1.1 200 OK\n".getBytes());
		}
		// file not found
		else {
			os.write("HTTP/1.1 404 Not Found\n".getBytes());
		}
		os.write("Date: ".getBytes());
		os.write((df.format(d)).getBytes());
		os.write("\n".getBytes());
		os.write("Server: Marco's very own server\n".getBytes());
		// os.write("Last-Modified: Wed, 08 Jan 2003 23:11:55 GMT\n".getBytes());
		// os.write("Content-Length: 438\n".getBytes());
		os.write("Connection: close\n".getBytes());
		os.write("Content-Type: ".getBytes());
		os.write(contentType.getBytes());
		os.write("\n\n".getBytes()); // HTTP header ends with 2 newlines
		return;
	}

	/**
	 * Write the data content to the client network connection. This MUST be done after the HTTP
	 * header has been written out.
	 * 
	 * @param os
	 *          is the OutputStream object to write to
	 **/
	private void writeContent(OutputStream os, File requestFile) throws Exception
	{
		os.write("<html><head></head><body>\n".getBytes());
		if (requestFile == null && !defaultPage) {
			// file not found
			os.write("<h3>404 Not Found</h3>\n".getBytes());
		}
		else if (defaultPage) {
			// default text
			os.write("<h3>My web server works!</h3>\n".getBytes());
		}
		else {
			// write file's contents to output stream
			Scanner scan = new Scanner(requestFile);
			
			while (scan.hasNextLine()) {
				
				String line = scan.nextLine();
				
				if (line.contains("<cs371date>")) {
					LocalDate date = LocalDate.now();
					line = line.replaceAll("<cs371date>", date.toString());
				}
				if (line.contains("<cs371server>")) {
					line = line.replaceAll("<cs371server>", "Marco's Server");
				}
				
				os.write(line.getBytes());
			}
			
			scan.close();
		}
		os.write("</body></html>\n".getBytes());
	}

} // end class
