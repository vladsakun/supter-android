package com.example.supter.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.example.supter.MOVIE_MESSAGE
import com.example.supter.R
import com.example.supter.data.db.entity.MovieEntity
import com.example.supter.ui.moviedetail.MovieDetailActivity
import java.io.ByteArrayOutputStream


class MovieListAdapter(
    private val context: Context,
    private val activity: AppCompatActivity,
    var moviesArrayList: List<MovieEntity>

) : RecyclerView.Adapter<MovieListAdapter.MovieListViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    inner class MovieListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        //Init views
        var moviePoster: ImageView = itemView.findViewById(R.id.movie_poster)
        var movieName: TextView = itemView.findViewById(R.id.movie_name)
        var movieRating: TextView = itemView.findViewById(R.id.rating)
        var movieOverview: TextView = itemView.findViewById(R.id.movie_overview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieListViewHolder {
        val view = inflater.inflate(R.layout.movie_item, parent, false)
        return MovieListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return moviesArrayList.size
    }

    override fun onBindViewHolder(holder: MovieListViewHolder, position: Int) {

        //Set text values to view of item's values
        holder.movieName.text = moviesArrayList[position].title
        holder.movieOverview.text = moviesArrayList[position].overview
        holder.movieRating.text = moviesArrayList[position].vote_average.toString()

        val bm: Bitmap
        bm = if (moviesArrayList[position].image == null) {
            val d: Drawable? = ContextCompat.getDrawable(context, R.mipmap.default_film)
            val bitmap = (d as BitmapDrawable).bitmap
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            bitmap
        } else {
            BitmapFactory.decodeByteArray(
                moviesArrayList[position].image,
                0,
                moviesArrayList[position].image!!.size
            )
        }

        //convert byte array to bitmap
//        val bm = BitmapFactory.decodeByteArray(
//            moviesArrayList[position].image,
//            0,
//            moviesArrayList[position].image!!.size
//        )

        //Set bitmap image on movie poster
        holder.moviePoster.setImageBitmap(bm)

        //On movie click switch on Detail Activity
        holder.itemView.setOnClickListener {

            //Pairs of views
            val p1 = Pair.create<View, String>(
                holder.moviePoster,
                MovieDetailActivity.VIEW_NAME_HEADER_IMAGE
            )
            val p2 = Pair.create<View, String>(
                holder.movieName,
                MovieDetailActivity.VIEW_NAME_HEADER_TITLE
            )

            val p3 = Pair.create<View, String>(
                holder.movieRating,
                MovieDetailActivity.VIEW_NAME_HEADER_VOTE_AVERAGE
            )

            val p4 = Pair.create<View, String>(
                holder.movieOverview,
                MovieDetailActivity.VIEW_NAME_OVERVIEW
            )

            // Construct an Intent
            val intent = Intent(context, MovieDetailActivity::class.java)
            intent.putExtra(MOVIE_MESSAGE, moviesArrayList[position])

            //Set pairs for transition
            val activityOptions: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    activity, p1, p2, p3, p4
                )

            //Start the Activity, providing the activity options as a bundle
            context.startActivity(intent, activityOptions.toBundle())
        }
    }
}