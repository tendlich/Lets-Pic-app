package ReminderOverview;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.letspicapp.R;
import com.example.letspicapp.model.Alarm;

public class ReminderListAdapter extends ArrayAdapter<Alarm>{
	private Context mContext;
	private int layoutResourceId;
	private Alarm alarmItems[];
	private List<Alarm> alarmList;
	public int currentposition;

	public ReminderListAdapter(Context mContext, int layoutResourceId, List<Alarm> data) {

		super(mContext, layoutResourceId, data);

		this.layoutResourceId = layoutResourceId;
		this.mContext = mContext;
//		this.alarmItems = data;
		this.alarmList = data;
	}

	public void loadBitmap(String path, ImageView imageView) {
		if (cancelPotentialWork(path, imageView)) {
	        final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
	        final AsyncDrawable asyncDrawable =
	                new AsyncDrawable(task);
	        imageView.setImageDrawable(asyncDrawable);
	    task.execute(path);
	}
	}
		
		public static boolean cancelPotentialWork(String data, ImageView imageView) {
		    final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

		    if (bitmapWorkerTask != null) {
		        final String bitmapData = bitmapWorkerTask.path;
		        // If bitmapData is not yet set or it differs from the new data
		        if (bitmapData == null || !bitmapData.equals(data)) {
		            // Cancel previous task
		            bitmapWorkerTask.cancel(true);
		        } else {
		            // The same work is already in progress
		            return false;
		        }
		    }
		    // No task associated with the ImageView, or an existing task was cancelled
		    return true;
		}
		
		private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
		    if (imageView != null) {
		        Drawable drawable = imageView.getDrawable();
		        if (drawable instanceof AsyncDrawable) {
		        	AsyncDrawable downloadedDrawable = (AsyncDrawable)drawable;
		            return downloadedDrawable.getBitmapWorkerTask();
		        }
		    }
		    return null;
		}

	
	/**
	 * @return the new convertView
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		if (convertView == null) {
			// inflate the layout
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			convertView = inflater.inflate(layoutResourceId, parent, false);
		}
				// object item based on the position
				Alarm alarm = alarmList.get(position);
		
				TextView textViewItem = (TextView) convertView.findViewById(R.id.reminderOverviewPicture);
				textViewItem.setText(alarm.getName());
				
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(alarm.getTime());
				SimpleDateFormat format1 = new SimpleDateFormat("HH:mm dd.MM.yy");
				textViewItem = (TextView) convertView.findViewById(R.id.reminderOverviewDescription);								
				textViewItem.setText("Reminder: " + format1.format(cal.getTime()));
				
				ImageView imageViewItem = (ImageView) convertView.findViewById(R.id.reminderOverviewThumbnail);
//				imageViewItem.setImageBitmap(getBitmap(alarm.getImagePath()));
				String[] path = {alarm.getImagePath()};
//				imageViewItem.setImageBitmap(new ThumbnailTask().execute(path));
				loadBitmap(alarm.getImagePath(), imageViewItem);

				
		return convertView;

	}

	/**
	 * @param currentposition
	 */
	public void setCurrentPosition(int currentposition) {
		this.currentposition = currentposition;
	}
	
	
	public Bitmap getBitmap(String path) {
		try{   //Decode image size
//        BitmapFactory.Options o = new BitmapFactory.Options();
//        o.inJustDecodeBounds = true;
//        BitmapFactory.decodeStream(new FileInputStream(path),null,o);
//
//        //The new size we want to scale to
//        final int REQUIRED_SIZE=10;
//
//        //Find the correct scale value. It should be the power of 2.
//        int scale=1;
//        while(o.outWidth/scale/2>=REQUIRED_SIZE && o.outHeight/scale/2>=REQUIRED_SIZE)
//            scale*=2;

        //Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
//        o2.inSampleSize=64;
        o2.inJustDecodeBounds = true;
      	Bitmap bm = BitmapFactory.decodeStream(new FileInputStream(path), null, o2);
	    return ThumbnailUtils.extractThumbnail(bm, 100, 100);
    } catch (FileNotFoundException e) {}
    return null;
    
	}
	

}

class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
	 private final WeakReference<ImageView> thumbnail;
	 String path;
    
    @Override
    protected void onPostExecute(Bitmap bitmap) {
//    	final ImageView imageView = thumbnail.get();
//    	imageView.setImageBitmap(bitmap);
    	 if (thumbnail != null && bitmap != null) {
             final ImageView imageView = thumbnail.get();
             if (imageView != null) {
                 imageView.setImageBitmap(bitmap);
             }
         }
    }
	
	private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
		   if (imageView != null) {
		       final Drawable drawable = imageView.getDrawable();
		       if (drawable instanceof AsyncDrawable) {
		           final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
		           return asyncDrawable.getBitmapWorkerTask();
		       }
		    }
		    return null;
		}
    
	 public BitmapWorkerTask(ImageView thumb) {
		 thumbnail = new WeakReference<ImageView>(thumb);
	    }
	
	@Override
    protected Bitmap doInBackground(String... path) {
			try{   //Decode image size
//	        BitmapFactory.Options o = new BitmapFactory.Options();
//	        o.inJustDecodeBounds = true;
//	        BitmapFactory.decodeStream(new FileInputStream(path),null,o);
	//
//	        //The new size we want to scale to
//	        final int REQUIRED_SIZE=10;
	//
//	        //Find the correct scale value. It should be the power of 2.
//	        int scale=1;
//	        while(o.outWidth/scale/2>=REQUIRED_SIZE && o.outHeight/scale/2>=REQUIRED_SIZE)
//	            scale*=2;
			this.path = path[0];
	        //Decode with inSampleSize
	        BitmapFactory.Options o2 = new BitmapFactory.Options();
	        o2.inSampleSize=32;
//	        o2.inJustDecodeBounds = true;
	      	Bitmap bm = BitmapFactory.decodeStream(new FileInputStream(path[0]), null, o2);
//		    return ThumbnailUtils.extractThumbnail(bm, 100, 100);
	      	return bm;
	    } catch (FileNotFoundException e) {}
	    return null;
	    
		}

//    @Override
//    protected void onPostExecute(Bitmap bmp) {
//        super.onPostExecute(bmp);
//
//        if (bmp != null) {
//            item = new MapLocation();
//            item.icon = BitmapDescriptorFactory.FromBitmap(bmp);
//            item.Location = new LatLng (-41.227834, 174.812857);
//            item.Snippet = "Snippet2";
//            item.Title = "Title2";
//            item.ShowInfoWindowOnStartup = true;
//            _mapLocationList.Add(item);
//        }
//    }
}

class AsyncDrawable extends ColorDrawable {
    private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

    public AsyncDrawable(BitmapWorkerTask bitmapDownloaderTask) {
        super(Color.BLACK);
        bitmapWorkerTaskReference =
            new WeakReference<BitmapWorkerTask>(bitmapDownloaderTask);
    }

    public BitmapWorkerTask getBitmapWorkerTask() {
        return bitmapWorkerTaskReference.get();
    }
}