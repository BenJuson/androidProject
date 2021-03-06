package ca.bcit.project.findwater;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nelson on 10/11/2017.
 */

public class FountainAdapter extends BaseAdapter implements Filterable {

    private Context _context;
    private List<Fountain> fountains;
    private List<Fountain> mStringFilterList;
    private ValueFilter valueFilter;


    public FountainAdapter(Context context, ArrayList<Fountain> fountains) {
        this._context = context;
        this.fountains = fountains;
        this.mStringFilterList = fountains;
    }


    @Override
    public int getCount() {
        return fountains.size();
    }

    @Override
    public Object getItem(int position) {
        return fountains.get(position);
    }

    @Override
    public long getItemId(int position) {
        return fountains.indexOf(getItem(position));
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        String parkname;
        LayoutInflater mInflater = (LayoutInflater) _context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        convertView = null;
        final MyDbHelper helper = new MyDbHelper(this._context);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_row_layout, parent, false);

            // Lookup view for data population
            TextView parkName = (TextView) convertView.findViewById(R.id.parkName);
            TextView Distance = (TextView) convertView.findViewById(R.id.distance);

            parkname = fountains.get(position).getParkName();
            Distance.setText(fountains.get(position).getDistance()+"KM");
            final ImageView imgOnePhoto = (ImageView) convertView.findViewById(R.id.fountainImage);
            final ImageView favorite = (ImageView) convertView.findViewById(R.id.favorite);

            //replace the space with dash for park image and then get the id
            int imageResource = _context.getResources().getIdentifier("@drawable/"+parkname.replace(' ','_').toLowerCase() , null, _context.getPackageName());

            if(imageResource != 0){
                parkName.setText(parkname);
                imgOnePhoto.setImageResource(imageResource);
            }else {
                parkName.setText("Myth Fountain");
                imgOnePhoto.setImageResource(R.drawable.not_found);
            }

            // display favorite icon
            final int FavoriteStatus = helper.getFavoriteStatus(fountains.get(position).getX(),
                    fountains.get(position).getY());

            if(FavoriteStatus == 1) {
                favorite.setImageResource(R.drawable.ic_action_favorite);
                Distance.setText(helper.getDistance(fountains.get(position).getX(),
                        fountains.get(position).getY())+"KM");
            } else {
                favorite.setImageResource(R.drawable.ic_action_favorite_border);
            }

            favorite.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if(FavoriteStatus == 1) {
                        // update favorite status to 0
                        helper.updateFavorite(fountains.get(position).getX(),
                                fountains.get(position).getY(), 0);
                        // display favorite border icon
                        favorite.setImageResource(R.drawable.ic_action_favorite_border);
                    } else {
                        // update favorite status to 1
                        helper.updateFavorite(fountains.get(position).getX(),
                                fountains.get(position).getY(), 1);
                        // display favorite icon
                        favorite.setImageResource(R.drawable.ic_action_favorite);
                    }
                }
            });
        }
        return convertView;
    }


    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                ArrayList<Fountain> filterList = new ArrayList<>();
                for (int i = 0; i < mStringFilterList.size(); i++) {
                    if ((mStringFilterList.get(i).getParkName().toUpperCase()).contains(constraint.toString().toUpperCase())) {

                        Fountain fountain = new Fountain(mStringFilterList.get(i).getParkName());
                        fountain.setDistance(mStringFilterList.get(i).getDistance());
                        filterList.add(fountain);
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = mStringFilterList.size();
                results.values = mStringFilterList;
            }
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            fountains = (ArrayList<Fountain>) results.values;

            notifyDataSetChanged();
        }
    }


}


