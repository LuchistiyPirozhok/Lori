package ru.ssau.rest_app.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import ru.ssau.rest_app.R;
import ru.ssau.rest_app.data.LinkedItem;

/**
 * Created by Дмитрий on 23.12.2016.
 */
public class LinkedItemAdapter extends BaseAdapter implements Filterable {
    private List<LinkedItem> original=null;
    private List<LinkedItem> filtered=null;
    private LayoutInflater mInflater;
    private LinkFilter mFilter = new LinkFilter();

    public LinkedItemAdapter(Context ctx, List<LinkedItem> original) {
        this.original = original;
        this.filtered = original;
        mInflater= LayoutInflater.from(ctx);
    }
    public void addLinkedItem(LinkedItem item){
        original.add(item);
    }

    @Override
    public int getCount() {
        return filtered.size();
    }

    @Override
    public Object getItem(int i) {
        return filtered.get(i);
    }
    public LinkedItem getLinkedItem(int i){
        return (LinkedItem) getItem(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public int getIndexById(String id){
        int res=-1;
        for(int i=0;i<filtered.size();i++){
            if(filtered.get(i).getId().equals(id)){
                res=i;
                break;
            }
        }
        Log.i("[idx]","id:"+id+",idx:"+res);
        return res;
    }
    @Override
    public View getView(int i, View cview, ViewGroup parent) {
        View view=cview;
        if(view==null){
            view=mInflater.inflate(R.layout.spinner_item,parent,false);
        }
        LinkedItem li=getLinkedItem(i);
        ((TextView) view.findViewById(R.id.spinner_text)).setText(li.getName());
        return view;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }
    private class LinkFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString;
            if(constraint==null){
                filterString="";
            }else{
                filterString = constraint.toString();
            }

            FilterResults results = new FilterResults();
            ArrayList<LinkedItem> newFiltered=new ArrayList<>();
            if(filterString.equals("")){
                newFiltered.addAll(original);
            }else {
                for (LinkedItem li : original) {
                    if (li.getLinkedId().equals(filterString)) {
                        newFiltered.add(li);
                    }
                }
            }
            results.values = newFiltered;
            results.count = newFiltered.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            filtered = (List<LinkedItem>) filterResults.values;
            notifyDataSetChanged();
        }
    }
}
