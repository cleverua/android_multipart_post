package com.cleverua.android.post;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import android.util.Log;

//http://www.codenet.ru/webmast/php/HTTP-POST.php
public class MultipartPost {

	private final String TAG = "MultipartPost";
	private List<PostParameter> params;
	private static final String CRLF = "\r\n";
	private static final String BOUNDARY = "AaB03x";

	public MultipartPost(List<PostParameter> params) {
		this.params = params;
	}
	
	public String send(String urlString) throws Exception {
	    
	    HttpURLConnection conn = null;
	    DataOutputStream dos = null;
	    String response = null;
	    InputStream is = null;
	    
		try {
			conn = (HttpURLConnection) new URL(urlString).openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + BOUNDARY);
			dos = new DataOutputStream(conn.getOutputStream());
			
			for(PostParameter param : params) {
				Log.d(TAG, "Processning param: " + param.getParamName());
				if(param.getValue() == null) {
				    param.setValue("");
				}
				if(param.getValue().getClass() == File.class) {
					postFileParameter(dos, param.getParamName(), (File) param.getValue(), param.getContentType());
				} 
				else {
					postStringParameter(dos, param.getParamName(), param.getValue().toString());
				}
			}

			dos.writeBytes(closeBoundary());
			dos.flush();

			is = conn.getInputStream();
			int ch;

			StringBuffer b = new StringBuffer();
			while ((ch = is.read()) != -1) {
				b.append((char) ch);
			}
			response = b.toString();
		} 
		finally {
		    if(dos != null) try { dos.close(); } catch(IOException ioe) { /* that's it */ }
		    if(is  != null) try { is .close(); } catch(IOException ioe) { /* that's it */ }
		}
		
		return response;
	}

	private void postStringParameter(DataOutputStream dos, String paramName, String paramValue) throws IOException {
		dos.writeBytes(boundary() + CRLF);
		dos.writeBytes("Content-Disposition: form-data; name=\"" + paramName + "\"" + CRLF + CRLF);
		dos.writeBytes(paramValue + CRLF);
	}

	private void postFileParameter(DataOutputStream dos, String paramName, File file, String contentType) throws IOException {
		dos.writeBytes(boundary() + CRLF);
		dos.writeBytes("Content-Disposition: form-data; name=\"" + paramName + "\"; filename=\"" + file.getName() + "\"" + CRLF);
		dos.writeBytes("Content-Type: "+ contentType + CRLF);
		dos.writeBytes("Content-Transfer-Encoding: binary" + CRLF);
		dos.writeBytes(CRLF);

		FileInputStream fileInputStream = new FileInputStream(file);
		int bytesAvailable = fileInputStream.available();
		int maxBufferSize = 1024;
		int bufferSize = Math.min(bytesAvailable, maxBufferSize);
		byte[] buffer = new byte[bufferSize];

		int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

		while (bytesRead > 0) {
			dos.write(buffer, 0, bufferSize);
			bytesAvailable = fileInputStream.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = null;
			buffer = new byte[bufferSize];
			bytesRead = fileInputStream.read(buffer, 0, bufferSize);
		}
		dos.writeBytes(CRLF);
		dos.flush();
		fileInputStream.close();
		System.gc();
	}


	private String closeBoundary() {
		return boundary() + "--" + CRLF;
	}

	private String boundary() {
		return "--" + BOUNDARY;
	}

}
