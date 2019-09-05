package com.example.me.fscbookfinder;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.me.fscbookfinder.model.ListItems;

import java.util.List;

public class ItemArrayAdapter extends RecyclerView.Adapter<ItemArrayAdapter.ViewHolder> {

    private List<ListItems> items;
    Context _context;

    public ItemArrayAdapter(Context context, List<ListItems> books) {
        items = books;
        _context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
       public TextView bookName;
       public TextView price;
       public Button buy;


        public ViewHolder(LinearLayout v) {
          super(v);
          bookName = (TextView)v.findViewById(R.id.nameTxt);
          price = (TextView)v.findViewById(R.id.priceTxt);
          buy = (Button)v.findViewById(R.id.buyBtn);
        }
    }


    @Override
    public ItemArrayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_list, parent, false);
        ViewHolder vh = new ViewHolder((LinearLayout) v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ItemArrayAdapter.ViewHolder holder, int position) {
        final String tempTitle = items.get(position).getTitleName();
        holder.bookName.setText(items.get(position).getTitleName());

        final String tempPrice = Double.toString(items.get(position).getPriceOfBook());
        final double tempPriceDouble = items.get(position).getPriceOfBook();
        holder.price.setText(tempPrice);

      final String UID = items.get(position).getUID();

        holder.buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(_context, Description_Activity.class);
                intent.putExtra("Title", tempTitle);
                intent.putExtra("Price", tempPriceDouble);
                intent.putExtra("UID", UID);
                _context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
