package com.intelliworx.jee;

import static com.intelliworx.jee.SerializationHelper.convertToString;
import static com.intelliworx.jee.SerializationHelper.getChecksum;
import static java.lang.System.lineSeparator;
import static javax.servlet.http.HttpServletResponse.SC_CREATED;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gurps Bassi
 *
 */
public class Servlet extends HttpServlet {

	private static final long serialVersionUID = -1145016484611467204L;
	private static final Logger LOGGER = LoggerFactory.getLogger(Servlet.class);

	private String getFileLocation;
	private String postFileLocation;

	private final Object fileLock = new Object();

	public Servlet() {
		super();
	}

	@Override
	public void init() throws ServletException {
		// must be valid file system paths
		//create the directory structures on startup since they're configured for the servlet instance
		this.postFileLocation = getServletConfig().getInitParameter("postFileLocation");
		this.getFileLocation = getServletConfig().getInitParameter("getFileLocation");

		if(postFileLocation == null || getFileLocation == null){
			throw new ServletException("both postFileLocation and getFileLocation need to be set");
		}

		//eagerly create the directories on initialisation
		new File(postFileLocation).getParentFile().mkdirs();
		new File(getFileLocation).getParentFile().mkdirs();
	}

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		response.setStatus(SC_CREATED); // May not be the right code to return but its performing a side effect.
		LOGGER.warn("'GET' is supposed to be a idempotent operation but the original servlet had the functionality in there to perform a side effect so I've left it in");
		writeToFile(request);
	}

	@Override
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		response.setStatus(SC_CREATED);
		writeToFile(request);
	}

	private String getTargetFilePath(final HttpServletRequest request) throws ServletException {
		if (request.getMethod().equalsIgnoreCase("GET")) {
			return getFileLocation;
		}
		if (request.getMethod().equalsIgnoreCase("POST")) {
			return postFileLocation;
		}
		throw new ServletException("target file path could not be determined from request");
	}

	private void writeToFile(final HttpServletRequest request) throws ServletException, IOException {

		//TODO move this method into a separate collaborator since it is doing too much. The servlet should act as a thin facade to the business tier.
		final String targetFilePath = getTargetFilePath(request);
		final Map<String, String[]> requestParams = request.getParameterMap();

		LOGGER.info("writing to file path {}", targetFilePath);

		//synchronized (fileLock) {
			// The servlet will be single instance. Multiple requests can try writing to the same file so we need to synchronize.
			try (Writer w = new BufferedWriter(new FileWriter(targetFilePath, true))) {
				final String mapAsString = convertToString(requestParams, lineSeparator());
				final String checksum = getChecksum(mapAsString);
				final String result = new StringBuilder().append(checksum)
														 .append(lineSeparator())
														 .append(mapAsString)
														 .append(lineSeparator())
														 .toString();
				w.write(result);
			}
		//}
	}
}