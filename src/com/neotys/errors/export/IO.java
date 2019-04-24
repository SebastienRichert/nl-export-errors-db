package com.neotys.errors.export;

import java.io.DataInput;
import java.io.IOException;

public class IO {

	/**
	 * Reads a String from a {@link DataInput}.
	 * It is recommended to use {@link DataInput#readUTF()} instead because
	 * it's standard and faster.
	 *
	 * This method must be called only if the {@link String} has been
	 * written using {@link IO#readString(DataInput)}.
	 *
	 * @param in input to read from
	 * @return String read from input, or {@code null} if none
	 * @throws IOException if any read error occurs
	 */
	public static String readString(final DataInput in) throws IOException {
		final int length = in.readInt();
		if (length < 0) {
			return null;
		}

		final char[] chars = new char[length];
		for (int i = 0 ; i < length ; i++) {
			chars[i] = in.readChar();
		}

		return String.valueOf(chars);
	}

}
