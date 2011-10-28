package br.com.vraptor.client;

public class UriUtils {
	public static String removeDoubleSlashes(String path) {
		return path.replaceAll("/+", "/");
	}
}
