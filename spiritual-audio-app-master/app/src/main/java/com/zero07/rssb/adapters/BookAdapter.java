package dummydata.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import dummydata.models.BookModel;
import dummydata.R;
import dummydata.activity.PdfViewerActivity;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {


    Context context;
    List<BookModel> bookModels;

    public BookAdapter(Context context, List<BookModel> bookModels) {
        this.context = context;
        this.bookModels = bookModels;
    }

    @Override
    public BookAdapter.BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_rv_book_activity, null);
        return new BookAdapter.BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookAdapter.BookViewHolder holder, int position) {
        BookModel bookModel = bookModels.get(position);

        holder.pdfName.setText(bookModel.getPdfName());

        Glide.with(context)
                .load(bookModel.getPdfImageUrl())
                .into(holder.pdfImageUrl);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PdfViewerActivity.class);
                intent.putExtra("url", bookModels.get(position).getPdfContentUrl());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookModels.size();
    }

    public class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView pdfImageUrl;
        TextView pdfName;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            pdfImageUrl = itemView.findViewById(R.id.book_image);
            pdfName = itemView.findViewById(R.id.book_name);
        }
    }
}
