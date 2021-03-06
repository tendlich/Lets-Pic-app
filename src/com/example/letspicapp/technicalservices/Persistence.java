package com.example.letspicapp.technicalservices;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

public class Persistence {
	
	private final static String TAG = "LetsPicAppPersistence";
	private String standardPath = this.getMediaStorageDir().getPath() + File.separator;
	
	
	public static String removeImageFileExtension(String string){
		return string.replaceAll("((\\.(?i)(jpg|png|gif|bmp))$)", "");
	}
	
	public String renameImage(Context context, String name, String newName) {
		name = removeImageFileExtension(name);
		newName = removeImageFileExtension(newName);
		String path = this.getMediaStorageDir().getPath() + File.separator;
		File picture = new File(path + name + ".jpg");
		File newPicture = new File(path + newName + ".jpg");
		if (picture.exists()) {
			boolean test = newPicture.exists();
			if (!test) {
				picture.renameTo(newPicture);
				this.refresh(context, newPicture);
				return newPicture.getPath();
			}
			return picture.getPath();			
		}
		return null;
	}
	
	public void refresh(Context context, File file){
		Log.d(TAG, Uri.fromFile(getMediaStorageDir()).toString());
		//refresh
		context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
				Uri.fromFile(file)));
	}
	
	public boolean deleteImage(Context context, String name){
		name = removeImageFileExtension(name);
		File image = new File(standardPath + name + ".jpg");
		if( image.exists() ){
			boolean deleted = image.delete();
			this.refresh(context,image);
			return deleted;
		}
		return false;
		
	}

	public boolean saveData(Context context, byte[] data, File file) {
		try {
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(data);
			fos.close();
			this.refresh(context,file);
			return true;
		} catch (FileNotFoundException e) {
			Log.d("LetsPicApp", "File not found: " + e.getMessage());
		} catch (IOException e) {
			Log.d("LetsPicApp", "Error accessing file: " + e.getMessage());
		}
		return false;
	}

	public byte[] getData(String path) {
		try {
			FileInputStream fileInputStream = new FileInputStream(path);
			ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
			int bufferSize = 1024;
			byte[] buffer = new byte[bufferSize];

			int len = 0;
			while ((len = fileInputStream.read(buffer)) != -1) {
				byteBuffer.write(buffer, 0, len);
			}
			fileInputStream.close();
			return byteBuffer.toByteArray();
		} catch (FileNotFoundException fnfe) {
			// TODO Auto-generated catch block
			System.err.println("Path: " + path);
			fnfe.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public Bitmap getImage(String path){
		byte[] data = this.getData(path);
		return BitmapFactory.decodeByteArray(data, 0, data.length);
	}

	public File getMediaStorageDir() {
		File mediaStorageDir = new File(
				Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"LetsPicApp");
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d(TAG, "failed to create directory");
				return null;
			}
		}
		return mediaStorageDir;
	}
	
	
	public String getRealPathFromURI(Uri contentUri, Activity a) {

        // can post image
        String [] proj={MediaStore.Images.Media.DATA};
        Cursor cursor = a.managedQuery( contentUri,
                        proj, // Which columns to return
                        null,       // WHERE clause; which rows to return (all rows)
                        null,       // WHERE clause selection arguments (none)
                        null); // Order-by clause (ascending by name)
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
	}

	
	//singleton
	private static Persistence instance = null;
	
	private Persistence(){}
	
	public static synchronized Persistence getInstance(){
		if (instance == null){
			instance = new Persistence();
		}
		return instance;
	}
	//singleton end
}
