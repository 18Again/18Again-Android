package com.cookandroid.withmt.MainPage;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.cookandroid.withmt.R;

import java.util.ArrayList;

class MyAdapter extends BaseAdapter implements Filterable{
    Context context;
    int layout;
    LayoutInflater inf;
    ArrayList<MainPageView.WritingList> li;
    ArrayList<MainPageView.WritingList> li_search = li;
    Filter listFilter;
    private Object BoardResponse;

    public MyAdapter(Context context, int layout, ArrayList<MainPageView.WritingList> li) {
        this.context = context;
        this.layout = layout;
        this.li = li;
        this.li_search = li;
        inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return li_search.size();
    }

    @Override
    public Object getItem(int position) {
        return li_search.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inf.inflate(R.layout.main_list, parent, false);
        }

        TextView title = (TextView) convertView.findViewById(R.id.title);
        ImageView calender_icon = (ImageView) convertView.findViewById(R.id.calender_icon);
        TextView date = (TextView) convertView.findViewById(R.id.date);
        TextView gender_info = (TextView) convertView.findViewById(R.id.gender_info);
        TextView gender = (TextView) convertView.findViewById(R.id.gender);
        TextView imoji = (TextView) convertView.findViewById(R.id.user_icon);
        TextView nickname = (TextView) convertView.findViewById(R.id.user);
        TextView update_time = (TextView) convertView.findViewById(R.id.update_time);

        MainPageView.WritingList w = li_search.get(position);
        title.setText(w.title);
        date.setText(w.date);
        gender.setText(w.gender);
        imoji.setText(w.imoji);
        nickname.setText(w.nickname);
        update_time.setText(w.update_time);

        return convertView;
    }

    @Override
    public Filter getFilter() {
        if(listFilter == null) {
            listFilter = new ListFilter();
        }
        return listFilter;
    }

    private class ListFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if(constraint == null || constraint.length() == 0) {
                results.values = li;
                results.count = li.size();
            } else {
                ArrayList<MainPageView.WritingList> li_item = new ArrayList<MainPageView.WritingList>();

                for(MainPageView.WritingList w : li) {
                    if(w.getTitle().toUpperCase().contains(constraint.toString().toUpperCase())) {
                        li_item.add(w);
                    }
                }

                results.values = li_item;
                results.count = li_item.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            li_search = (ArrayList<MainPageView.WritingList>) results.values;
            if(results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}