package com.kite9.k9server.adl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

public class StreamHelp {

	public static void streamCopy(InputStream zis, OutputStream fos, boolean closeOs) throws IOException {
		try {
			byte[] buffer = new byte[2000];
			int amt = 0;
			while (amt != -1) {
				fos.write(buffer, 0, amt);
				amt = zis.read(buffer);
			}
		} finally {
			try {
				if (closeOs)
					fos.close();
			} catch (IOException e) {
			}
		}
	}

	public static void streamCopy(Reader zis, Writer fos, boolean closeOs) throws IOException {
		try {
			char[] buffer = new char[2000];
			int amt = 0;
			while (amt != -1) {
				fos.write(buffer, 0, amt);
				amt = zis.read(buffer);
			}
		} finally {
			try {
				if (closeOs)
					fos.close();
			} catch (IOException e) {
			}
		}
	}

}
