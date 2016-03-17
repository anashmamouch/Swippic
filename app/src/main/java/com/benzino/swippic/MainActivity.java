package com.benzino.swippic;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.benzino.swippic.cards.SwipeDeck;
import com.benzino.swippic.model.Card;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ANAS@ " + MainActivity.class.getSimpleName();

    private Context context = this;

    private SwipeDeck cardStack;
    private ArrayList<Card> data;

    private Cursor cursor;
    private int dataColumn;
    private int sizeColumn;

    private TextView mCounterTextView;
    private TextView mNotificationTextView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCounterTextView = (TextView) findViewById(R.id.size_textView);
        mNotificationTextView = (TextView) findViewById(R.id.text_notification);

        final String[] projection = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.SIZE};
        cursor = this.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                null);

        dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);

        cardStack = (SwipeDeck) findViewById(R.id.swipe_deck);
        cardStack.setHardwareAccelerationEnabled(true);

        data = new ArrayList<>();

        if (cursor.moveToFirst()){
            data.add(new Card(cursor.getString(dataColumn), cursor.getString(sizeColumn)));
            cursor.moveToNext();

            data.add(new Card(cursor.getString(dataColumn), cursor.getString(sizeColumn)));
            cursor.moveToNext();

            data.add(new Card(cursor.getString(dataColumn), cursor.getString(sizeColumn)));
            cursor.moveToNext();

        }

        cardStack.setAdapter(new SwipeDeckAdapter(data, this));

        final int[] sum = {0};
        final int[] counter = {0};

        cardStack.setLeftImage(R.id.left_image);
        cardStack.setRightImage(R.id.right_image);

        cardStack.setEventCallback(new SwipeDeck.SwipeEventCallback() {
            @Override
            public void cardSwipedLeft(int position) {
                Log.e(TAG, "LEFT SWIPE " + position + " / " + data.get(position).getPath());

                counter[0]++;

                sum[0] += Integer.parseInt(data.get(position).getSize());

                float size = (float)sum[0] / (float)(1024*1024) ;

                mCounterTextView.setText(String.format("%.02f", size) + " MB");
                mNotificationTextView.setText(String.valueOf(counter[0]));

                if (cursor.moveToNext()){
                    data.remove(position);
                    data.add(new Card(cursor.getString(dataColumn), cursor.getString(sizeColumn)));
                    //cursor.moveToNext();
                    cardStack.setAdapter(new SwipeDeckAdapter(data, getApplicationContext()));
                }

            }

            @Override
            public void cardSwipedRight(int position) {
                Log.i(TAG, "RIGHT SWIPE " + position + " / " + data.get(position).getPath());

                if (cursor.moveToNext()){
                    data.remove(position);
                    data.add(new Card(cursor.getString(dataColumn), cursor.getString(sizeColumn)));
                    //cursor.moveToNext();
                    cardStack.setAdapter(new SwipeDeckAdapter(data, getApplicationContext()));
                }else {
                    //cursor.close();
                }
            }

            @Override
            public void cardsDepleted() {
                Log.i("MainActivity", "no more cards");
                cursor.close();

            }

            @Override
            public void cardActionDown() {

            }

            @Override
            public void cardActionUp() {

            }
        });





    }



    public void onKeep(View view){
        cardStack.swipeTopCardRight(view.getId());

    }

    public void onTrash(View view){
        cardStack.swipeTopCardLeft(view.getId());

    }


    /*
    * Adapter class
    * */

    public class SwipeDeckAdapter extends BaseAdapter {

        private List<Card> data;
        private Context context;

        public SwipeDeckAdapter(List<Card> data, Context context) {
            this.data = data;
            this.context = context;
        }

        public void add(Card card){
            data.add(card);
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View v = convertView;
            if(v == null){
                LayoutInflater inflater = getLayoutInflater();
                // normally use a viewholder
                v = inflater.inflate(R.layout.card_view, parent, false);
            }

            float size = (float)Integer.parseInt(data.get(position).getSize()) / (float)(1024 * 1024);

            ((TextView) v.findViewById(R.id.size)).setText(String.format("%.02f", size) + " MB");
            ImageView imageView = (ImageView) v.findViewById(R.id.image);

            Picasso.with(context).load(new File(data.get(position).getPath())).resize(480, 640).centerCrop().into(imageView);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //String item = (String)getItem(position);
                    Log.i("MainActivity", ""+ position);
                }
            });

            return v;
        }
    }

}
