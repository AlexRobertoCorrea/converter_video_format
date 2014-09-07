package converter_video_format;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.primefaces.json.JSONException;

public class FileUploadViewTest {

	@Test
	public void getUrlStreamTest() throws IOException, JSONException {
		FileUploadView file_upload_view = new FileUploadView();
		Assert.assertNotEquals(file_upload_view.getUrlStream(), "");
	}

}
