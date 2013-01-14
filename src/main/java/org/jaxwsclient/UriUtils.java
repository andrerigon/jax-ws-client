package org.jaxwsclient;

public class UriUtils {
	public static String removeDoubleSlashes(String path) {
		return path.replaceAll("/+", "/");
	}
}
