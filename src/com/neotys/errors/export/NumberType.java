package com.neotys.errors.export;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.DataInput;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.math.BigDecimal;
import java.math.BigInteger;

import com.google.common.base.Predicate;

enum NumberType implements Predicate<Number> {
	BYTE(0) {
		@Override
		public boolean apply(final Number n) {
			return n instanceof Byte;
		}

		@Override
		protected Number read(final DataInput in) throws IOException {
			return Byte.valueOf(in.readByte());
		}
	},
	DOUBLE(1) {
		@Override
		public boolean apply(final Number n) {
			return n instanceof Double;
		}

		@Override
		protected Number read(final DataInput in) throws IOException {
			return Double.valueOf(in.readDouble());
		}
	},
	INTEGER(2) {
		@Override
		public boolean apply(final Number n) {
			return n instanceof Integer;
		}

		@Override
		protected Number read(final DataInput in) throws IOException {
			return Integer.valueOf(in.readInt());
		}
	},
	LONG(3) {
		@Override
		public boolean apply(final Number n) {
			return n instanceof Long;
		}

		@Override
		protected Number read(final DataInput in) throws IOException {
			return Long.valueOf(in.readLong());
		}
	},
	FLOAT(4) {
		@Override
		public boolean apply(final Number n) {
			return n instanceof Float;
		}

		@Override
		protected Number read(final DataInput in) throws IOException {
			return Float.valueOf(in.readFloat());
		}
	},
	SHORT(5) {
		@Override
		public boolean apply(final Number n) {
			return n instanceof Short;
		}

		@Override
		protected Number read(final DataInput in) throws IOException {
			return Short.valueOf(in.readShort());
		}

	},
	BIGDECIMAL(6) {
		@Override
		public boolean apply(final Number n) {
			return n instanceof BigDecimal;
		}

		@Override
		protected Number read(final DataInput in) throws IOException {
			return new BigDecimal(IO.readString(in));
		}
	},
	BIGINTEGER(7) {
		@Override
		public boolean apply(final Number n) {
			return n instanceof BigInteger;
		}

		@Override
		protected Number read(final DataInput in) throws IOException {
			return new BigInteger(IO.readString(in));
		}

	};

	private final byte id;

	private NumberType(final int id) {
		this.id = (byte) id;
	}

	/**
	 * Returns the id of this {@link NumberType}.
	 * @return id
	 */
	protected byte getId() {
		return id;
	}

	protected abstract Number read(final DataInput in) throws IOException;

	/**
	 * Reads a number for the input stream.
	 *
	 * @param in
	 * @return
	 * @throws IOException
	 */
	static Number readNumber(final DataInput in) throws IOException {
		final byte id = checkNotNull(in).readByte();
		for (final NumberType type : values()) {
			if (type.getId() == id) {
				return type.read(in);
			}
		}
		throw new StreamCorruptedException();
	}

}