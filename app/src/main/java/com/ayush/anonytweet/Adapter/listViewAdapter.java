package com.ayush.anonytweet.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ayush.anonytweet.R;
import com.ayush.anonytweet.User;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class listViewAdapter extends BaseAdapter {

    Activity activity;
    List<User> firstUsers;
    LayoutInflater inflater;

    public listViewAdapter(Activity activity, List<User> firstUsers) {
        this.activity = activity;
        this.firstUsers = firstUsers;
    }

    @Override
    public int getCount() {
        return firstUsers.size();
    }

    @Override
    public Object getItem(int i) {
        return firstUsers.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        inflater = (LayoutInflater) activity.getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.item_list, null);

        TextView txtUser  = (TextView) itemView.findViewById(R.id.list_desc);
        ImageView imgUser = (ImageView) itemView.findViewById(R.id.list_avatar);

        if (firstUsers.get(i).getText().equals("") == false){
            txtUser.setText(firstUsers.get(i).getText());
        }

        Bitmap bitmap = null;

        if (firstUsers.get(i).getImagePath().equals("") == false){
              //URL url = new URL(firstUsers.get(i).getImagePath().toString());
            //new GetImageFromUrl(imgUser).execute(firstUsers.get(i).getImagePath().toString());

            // avoids android.os.NetworkOnMainThreadException
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            getImageFromUrl obj = new getImageFromUrl(firstUsers.get(i).getImagePath());
            bitmap = obj.getBitmap();
            imgUser.setImageBitmap(bitmap);

        }

        return itemView;
    }

    /* public class GetImageFromUrl extends AsyncTask<String, Void, Bitmap> {

        //ImageView imgView;
        Bitmap img;

        public GetImageFromUrl(ImageView imgView) {
            //this.imgView = imgView;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            //imgView.setImageBitmap(bitmap);

        }

        @Override
        protected Bitmap doInBackground(String... url) {

            String urlDisplay = url[0];
            img = null;
            try{
                InputStream srt = new java.net.URL(urlDisplay).openStream();
                img = BitmapFactory.decodeStream(srt);
            }
            catch (IOException e){
            }

            return img;
        }

        protected Bitmap getBitmap(){
            return img;
        }
    }
*/

    public class getImageFromUrl{
        Bitmap bitmap;
        String url;

        public getImageFromUrl(Bitmap bitmap) {
        }

        public getImageFromUrl(String url) {
            this.url = url;
        }

        public Bitmap getBitmap(){
            try{
                InputStream srt = new URL(url).openStream();
                bitmap = BitmapFactory.decodeStream(srt);
            }
            catch (IOException e){
            }

            return bitmap;
        }
    }

}
