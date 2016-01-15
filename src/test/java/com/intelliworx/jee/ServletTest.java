package com.intelliworx.jee;

import static com.intelliworx.jee.SerializationHelper.convertToString;
import static com.intelliworx.jee.SerializationHelper.getChecksum;
import static java.lang.System.lineSeparator;
import static javax.servlet.http.HttpServletResponse.SC_CREATED;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.intelliworx.jee.Servlet;

public class ServletTest {

	@Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
	private File outputFolder;

	private Servlet servlet;

	private final HttpServletRequest request = mock(HttpServletRequest.class);
	private final HttpServletResponse response = mock(HttpServletResponse.class);
	private final ServletConfig servletConfig = mock(ServletConfig.class);

	private static final Map<String, String[]> params = new HashMap<>();
	static{
		params.put("param1", new String[]{"a", "b", "c"});
		params.put("param2", new String[]{"d", "e", "f"});
		params.put("param3", new String[]{"g", "h", "i"});
	}

	private static final String GET_FILE_NAME = "get.txt";
	private static final String POST_FILE_NAME = "post.txt";

	@Before
	public void setup() throws ServletException, IOException{
		outputFolder = testFolder.newFolder("servlet-test");

		servlet = new Servlet();

		when(servletConfig.getInitParameter("postFileLocation")).thenReturn(outputFolder.toPath().resolve(POST_FILE_NAME).toString());
		when(servletConfig.getInitParameter("getFileLocation")).thenReturn(outputFolder.toPath().resolve(GET_FILE_NAME).toString());

		servlet.init(servletConfig);
	}

	@Test(expected=ServletException.class)
	public void testMissingInitParams() throws ServletException, IOException{
		givenServletNotInitialisedCorrectly();
		servlet.doGet(request, response);
	}

	@Test
	public void testGet() throws IOException, ServletException {

		givenARequestIsMade("GET");
		givenRequiredRequestParamsSupplied();

		servlet.doGet(request, response);

		thenAFileIsProducedWithTheRequestParams(GET_FILE_NAME);
		verify(response).setStatus(SC_CREATED);
	}

	@Test
	public void testPost() throws IOException, ServletException {

		givenARequestIsMade("POST");
		givenRequiredRequestParamsSupplied();

		servlet.doPost(request, response);

		thenAFileIsProducedWithTheRequestParams(POST_FILE_NAME);
		verify(response).setStatus(SC_CREATED);
	}

	@Test
	@Ignore(value = "Need to work out how to do this")
	public void testMultipleThreadsWritingToSameFile(){
		//TODO need to work out how to do this
	}

	@Test
	@Ignore
	public void testRequestWithNoParamsSupplied(){
		//TODO - check should the checksum still be written if no params supplied in request?
	}

	private void thenAFileIsProducedWithTheRequestParams(final String filename) throws FileNotFoundException, IOException {
		final File file = new File(outputFolder.getPath() + File.separator + filename);
		//It is enough to check the checksum in the file is as expected rather than checking the entire content of the file
		String actualChecksum = null;
		try (BufferedReader reader = new BufferedReader(new FileReader(file));) {
			actualChecksum = reader.readLine(); //first line in the file is always the checksum
		}

		final String mapAsString = convertToString(params, lineSeparator());
		assertEquals(getChecksum(mapAsString), actualChecksum);
	}

	private void givenARequestIsMade(final String requestType){
		when(request.getMethod()).thenReturn(requestType);
	}

	private void givenRequiredRequestParamsSupplied(){
		when(request.getParameterMap()).thenReturn(params);
	}

	private void givenServletNotInitialisedCorrectly() throws ServletException{
		when(servletConfig.getInitParameter("postFileLocation")).thenReturn(null);
		when(servletConfig.getInitParameter("getFileLocation")).thenReturn(null);
		servlet.init(servletConfig);
	}
}
