package com.sdsmdg.bookshareapp.BSA.ui;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sdsmdg.bookshareapp.BSA.R;
import com.sdsmdg.bookshareapp.BSA.api.NetworkingFactory;
import com.sdsmdg.bookshareapp.BSA.api.UsersAPI;
import com.sdsmdg.bookshareapp.BSA.api.models.LocalBooks.Book;
import com.sdsmdg.bookshareapp.BSA.api.models.UserInfo;
import com.sdsmdg.bookshareapp.BSA.ui.adapter.Local.UsersAdapter;
import com.sdsmdg.bookshareapp.BSA.utils.Helper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.blurry.Blurry;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookDetailsActivity extends AppCompatActivity {

    public static final String TAG = BookDetailsActivity.class.getSimpleName();

    Book book;
    String title, author, gr_id, gr_img_url;
    Long ratingsCount;
    Float rating;
    public TextView authorBook;
    TextView bookTitle;
    public RatingBar ratingBook;
    public TextView ratingCount;
    List<UserInfo> userInfoList;
    UsersAdapter usersAdapter;
    String bookId, bookTitleText;
    ImageView image;
    public static String Response;
    ProgressBar progressBar;
    FrameLayout rootView;
    NestedScrollView scrollView;

    public static String getResponse() {
        return Response;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        authorBook = (TextView) findViewById(R.id.book_author);
        ratingBook = (RatingBar) findViewById(R.id.book_rating);
        ratingCount = (TextView) findViewById(R.id.ratings_count);
        image = (ImageView) findViewById(R.id.book_image);
        bookTitle = (TextView) findViewById(R.id.book_title);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        rootView = (FrameLayout) findViewById(R.id.root_view);
        scrollView = (NestedScrollView) findViewById(R.id.scroll_view);
        scrollView.setVisibility(View.INVISIBLE);

        SharedPreferences prefs = getSharedPreferences("Token", MODE_PRIVATE);

        String id = getIntent().getExtras().getString("id");
        String idd = prefs.getString("id", "");

        UsersAPI api = NetworkingFactory.getLocalInstance().getUsersAPI();
        Call<Book> call = api.getBookDetails(id);
        call.enqueue(new Callback<Book>() {
            @Override
            public void onResponse(Call<Book> call, Response<Book> response) {
                if (response.body() != null && response.body().getDetail() == null) {
                    book = response.body();
                    Response = response.toString();
                    Helper.setBookId(book.getId());
                    Helper.setBookTitle(book.getTitle());
                    bookId = book.getId();
                    gr_id = book.getId();
                    bookTitleText = book.getTitle();
                    bookTitle.setText(book.getTitle());
                    title = book.getTitle();
                    authorBook.setText("by  " + book.getAuthor());
                    author = book.getAuthor();
                    ratingCount.setText("(" + book.getRatingsCount().toString() + ")");
                    ratingsCount = book.getRatingsCount();
                    ratingBook.setRating(book.getRating());
                    rating = book.getRating();
                    Picasso.with(BookDetailsActivity.this).load(book.getGrImgUrl()).into(image);
                    Blurry.with(BookDetailsActivity.this)
                            .radius(25)
                            .sampling(1)
                            .color(Color.argb(66, 0, 0, 0))
                            .async()
                            .capture(findViewById(R.id.book_image))
                            .into((ImageView) findViewById(R.id.book_image));
                    gr_img_url = book.getGrImgUrl();
                    List<UserInfo> userTempInfoList = book.getUserInfoList();
                    userInfoList.clear();
                    userInfoList.addAll(userTempInfoList);
                    usersAdapter.setBookId(book.getId());
                    usersAdapter.setBookTitle(book.getTitle());
                    usersAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getApplicationContext(), "Book not found", Toast.LENGTH_SHORT).show();
                }
                TransitionManager.beginDelayedTransition(rootView);
                progressBar.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<Book> call, Throwable t) {
                Log.d("BookDetails fail", t.toString());
                TransitionManager.beginDelayedTransition(rootView);
                progressBar.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
            }
        });
        RecyclerView usersList = (RecyclerView) findViewById(R.id.reader_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        usersList.setLayoutManager(layoutManager);
        userInfoList = new ArrayList<>();
        usersAdapter = new UsersAdapter(idd, this, userInfoList, bookTitleText, bookId, new UsersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(UserInfo userInfo) {
                Log.i(TAG, "onItemClick");
            }
        });
        usersList.setAdapter(usersAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return (true);
        }

        return (super.onOptionsItemSelected(item));
    }

    public void addToMyLibraryClicked(View view) {
        UsersAPI usersAPI = NetworkingFactory.getLocalInstance().getUsersAPI();
        Call<Book> addBookCall = usersAPI.addBook(Helper.getUserEmail(), title, author, gr_id, ratingsCount, rating, gr_img_url);
        addBookCall.enqueue(new Callback<Book>() {
            @Override
            public void onResponse(Call<Book> call, Response<Book> response) {
                Log.i("Email iD ", Helper.getUserEmail());
                if (response.body() != null) {
                    Toast.makeText(BookDetailsActivity.this, response.body().getDetail(), Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(BookDetailsActivity.this, response.body().getDetail() + "ssss", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Book> call, Throwable t) {
                Log.i("AddBook", "Failed!!");
            }
        });
    }

}

