package converter_video_format;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.primefaces.json.JSONException;

public class FileUploadViewTest {

	@Test
	public void getUrlStreamTest() throws IOException, JSONException {
	    String fileName = "sample.dv";
		FileUploadView file_upload_view = new FileUploadView();
		Assert.assertNotEquals(file_upload_view.getUrlStream(fileName), "");
	}
	
	@Test
	public void uploadToAmazonTest() throws IOException
	{
		String fileName = "alex.jpg";
		FileUploadView file_upload_view = new FileUploadView();
		file_upload_view.uploadToAmazon(fileName);
	}
	
	@Test
	public void saveUrlTest() throws IOException
	{
		FileUploadView file_upload_view = new FileUploadView();
		String url = "https://zencoder-temp-storage-us-east-1.s3.amazonaws.com/o/20140907/a923e26fdf208654c8ae1e9361b6588a/76d6e5823f1f70af9a90b6755fa4be49.mp4?AWSAccessKeyId=AKIAI456JQ76GBU7FECA&Signature=OBt57rZ%2BRL%2BgJDPtSTT66F%2FbIiM%3D&Expires=1410201200";
		Assert.assertNotEquals(file_upload_view.saveUrl(url), "");
	}
}
