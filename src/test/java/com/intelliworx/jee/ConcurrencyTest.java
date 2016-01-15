package com.intelliworx.jee;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.databene.contiperf.PerfTest;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ConcurrencyTest {

	@ClassRule
    public static TemporaryFolder concurrencyTestFolder = new TemporaryFolder();
	
	@Rule
	public ContiPerfRule perfRule = new ContiPerfRule();
	
	private static File outputFolder;
	
	private static Servlet servlet;

	private final HttpServletRequest request = mock(HttpServletRequest.class);
	private final HttpServletResponse response = mock(HttpServletResponse.class);
	private final static ServletConfig servletConfig = mock(ServletConfig.class);

	private static final Map<String, String[]> params = new HashMap<>();
	static{
		params.put("param1", new String[]{"a", "b", "c"});
		params.put("param2", new String[]{"d", "e", "f"});
		params.put("param3", new String[]{"g", "h", "i"});
	}

	private static final String GET_FILE_NAME = "get.txt";
	private static final String POST_FILE_NAME = "post.txt";
	
	@BeforeClass
	public static void setup() throws ServletException, IOException{
		//outputFolder = concurrencyTestFolder.newFolder("servlet-test");
		outputFolder  = new File("/tmp");
		servlet = new Servlet();

		when(servletConfig.getInitParameter("postFileLocation")).thenReturn(outputFolder.toPath().resolve(POST_FILE_NAME).toString());
		when(servletConfig.getInitParameter("getFileLocation")).thenReturn(outputFolder.toPath().resolve(GET_FILE_NAME).toString());

		servlet.init(servletConfig);
	}
	
	@Test
	@PerfTest(invocations = 1000, threads = 20)
	public void testMultipleRequestWriteToSameFile() throws ServletException, IOException {
		givenARequestIsMade("POST");
		givenRequiredRequestParamsSupplied();

		servlet.doPost(request, response);
	}
	
	private void givenARequestIsMade(final String requestType){
		when(request.getMethod()).thenReturn(requestType);
	}

	private void givenRequiredRequestParamsSupplied(){
		when(request.getParameterMap()).thenReturn(params);
	}
}
