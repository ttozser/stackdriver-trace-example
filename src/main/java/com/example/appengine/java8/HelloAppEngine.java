/*
 * Copyright 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.appengine.java8;

// [START example]

import com.google.appengine.api.utils.SystemProperty;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.opencensus.common.Scope;
import io.opencensus.exporter.trace.stackdriver.StackdriverTraceConfiguration;
import io.opencensus.exporter.trace.stackdriver.StackdriverTraceExporter;
import io.opencensus.trace.Tracer;
import io.opencensus.trace.Tracing;
import io.opencensus.trace.samplers.Samplers;

// With @WebServlet annotation the webapp/WEB-INF/web.xml is no longer required.
@WebServlet(name = "HelloAppEngine", value = "/hello")
public class HelloAppEngine extends HttpServlet {
	
	private static final Tracer tracer = Tracing.getTracer();
	
	static {
		try {
			System.out.println("Init StackdriverTraceExporter");
			StackdriverTraceExporter.createAndRegister(
					StackdriverTraceConfiguration.builder()
							.setProjectId("ttozser-da11e")
							.build());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		Properties properties = System.getProperties();
		
		response.setContentType("text/plain");
		response.getWriter().println("Hello App Engine - Standard using "
				+ SystemProperty.version.get() + " Java "
				+ properties.get("java.specification.version"));
	}
	
	public static String getInfo() {
		
		try (Scope ignored = tracer.spanBuilder("MyChildWorkSpan").setSampler(Samplers.alwaysSample()).startScopedSpan()) {
			tracer.getCurrentSpan().addAnnotation("annotation example");
			try {
				Thread.sleep(100);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			System.out.println("annotation created");
			return "Version: " + System.getProperty("java.version")
					+ " OS: " + System.getProperty("os.name")
					+ " User: " + System.getProperty("user.name")
					+ " Span: " + tracer.getCurrentSpan();
		}
	}
	
}
// [END example]
