package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bruno.gerenciadordearquivos.MainActivity;
import com.example.bruno.gerenciadordearquivos.R;

public class CustomAdapter extends BaseAdapter {

    private Context mContext;
    private String[] mTitles;
    private int[] mImgIds;

    public CustomAdapter(Context context, String[] titles, int[] imgIds) {
        this.mContext = context;
        this.mTitles = titles;
        this.mImgIds = imgIds;
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }

    @Override
    public Object getItem(int position) {
        return mTitles[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.row, parent, false);
        ImageView iv = row.findViewById(R.id.imgIcon);
        TextView tv = row.findViewById(R.id.fileSystemEntryName);

        tv.setText(mTitles[position]);
        iv.setImageResource(mImgIds[position]);

        return row;
    }
}
