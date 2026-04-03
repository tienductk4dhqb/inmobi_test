package cooccon.spring;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class DelegatingServletInputStream extends ServletInputStream {
	private final ByteArrayInputStream sourceStream;

	public DelegatingServletInputStream(ByteArrayInputStream sourceStream) {
		this.sourceStream = sourceStream;
	}

	@Override
	public int read() throws IOException {
		return sourceStream.read();
	}

	@Override
	public boolean isFinished() {
		return sourceStream.available() == 0;
	}

	@Override
	public boolean isReady() {
		return true;
	}

	@Override
	public void setReadListener(ReadListener readListener) {
		// không cần implement cho test
	}
}
